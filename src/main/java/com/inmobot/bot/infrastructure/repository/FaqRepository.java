package com.inmobot.bot.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.inmobot.bot.infrastructure.entity.FaqEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface FaqRepository extends JpaRepository<FaqEntity, Long> {

    List<FaqEntity> findByEnabledTrue();

    List<FaqEntity> findByIntentAndEnabledTrue(String intent);

    /**
     * Busca una FAQ cuyo keyword coincida (case-insensitive, contenido parcial)
     * con el texto del usuario. Retorna la primera coincidencia.
     */
    @Query("SELECT f FROM FaqEntity f WHERE f.enabled = true AND LOWER(:text) LIKE CONCAT('%', LOWER(f.keyword), '%') ORDER BY LENGTH(f.keyword) DESC")
    List<FaqEntity> findMatchingFaqs(@Param("text") String text);
}
