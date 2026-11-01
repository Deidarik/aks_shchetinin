package com.vocab.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.vocab.entity.Category;
import com.vocab.entity.Translation;
import com.vocab.entity.Word;
import java.util.ArrayList;
import java.util.List;

public class WordDto {

    // ВАЖНО: Пустой конструктор для Jackson
    public WordDto() {}

    private Long id;
    private String englishWord;
    private String phonetics;
    private String partOfSpeech;

    // Для POST/PUT мы будем принимать ID категории
    private Long categoryId;
    // Для GET мы будем отдавать имя категории для красоты
    private String categoryName;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "translation")
    private List<TranslationDto> translations = new ArrayList<>();

    // Конструктор Entity -> DTO (используется в GET методах)
    public WordDto(Word word) {
        this.id = word.getId();
        this.englishWord = word.getEnglishWord();
        this.phonetics = word.getPhonetics();
        this.partOfSpeech = word.getPartOfSpeech();
        this.categoryId = word.getCategory() != null ? word.getCategory().getId() : null;
        this.categoryName = word.getCategory() != null ? word.getCategory().getName() : "Unknown";

        if (word.getTranslations() != null) {
            this.translations = word.getTranslations().stream()
                    .map(TranslationDto::new)
                    .toList();
        }
    }

    // Метод DTO -> Entity (используется в POST/PUT методах)
    public Word toEntity(Category category) {
        Word word = new Word();
        if (this.id != null) {
            word.setId(this.id);
        }
        word.setEnglishWord(this.englishWord);
        word.setPhonetics(this.phonetics);
        word.setPartOfSpeech(this.partOfSpeech);
        word.setCategory(category);

        if (this.translations != null) {
            for (TranslationDto tDto : this.translations) {
                Translation t = new Translation();
                t.setMeaning(tDto.getMeaning());
                t.setExample(tDto.getExample());
                t.setWord(word);
                word.getTranslations().add(t);
            }
        }
        return word;
    }

    // Геттеры и сеттеры (ВСЕ обязательны!)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEnglishWord() { return englishWord; }
    public void setEnglishWord(String englishWord) { this.englishWord = englishWord; }
    public String getPhonetics() { return phonetics; }
    public void setPhonetics(String phonetics) { this.phonetics = phonetics; }
    public String getPartOfSpeech() { return partOfSpeech; }
    public void setPartOfSpeech(String partOfSpeech) { this.partOfSpeech = partOfSpeech; }
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    public List<TranslationDto> getTranslations() { return translations; }
    public void setTranslations(List<TranslationDto> translations) { this.translations = translations; }
}