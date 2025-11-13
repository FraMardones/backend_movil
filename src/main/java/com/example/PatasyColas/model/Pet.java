package com.example.PatasyColas.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "pets")
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;
    private String species;
    private String breed;
    private String age;
    private String weight;
    private String imageUri;

    // Relación: Una mascota tiene muchos registros de vacunas
    // cascade = CascadeType.ALL: Si borro una mascota, se borran sus vacunas
    // orphanRemoval = true: Si quito una vacuna de esta lista, se borra de la BD
    @OneToMany(mappedBy = "pet", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<VaccineRecord> vaccineRecords;
}