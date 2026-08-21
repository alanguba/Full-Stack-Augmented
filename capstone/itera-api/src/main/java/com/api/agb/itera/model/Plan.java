package com.api.agb.itera.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "plan")
public class Plan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "destino", nullable = false, length = 255)
    private String destino;

    @Column(name = "dias", nullable = false)
    private Integer dias;

    @Column(name = "ritmo", nullable = false, length = 255)
    private String ritmo;

    @Column(name = "url")
    private String url;

    @Column(name = "created_at", insertable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "estado", nullable = false)
    private Short estado;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "usuario_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_usuario_plan")
    )
    private Usuario usuario;


    @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @Builder.Default
    private List<Lugar> lugares = new ArrayList<>();


    @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @Builder.Default
    private List<Itinerario> itinerarios = new ArrayList<>();


    public void addLugar(Lugar lugar) {
        lugares.add(lugar);
        lugar.setPlan(this);
    }

    public void removeLugar(Lugar lugar) {
        lugares.remove(lugar);
        lugar.setPlan(null);
    }

    public void addItinerario(Itinerario itinerario) {
        itinerarios.add(itinerario);
        itinerario.setPlan(this);
    }

    public void removeItinerario(Itinerario itinerario) {
        itinerarios.remove(itinerario);
        itinerario.setPlan(null);
    }

}