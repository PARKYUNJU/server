package com.sparta.matchgi.model;


import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class Notification extends Timestamped {


    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "NOTIFICATION_ID")
    private Long id;

    @Column(nullable = false)
    private String content; //알림내용

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "POST_ID")
    private Post post;


    @Column(nullable = false)
    private boolean isread;

    public Notification(String content,User user, Post post){
        this.content = content;
        this.user =user;
        this.post = post;
    }

    public void change(){
        this.isread = true;
    }

}
