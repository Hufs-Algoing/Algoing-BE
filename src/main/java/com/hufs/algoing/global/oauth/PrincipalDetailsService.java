package com.hufs.algoing.global.oauth;

import com.hufs.algoing.user.entity.User;
import com.hufs.algoing.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class PrincipalDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User userEntity = userRepository.findByEmail(email).orElse(null);
        if (userEntity != null) {
            return new PrincipalDetails(userEntity);
        } else {
            return null;
        }
    }

}
