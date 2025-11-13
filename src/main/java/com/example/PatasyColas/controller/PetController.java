package com.example.PatasyColas.controller;

import com.example.PatasyColas.model.Pet;
import com.example.PatasyColas.service.PetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pets") // La URL base protegida
public class PetController {

    @Autowired
    private PetService petService;

    // GET /api/pets -> Obtener todas las mascotas
    @GetMapping
    public ResponseEntity<List<Pet>> getAllPets() {
        return ResponseEntity.ok(petService.getAllPets());
    }

    // GET /api/pets/1 -> Obtener una mascota por ID
    @GetMapping("/{id}")
    public ResponseEntity<Pet> getPetById(@PathVariable Integer id) {
        Pet pet = petService.getPetById(id);
        return ResponseEntity.ok(pet);
    }

    // POST /api/pets -> Crear una nueva mascota
    @PostMapping
    public ResponseEntity<Pet> createPet(@RequestBody Pet pet) {
        Pet newPet = petService.createPet(pet);
        return ResponseEntity.status(HttpStatus.CREATED).body(newPet);
    }

    // PUT /api/pets/1 -> Actualizar una mascota por ID
    @PutMapping("/{id}")
    public ResponseEntity<Pet> updatePet(@PathVariable Integer id, @RequestBody Pet petDetails) {
        Pet updatedPet = petService.updatePet(id, petDetails);
        return ResponseEntity.ok(updatedPet);
    }

    // DELETE /api/pets/1 -> Borrar una mascota por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePet(@PathVariable Integer id) {
        petService.deletePet(id);
        return ResponseEntity.noContent().build(); // Retorna 204 No Content
    }
}