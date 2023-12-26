package com.project.repository;

import com.project.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
public class UserCacheRepository {

    public static final String USER_KEY_PREFIX="user::";
    public static final Integer USER_CACHE_KEY_EXPIRY=600;

    @Autowired
    RedisTemplate<String,Object> redisTemplate;
    public User get(Integer userId){
        Object result=redisTemplate.opsForValue().get(getKey(userId));
        return result==null ? null : (User) result;
    }

    public void set(User user){
        redisTemplate.opsForValue().set(getKey(user.getId()),user,USER_CACHE_KEY_EXPIRY, TimeUnit.MINUTES);
    }

    private String getKey(Integer id){
        return USER_KEY_PREFIX+id;
    }
}
