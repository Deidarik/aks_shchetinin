package com.vocab.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import java.util.List;

@JacksonXmlRootElement(localName = "wordList")
public class WordListDto {

    public WordListDto() {}

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "word")
    private List<WordDto> words;

    public WordListDto(List<WordDto> words) {
        this.words = words;
    }

    public List<WordDto> getWords() { return words; }
    public void setWords(List<WordDto> words) { this.words = words; }
}