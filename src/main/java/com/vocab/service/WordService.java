package com.vocab.service;

import com.vocab.dto.ChangeMessage;
import com.vocab.entity.Category;
import com.vocab.entity.Translation;
import com.vocab.entity.Word;
import com.vocab.repository.CategoryRepository;
import com.vocab.repository.WordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class WordService {

    @Autowired
    private WordRepository wordRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private JmsNotificationService jmsService; // <-- ДОБАВЬ ЭТО

    public List<Word> findAll() {
        return wordRepository.findAllWithDetails();
    }

    public Page<Word> findAllPaged(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("englishWord"));
        return wordRepository.findAllWithDetailsPaged(pageable);
    }

    public Page<Word> searchPaged(String query, int page, int size) {
        if (query == null || query.trim().isEmpty()) {
            return findAllPaged(page, size);
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by("englishWord"));
        return wordRepository.searchWithDetailsPaged("%" + query.trim() + "%", pageable);
    }

    public Word findById(Long id) {
        return wordRepository.findById(id).orElse(null);
    }

    public void create(String englishWord, String phonetics, String partOfSpeech,
                       Long categoryId, String meaning, String example) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Категория не найдена"));

        Word word = new Word();
        word.setEnglishWord(englishWord.trim());
        word.setPhonetics(phonetics.trim());
        word.setPartOfSpeech(partOfSpeech);
        word.setCategory(category);

        Translation translation = new Translation();
        translation.setMeaning(meaning.trim());
        translation.setExample(example);
        translation.setWord(word);

        word.getTranslations().add(translation);
        Word saved = wordRepository.save(word);

        // === ОТПРАВКА JMS СООБЩЕНИЙ ===
        ChangeMessage auditMsg = new ChangeMessage(
                "INSERT", "Word", saved.getId(),
                "englishWord", null, englishWord
        );
        jmsService.sendAuditMessage(auditMsg);

        // Уведомление по email при создании нового слова (Задание 5)
        jmsService.sendEmailNotification(auditMsg);
    }

    public void update(Long id, String englishWord, String phonetics, String partOfSpeech,
                       Long categoryId, Long translationId, String meaning, String example) {
        Word word = wordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Слово не найдено"));
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Категория не найдена"));

        String oldEnglish = word.getEnglishWord();

        word.setEnglishWord(englishWord.trim());
        word.setPhonetics(phonetics.trim());
        word.setPartOfSpeech(partOfSpeech);
        word.setCategory(category);

        if (!word.getTranslations().isEmpty()) {
            Translation t = word.getTranslations().get(0);
            String oldMeaning = t.getMeaning();
            t.setMeaning(meaning.trim());
            t.setExample(example);

            // Логируем изменение перевода
            ChangeMessage transMsg = new ChangeMessage(
                    "UPDATE", "Translation", t.getId(),
                    "meaning", oldMeaning, meaning
            );
            jmsService.sendAuditMessage(transMsg);
        }

        wordRepository.save(word);

        // Логируем изменение слова
        ChangeMessage wordMsg = new ChangeMessage(
                "UPDATE", "Word", id,
                "englishWord", oldEnglish, englishWord
        );
        jmsService.sendAuditMessage(wordMsg);
    }

    public void delete(Long id) {
        Word word = wordRepository.findById(id).orElse(null);
        if (word != null) {
            wordRepository.deleteById(id);

            ChangeMessage msg = new ChangeMessage(
                    "DELETE", "Word", id,
                    "englishWord", word.getEnglishWord(), null
            );
            jmsService.sendAuditMessage(msg);
            jmsService.sendEmailNotification(msg);
        }
    }
}