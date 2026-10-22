package com.vocab.web;

import com.vocab.ejb.CategoryEJB;
import com.vocab.ejb.TranslationEJB;
import com.vocab.ejb.WordEJB;
import com.vocab.entity.Category;
import com.vocab.entity.Translation;
import com.vocab.entity.Word;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;

@Named("vocab")
@ViewScoped
public class VocabularyBean implements Serializable {
    private static final long serialVersionUID = 1L;
    private Category categoryForm = new Category();
    private Long editingCategoryId;
    @EJB private WordEJB wordEJB;
    @EJB private TranslationEJB translationEJB;
    @EJB private CategoryEJB categoryEJB;

    private List<Word> words;
    private List<Category> categories;
    private List<String> partsOfSpeech = List.of("noun", "verb", "adjective", "adverb", "pronoun", "preposition", "conjunction", "interjection");


    private int page = 1;
    private int pageSize = 5;
    private long totalElements = 0;
    private String searchQuery = "";


    private Word wordForm = new Word();
    private Long categoryId;
    private Long editingWordId;

    private Long editingTranslationId;
    private String primaryMeaning = "";
    private String primaryExample = "";

    @PostConstruct
    public void init() {
        categories = categoryEJB.findAll();
        loadPage();
    }

    private void loadPage() {
        int firstResult = (page - 1) * pageSize;
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            words = wordEJB.search(searchQuery, firstResult, pageSize);
            totalElements = wordEJB.countSearch(searchQuery);
        } else {
            words = wordEJB.findPaged(firstResult, pageSize);
            totalElements = wordEJB.count();
        }
    }

    public int getTotalPages() {
        return (int) Math.ceil((double) totalElements / pageSize);
    }

    public void nextPage() {
        if (page < getTotalPages()) {
            page++;
            loadPage();
        }
    }

    public void prevPage() {
        if (page > 1) {
            page--;
            loadPage();
        }
    }

    public void search() {
        page = 1;
        loadPage();
    }

    public void resetSearch() {
        searchQuery = "";
        page = 1;
        loadPage();
    }

    public void prepareNewWord() {
        resetWordForm();
    }

    public void editWord(Long id) {
        Word found = wordEJB.find(id);
        editingWordId = id;
        wordForm = new Word();
        wordForm.setEnglishWord(found.getEnglishWord());
        wordForm.setPhonetics(found.getPhonetics());
        wordForm.setPartOfSpeech(found.getPartOfSpeech());
        categoryId = found.getCategory().getId();

        if (found.getTranslations() != null && !found.getTranslations().isEmpty()) {
            Translation t = found.getTranslations().get(0);
            editingTranslationId = t.getId();
            primaryMeaning = t.getMeaning();
            primaryExample = t.getExample();
        } else {
            editingTranslationId = null;
            primaryMeaning = "";
            primaryExample = "";
        }
    }

    public void saveWord() {
        if (editingWordId == null) {
            wordEJB.createWordWithTranslation(wordForm.getEnglishWord(), wordForm.getPhonetics(),
                    wordForm.getPartOfSpeech(), categoryId, primaryMeaning, primaryExample);
        } else {
            wordEJB.updateWordWithTranslation(editingWordId, wordForm.getEnglishWord(), wordForm.getPhonetics(),
                    wordForm.getPartOfSpeech(), categoryId, editingTranslationId, primaryMeaning, primaryExample);
        }
        resetWordForm();
        loadPage();
    }

    public void deleteWord(Long id) {
        wordEJB.delete(id);
        if (words.size() == 1 && page > 1) {
            page--;
        }
        loadPage();
    }

    public void cancelWordEdit() {
        resetWordForm();
    }

    private void resetWordForm() {
        editingWordId = null;
        editingTranslationId = null;
        categoryId = null;
        primaryMeaning = "";
        primaryExample = "";
        wordForm = new Word();
        wordForm.setPartOfSpeech("noun");
    }


    public List<Word> getWords() { return words; }
    public List<Category> getCategories() { return categories; }
    public List<String> getPartsOfSpeech() { return partsOfSpeech; }

    public int getPage() { return page; }
    public int getPageSize() { return pageSize; }
    public long getTotalElements() { return totalElements; }
    public String getSearchQuery() { return searchQuery; }
    public void setSearchQuery(String searchQuery) { this.searchQuery = searchQuery; }

    public Word getWordForm() { return wordForm; }
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    public Long getEditingWordId() { return editingWordId; }

    public String getPrimaryMeaning() { return primaryMeaning; }
    public void setPrimaryMeaning(String primaryMeaning) { this.primaryMeaning = primaryMeaning; }
    public String getPrimaryExample() { return primaryExample; }
    public void setPrimaryExample(String primaryExample) { this.primaryExample = primaryExample; }

    public void prepareNewCategory() {
        editingCategoryId = null;
        categoryForm = new Category();
    }

    public void editCategory(Long id) {
        Category found = categoryEJB.find(id);
        editingCategoryId = id;
        categoryForm = new Category();
        categoryForm.setName(found.getName());
        categoryForm.setDescription(found.getDescription());
    }

    public void saveCategory() {
        if (editingCategoryId == null) {
            categoryEJB.create(categoryForm.getName(), categoryForm.getDescription());
        } else {
            categoryEJB.update(editingCategoryId, categoryForm.getName(), categoryForm.getDescription());
        }
        cancelCategoryEdit();
        categories = categoryEJB.findAll();
    }

    public void deleteCategory(Long id) {
        categoryEJB.delete(id);
        categories = categoryEJB.findAll();
    }

    public void cancelCategoryEdit() {
        editingCategoryId = null;
        categoryForm = new Category();
    }


    public Category getCategoryForm() { return categoryForm; }
    public Long getEditingCategoryId() { return editingCategoryId; }
}