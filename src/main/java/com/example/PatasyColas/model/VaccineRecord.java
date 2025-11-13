package com.example.PatasyColas.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "vaccine_records")
public class VaccineRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String vaccineName;
    @Column(name = "VACCINATION_DATE")
    private String vaccinationDate; 
    // Relación: Muchas vacunas pertenecen a una mascota
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id", nullable = false) // La columna en la BD
    @JsonIgnore // Evita bucles infinitos al convertir a JSON
    private Pet pet;
}