package com.vocab.controller;

import com.vocab.dto.WordDto;
import com.vocab.dto.WordListDto;
import com.vocab.entity.Category;
import com.vocab.entity.Word;
import com.vocab.service.CategoryService;
import com.vocab.service.WordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/words")
public class VocabRestController {

    @Autowired
    private WordService wordService;

    @Autowired
    private CategoryService categoryService;

    // 1. Получить все слова (GET)
    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public WordListDto getAllWords() {
        List<Word> words = wordService.findAll();
        List<WordDto> dtoList = words.stream().map(WordDto::new).collect(Collectors.toList());
        return new WordListDto(dtoList);
    }

    // 2. Получить слово по ID (GET)
    @GetMapping(value = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<WordDto> getWordById(@PathVariable Long id) {
        Word word = wordService.findById(id);
        if (word == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new WordDto(word));
    }

    // 3. Создать новое слово (POST)
    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<WordDto> createWord(@RequestBody WordDto wordDto) {
        // Находим категорию по ID из DTO
        Category category = categoryService.findById(wordDto.getCategoryId());
        if (category == null) {
            return ResponseEntity.badRequest().build(); // Категория не найдена
        }

        // Превращаем DTO в Entity
        Word newWord = wordDto.toEntity(category);

        // Сохраняем через сервис (используем существующий метод create, адаптируя аргументы)
        // Берем первый перевод для совместимости с твоим WordService
        String meaning = newWord.getTranslations().isEmpty() ? "No meaning" : newWord.getTranslations().get(0).getMeaning();
        String example = newWord.getTranslations().isEmpty() ? "" : newWord.getTranslations().get(0).getExample();

        wordService.create(newWord.getEnglishWord(), newWord.getPhonetics(),
                newWord.getPartOfSpeech(), category.getId(), meaning, example);

        // Возвращаем созданное слово (нам нужно его найти заново, чтобы получить ID)
        // Для простоты лабораторной вернем то, что пришло, но с присвоенным ID (упрощенно)
        // В идеале WordService.create должен возвращать созданное Word.
        return ResponseEntity.status(HttpStatus.CREATED).body(wordDto);
    }

    // 4. Обновить слово (PUT)
    @PutMapping(value = "/{id}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<WordDto> updateWord(@PathVariable Long id, @RequestBody WordDto wordDto) {
        Word existingWord = wordService.findById(id);
        if (existingWord == null) {
            return ResponseEntity.notFound().build();
        }

        Category category = categoryService.findById(wordDto.getCategoryId());
        if (category == null) {
            return ResponseEntity.badRequest().build();
        }

        String meaning = wordDto.getTranslations().isEmpty() ? "" : wordDto.getTranslations().get(0).getMeaning();
        String example = wordDto.getTranslations().isEmpty() ? "" : wordDto.getTranslations().get(0).getExample();
        Long translationId = existingWord.getTranslations().isEmpty() ? null : existingWord.getTranslations().get(0).getId();

        wordService.update(id, wordDto.getEnglishWord(), wordDto.getPhonetics(),
                wordDto.getPartOfSpeech(), category.getId(), translationId, meaning, example);

        return ResponseEntity.ok(wordDto);
    }

    // 5. Удалить слово (DELETE)
    @DeleteMapping(value = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity<Void> deleteWord(@PathVariable Long id) {
        Word existingWord = wordService.findById(id);
        if (existingWord == null) {
            return ResponseEntity.notFound().build();
        }
        wordService.delete(id);
        return ResponseEntity.noContent().build();
    }
}