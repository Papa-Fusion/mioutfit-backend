package com.outfitapp.backend.service;

import com.outfitapp.backend.model.Usuario;
import com.outfitapp.backend.repository.UsuarioRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UsuarioService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          @Lazy PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + email));
    }

    public Usuario registrar(Usuario usuario) {
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        return usuarioRepository.save(usuario);
    }
    
    public Usuario buscarOCrearUsuarioGoogle(String email, String nombre, String fotoUrl) {
        // Si ya existe un usuario con ese email (registrado con email/password),
        // lo reutilizamos — así no se duplican cuentas
        Optional<Usuario> existente = usuarioRepository.findByEmail(email);
     
        if (existente.isPresent()) {
            Usuario usuario = existente.get();
            // Actualizar foto si no tenía una
            if (usuario.getFotoUrl() == null && fotoUrl != null) {
                usuario.setFotoUrl(fotoUrl);
                usuarioRepository.save(usuario);
            }
            return usuario;
        }
     
        // Usuario nuevo — crear sin password (no lo necesita para login con Google)
        Usuario nuevo = new Usuario();
        nuevo.setEmail(email);
        nuevo.setNombre(nombre != null ? nombre : email.split("@")[0]);
        nuevo.setFotoUrl(fotoUrl);
        // Password aleatorio hasheado — nunca se usará para login tradicional
        nuevo.setPassword(passwordEncoder.encode(java.util.UUID.randomUUID().toString()));
     
        return usuarioRepository.save(nuevo);
    }
}