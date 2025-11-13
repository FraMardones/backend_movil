package com.example.PatasyColas.service;

import com.example.PatasyColas.model.Pet;
import com.example.PatasyColas.model.VaccineRecord;
import com.example.PatasyColas.repository.PetRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.PatasyColas.model.Usuario;
import com.example.PatasyColas.repository.UsuarioRepository; // Importar

import java.util.List;

@Service
public class PetService {

    @Autowired
    private PetRepository petRepository;
    @Autowired
    private UsuarioRepository usuarioRepository; // Inyectar repositorio de usuarios

    // Obtener mascotas SOLO del usuario logueado
    public List<Pet> getPetsByUser(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
        return petRepository.findByUsuarioId(usuario.getId());
    }

    public Pet getPetById(Integer petId) {
        return petRepository.findById(petId)
                .orElseThrow(() -> new EntityNotFoundException("Mascota no encontrada con id: " + petId));
    }

    // Crear mascota asignada al usuario logueado
    public Pet createPet(Pet pet, String userEmail) {
        Usuario usuario = usuarioRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
        
        pet.setUsuario(usuario); // Asignar dueño

        if (pet.getVaccineRecords() != null) {
            for (VaccineRecord record : pet.getVaccineRecords()) {
                record.setPet(pet);
            }
        }
        return petRepository.save(pet);
    }
    // Equivalente a tu updatePet(pet)
    public Pet updatePet(Integer petId, Pet petDetails) {
        Pet existingPet = getPetById(petId);

        // Actualizamos los datos básicos
        existingPet.setName(petDetails.getName());
        existingPet.setSpecies(petDetails.getSpecies());
        existingPet.setBreed(petDetails.getBreed());
        existingPet.setAge(petDetails.getAge());
        existingPet.setWeight(petDetails.getWeight());
        existingPet.setImageUri(petDetails.getImageUri());

        // --- CORRECCIÓN: Gestión de Vacunas ---
        // 1. Limpiamos la lista actual (borra las vacunas viejas o huerfanas)
        if (existingPet.getVaccineRecords() != null) {
            existingPet.getVaccineRecords().clear();
        }
        
        // 2. Agregamos las nuevas vacunas que vienen del celular
        if (petDetails.getVaccineRecords() != null) {
            existingPet.getVaccineRecords().addAll(petDetails.getVaccineRecords());
            
            // 3. ¡IMPORTANTE! Asignamos la mascota como "dueño" de cada vacuna
            for (VaccineRecord record : existingPet.getVaccineRecords()) {
                record.setPet(existingPet);
            }
        }

        return petRepository.save(existingPet);
    }

    // Equivalente a tu deletePet(pet)
    public void deletePet(Integer petId) {
        Pet pet = getPetById(petId); // Verifica que exista
        petRepository.delete(pet);
    }
}