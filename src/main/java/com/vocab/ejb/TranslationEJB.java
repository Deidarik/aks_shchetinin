package com.vocab.ejb;

import com.vocab.entity.Translation;
import com.vocab.entity.Word;
import jakarta.ejb.EJBException;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class TranslationEJB {

    @PersistenceContext(unitName = "vocabPU")
    private EntityManager em;

    public List<Translation> findAll() {
        return em.createQuery(
                "select distinct t from Translation t " +
                        "join fetch t.word join fetch t.word.category " +
                        "order by t.word.englishWord, t.id", Translation.class
        ).getResultList();
    }

    public Long create(Long wordId, String meaning, String example) {
        Word word = resolveWord(wordId);
        validateMeaning(meaning);

        Translation translation = new Translation();
        translation.setWord(word);
        translation.setMeaning(meaning.trim());
        translation.setExample(example);

        em.persist(translation);
        return translation.getId();
    }

    public void update(Long id, Long wordId, String meaning, String example) {
        Translation translation = em.find(Translation.class, id);
        if (translation == null) {
            throw new EJBException("Перевод не найден: " + id);
        }
        validateMeaning(meaning);

        translation.setWord(resolveWord(wordId));
        translation.setMeaning(meaning.trim());
        translation.setExample(example);
    }

    public void delete(Long id) {
        Translation translation = em.find(Translation.class, id);
        if (translation != null) {
            em.remove(translation);
        }
    }

    private Word resolveWord(Long wordId) {
        if (wordId == null) {
            throw new EJBException("Выберите слово");
        }
        Word word = em.find(Word.class, wordId);
        if (word == null) {
            throw new EJBException("Слово не найдено: " + wordId);
        }
        return word;
    }

    private void validateMeaning(String meaning) {
        if (meaning == null || meaning.isBlank()) {
            throw new EJBException("Перевод не может быть пустым");
        }
    }
}