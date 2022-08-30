package com.sparta.matchgi.auth.oauth2;

import com.sparta.matchgi.model.User;
import com.sparta.matchgi.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
public class UserOAuth2Service extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserOAuth2Service(UserRepository userRepository,BCryptPasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, Object> attributes = oAuth2User.getAttributes();

        Map<String, Object> kakao_account = (Map<String, Object>) attributes.get("kakao_account");
        String email = (String) kakao_account.get("email");

        Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");
        String profile_image = (String) properties.get("profile_image");


        Optional<User> userFound = userRepository.findByEmail(email);

        if (!userFound.isPresent()) {
            User user = new User(email, passwordEncoder.encode(UUID.randomUUID().toString()),UUID.randomUUID().toString(),profile_image);
            userRepository.save(user);
        } else{
            if(!userFound.get().isKakaoId()){
                throw new LockedException(email);
            }

        }

        return new DefaultOAuth2User(Collections.singleton(new SimpleGrantedAuthority("ROLE_MEMBER")), attributes, "id");
    }


}
