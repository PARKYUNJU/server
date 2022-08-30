package com.sparta.matchgi.auth.oauth2;

import com.sparta.matchgi.auth.jwt.JwtTokenUtils;
import com.sparta.matchgi.model.RefreshToken;
import com.sparta.matchgi.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;



@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

//        login 성공한 사용자 목록.
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        Map<String, Object> kakao_account = (Map<String, Object>) oAuth2User.getAttributes().get("kakao_account");
        String email = (String) kakao_account.get("email");

        String accessToken = JwtTokenUtils.generateJwtToken(email);

        String refreshToken = JwtTokenUtils.generateJwtRefreshToken();

        Optional<RefreshToken> refreshTokenFound = refreshTokenRepository.findByEmail(email);

        String isFirst;

        if(refreshTokenFound.isPresent()){
            refreshTokenFound.get().updateRefreshToken(refreshToken);
            refreshTokenRepository.save(refreshTokenFound.get());
            isFirst="false";
        }else{
            refreshTokenRepository.save(new RefreshToken(email,refreshToken));
            isFirst ="true";
        }

        String url = makeRedirectUrl(accessToken,refreshToken,isFirst);

        if (response.isCommitted()) {
            logger.debug("응답이 이미 커밋된 상태입니다. " + url + "로 리다이렉트하도록 바꿀 수 없습니다.");
            return;
        }

        getRedirectStrategy().sendRedirect(request, response, url);


    }
    private String makeRedirectUrl(String accessToken,String refreshToken,String isFirst) {
        return UriComponentsBuilder.fromUriString("http://match-gi.com/redirectKakao?accessToken="+accessToken+"&refreshToken="+refreshToken+"&isFirst="+isFirst)
                .build().toUriString();
    }
}
