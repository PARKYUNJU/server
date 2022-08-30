package com.sparta.matchgi.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewImgUrl {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "REVIEWIMG_ID")
    private Long id;

    @Column(nullable = false)
    private String url;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REVIEW_ID")
    private Review review;

    public ReviewImgUrl(String url, Review review) {
        this.url = url;
        this.review = review;
    }
}
