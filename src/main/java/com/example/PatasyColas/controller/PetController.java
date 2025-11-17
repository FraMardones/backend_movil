package com.example.PatasyColas.controller;

import com.example.PatasyColas.model.Pet;
import com.example.PatasyColas.service.PetService;
import com.example.PatasyColas.service.StorageService; 
import lombok.RequiredArgsConstructor; 
import org.springframework.core.io.Resource; 
import org.springframework.http.HttpHeaders; 
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication; 
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile; 

import java.util.List;

@RestController
@RequestMapping("/api/pets")
@RequiredArgsConstructor 
public class PetController {

    private final PetService petService;
    private final StorageService storageService; 

    @GetMapping
    public ResponseEntity<List<Pet>> getAllPets(Authentication authentication) {
        String email = authentication.getName(); 
        return ResponseEntity.ok(petService.getPetsByUser(email));
    }

    @PostMapping
    public ResponseEntity<Pet> createPet(
            @RequestParam("name") String name,
            @RequestParam("species") String species,
            @RequestParam("breed") String breed,
            @RequestParam("age") String age, // <-- CORREGIDO A STRING
            @RequestParam("weight") String weight, // <-- CORREGIDO A STRING
            @RequestParam("imageFile") MultipartFile imageFile, 
            Authentication authentication) {
        
        String email = authentication.getName();
        
        // Guardamos la imagen y obtenemos la URL del servicio
        String imageUrlFromService = storageService.store(imageFile);

        Pet pet = Pet.builder()
                .name(name)
                .species(species)
                .breed(breed)
                .age(age) // <-- Ahora coincide (String)
                .weight(weight) // <-- Ahora coincide (String)
                .imageUri(imageUrlFromService) // <-- CORREGIDO a imageUri
                .build();
        
        Pet newPet = petService.createPet(pet, email);
        return ResponseEntity.status(HttpStatus.CREATED).body(newPet);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Pet> updatePet(
            @PathVariable Integer id,
            @RequestParam("name") String name,
            @RequestParam("species") String species,
            @RequestParam("breed") String breed,
            @RequestParam("age") String age, // <-- CORREGIDO A STRING
            @RequestParam("weight") String weight, // <-- CORREGIDO A STRING
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) {

        Pet existingPet = petService.getPetById(id);

        // --- Este es el bloque que citaste, ahora corregido ---
        Pet petDetails = Pet.builder()
                .name(name)
                .species(species)
                .breed(breed)
                .age(age) // <-- Ahora coincide (String)
                .weight(weight) // <-- Ahora coincide (String)
                .vaccineRecords(existingPet.getVaccineRecords()) 
                .build();
        // --- Fin del bloque ---

        if (imageFile != null && !imageFile.isEmpty()) {
            String newImageUrl = storageService.store(imageFile);
            petDetails.setImageUri(newImageUrl); // <-- CORREGIDO a imageUri
        } else {
            petDetails.setImageUri(existingPet.getImageUri()); // <-- CORREGIDO a imageUri
        }
        
        Pet updatedPet = petService.updatePet(id, petDetails);
        return ResponseEntity.ok(updatedPet);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePet(@PathVariable Integer id) {
        petService.deletePet(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/images/{filename:.+}")
    public ResponseEntity<Resource> getImage(@PathVariable String filename) {
        Resource file = storageService.loadAsResource(filename);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getFilename() + "\"")
                .body(file);
    }
}