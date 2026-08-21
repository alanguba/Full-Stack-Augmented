package com.api.agb.itera.model;

import jakarta.persistence.*;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "actividad")
public class Actividad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tiempo", nullable = false, length = 100)
    private String tiempo;

    @Column(name = "nombre", nullable = false, length = 255)
    private String nombre;

    @Column(name = "nota", nullable = false, length = 500)
    private String nota;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "itinerario_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_itinerario_actividad")
    )
    @ToString.Exclude
    private Itinerario itinerario;

}