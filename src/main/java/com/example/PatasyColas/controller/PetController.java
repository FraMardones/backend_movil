package com.example.PatasyColas.controller;

import com.example.PatasyColas.model.Pet;
import com.example.PatasyColas.service.PetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication; // Importar
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pets")
public class PetController {

    @Autowired
    private PetService petService;

    @GetMapping
    public ResponseEntity<List<Pet>> getAllPets(Authentication authentication) {
        // Obtenemos el email del usuario desde el Token
        String email = authentication.getName(); 
        return ResponseEntity.ok(petService.getPetsByUser(email));
    }

    @PostMapping
    public ResponseEntity<Pet> createPet(@RequestBody Pet pet, Authentication authentication) {
        String email = authentication.getName();
        Pet newPet = petService.createPet(pet, email);
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