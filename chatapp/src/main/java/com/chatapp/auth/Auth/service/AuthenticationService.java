package com.chatapp.auth.Auth.service;

import com.chatapp.auth.Auth.dto.LoginUserDto;
import com.chatapp.auth.Auth.dto.SignupUserDto;
import com.chatapp.auth.Auth.dto.VerifyDto;
import com.chatapp.auth.Auth.model.User;
import com.chatapp.auth.Auth.repository.UserRepository;
import jakarta.mail.MessagingException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class AuthenticationService {
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;

    public AuthenticationService(AuthenticationManager authenticationManager, UserRepository userRepo, PasswordEncoder passwordEncoder, EmailService emailService) {
        this.authenticationManager = authenticationManager;
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    public User signup(SignupUserDto input) {
        User user = new User(input.getUsername(), input.getEmail(), passwordEncoder.encode(input.getPassword()));
        user.setVerificationCode(generateVerificationCode());
        user.setVerificationCodeExpired(LocalDateTime.now().plusMinutes(15));
        user.setEnabled(false);
        sendVerificationEmail(user);
        return (User) userRepo.save(user);
    }

    public User authenticate(LoginUserDto input) {
        User user = userRepo.findByEmail(input.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found."));
        if (!user.isEnabled()) {
            throw new RuntimeException("Account not Verified.");
        }
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getEmail(),
                        input.getPassword()
                )
        );
        return user;
    }

    public void verifyUser(VerifyDto input) {
        Optional<User> optionalUser = userRepo.findByEmail(input.getEmail());
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.getVerificationCodeExpired().isBefore(LocalDateTime.now())) {
                throw new RuntimeException("Verification Code is Expires.");
            }
            if (user.getVerificationCode().equals(input.getVerificationCode())) {
                user.setEnabled(true);
                user.setVerificationCode(null);
                user.setVerificationCodeExpired(null);
                userRepo.save(user);

            } else {
                throw new RuntimeException("Invalid Verification Code.");
            }
        } else {
            throw new RuntimeException("User not found.");
        }
    }

    public void resendVerificationCode(String email) {
        Optional<User> optionalUser = userRepo.findByEmail(email);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.isEnabled()) {
                throw new RuntimeException("Account is already verified.");
            }
            user.setVerificationCode(generateVerificationCode());
            user.setVerificationCodeExpired(LocalDateTime.now().plusMinutes(15));
            sendVerificationEmail(user);
            userRepo.save(user);
        } else {
            throw new RuntimeException("User not found.");
        }
    }

    public void sendVerificationEmail(User user) {
        String subject = "Account Verification";
        String verificationCode = "VERIFICATION CODE " + user.getVerificationCode();
        String htmlMessage = "<html>"
                + "<body style=\"font-family: Arial, sans-serif;\">"
                + "<div style=\"background-color: #f5f5f5; padding: 20px;\">"
                + "<h2 style=\"color: #333;\">Welcome to our app!</h2>"
                + "<p style=\"font-size: 16px;\">Please enter the verification code below to continue:</p>"
                + "<div style=\"background-color: #fff; padding: 20px; border-radius: 5px; box-shadow: 0 0 10px rgba(0,0,0,0.1);\">"
                + "<h3 style=\"color: #333;\">Verification Code:</h3>"
                + "<p style=\"font-size: 18px; font-weight: bold; color: #007bff;\">" + verificationCode + "</p>"
                + "</div>"
                + "</div>"
                + "</body>"
                + "</html>";
        try {
            emailService.sendVerificationEmail(user.getEmail(), subject, htmlMessage);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send verification email.");
        }
    }

    public void sendForgetCodeToEmail(User user) {
        String subject = "Forget Password";
        String verificationCode = "VERIFICATION CODE " + user.getPasswordResetCode();
        String htmlMessage = "<html>"
                + "<body style=\"font-family: Arial, sans-serif;\">"
                + "<div style=\"background-color: #f5f5f5; padding: 20px;\">"
                + "<h2 style=\"color: #333;\">Welcome to our app!</h2>"
                + "<p style=\"font-size: 16px;\">Please enter the Password reset code below to continue:</p>"
                + "<div style=\"background-color: #fff; padding: 20px; border-radius: 5px; box-shadow: 0 0 10px rgba(0,0,0,0.1);\">"
                + "<h3 style=\"color: #333;\">Verification Code:</h3>"
                + "<p style=\"font-size: 18px; font-weight: bold; color: #007bff;\">" + verificationCode + "</p>"
                + "</div>"
                + "</div>"
                + "</body>"
                + "</html>";
        try {
            emailService.sendVerificationEmail(user.getEmail(), subject, htmlMessage);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send Forget email.");
        }
    }

    private String generateVerificationCode() {
        Random random = new Random();
        int code = random.nextInt(9999) + 1000;
        return String.valueOf(code);
    }

    public boolean sendPasswordResetCode(String email) {
        Optional<User> optionalUser = userRepo.findByEmail(email.toLowerCase()); // Normalize email

        if (optionalUser.isEmpty()) {
            return false;  // User not found
        }

        User user = optionalUser.get();
        // Check if reset code has expired
        if (user.getPasswordResetCodeExpired() == null || user.getPasswordResetCodeExpired().isBefore(LocalDateTime.now())) {
            user.setPasswordResetCode(generateVerificationCode());
            user.setPasswordResetCodeExpired(LocalDateTime.now().plusMinutes(15));// Set expiration to 15 minutes from now
            userRepo.save(user);
            sendForgetCodeToEmail(user);
            return true;
        }

        return false;  // Reset code has not yet expired
    }


    public boolean resetPassword(String email, String passwordResetCode, String newPassword) {
        Optional<User> optionalUser = userRepo.findByEmail(email);
        if (optionalUser.isEmpty()) {
            return false;  // User not found
        }

        User user = optionalUser.get();
        // Check if the provided code matches and has not expired
        if (user.getPasswordResetCode() != null
                && user.getPasswordResetCode().equals(passwordResetCode)
                && user.getPasswordResetCodeExpired() != null
                && user.getPasswordResetCodeExpired().isAfter(LocalDateTime.now())) {

            user.setPassword(passwordEncoder.encode(newPassword));  // Update password
            user.setPasswordResetCode(null);  // Clear reset code
            user.setPasswordResetCodeExpired(null);  // Clear expiration date
            userRepo.save(user);
            return true;
        }

        return false;  // Code is incorrect or expired
    }

}
