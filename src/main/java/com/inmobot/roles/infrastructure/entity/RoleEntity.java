package com.inmobot.roles.infrastructure.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tb_roles")
@Getter
@Setter
public class RoleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rol_id")
    private Long id;

    @Column(name = "rolname", unique = true, nullable = false, length = 50)
    private String name;

    @Column(name = "descripcion")
    private String description;

    @Column(name = "enabled")
    private Boolean enabled = true;
}
