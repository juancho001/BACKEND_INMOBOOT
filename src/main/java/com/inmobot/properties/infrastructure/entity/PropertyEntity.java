package com.inmobot.properties.infrastructure.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tb_propiedad")
@Getter
@Setter
public class PropertyEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "propiedad_id")
    private Long id;

    @Column(name = "nombre_propiedad")
    private String nombrePropiedad;

    @Column(name = "tipo_propiedad")
    private String tipoPropiedad;

    @Column(name = "descripcion")
    private String descripcion;

    // @Column(name = "codigo_interno")
    // private String codigoInterno;

    @Column(name = "direccion_completa")
    private String direccionCompleta;
}
