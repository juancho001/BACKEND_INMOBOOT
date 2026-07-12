package com.inmobot.properties.infrastructure.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.inmobot.properties.application.port.in.ManagePropertyUseCase;
import com.inmobot.properties.infrastructure.entity.PropertyEntity;

import java.util.List;

@RestController
@RequestMapping("/api/properties")
@RequiredArgsConstructor
public class PropertyController {

    private final ManagePropertyUseCase useCase;

    @GetMapping
    public ResponseEntity<List<PropertyEntity>> getAll() {
        return ResponseEntity.ok(useCase.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PropertyEntity> getById(@PathVariable Long id) {
        return useCase.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<PropertyEntity> create(@RequestBody PropertyEntity entity) {
        return ResponseEntity.ok(useCase.save(entity));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        useCase.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
