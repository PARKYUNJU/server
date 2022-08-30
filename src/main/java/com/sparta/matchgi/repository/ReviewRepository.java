package com.sparta.matchgi.repository;

import com.sparta.matchgi.model.Post;
import com.sparta.matchgi.model.Review;
import com.sparta.matchgi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review,Long> {

    List<Review> findByPost(Post post);

    List<Review> findAllByPost(Post post);
    Optional<Review> findByPostAndUser(Post post, User user);
}
