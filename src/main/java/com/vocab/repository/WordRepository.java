package com.vocab.repository;

import com.vocab.entity.Word;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface WordRepository extends JpaRepository<Word, Long> {

    @Query("SELECT DISTINCT w FROM Word w LEFT JOIN FETCH w.category LEFT JOIN FETCH w.translations ORDER BY w.englishWord")
    List<Word> findAllWithDetails();

    @Query("SELECT DISTINCT w FROM Word w LEFT JOIN FETCH w.category LEFT JOIN FETCH w.translations WHERE LOWER(w.englishWord) LIKE LOWER(:query) ORDER BY w.englishWord")
    List<Word> searchWithDetails(@Param("query") String query);

    // Для пагинации с JOIN FETCH нужно использовать countQuery
    @Query(value = "SELECT DISTINCT w FROM Word w LEFT JOIN FETCH w.category LEFT JOIN FETCH w.translations ORDER BY w.englishWord",
            countQuery = "SELECT COUNT(w) FROM Word w")
    Page<Word> findAllWithDetailsPaged(Pageable pageable);

    @Query(value = "SELECT DISTINCT w FROM Word w LEFT JOIN FETCH w.category LEFT JOIN FETCH w.translations WHERE LOWER(w.englishWord) LIKE LOWER(:query) ORDER BY w.englishWord",
            countQuery = "SELECT COUNT(w) FROM Word w WHERE LOWER(w.englishWord) LIKE LOWER(:query)")
    Page<Word> searchWithDetailsPaged(@Param("query") String query, Pageable pageable);
}