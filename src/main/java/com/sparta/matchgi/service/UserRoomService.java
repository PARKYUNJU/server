package com.sparta.matchgi.service;

import com.sparta.matchgi.RedisRepository.RedisChatRepository;
import com.sparta.matchgi.auth.auth.UserDetailsImpl;
import com.sparta.matchgi.dto.ShowRoomResponseDto;
import com.sparta.matchgi.model.*;
import com.sparta.matchgi.repository.ChatRepository;
import com.sparta.matchgi.repository.RoomRepository;
import com.sparta.matchgi.repository.UserRoomRepository;
import com.sparta.matchgi.repository.UserRoomRepositoryImpl;
import com.sparta.matchgi.util.converter.DateConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserRoomService {

    private final UserRoomRepositoryImpl userRoomRepositoryImpl;

    private final UserRoomRepository userRoomRepository;
    private final RedisChatRepository redisChatRepository;

    private final ChatRepository chatRepository;

    private final RoomRepository roomRepository;

    public ResponseEntity<?> showUserRoom(UserDetailsImpl userDetails) {

        User user = userDetails.getUser();

        List<Room> roomList = userRoomRepository.findRoomList(user);

        for(Room room:roomList){

            List<RedisChat> redisChatList = redisChatRepository.findByRoomIdOrderByCreatedAt(room.getId().toString());

            List<Chat> chatList = redisChatList.stream().map(r->
                    new Chat(r.getRoom(),r.getMessage(),r.getUser(),r.getCreatedAt())
            ).collect(Collectors.toList());

            chatRepository.saveAll(chatList);

            redisChatRepository.deleteAll(redisChatList);
        }


        List<ShowRoomResponseDto> showRoomResponseDtoList = userRoomRepositoryImpl.ShowRoomPost(user);

        return new ResponseEntity<>(showRoomResponseDtoList, HttpStatus.valueOf(200));

    }

    public ResponseEntity<?> updateLastActive(Long roomId, UserDetailsImpl userDetails) {

        Room room = roomRepository.findById(roomId).orElseThrow(
                ()-> new IllegalArgumentException("일치하는 채팅방이 없습니다.")
        );

        User user = userDetails.getUser();

        Optional<UserRoom> userRoomFound = userRoomRepository.findByUserAndRoom(user,room);

        if(userRoomFound.isPresent()){
            userRoomFound.get().updateLastActive(DateConverter.millsToLocalDateTime(System.currentTimeMillis()));
            userRoomRepository.save(userRoomFound.get());
        }else{
            throw new IllegalArgumentException("채팅방에 속해있지 않습니다.");
        }

        return new ResponseEntity<>(HttpStatus.valueOf(200));

    }
}



