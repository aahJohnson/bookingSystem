package com.example.BookingSystem.Services;

import com.example.BookingSystem.Exceptions.LoginFailureException;
import com.example.BookingSystem.Exceptions.UserRegistrationException;
import com.example.BookingSystem.Models.User;
import com.example.BookingSystem.Models.UserForClient;
import com.example.BookingSystem.Repositories.UserRepository;
import com.example.BookingSystem.Utils.PasswordUtils;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class UserService {

    private final UserRepository userRepository;

    public Optional<User> login(String username, String password) throws LoginFailureException {
        Optional<User> user = userRepository.findUserByUsername(username);
        if (user.isPresent()) {
            if (PasswordUtils.verifyPassword(password, user.get().getPassword(), user.get().getSalt())) {
                return user;
            }
        }
        return Optional.empty();
    }

    public void addUser(User user) throws UserRegistrationException {
        Optional<User> userOptional = userRepository.findUserByUsername(user.getUsername());
        if (userOptional.isPresent()) {
            System.out.println("user optional name: " + userOptional.get().getUsername());
            throw new UserRegistrationException("The username is already taken");
        } else {
            userOptional = userRepository.findUserByEmail(user.getEmail());
            if (userOptional.isPresent()) {
                throw new UserRegistrationException("This email is already registered");
            } else {
                userRepository.save(new User(
                        user.getUsername(),
                        user.getPassword(),
                        user.getName(),
                        user.getAddress(),
                        user.getEmail()));
            }
        }
    }

    public UserForClient getUser(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        return user.map(value -> new UserForClient(
                value.getId(),
                value.getUsername(),
                value.getName(),
                value.getAddress(),
                value.getEmail())).orElse(null);
    }
}
