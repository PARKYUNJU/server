package com.sparta.matchgi.repository;

import com.sparta.matchgi.dto.ChatResponseDto;
import com.sparta.matchgi.model.Chat;
import com.sparta.matchgi.model.Room;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ChatRepository extends JpaRepository<Chat,Long> {
    @Query("SELECT new com.sparta.matchgi.dto.ChatResponseDto(c.id,u.id,u.nickname,u.profileImgUrl,c.message,c.createdAt) " +
            "FROM Chat c  " +
            "JOIN c.user u " +
            "WHERE c.room = :room" +
            " ORDER BY c.createdAt DESC ")
    Slice<ChatResponseDto> showChatsFirst(Room room, Pageable pageable);

    @Query("SELECT new com.sparta.matchgi.dto.ChatResponseDto(c.id,u.id,u.nickname,u.profileImgUrl,c.message,c.createdAt) " +
            "FROM Chat c  " +
            "JOIN c.user u " +
            "WHERE c.room = :room AND c.id<:lastChat" +
            " ORDER BY c.createdAt DESC ")
    Slice<ChatResponseDto> showChatsAfter(Room room, Long lastChat, Pageable pageable);
    List<Chat> findAllByRoom(Room room);



}
