package com.example.PatasyColas.service;

import com.example.PatasyColas.model.Pet;
import com.example.PatasyColas.model.VaccineRecord;
import com.example.PatasyColas.repository.PetRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PetService {

    @Autowired
    private PetRepository petRepository;

    // Equivalente a tu getAllPets()
    public List<Pet> getAllPets() {
        return petRepository.findAll();
    }

    // Equivalente a tu getPetById(petId)
    public Pet getPetById(Integer petId) {
        return petRepository.findById(petId)
                .orElseThrow(() -> new EntityNotFoundException("Mascota no encontrada con id: " + petId));
    }

    // Equivalente a tu insertPet(pet)
    public Pet createPet(Pet pet) {
        // Aseguramos la relación bidireccional
        if (pet.getVaccineRecords() != null) {
            for (VaccineRecord record : pet.getVaccineRecords()) {
                record.setPet(pet);
            }
        }
        return petRepository.save(pet);
    }

    // Equivalente a tu updatePet(pet)
    public Pet updatePet(Integer petId, Pet petDetails) {
        Pet existingPet = getPetById(petId); // Reusa la lógica de búsqueda

        existingPet.setName(petDetails.getName());
        existingPet.setSpecies(petDetails.getSpecies());
        existingPet.setBreed(petDetails.getBreed());
        existingPet.setAge(petDetails.getAge());
        existingPet.setWeight(petDetails.getWeight());
        existingPet.setImageUri(petDetails.getImageUri());

        // Manejo complejo de la lista de vacunas (opcional pero recomendado)
        // Aquí podrías borrar las antiguas y añadir las nuevas, o unirlas
        // Por simplicidad, copiaremos la lista (requiere manejo cuidadoso)
        // existingPet.setVaccineRecords(petDetails.getVaccineRecords());

        return petRepository.save(existingPet);
    }

    // Equivalente a tu deletePet(pet)
    public void deletePet(Integer petId) {
        Pet pet = getPetById(petId); // Verifica que exista
        petRepository.delete(pet);
    }
}