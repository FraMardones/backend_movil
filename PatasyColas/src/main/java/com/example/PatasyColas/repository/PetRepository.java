package com.example.PatasyColas.repository;

import com.example.PatasyColas.model.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PetRepository extends JpaRepository<Pet, Integer> {
    // Spring Data JPA te da findAll(), findById(), save(), deleteById() gratis
}