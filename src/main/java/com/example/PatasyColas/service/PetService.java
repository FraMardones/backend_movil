package com.example.PatasyColas.service;

import com.example.PatasyColas.model.Pet;
import com.example.PatasyColas.model.VaccineRecord;
import com.example.PatasyColas.repository.PetRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// ¡Asegúrate de que esta importación exista!
import java.util.ArrayList; 
import java.util.List;

@Service
public class PetService {

    @Autowired
    private PetRepository petRepository;

    public List<Pet> getAllPets() {
        return petRepository.findAll();
    }

    public Pet getPetById(Integer petId) {
        return petRepository.findById(petId)
                .orElseThrow(() -> new EntityNotFoundException("Mascota no encontrada con id: " + petId));
    }

    public Pet createPet(Pet pet) {
        // Aseguramos la relación bidireccional al crear
        if (pet.getVaccineRecords() != null) {
            for (VaccineRecord record : pet.getVaccineRecords()) {
                record.setPet(pet);
            }
        }
        return petRepository.save(pet);
    }

    // --- MÉTODO CORREGIDO A PRUEBA DE NULOS ---
    public Pet updatePet(Integer petId, Pet petDetails) {
        // 1. Buscamos la mascota que ya existe en la BD
        Pet existingPet = getPetById(petId); 

        // 2. Actualizamos los datos simples
        existingPet.setName(petDetails.getName());
        existingPet.setSpecies(petDetails.getSpecies());
        existingPet.setBreed(petDetails.getBreed());
        existingPet.setAge(petDetails.getAge());
        existingPet.setWeight(petDetails.getWeight());
        existingPet.setImageUri(petDetails.getImageUri());

        // --- GESTIÓN SEGURA DE VACUNAS ---

        // 3. Limpiamos la lista de vacunas antiguas, SOLO SI existe
        if (existingPet.getVaccineRecords() != null) {
            existingPet.getVaccineRecords().clear();
        }

        // 4. Verificamos si la mascota que nos envían (petDetails) trae vacunas nuevas
        if (petDetails.getVaccineRecords() != null && !petDetails.getVaccineRecords().isEmpty()) {
            
            // 5. Si la lista original era nula, la creamos
            if (existingPet.getVaccineRecords() == null) {
                 existingPet.setVaccineRecords(new ArrayList<>());
            }

            // 6. Añadimos las vacunas nuevas a la mascota existente
            existingPet.getVaccineRecords().addAll(petDetails.getVaccineRecords());

            // 7. Asignamos la mascota (dueño) a cada vacuna nueva para la relación
            for (VaccineRecord record : existingPet.getVaccineRecords()) {
                record.setPet(existingPet);
            }
        }
        
        // 8. Guardamos la mascota con los datos simples Y las vacunas actualizadas
        return petRepository.save(existingPet);
    }

    public void deletePet(Integer petId) {
        Pet pet = getPetById(petId); // Verifica que exista
        petRepository.delete(pet);
    }
}