package com.vocab.repository;

import com.vocab.entity.Translation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TranslationRepository extends JpaRepository<Translation, Long> {

    List<Translation> findAllByWordId(Long wordId);
}