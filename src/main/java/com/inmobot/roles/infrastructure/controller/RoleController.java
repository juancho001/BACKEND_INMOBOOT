package com.inmobot.roles.infrastructure.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.inmobot.roles.application.port.in.ManageRoleUseCase;
import com.inmobot.roles.infrastructure.entity.RoleEntity;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final ManageRoleUseCase useCase;

    @GetMapping
    public ResponseEntity<List<RoleEntity>> getAll() {
        return ResponseEntity.ok(useCase.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoleEntity> getById(@PathVariable Long id) {
        return useCase.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<RoleEntity> create(@RequestBody RoleEntity entity) {
        return ResponseEntity.ok(useCase.save(entity));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        useCase.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
