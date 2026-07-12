package com.inmobot.advisers.infrastructure.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.inmobot.advisers.application.port.in.ManageAdviserUseCase;
import com.inmobot.advisers.infrastructure.entity.AdviserEntity;

import java.util.List;

@RestController
@RequestMapping("/api/advisers")
@RequiredArgsConstructor
public class AdviserController {

    private final ManageAdviserUseCase useCase;

    @GetMapping
    public ResponseEntity<List<AdviserEntity>> getAll() {
        return ResponseEntity.ok(useCase.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdviserEntity> getById(@PathVariable Long id) {
        return useCase.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<AdviserEntity> create(@RequestBody AdviserEntity entity) {
        return ResponseEntity.ok(useCase.save(entity));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        useCase.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
