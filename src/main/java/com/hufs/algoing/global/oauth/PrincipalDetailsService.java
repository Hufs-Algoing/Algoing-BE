package com.hufs.algoing.global.oauth;

import com.hufs.algoing.user.entity.User;
import com.hufs.algoing.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PrincipalDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

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
