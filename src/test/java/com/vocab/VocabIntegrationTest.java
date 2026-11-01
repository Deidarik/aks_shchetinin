package com.vocab;

import com.vocab.entity.Category;
import com.vocab.entity.Word;
import com.vocab.service.CategoryService;
import com.vocab.service.WordService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class VocabIntegrationTest {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private WordService wordService;

    @Test
    public void contextLoads() {
        // Этот тест просто проверяет, что Spring-контекст успешно запустился
        assertNotNull(categoryService);
        assertNotNull(wordService);
    }

    @Test
    public void testCreateAndFindWordWithTranslation() {
        // 1. Создаем тестовую категорию
        Category testCategory = categoryService.create("TestCategory_JUnit", "Категория для автотеста");
        assertNotNull(testCategory.getId(), "ID категории не должен быть null");

        // 2. Создаем слово с переводом через Сервис
        wordService.create(
                "junittest",
                "[dʒʌnɪt tɛst]",
                "noun",
                testCategory.getId(),
                "юнит-тест",
                "This is a unit test example."
        );

        // 3. Получаем все слова из базы
        List<Word> allWords = wordService.findAll();
        assertFalse(allWords.isEmpty(), "Список слов не должен быть пустым");

        // 4. Ищем наше созданное слово
        Word foundWord = allWords.stream()
                .filter(w -> "junittest".equals(w.getEnglishWord()))
                .findFirst()
                .orElse(null);

        // 5. Проверяем, что слово и его перевод сохранились корректно
        assertNotNull(foundWord, "Слово 'junittest' должно быть найдено в базе");
        assertEquals("юнит-тест", foundWord.getTranslations().get(0).getMeaning(), "Перевод должен совпадать");
        assertEquals("TestCategory_JUnit", foundWord.getCategory().getName(), "Категория должна совпадать");
    }
}