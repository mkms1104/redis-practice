package com.example.nftMachine.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.SplittableRandom;

@Slf4j
@RestController
@RequestMapping("lettuce-endpoint")
@RequiredArgsConstructor
public class RedisWithLettuceController {
    private final RedisConnectionFactory jedisConnectionFactory;
    private final RedisTemplate<String, String> redisTemplate;

    @GetMapping("ping")
    public String ping() {
        jedisConnectionFactory.getConnection().ping();
        return "ping";
    }

    @GetMapping("save")
    public String save() {
        var opsForValue = redisTemplate.opsForValue();
        opsForValue.set("name:lettuce", "mskim");
        return "save";
    }

    @GetMapping("get")
    public String get() {
        var opsForValue = redisTemplate.opsForValue();
        return opsForValue.get("name:lettuce");
    }

    @GetMapping("getKeys")
    public String getAll() {
        redisTemplate.keys("*");
        return "getKeys";
    }

    @GetMapping("dummyInsert")
    public String dummyInsert() {
        List<Object> results = redisTemplate.executePipelined(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                for (int i = 0; i < 10000; i++) {
                    var key = ("kwd:" + createId()).getBytes();
                    var hashes = Map.of(
                            "actYn".getBytes(), "true".getBytes(),
                            "cpc".getBytes(), "100".getBytes()
                    );
                    connection.hMSet(key, hashes);
                }
                return null;
            }
        });

        return "dummyInsert";
    }

    private String createId() {
        var random = new SplittableRandom();
        return String.valueOf(random.nextInt(1, 1000000000));
    }
}
