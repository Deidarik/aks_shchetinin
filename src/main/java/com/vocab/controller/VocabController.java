package com.vocab.controller;

import com.vocab.entity.Category;
import com.vocab.entity.Word;
import com.vocab.service.CategoryService;
import com.vocab.service.WordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;

@Controller
public class VocabController {

    // Логгер для отладки в консоли
    private static final Logger log = LoggerFactory.getLogger(VocabController.class);

    @Autowired
    private WordService wordService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/")
    public String index(@RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "5") int size,
                        @RequestParam(defaultValue = "0") int catPage,
                        @RequestParam(defaultValue = "10") int catSize, // Показываем по 10 категорий
                        Model model) {

        Page<Word> wordsPage = wordService.findAllPaged(page, size);
        Page<Category> categoriesPage = categoryService.findAllPaged(catPage, catSize);

        model.addAttribute("wordsPage", wordsPage);
        model.addAttribute("categoriesPage", categoriesPage);
        model.addAttribute("word", new Word());
        model.addAttribute("editingWord", null);

        return "index";
    }

    @GetMapping("/search")
    public String search(@RequestParam(required = false) String query,
                         @RequestParam(defaultValue = "0") int page,
                         @RequestParam(defaultValue = "5") int size,
                         @RequestParam(defaultValue = "0") int catPage,
                         @RequestParam(defaultValue = "10") int catSize,
                         Model model) {

        Page<Word> wordsPage = wordService.searchPaged(query, page, size);
        Page<Category> categoriesPage = categoryService.findAllPaged(catPage, catSize);

        model.addAttribute("wordsPage", wordsPage);
        model.addAttribute("categoriesPage", categoriesPage);
        model.addAttribute("word", new Word());
        model.addAttribute("editingWord", null);
        model.addAttribute("searchQuery", query);

        return "index";
    }

    // Сохранение/Обновление слова
    @PostMapping("/word/save")
    public String saveWord(@RequestParam(required = false) Long id,
                           @RequestParam String englishWord,
                           @RequestParam String phonetics,
                           @RequestParam String partOfSpeech,
                           @RequestParam Long categoryId,
                           @RequestParam String meaning,
                           @RequestParam(required = false) String example) {

        log.info(">>> Сохранение слова: '{}'", englishWord);

        if (id == null || id == 0) {
            wordService.create(englishWord, phonetics, partOfSpeech, categoryId, meaning, example);
        } else {
            // При обновлении передаем null вместо translationId, сервис сам найдет первый перевод
            wordService.update(id, englishWord, phonetics, partOfSpeech, categoryId, null, meaning, example);
        }
        return "redirect:/"; // Перенаправление на главную после сохранения
    }

    // Подготовка формы редактирования
    @GetMapping("/word/edit/{id}")
    public String editWord(@PathVariable Long id, Model model) {
        log.info(">>> Редактирование слова с ID: {}", id);

        Word word = wordService.findById(id);
        model.addAttribute("words", wordService.findAll());
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("word", word);
        model.addAttribute("editingWord", word); // Флаг, что мы в режиме редактирования

        return "index";
    }

    // Удаление слова
    @GetMapping("/word/delete/{id}")
    public String deleteWord(@PathVariable Long id) {
        log.info(">>> Удаление слова с ID: {}", id);
        wordService.delete(id);
        return "redirect:/";
    }

    // Создание категории
    @PostMapping("/category/save")
    public String saveCategory(@RequestParam String name,
                               @RequestParam(required = false) String description) {
        log.info(">>> Создание категории: '{}'", name);
        categoryService.create(name, description);
        return "redirect:/";
    }

    // Удаление категории
    @GetMapping("/category/delete/{id}")
    public String deleteCategory(@PathVariable Long id) {
        log.info(">>> Удаление категории с ID: {}", id);
        categoryService.delete(id);
        return "redirect:/";
    }
}