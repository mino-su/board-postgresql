package com.example.board.service;

import com.example.board.exception.follow.FollowAlreadyExistException;
import com.example.board.exception.follow.FollowNotExistException;
import com.example.board.exception.follow.InvalidFollowException;
import com.example.board.exception.user.UserAlreadyExistException;
import com.example.board.exception.user.UserNotAllowedException;
import com.example.board.exception.user.UserNotFoundException;
import com.example.board.model.entity.FollowEntity;
import com.example.board.model.entity.UserEntity;
import com.example.board.model.user.User;
import com.example.board.model.user.UserAuthenticationResponse;
import com.example.board.model.user.UserPatchRequestBody;
import com.example.board.repository.FollowEntityRepository;
import com.example.board.repository.UserEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserEntityRepository userEntityRepository;

    private final JwtService jwtService;

    private final BCryptPasswordEncoder passwordEncoder;

    private final FollowEntityRepository followEntityRepository;

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

    public UserAuthenticationResponse authenticate(String username, String password) {
        var userEntity = userEntityRepository
                .findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        if (passwordEncoder.matches(password, userEntity.getPassword())) {
            return new UserAuthenticationResponse( jwtService.generateAccessToken(userEntity));
        } else{
            throw  new UsernameNotFoundException("비밀번호가 틀렸습니다.");
        }
    }

    public List<User> getUsers(String query) {
        List<UserEntity> userEntities;

        if (query != null && !query.isBlank()) {
            // TODO: query 검색어 기반, 해당 검색어가 username에 포함되어 있는 유저목록 가져오기
            userEntities = userEntityRepository.findByUsernameIsContaining(query);
        } else{
            userEntities = userEntityRepository.findAll();
        }
        return userEntities.stream().map(User::from).toList();
    }
    public User getUser(String username) {
        var userEntity = userEntityRepository
                .findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));

        return User.from(userEntity);
    }

    public User updateUser(String username, UserPatchRequestBody userPatchRequestBody, UserEntity currentUser) {
        var userEntity =
                userEntityRepository.findByUsername(username)
                        .orElseThrow(() -> new UserNotFoundException(username));


        if (!userEntity.equals(currentUser)) {
            throw new UserNotAllowedException();
        }
        if (userPatchRequestBody.description() != null) {
            userEntity.setDescription(userPatchRequestBody.description());
        }

        UserEntity savedEntity = userEntityRepository.save(userEntity);
        return User.from(savedEntity);


    }

    @Transactional
    public User follow(String username, UserEntity currentUser) {
        var following = userEntityRepository.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException(username));
        if (following.equals(currentUser)) {
            throw new InvalidFollowException("A user cannot follow themselves.");
        }


        if (followEntityRepository.findByFollowerAndFollowing(currentUser, following).isPresent()) {
            throw new FollowAlreadyExistException();
        } else{
            followEntityRepository.save(FollowEntity.of(currentUser, following));
            following.setFollowersCount(following.getFollowersCount() + 1);
            currentUser.setFollowingsCount(currentUser.getFollowingsCount()+1);

            userEntityRepository.saveAll(List.of(currentUser, following));

            return User.from(following);
        }

    }

    @Transactional
    public User unfollow(String username, UserEntity currentUser) {
        var following = userEntityRepository.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException(username));
        if (following.equals(currentUser)) {
            throw new InvalidFollowException("A user cannot unfollow themselves.");
        }

        var followEntity = followEntityRepository.findByFollowerAndFollowing(currentUser, following);

        if (followEntity.isPresent()) {

            followEntityRepository.delete(followEntity.get());
            following.setFollowersCount(Math.max(0,following.getFollowersCount()-1));
            currentUser.setFollowingsCount(Math.max(0,currentUser.getFollowingsCount()-1));
            userEntityRepository.save(currentUser);
            var savedEntity = userEntityRepository.save(following);
            return User.from(savedEntity);
        } else{
            throw new FollowNotExistException(currentUser,following);
        }

    }





}
