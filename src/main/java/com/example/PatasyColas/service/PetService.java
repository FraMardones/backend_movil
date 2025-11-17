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

    @Autowired
    private UsuarioRepository usuarioRepository;

    // --- ¡NUEVO MÉTODO! ---
    // Este es el método que pedía tu nota. 
    // Es una forma más limpia de llamarlo desde el PetController
    // después de que el controlador obtiene el Usuario autenticado.
    public List<Pet> getPetsByUsuario(Usuario usuario) {
        // Llama al método que ya tenías en tu PetRepository
        return petRepository.findByUsuarioId(usuario.getId());
    }

    // --- MÉTODO EXISTENTE ---
    // Lo mantenemos por si lo usas en otro lugar.
    public List<Pet> getPetsByUser(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
        
        return petRepository.findByUsuarioId(usuario.getId());
    }

    public Pet getPetById(Integer petId) {
        return petRepository.findById(petId)
                .orElseThrow(() -> new EntityNotFoundException("Mascota no encontrada con id: " + petId));
    }

    // --- ¡MODIFICADO! ---
    // Este método ahora será llamado por el PetController,
    // el cual ya habrá construido el objeto Pet (incluyendo la imageUrl
    // que obtuvo del StorageService).
    public Pet createPet(Pet pet, String userEmail) {
        // Buscamos al dueño
        Usuario usuario = usuarioRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
        
        // Asignamos el dueño a la mascota
        pet.setUsuario(usuario);

        // El campo pet.imageUrl ya debería venir asignado desde el Controller
        
        // Aseguramos la relación bidireccional
        if (pet.getVaccineRecords() != null) {
            for (VaccineRecord record : pet.getVaccineRecords()) {
                record.setPet(pet);
            }
        }
        return petRepository.save(pet);
    }

    // --- ¡MODIFICADO! ---
    // Cambiamos imageUri por imageUrl
    public Pet updatePet(Integer petId, Pet petDetails) {
        Pet existingPet = getPetById(petId);

        existingPet.setName(petDetails.getName());
        existingPet.setSpecies(petDetails.getSpecies()); // Mantengo esto
        existingPet.setBreed(petDetails.getBreed());
        existingPet.setAge(petDetails.getAge());
        existingPet.setWeight(petDetails.getWeight()); // Mantengo esto
        
        // --- CAMBIO PRINCIPAL ---
        existingPet.setImageUrl(petDetails.getImageUrl()); // Cambiado de imageUri a imageUrl

        // --- GESTIÓN SEGURA DE VACUNAS (Tu lógica original) ---
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