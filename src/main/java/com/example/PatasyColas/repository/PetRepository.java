package com.example.PatasyColas.repository;

import com.example.PatasyColas.model.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PetRepository extends JpaRepository<Pet, Integer> {
    // Spring Data JPA creará la consulta automáticamente
    List<Pet> findByUsuarioId(Integer usuarioId);
}