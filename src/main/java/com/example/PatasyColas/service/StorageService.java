package com.example.PatasyColas.service;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class StorageService {

    // Define la carpeta donde se guardarán las imágenes
    // Se creará en la raíz de tu proyecto (ej: /backend_movil/upload-dir)
    private final Path root = Paths.get("upload-dir");

    // El constructor se asegura de que la carpeta exista
    public StorageService() {
        try {
            if (!Files.exists(root)) {
                Files.createDirectory(root);
            }
        } catch (IOException e) {
            throw new RuntimeException("¡No se pudo inicializar la carpeta para subidas!");
        }
    }

    /**
     * Guarda un archivo en el disco.
     * @param file El archivo (imagen) enviado desde la app.
     * @return La URL completa para acceder a esa imagen.
     */
    public String store(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new RuntimeException("Falló al guardar un archivo vacío.");
            }
            
            // 1. Genera un nombre de archivo único para evitar colisiones
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                 extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String uniqueFilename = UUID.randomUUID().toString() + extension;

            // 2. Resuelve la ruta de destino y copia el archivo
            Path destinationFile = this.root.resolve(uniqueFilename);
            Files.copy(file.getInputStream(), destinationFile, StandardCopyOption.REPLACE_EXISTING);

            // 3. Devuelve la URL de acceso
            // Ej: http://localhost:8080/api/pets/images/mi-imagen-unica.jpg
            return ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/pets/images/") // Este path debe coincidir con el @GetMapping en PetController
                    .path(uniqueFilename)
                    .toUriString();

        } catch (IOException e) {
            throw new RuntimeException("Falló al guardar el archivo.", e);
        }
    }

    /**
     * Carga un archivo como un Recurso para que el Controller lo pueda servir.
     * @param filename El nombre único del archivo (ej: mi-imagen-unica.jpg)
     * @return Un objeto Resource que Spring puede enviar como respuesta.
     */
    public Resource loadAsResource(String filename) {
        try {
            Path file = root.resolve(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("¡No se pudo leer el archivo!");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }
}