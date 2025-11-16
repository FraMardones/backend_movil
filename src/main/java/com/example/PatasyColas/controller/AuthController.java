package com.example.PatasyColas.controller;

import com.example.PatasyColas.dto.AuthResponse;
import com.example.PatasyColas.dto.LoginRequest;
import com.example.PatasyColas.dto.RegisterRequest;
import com.example.PatasyColas.service.AuthService;

// --- Imports Adicionales ---
import com.example.PatasyColas.service.JwtService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.http.HttpStatus;
import java.util.Map;
// ---

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // --- ¡NUEVO! ---
    // Inyectamos los servicios necesarios para validar el refresh token
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;


    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    // --- ¡NUEVO ENDPOINT! ---
    // Este es el endpoint de "recepción" que valida el "Refresh Token"
    // y entrega un "Access Token" nuevo.
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        
        try {
            // 1. Extraemos el email del refresh token
            String userEmail = jwtService.getUsernameFromToken(refreshToken);
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

            // 2. Validamos que el refresh token sea correcto y no haya expirado
            if (jwtService.isTokenValid(refreshToken, userDetails)) {
                
                // 3. Si es válido, creamos un NUEVO Access Token (el de 15 min)
                String newAccessToken = jwtService.generateToken(userDetails);
                
                // 4. Devolvemos el nuevo token de acceso
                return ResponseEntity.ok(AuthResponse.builder()
                        .token(newAccessToken) // El nuevo token de acceso
                        .refreshToken(refreshToken) // El mismo token de refresco
                        .build());
            }
        } catch (Exception e) {
            // Si el token es inválido o expira, forzamos al usuario a hacer login de nuevo
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or Expired Refresh Token");
        }
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or Expired Refresh Token");
    }
}