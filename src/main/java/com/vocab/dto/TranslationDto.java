package com.vocab.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.vocab.entity.Translation;

public class TranslationDto {

    // ВАЖНО: Пустой конструктор для Jackson (десериализации)
    public TranslationDto() {}

    @JacksonXmlProperty(localName = "meaning")
    private String meaning;

    @JacksonXmlProperty(localName = "example")
    private String example;

    // Конструктор для превращения Entity -> DTO (для GET запросов)
    public TranslationDto(Translation t) {
        this.meaning = t.getMeaning();
        this.example = t.getExample();
    }

    // Геттеры и сеттеры (ОБЯЗАТЕЛЬНЫ для Jackson)
    public String getMeaning() { return meaning; }
    public void setMeaning(String meaning) { this.meaning = meaning; }
    public String getExample() { return example; }
    public void setExample(String example) { this.example = example; }
}