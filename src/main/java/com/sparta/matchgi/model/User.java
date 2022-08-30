package com.sparta.matchgi.model;


import com.sparta.matchgi.auth.auth.UserDetailsImpl;
import com.sparta.matchgi.dto.ReviseUserRequestDto;
import com.sparta.matchgi.util.Image.S3Image;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class User {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "USER_ID")
    private Long id;


    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Column
    private String profileImgUrl;

    @Column(nullable = false)
    private boolean kakaoId = false;

    public User(String nickname, String email, String password) {
        this.nickname = nickname;
        this.email = email;
        this.password = password;

    }

    public User(String email,String password,String nickname,String profileImgUrl){
        this.nickname =  nickname;
        this.email = email;
        this.password = password;
        this.profileImgUrl = profileImgUrl;
        this.kakaoId = true;
    }

    public void updateNickname(ReviseUserRequestDto reviseUserRequestDto){
        this.nickname = reviseUserRequestDto.getNickname();
    }

    public void changePassword(String password){
        this.password = password;
    }

    public void updateProfileImgUrl(String profileImgUrl){
        this.profileImgUrl = profileImgUrl;
    }
}