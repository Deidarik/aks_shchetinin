package com.vocab.service;

import com.vocab.entity.Category;
import com.vocab.entity.Translation;
import com.vocab.entity.Word;
import com.vocab.repository.CategoryRepository;
import com.vocab.repository.WordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Service
@Transactional
public class WordService {

    @Autowired
    private WordRepository wordRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    public List<Word> findAll() {
        return wordRepository.findAllWithDetails();
    }

    public List<Word> search(String query) {
        if (query == null || query.trim().isEmpty()) {
            return findAll();
        }
        return wordRepository.searchWithDetails("%" + query.trim() + "%");
    }

    public Word findById(Long id) {
        return wordRepository.findById(id).orElse(null);
    }

    public void create(String englishWord, String phonetics, String partOfSpeech,
                       Long categoryId, String meaning, String example) {
        Category category = categoryRepository.findById(categoryId).orElseThrow();

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
        wordRepository.save(word);
    }

    public void update(Long id, String englishWord, String phonetics, String partOfSpeech,
                       Long categoryId, Long translationId, String meaning, String example) {
        Word word = wordRepository.findById(id).orElseThrow();
        Category category = categoryRepository.findById(categoryId).orElseThrow();

        word.setEnglishWord(englishWord.trim());
        word.setPhonetics(phonetics.trim());
        word.setPartOfSpeech(partOfSpeech);
        word.setCategory(category);

        // Обновляем первый перевод (для простоты UI)
        if (!word.getTranslations().isEmpty()) {
            Translation t = word.getTranslations().get(0);
            t.setMeaning(meaning.trim());
            t.setExample(example);
        } else {
            Translation translation = new Translation();
            translation.setMeaning(meaning.trim());
            translation.setExample(example);
            translation.setWord(word);
            word.getTranslations().add(translation);
        }
        wordRepository.save(word);
    }

    public void delete(Long id) {
        wordRepository.deleteById(id);
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
}