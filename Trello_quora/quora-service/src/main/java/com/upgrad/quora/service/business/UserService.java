package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserAuthRepository;
import com.upgrad.quora.service.dao.UserRepository;
import com.upgrad.quora.service.entity.User;
import com.upgrad.quora.service.entity.UserAuth;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.UUID;


@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserAuthRepository userAuthRepository;

    @Autowired
    private PasswordCryptographyProvider cryptographyProvider;

    @Transactional(propagation = Propagation.REQUIRED)
    public User signup(User user) throws SignUpRestrictedException {

        final User userByUserName = userRepository.getUserByUserName(user.getUserName());
        if(userByUserName != null) {
            throw new SignUpRestrictedException("SGR-001", "Try any other Username, this Username has already been taken");
        }

        final User userByEmail = userRepository.getUserByEmail(user.getEmail());
        if(userByEmail != null) {
            throw new SignUpRestrictedException("SGR-002", "This user has already been registered, try with any other emailId");
        }

        String[] encryptedText = cryptographyProvider.encrypt(user.getPassword());
        user.setSalt(encryptedText[0]);
        user.setPassword(encryptedText[1]);

        return userRepository.createUser(user);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public UserAuth login(final String username, final String password) throws AuthenticationFailedException {

        User user = userRepository.getUserByUserName(username);
        if(user == null){
            throw new AuthenticationFailedException("ATH-001", "This username does not exist");
        }

        final String encryptedPassword = cryptographyProvider.encrypt(password, user.getSalt());
        if(encryptedPassword.equals(user.getPassword())){

            JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(encryptedPassword);
            UserAuth userAuth = new UserAuth();
            userAuth.setUserId(user.getId());

            final ZonedDateTime now = ZonedDateTime.now();
            final ZonedDateTime expiresAt = now.plusHours(8);
            userAuth.setAccessToken(jwtTokenProvider.generateToken(user.getUuid(), now, expiresAt));
            userAuth.setUuid(UUID.randomUUID().toString());
            userAuth.setLoginAt(now);
            userAuth.setExpiresAt(expiresAt);

            userAuthRepository.createUserAuth(userAuth);
            return userAuth;

        }
        else{
            throw new AuthenticationFailedException("ATH-002", "Password failed");
        }

    }

    public User getUserById(Integer id) {
        return userRepository.getUserById(id);
    }

}
