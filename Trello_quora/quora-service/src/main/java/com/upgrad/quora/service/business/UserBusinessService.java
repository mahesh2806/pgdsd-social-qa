package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserAuthTokenDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.UUID;


@Service
public class UserBusinessService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserAuthTokenDao userAuthTokenDao;

    @Autowired
    private PasswordCryptographyProvider cryptographyProvider;

    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity signup(UserEntity userEntity) throws SignUpRestrictedException {

        final UserEntity userEntityByUserName = userDao.getUserByUserName(userEntity.getUserName());
        if(userEntityByUserName != null) {
            throw new SignUpRestrictedException("SGR-001", "Try any other Username, this Username has already been taken");
        }

        final UserEntity userEntityByEmail = userDao.getUserByEmail(userEntity.getEmail());
        if(userEntityByEmail != null) {
            throw new SignUpRestrictedException("SGR-002", "This user has already been registered, try with any other emailId");
        }

        String[] encryptedText = cryptographyProvider.encrypt(userEntity.getPassword());
        userEntity.setSalt(encryptedText[0]);
        userEntity.setPassword(encryptedText[1]);

        return userDao.createUser(userEntity);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public UserAuthTokenEntity login(final String username, final String password) throws AuthenticationFailedException {

        UserEntity userEntity = userDao.getUserByUserName(username);
        if(userEntity == null){
            throw new AuthenticationFailedException("ATH-001", "This username does not exist");
        }

        final String encryptedPassword = cryptographyProvider.encrypt(password, userEntity.getSalt());
        if(encryptedPassword.equals(userEntity.getPassword())){

            JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(encryptedPassword);
            UserAuthTokenEntity userAuthToken = new UserAuthTokenEntity();
            userAuthToken.setUser(userEntity);

            final ZonedDateTime now = ZonedDateTime.now();
            final ZonedDateTime expiresAt = now.plusHours(8);
            userAuthToken.setAccessToken(jwtTokenProvider.generateToken(userEntity.getUuid(), now, expiresAt));
            userAuthToken.setUuid(UUID.randomUUID().toString());
            userAuthToken.setLoginAt(now);
            userAuthToken.setExpiresAt(expiresAt);

            userAuthTokenDao.createUserAuth(userAuthToken);
            return userAuthToken;

        }
        else{
            throw new AuthenticationFailedException("ATH-002", "Password failed");
        }

    }

    public UserEntity getUserById(Integer id) {
        return userDao.getUserById(id);
    }

    public UserAuthTokenEntity getUserAuthToken(final String authorizationToken) {
        UserAuthTokenEntity userAuthTokenEntity = null;
        if (authorizationToken != null && !authorizationToken.isEmpty()) {
            String accessToken;
            if (authorizationToken.indexOf("Bearer ") != -1) {
                String[] bearer = authorizationToken.split("Bearer ");
                accessToken = bearer[1];
            } else {
                accessToken = authorizationToken;
            }
            userAuthTokenEntity = userDao.getAuthToken(accessToken);

            return userAuthTokenEntity;
        }
        return userAuthTokenEntity;
    }

    public boolean isUserSignedIn(UserAuthTokenEntity userAuthTokenEntity) {
        boolean isUserSignedIn = false;
        if (userAuthTokenEntity != null && userAuthTokenEntity.getLoginAt() != null && userAuthTokenEntity.getExpiresAt() != null) {
            if ((userAuthTokenEntity.getLogoutAt() == null)) {
                isUserSignedIn = true;
            }
        }
        return isUserSignedIn;
    }

/**
     * checks if the user is an admin
     * @param user
     * @return
     */
    public boolean isUserAdmin(UserEntity user) {
        boolean isUserAdmin = false;
        if (user != null && "admin".equals(user.getRole())) {
            isUserAdmin = true;
        }
        return isUserAdmin;
    }

}
