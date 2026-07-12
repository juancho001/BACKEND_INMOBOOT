package com.inmobot.bot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO compacto de propiedad para el bot de WhatsApp.
 * Reduce el payload de ~2KB a ~300 bytes por propiedad,
 * disminuyendo el consumo de tokens del LLM.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PropertySummaryDTO {

    private Long id;
    private String nombre;
    private String tipo;
    private String descripcionCorta;
    private String direccion;
    private BigDecimal precio;
    private BigDecimal canon;
    private Integer habitaciones;
    private Integer banos;
    private BigDecimal areaM2;

    /**
     * Convierte a formato texto plano compacto para el LLM.
     * Texto plano consume ~40% menos tokens que JSON.
     */
    public String toCompactText() {
        StringBuilder sb = new StringBuilder();
        sb.append("🏠 ").append(nombre != null ? nombre : "Sin nombre");
        sb.append(" | ").append(tipo != null ? tipo : "N/A");
        if (precio != null) sb.append(" | $").append(precio.toPlainString());
        if (canon != null) sb.append(" | Canon: $").append(canon.toPlainString());
        if (direccion != null) sb.append(" | 📍 ").append(direccion);
        if (habitaciones != null) sb.append(" | ").append(habitaciones).append(" hab");
        if (banos != null) sb.append(", ").append(banos).append(" baños");
        if (areaM2 != null) sb.append(" | ").append(areaM2).append("m²");
        return sb.toString();
    }
}
