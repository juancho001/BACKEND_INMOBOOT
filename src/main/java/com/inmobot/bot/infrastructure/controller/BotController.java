package com.inmobot.bot.infrastructure.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.inmobot.bot.application.service.BotService;
import com.inmobot.bot.dto.PropertySummaryDTO;
import com.inmobot.bot.infrastructure.entity.FaqEntity;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Controller especializado para el agente AI de WhatsApp.
 * Endpoints optimizados para reducir consumo de tokens del LLM.
 * 
 * Todos los endpoints son públicos (sin JWT) ya que son consumidos
 * por el agente AI interno, no por usuarios finales.
 */
@RestController
@RequestMapping("/api/bot")
@RequiredArgsConstructor
public class BotController {

    private final BotService botService;

    // ==================== FAQ ====================

    /**
     * Lista todas las FAQs activas.
     * Usado por el agente para cargar FAQs en caché.
     */
    @GetMapping("/faq")
    public ResponseEntity<List<FaqEntity>> getAllFaqs() {
        return ResponseEntity.ok(botService.getAllActiveFaqs());
    }

    /**
     * Busca una FAQ que coincida con el texto del usuario.
     * Si hay match, el agente puede responder SIN usar el LLM.
     * 
     * @param q Texto del usuario (ej: "hola", "horario", "gracias")
     * @return FAQ con la respuesta, o 204 No Content si no hay match
     */
    @GetMapping("/faq/match")
    public ResponseEntity<Map<String, String>> matchFaq(@RequestParam String q) {
        return botService.matchFaq(q)
                .map(faq -> ResponseEntity.ok(Map.of(
                        "intent", faq.getIntent(),
                        "response", faq.getResponse()
                )))
                .orElse(ResponseEntity.noContent().build());
    }

    // ==================== PROPIEDADES ====================

    /**
     * Resumen compacto de todas las propiedades.
     * Payload ~70% menor que GET /api/properties.
     */
    @GetMapping("/properties/summary")
    public ResponseEntity<List<PropertySummaryDTO>> getPropertySummaries() {
        return ResponseEntity.ok(botService.getPropertySummaries());
    }

    /**
     * Búsqueda de propiedades con filtros en la DB.
     * Evita traer todo y filtrar en Python.
     * 
     * @param q Texto libre de búsqueda (nombre, descripción, dirección)
     * @param tipo Tipo de propiedad (CASA, APARTAMENTO, etc.)
     */
    @GetMapping("/properties/search")
    public ResponseEntity<List<PropertySummaryDTO>> searchProperties(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String tipo) {
        return ResponseEntity.ok(botService.searchProperties(q, tipo));
    }

    /**
     * Versión texto plano de las propiedades para inyectar directamente al LLM.
     * Formato optimizado que consume ~40% menos tokens que JSON.
     */
    @GetMapping("/properties/compact")
    public ResponseEntity<Map<String, String>> getPropertiesCompactText(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String tipo) {
        List<PropertySummaryDTO> properties = (q != null || tipo != null)
                ? botService.searchProperties(q, tipo)
                : botService.getPropertySummaries();

        if (properties.isEmpty()) {
            return ResponseEntity.ok(Map.of(
                    "count", "0",
                    "text", "No se encontraron propiedades con esos criterios."
            ));
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Propiedades encontradas (").append(properties.size()).append("):\n");
        for (PropertySummaryDTO p : properties) {
            sb.append(p.toCompactText()).append("\n");
        }

        return ResponseEntity.ok(Map.of(
                "count", String.valueOf(properties.size()),
                "text", sb.toString()
        ));
    }
}
