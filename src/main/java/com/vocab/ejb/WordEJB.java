package com.vocab.ejb;

import com.vocab.entity.Category;
import com.vocab.entity.Translation;
import com.vocab.entity.Word;
import jakarta.ejb.EJBException;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.Set;

@Stateless
public class WordEJB {

    private static final Set<String> PARTS_OF_SPEECH = Set.of(
            "noun", "verb", "adjective", "adverb",
            "pronoun", "preposition", "conjunction", "interjection"
    );

    @PersistenceContext(unitName = "vocabPU")
    private EntityManager em;


    public List<Word> findPaged(int firstResult, int maxResults) {
        return em.createQuery(
                        "select distinct w from Word w " +
                                "join fetch w.category left join fetch w.translations " +
                                "order by w.englishWord", Word.class)
                .setFirstResult(firstResult)
                .setMaxResults(maxResults)
                .getResultList();
    }

    public long count() {
        return em.createQuery("select count(w) from Word w", Long.class).getSingleResult();
    }


    public List<Word> search(String query, int firstResult, int maxResults) {
        if (query == null || query.trim().isEmpty()) {
            return findPaged(firstResult, maxResults);
        }
        return em.createQuery(
                        "select distinct w from Word w " +
                                "join fetch w.category left join fetch w.translations " +
                                "where lower(w.englishWord) like lower(:query) " +
                                "order by w.englishWord", Word.class)
                .setParameter("query", "%" + query.trim() + "%")
                .setFirstResult(firstResult)
                .setMaxResults(maxResults)
                .getResultList();
    }

    public long countSearch(String query) {
        if (query == null || query.trim().isEmpty()) {
            return count();
        }
        return em.createQuery(
                        "select count(w) from Word w where lower(w.englishWord) like lower(:query)", Long.class)
                .setParameter("query", "%" + query.trim() + "%")
                .getSingleResult();
    }


    public Long createWordWithTranslation(String englishWord, String phonetics, String partOfSpeech,
                                          Long categoryId, String meaning, String example) {
        validate(englishWord, phonetics, partOfSpeech);
        if (meaning == null || meaning.isBlank()) {
            throw new EJBException("Перевод (значение) обязателен при создании слова");
        }

        Word word = new Word();
        word.setEnglishWord(englishWord.trim());
        word.setPhonetics(phonetics.trim());
        word.setPartOfSpeech(partOfSpeech);
        word.setCategory(resolveCategory(categoryId));
        em.persist(word);

        Translation translation = new Translation();
        translation.setWord(word);
        translation.setMeaning(meaning.trim());
        translation.setExample(example);
        em.persist(translation);

        return word.getId();
    }

    public void updateWordWithTranslation(Long id, String englishWord, String phonetics,
                                          String partOfSpeech, Long categoryId,
                                          Long translationId, String meaning, String example) {
        Word word = em.find(Word.class, id);
        if (word == null) throw new EJBException("Слово не найдено: " + id);
        validate(englishWord, phonetics, partOfSpeech);

        word.setEnglishWord(englishWord.trim());
        word.setPhonetics(phonetics.trim());
        word.setPartOfSpeech(partOfSpeech);
        word.setCategory(resolveCategory(categoryId));

        Translation translation = (translationId != null) ? em.find(Translation.class, translationId) : null;
        if (translation == null) {
            translation = new Translation();
            translation.setWord(word);
        }
        translation.setMeaning(meaning != null ? meaning.trim() : "");
        translation.setExample(example);

        if (translation.getId() == null) {
            em.persist(translation);
        }
    }

    public Word find(Long id) {
        List<Word> result = em.createQuery(
                        "select w from Word w " +
                                "left join fetch w.category " +
                                "left join fetch w.translations " +
                                "where w.id = :id", Word.class)
                .setParameter("id", id)
                .getResultList();

        return result.isEmpty() ? null : result.get(0);
    }

    public void delete(Long id) {
        Word word = em.find(Word.class, id);
        if (word != null) {
            em.remove(word);
        }
    }

    private Category resolveCategory(Long categoryId) {
        if (categoryId == null) throw new EJBException("Выберите категорию");
        Category category = em.find(Category.class, categoryId);
        if (category == null) throw new EJBException("Категория не найдена: " + categoryId);
        return category;
    }

    private void validate(String englishWord, String phonetics, String partOfSpeech) {
        if (englishWord == null || englishWord.isBlank()) throw new EJBException("Слово не может быть пустым");
        if (phonetics == null || phonetics.isBlank()) throw new EJBException("Транскрипция не может быть пустой");
        if (partOfSpeech == null || !PARTS_OF_SPEECH.contains(partOfSpeech)) throw new EJBException("Некорректная часть речи");
    }
}