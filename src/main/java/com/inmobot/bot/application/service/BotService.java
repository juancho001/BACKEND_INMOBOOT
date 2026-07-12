package com.inmobot.bot.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.inmobot.bot.dto.PropertySummaryDTO;
import com.inmobot.bot.infrastructure.entity.FaqEntity;
import com.inmobot.bot.infrastructure.repository.FaqRepository;
import com.inmobot.properties.infrastructure.entity.PropertyEntity;
import com.inmobot.properties.infrastructure.repository.PropertyRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Servicio especializado para el bot de WhatsApp.
 * Proporciona datos optimizados para minimizar tokens del LLM.
 */
@Service
@RequiredArgsConstructor
public class BotService {

    private final FaqRepository faqRepository;
    private final PropertyRepository propertyRepository;

    @PersistenceContext
    private EntityManager entityManager;

    // ==================== FAQ ====================

    public List<FaqEntity> getAllActiveFaqs() {
        return faqRepository.findByEnabledTrue();
    }

    /**
     * Busca una respuesta rápida que coincida con el texto del usuario.
     * Retorna la de keyword más largo (más específica) si hay varias.
     */
    public Optional<FaqEntity> matchFaq(String userText) {
        if (userText == null || userText.isBlank()) {
            return Optional.empty();
        }
        List<FaqEntity> matches = faqRepository.findMatchingFaqs(userText.trim());
        return matches.isEmpty() ? Optional.empty() : Optional.of(matches.get(0));
    }

    // ==================== PROPIEDADES ====================

    /**
     * Retorna resumen compacto de todas las propiedades.
     * JOIN con detalle_propiedad para incluir precio y dimensiones.
     */
    @SuppressWarnings("unchecked")
    public List<PropertySummaryDTO> getPropertySummaries() {
        String jpql = """
            SELECT p.id, p.nombrePropiedad, p.tipoPropiedad, p.descripcion, p.direccionCompleta,
                   d.precio, d.canonDeArrendamiento, d.numeroDeHabitaciones, d.numeroDeBanos, d.areaTotalM2
            FROM PropertyEntity p
            LEFT JOIN DetallePropertyEntity d ON d.propiedadId = p.id
            WHERE p.enabled = true
            """;

        // Fallback: si no hay entidad de detalle mapeada, usar query nativo
        return getPropertySummariesNative();
    }

    /**
     * Query nativo que hace JOIN con tb_detalle_propiedad.
     */
    @SuppressWarnings("unchecked")
    private List<PropertySummaryDTO> getPropertySummariesNative() {
        String sql = """
            SELECT p.propiedad_id, p.nombre_propiedad, p.tipo_propiedad,
                   LEFT(p.descripcion, 100) as descripcion, p.direccion_completa,
                   d.precio, d.canon_de_arrendamiento, d.numero_de_habitaciones,
                   d.numero_de_banos, d.area_total_m2
            FROM tb_propiedad p
            LEFT JOIN tb_detalle_propiedad d ON d.propiedad_id = p.propiedad_id
            WHERE p.enabled = true
            """;

        Query query = entityManager.createNativeQuery(sql);
        List<Object[]> results = query.getResultList();

        return results.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    /**
     * Búsqueda inteligente de propiedades.
     * Extrae intención (arriendo/venta) y ubicación del texto libre del usuario.
     */
    @SuppressWarnings("unchecked")
    public List<PropertySummaryDTO> searchProperties(String queryText, String tipoPropiedad) {
        StringBuilder sql = new StringBuilder("""
            SELECT p.propiedad_id, p.nombre_propiedad, p.tipo_propiedad,
                   LEFT(p.descripcion, 100) as descripcion, p.direccion_completa,
                   d.precio, d.canon_de_arrendamiento, d.numero_de_habitaciones,
                   d.numero_de_banos, d.area_total_m2
            FROM tb_propiedad p
            LEFT JOIN tb_detalle_propiedad d ON d.propiedad_id = p.propiedad_id
            LEFT JOIN tb_municipio m ON m.municipio_id = d.ciudad_id
            LEFT JOIN tb_departamento dep ON dep.departamento_id = d.departamento_id
            WHERE p.enabled = true
            """);

        String cleanQuery = (queryText != null) ? queryText.trim().toLowerCase() : "";
        boolean filterArriendo = false;
        boolean filterVenta = false;
        String locationTerm = null;

        // --- Extraer intención: arriendo o venta ---
        String[] arriendoKeywords = {"arriendo", "arriendos", "arrendar", "alquiler", "alquilar", "renta", "rentar", "canon"};
        String[] ventaKeywords = {"venta", "ventas", "comprar", "compra", "adquirir"};

        for (String kw : arriendoKeywords) {
            if (cleanQuery.contains(kw)) { filterArriendo = true; break; }
        }
        for (String kw : ventaKeywords) {
            if (cleanQuery.contains(kw)) { filterVenta = true; break; }
        }

        // --- Extraer ubicación: quitar palabras de relleno y buscar lo que queda ---
        String[] stopWords = {"arriendos", "arriendo", "arrendar", "alquiler", "alquilar",
                "venta", "ventas", "comprar", "compra", "renta", "rentar", "canon",
                "busco", "quiero", "necesito", "disponibles", "disponible",
                "informacion", "información", "saber", "ver", "mostrar",
                "en", "de", "los", "las", "el", "la", "un", "una", "para",
                "quisiera", "por", "favor", "que", "hay", "tienen", "tienes",
                "propiedades", "propiedad", "inmueble", "inmuebles",
                "apartamento", "apartamentos", "casa", "casas", "apto"};

        String locationCandidate = cleanQuery;
        for (String sw : stopWords) {
            locationCandidate = locationCandidate.replaceAll("\\b" + sw + "\\b", "");
        }
        locationCandidate = locationCandidate.replaceAll("\\s+", " ").trim();

        if (!locationCandidate.isEmpty() && locationCandidate.length() >= 3) {
            locationTerm = locationCandidate;
        }

        // --- Construir filtros SQL ---
        if (filterArriendo) {
            sql.append(" AND d.canon_de_arrendamiento IS NOT NULL AND d.canon_de_arrendamiento > 0");
        }
        if (filterVenta) {
            sql.append(" AND d.precio IS NOT NULL AND d.precio > 0");
        }

        if (locationTerm != null) {
            sql.append(" AND (unaccent(LOWER(p.direccion_completa)) LIKE unaccent(LOWER(CONCAT('%', :loc, '%')))")
               .append(" OR unaccent(LOWER(p.nombre_propiedad)) LIKE unaccent(LOWER(CONCAT('%', :loc, '%')))")
               .append(" OR unaccent(LOWER(p.descripcion)) LIKE unaccent(LOWER(CONCAT('%', :loc, '%')))")
               .append(" OR unaccent(LOWER(d.barrio)) LIKE unaccent(LOWER(CONCAT('%', :loc, '%')))")
               .append(" OR unaccent(LOWER(m.descripcion_municipio)) LIKE unaccent(LOWER(CONCAT('%', :loc, '%')))")
               .append(" OR unaccent(LOWER(dep.descripcion_departamento)) LIKE unaccent(LOWER(CONCAT('%', :loc, '%'))))");
        }

        if (tipoPropiedad != null && !tipoPropiedad.isBlank()) {
            sql.append(" AND UPPER(p.tipo_propiedad) = UPPER(:tipo)");
        }

        // Si no se extrajo ningún filtro útil, buscar texto completo como fallback
        if (!filterArriendo && !filterVenta && locationTerm == null
                && (tipoPropiedad == null || tipoPropiedad.isBlank())
                && !cleanQuery.isEmpty()) {
            sql.append(" AND (LOWER(p.nombre_propiedad) LIKE LOWER(CONCAT('%', :q, '%'))")
               .append(" OR LOWER(p.descripcion) LIKE LOWER(CONCAT('%', :q, '%'))")
               .append(" OR LOWER(p.direccion_completa) LIKE LOWER(CONCAT('%', :q, '%'))")
               .append(" OR LOWER(d.barrio) LIKE LOWER(CONCAT('%', :q, '%'))")
               .append(" OR LOWER(m.descripcion_municipio) LIKE LOWER(CONCAT('%', :q, '%'))")
               .append(" OR LOWER(dep.descripcion_departamento) LIKE LOWER(CONCAT('%', :q, '%')))");
        }

        Query query = entityManager.createNativeQuery(sql.toString());

        if (locationTerm != null) {
            query.setParameter("loc", locationTerm);
        }
        if (tipoPropiedad != null && !tipoPropiedad.isBlank()) {
            query.setParameter("tipo", tipoPropiedad.trim());
        }
        if (!filterArriendo && !filterVenta && locationTerm == null
                && (tipoPropiedad == null || tipoPropiedad.isBlank())
                && !cleanQuery.isEmpty()) {
            query.setParameter("q", cleanQuery);
        }

        List<Object[]> results = query.getResultList();
        return results.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    private PropertySummaryDTO mapToDTO(Object[] row) {
        return PropertySummaryDTO.builder()
                .id(row[0] != null ? ((Number) row[0]).longValue() : null)
                .nombre(row[1] != null ? row[1].toString() : null)
                .tipo(row[2] != null ? row[2].toString() : null)
                .descripcionCorta(row[3] != null ? row[3].toString() : null)
                .direccion(row[4] != null ? row[4].toString() : null)
                .precio(row[5] != null ? new BigDecimal(row[5].toString()) : null)
                .canon(row[6] != null ? new BigDecimal(row[6].toString()) : null)
                .habitaciones(row[7] != null ? ((Number) row[7]).intValue() : null)
                .banos(row[8] != null ? ((Number) row[8]).intValue() : null)
                .areaM2(row[9] != null ? new BigDecimal(row[9].toString()) : null)
                .build();
    }
}
