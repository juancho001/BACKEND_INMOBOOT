package com.inmobot.roles.infrastructure.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tb_permisos")
@Getter
@Setter
public class PermissionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "permiso_id")
    private Long id;

    @Column(name = "can_read", nullable = false)
    private Boolean canRead = true;

    @Column(name = "can_writed", nullable = false)
    private Boolean canWrited = false;

    @Column(name = "can_update", nullable = false)
    private Boolean canUpdate = false;

    @Column(name = "can_delete", nullable = false)
    private Boolean canDelete = false;
}
