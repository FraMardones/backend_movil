package com.example.PatasyColas.service;

import com.example.PatasyColas.dto.AuthResponse;
import com.example.PatasyColas.dto.LoginRequest;
import com.example.PatasyColas.dto.RegisterRequest;
import com.example.PatasyColas.model.Role;
import com.example.PatasyColas.model.Usuario;
import com.example.PatasyColas.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        
        // Obtenemos el usuario (que implementa UserDetails)
        UserDetails user = usuarioRepository.findByEmail(request.getEmail()).orElseThrow();
        
        // --- CORRECCIÓN ---
        // Generamos ambos tokens usando los nuevos métodos de JwtService
        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        
        // Devolvemos ambos tokens en la respuesta
        return AuthResponse.builder()
                .token(accessToken) // 'token' es el Access Token
                .refreshToken(refreshToken)
                .build();
    }

    public AuthResponse register(RegisterRequest request) {
        Usuario user = Usuario.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .role(Role.USER) // Todos se registran como USER por defecto
                .build();

        usuarioRepository.save(user);

        // 'user' es una instancia de UserDetails, así que podemos usarlo
        
        // --- CORRECCIÓN ---
        // Generamos ambos tokens
        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        // Devolvemos ambos tokens en la respuesta
        return AuthResponse.builder()
                .token(accessToken) // 'token' es el Access Token
                .refreshToken(refreshToken)
                .build();
    }
}