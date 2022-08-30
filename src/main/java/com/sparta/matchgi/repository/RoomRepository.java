package com.sparta.matchgi.repository;

import com.sparta.matchgi.model.Post;
import com.sparta.matchgi.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room,Long> {

    Room findByPostId(Long postId);

    Room findByPost(Post post);
}
