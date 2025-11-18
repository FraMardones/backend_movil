package com.example.PatasyColas.controller;

import com.example.PatasyColas.model.Pet;
import com.example.PatasyColas.model.VaccineRecord; // <-- 1. IMPORTAR
import com.example.PatasyColas.service.PetService;
import com.example.PatasyColas.service.StorageService;
import com.fasterxml.jackson.core.type.TypeReference; // <-- 2. IMPORTAR
import com.fasterxml.jackson.databind.ObjectMapper; // <-- 3. IMPORTAR
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource; 
import org.springframework.http.HttpHeaders; 
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication; 
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile; 

import java.util.Collections; // <-- 4. IMPORTAR
import java.util.List;

@RestController
@RequestMapping("/api/pets")
@RequiredArgsConstructor 
public class PetController {

    private final PetService petService;
    private final StorageService storageService; 
    private final ObjectMapper objectMapper = new ObjectMapper(); // <-- 5. AÑADIR MAPPER

    // --- (getAllPets sin cambios) ---
    @GetMapping
    public ResponseEntity<List<Pet>> getAllPets(Authentication authentication) {
        String email = authentication.getName(); 
        return ResponseEntity.ok(petService.getPetsByUser(email));
    }

    // --- ENDPOINT POST (CORREGIDO) ---
    @PostMapping
    public ResponseEntity<Pet> createPet(
            @RequestParam("name") String name,
            @RequestParam("species") String species,
            @RequestParam("breed") String breed,
            @RequestParam("age") String age,
            @RequestParam("weight") String weight,
            @RequestParam("imageFile") MultipartFile imageFile, 
            @RequestParam("vaccines") String vaccinesJson, // <-- 6. AÑADIR CAMPO DE VACUNAS
            Authentication authentication) {
        
        try {
            String email = authentication.getName();
            String imageUrl = storageService.store(imageFile);

            // 7. Convertir el JSON de vacunas en una Lista
            List<VaccineRecord> vaccines = Collections.emptyList();
            if (vaccinesJson != null && !vaccinesJson.isEmpty()) {
                vaccines = objectMapper.readValue(vaccinesJson, new TypeReference<List<VaccineRecord>>(){});
            }

            Pet pet = Pet.builder()
                    .name(name)
                    .species(species)
                    .breed(breed)
                    .age(age)
                    .weight(weight)
                    .imageUri(imageUrl)
                    .vaccineRecords(vaccines) // <-- 8. AÑADIR VACUNAS AL OBJETO
                    .build();
            
            Pet newPet = petService.createPet(pet, email);
            return ResponseEntity.status(HttpStatus.CREATED).body(newPet);
        
        } catch (Exception e) {
            // Log del error
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // --- ENDPOINT PUT (CORREGIDO) ---
    @PutMapping("/{id}")
    public ResponseEntity<Pet> updatePet(
            @PathVariable Integer id,
            @RequestParam("name") String name,
            @RequestParam("species") String species,
            @RequestParam("breed") String breed,
            @RequestParam("age") String age,
            @RequestParam("weight") String weight,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            @RequestParam("vaccines") String vaccinesJson) { // <-- 9. AÑADIR CAMPO DE VACUNAS

        try {
            Pet existingPet = petService.getPetById(id);

            // 10. Convertir el JSON de vacunas
            List<VaccineRecord> vaccines = Collections.emptyList();
             if (vaccinesJson != null && !vaccinesJson.isEmpty()) {
                vaccines = objectMapper.readValue(vaccinesJson, new TypeReference<List<VaccineRecord>>(){});
            }

            Pet petDetails = Pet.builder()
                    .name(name)
                    .species(species)
                    .breed(breed)
                    .age(age)
                    .weight(weight)
                    .vaccineRecords(vaccines) // <-- 11. AÑADIR VACUNAS
                    .build();

            if (imageFile != null && !imageFile.isEmpty()) {
                String newImageUrl = storageService.store(imageFile);
                petDetails.setImageUri(newImageUrl);
            } else {
                petDetails.setImageUri(existingPet.getImageUri());
            }
            
            Pet updatedPet = petService.updatePet(id, petDetails);
            return ResponseEntity.ok(updatedPet);
        
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // --- (deletePet y getImage sin cambios) ---
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