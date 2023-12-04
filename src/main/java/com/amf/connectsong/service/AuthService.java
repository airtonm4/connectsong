package com.amf.connectsong.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.amf.connectsong.config.jwt.JwtResponse;
import com.amf.connectsong.config.jwt.JwtUtils;
import com.amf.connectsong.config.services.UserDetailsImpl;
import com.amf.connectsong.dto.LoginDTO;
import com.amf.connectsong.dto.SignupDTO;
import com.amf.connectsong.model.ERole;
import com.amf.connectsong.model.Role;
import com.amf.connectsong.model.User;
import com.amf.connectsong.repository.RoleRepository;
import com.amf.connectsong.repository.UserRepository;

@Service
public class AuthService {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    public AuthService() {
    }

    public JwtResponse login(LoginDTO loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        return new JwtResponse(jwt, userDetails.getId(), userDetails.getUsername(), userDetails.getEmail(), roles);

    }

    public ResponseEntity<?> signUp(SignupDTO singUpRequest) {

        if (userRepository.existsByEmail(singUpRequest.getEmail())) {
            throw new RuntimeException("EMAIL_ALREADY_TAKEN");
        }

        if (userRepository.existsByUsername(singUpRequest.getUsername())) {
            throw new RuntimeException("USERNAME_ALREADY_TAKEN");
        }

        if (!isPasswordStrong(singUpRequest.getPassword())) {
            throw new RuntimeException("PASSWORD_NOT_STRONG");
        }

        if (!isEmailValid(singUpRequest.getEmail())) {
            throw new RuntimeException("INVALID_EMAIL");
        }

        User user = new User(
                singUpRequest.getName(),
                singUpRequest.getUsername(),
                singUpRequest.getEmail(),
                encoder.encode(singUpRequest.getPassword()));

        Set<String> strRoles = singUpRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRole = roleRepository.findByName(ERole.USER)
                    .orElseThrow(() -> new RuntimeException("ROLE_NOT_FOUND"));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(ERole.ADMIN)
                                .orElseThrow(() -> new RuntimeException("ROLE_NOT_FOUND"));
                        roles.add(adminRole);
                        break;
                    default:
                        Role userRole = roleRepository.findByName(ERole.USER)
                                .orElseThrow(() -> new RuntimeException("ROLE_NOT_FOUND"));
                        roles.add(userRole);
                }
            });
        }

        user.setRoles(roles);
        userRepository.save(user);

        return ResponseEntity.ok("User registered successfully!");
    }

    public boolean isPasswordStrong(String password) {
        String regex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";
        return password.matches(regex);
    }

    public boolean isEmailValid(String email) {
        String regex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email.matches(regex);
    }

}
