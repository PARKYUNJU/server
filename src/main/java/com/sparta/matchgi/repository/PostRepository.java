package com.sparta.matchgi.repository;

import com.sparta.matchgi.dto.PostFilterDto;
import com.sparta.matchgi.model.Post;
import com.sparta.matchgi.model.Request;
import com.sparta.matchgi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Repository
public interface PostRepository extends JpaRepository<Post,Long>, PostRepositoryCustom {


    Post findPostById(Long id);
    List<Post> findAll();

    List<Post> findAllByUser(User user);

    @Modifying
    @Query("update Post p set p.viewCount = p.viewCount + 1 where p.id = :postId")
    int updateView(Long postId);

    Optional<Post> findByCreatedAtAfterAndUser(LocalDateTime createdAt, User user);




}
