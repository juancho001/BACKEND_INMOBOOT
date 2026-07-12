package com.inmobot.bot.infrastructure.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Entidad para respuestas rápidas (FAQ) del bot de WhatsApp.
 * Permite responder consultas frecuentes sin consumir tokens del LLM.
 */
@Entity
@Table(name = "tb_bot_faq")
@Getter
@Setter
public class FaqEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "faq_id")
    private Long id;

    @Column(name = "keyword", nullable = false, length = 100)
    private String keyword;

    @Column(name = "intent", nullable = false, length = 50)
    private String intent;

    @Column(name = "response", nullable = false, columnDefinition = "text")
    private String response;

    @Column(name = "enabled", nullable = false)
    private Boolean enabled = true;
}
