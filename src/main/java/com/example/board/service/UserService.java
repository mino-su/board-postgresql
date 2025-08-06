package com.example.board.service;

import com.example.board.exception.user.UserAlreadyExistException;
import com.example.board.exception.user.UserNotFoundException;
import com.example.board.model.entity.UserEntity;
import com.example.board.model.user.User;
import com.example.board.repository.UserEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserEntityRepository userEntityRepository;

    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userEntityRepository
                .findByUsername(username)
                .orElseThrow(()-> new UsernameNotFoundException(username));
    }

    public User signUp(String username, String password) {
        if(userEntityRepository.findByUsername(username).isPresent()){
            throw new UserAlreadyExistException(username);
        }
        var userEntity = UserEntity.of(username, passwordEncoder.encode(password));
        var savedEntity = userEntityRepository.save(userEntity);
        return User.from(savedEntity);
    }
}
