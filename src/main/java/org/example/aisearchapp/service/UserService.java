package org.example.aisearchapp.service;

import org.example.aisearchapp.model.User;
import org.example.aisearchapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public User processOAuthPostLogin(String email, String name, String pictureUrl) {
        Optional<User> existUser = userRepository.findByEmail(email);

        if (existUser.isPresent()) {
            User user = existUser.get();
            user.setName(name);
            user.setPictureUrl(pictureUrl);
            return userRepository.save(user);
        } else {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setName(name);
            newUser.setPictureUrl(pictureUrl);
            return userRepository.save(newUser);
        }
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Transactional
    public void deleteUserByEmail(String email) {
        userRepository.findByEmail(email).ifPresent(userRepository::delete);
    }
}
