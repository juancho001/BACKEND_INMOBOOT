package com.inmobot.advisers.infrastructure.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tb_asesor")
@Getter
@Setter
public class AdviserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "asesor_id")
    private Long id;
}
