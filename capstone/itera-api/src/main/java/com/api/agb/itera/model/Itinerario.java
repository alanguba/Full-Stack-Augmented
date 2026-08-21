package com.api.agb.itera.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "itinerario")
public class Itinerario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "dia", nullable = false)
    private Integer dia;

    @Column(name = "titulo", nullable = false, length = 255)
    private String titulo;

    @Column(name = "resumen", nullable = false)
    private String resumen;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "plan_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_plan_itinerario")
    )
    private Plan plan;


    @OneToMany(mappedBy = "itinerario", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @ToString.Exclude
    private List<Actividad> actividades = new ArrayList<>();

    public void addActividad(Actividad actividad) {
        actividades.add(actividad);
        actividad.setItinerario(this);
    }

    public void removeActividad(Actividad actividad) {
        actividades.remove(actividad);
        actividad.setItinerario(null);
    }

}