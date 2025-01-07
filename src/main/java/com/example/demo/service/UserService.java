package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public ResponseEntity<?> registerUser(User user) {
        try {
            if (userRepository.findByEmail(user.getEmail()) != null) {
                return ResponseEntity.badRequest().body("Bu e-posta adresi zaten kayıtlı!");
            }
            
            // Şifreyi hashle
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            
            // Kullanıcıyı kaydet
            User savedUser = userRepository.save(user);
            
            // Hassas bilgileri temizle
            savedUser.setPassword(null);
            
            return ResponseEntity.ok(savedUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Kayıt işlemi sırasında bir hata oluştu: " + e.getMessage());
        }
    }

    public ResponseEntity<?> loginUser(User user) {
        try {
            User existingUser = userRepository.findByEmail(user.getEmail());
            
            if (existingUser == null) {
                return ResponseEntity.badRequest().body("Kullanıcı bulunamadı!");
            }
            
            if (!passwordEncoder.matches(user.getPassword(), existingUser.getPassword())) {
                return ResponseEntity.badRequest().body("Hatalı şifre!");
            }
            
            // Hassas bilgileri temizle
            existingUser.setPassword(null);
            
            return ResponseEntity.ok(existingUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Giriş işlemi sırasında bir hata oluştu: " + e.getMessage());
        }
    }

    public ResponseEntity<?> updatePassword(Long userId, String oldPassword, String newPassword) {
        try {
            User user = userRepository.findById(userId).orElse(null);
            if (user == null) {
                return ResponseEntity.badRequest().body("Kullanıcı bulunamadı!");
            }

            if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
                return ResponseEntity.badRequest().body("Mevcut şifre yanlış!");
            }

            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            
            return ResponseEntity.ok("Şifre başarıyla güncellendi!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Şifre güncellenirken hata oluştu: " + e.getMessage());
        }
    }

    public ResponseEntity<?> updateProfileImage(Long userId, String base64Image) {
        try {
            User user = userRepository.findById(userId).orElse(null);
            if (user == null) {
                return ResponseEntity.badRequest().body("Kullanıcı bulunamadı!");
            }

            user.setProfileImage(base64Image);
            User updatedUser = userRepository.save(user);
            
            // Hassas bilgileri temizle
            updatedUser.setPassword(null);
            
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Profil resmi güncellenirken hata oluştu: " + e.getMessage());
        }
    }

    public ResponseEntity<?> updateUser(User updatedUser) {
        try {
            User existingUser = userRepository.findById(updatedUser.getId()).orElse(null);
            if (existingUser == null) {
                return ResponseEntity.badRequest().body("Kullanıcı bulunamadı!");
            }

            existingUser.setFirstName(updatedUser.getFirstName());
            existingUser.setLastName(updatedUser.getLastName());
            
            User saved = userRepository.save(existingUser);
            saved.setPassword(null); // Hassas bilgileri temizle
            
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Bilgiler güncellenirken hata oluştu: " + e.getMessage());
        }
    }
} 