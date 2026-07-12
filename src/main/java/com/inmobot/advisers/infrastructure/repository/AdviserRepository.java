package com.inmobot.advisers.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.inmobot.advisers.infrastructure.entity.AdviserEntity;

@Repository
public interface AdviserRepository extends JpaRepository<AdviserEntity, Long> {
}
