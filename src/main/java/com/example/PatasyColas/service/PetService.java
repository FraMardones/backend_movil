package com.example.PatasyColas.service;

import com.example.PatasyColas.model.Pet;
import com.example.PatasyColas.model.Usuario; // ¡IMPORTANTE!
import com.example.PatasyColas.model.VaccineRecord;
import com.example.PatasyColas.repository.PetRepository;
import com.example.PatasyColas.repository.UsuarioRepository; // ¡IMPORTANTE!
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList; // ¡IMPORTANTE!
import java.util.List;

@Service
public class PetService {

    @Autowired
    private PetRepository petRepository;

    // --- ¡NUEVO! ---
    // Inyectamos el repositorio de usuarios para poder encontrar al dueño
    @Autowired
    private UsuarioRepository usuarioRepository;

    // --- ¡NUEVO! ---
    // Busca mascotas POR EMAIL de usuario, no todas
    public List<Pet> getPetsByUser(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
        
        // Usamos el método que creamos en el PetRepository
        return petRepository.findByUsuarioId(usuario.getId());
    }

    public Pet getPetById(Integer petId) {
        return petRepository.findById(petId)
                .orElseThrow(() -> new EntityNotFoundException("Mascota no encontrada con id: " + petId));
    }

    // --- ¡MODIFICADO! ---
    // Ahora acepta el email del dueño para asignar la mascota
    public Pet createPet(Pet pet, String userEmail) {
        // Buscamos al dueño
        Usuario usuario = usuarioRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
        
        // Asignamos el dueño a la mascota
        pet.setUsuario(usuario);

        // Aseguramos la relación bidireccional
        if (pet.getVaccineRecords() != null) {
            for (VaccineRecord record : pet.getVaccineRecords()) {
                record.setPet(pet);
            }
        }
        return petRepository.save(pet);
    }

    // --- ¡MODIFICADO! ---
    // Esta es la versión segura que hicimos (a prueba de nulos)
    public Pet updatePet(Integer petId, Pet petDetails) {
        Pet existingPet = getPetById(petId); 

        existingPet.setName(petDetails.getName());
        existingPet.setSpecies(petDetails.getSpecies());
        existingPet.setBreed(petDetails.getBreed());
        existingPet.setAge(petDetails.getAge());
        existingPet.setWeight(petDetails.getWeight());
        existingPet.setImageUri(petDetails.getImageUri());

        // --- GESTIÓN SEGURA DE VACUNAS ---
        if (existingPet.getVaccineRecords() != null) {
            existingPet.getVaccineRecords().clear();
        }

        if (petDetails.getVaccineRecords() != null && !petDetails.getVaccineRecords().isEmpty()) {
            if (existingPet.getVaccineRecords() == null) {
                 existingPet.setVaccineRecords(new ArrayList<>());
            }
            existingPet.getVaccineRecords().addAll(petDetails.getVaccineRecords());
            for (VaccineRecord record : existingPet.getVaccineRecords()) {
                record.setPet(existingPet);
            }
        }
        
        return petRepository.save(existingPet);
    }

    public void deletePet(Integer petId) {
        Pet pet = getPetById(petId); // Verifica que exista
        petRepository.delete(pet);
    }
}