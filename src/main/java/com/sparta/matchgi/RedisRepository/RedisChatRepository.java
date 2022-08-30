package com.sparta.matchgi.RedisRepository;
import com.sparta.matchgi.model.RedisChat;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface RedisChatRepository extends CrudRepository<RedisChat,String> {

    List<RedisChat> findByRoomIdOrderByCreatedAt(String roomId);

}
