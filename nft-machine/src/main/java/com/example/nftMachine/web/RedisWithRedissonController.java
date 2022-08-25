package com.example.nftMachine.web;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RKeys;
import org.redisson.api.RedissonClient;
import org.redisson.api.redisnode.RedisNodes;
import org.redisson.client.codec.StringCodec;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.SplittableRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("redisson-endpoint")
@RequiredArgsConstructor
public class RedisWithRedissonController {
    private final RedissonClient redissonClient;

    @GetMapping("ping")
    public String ping() {
        redissonClient.getRedisNodes(RedisNodes.CLUSTER).pingAll();
        return "ping";
    }

    @GetMapping("save")
    public String save() {
        redissonClient.getBucket("name:redisson", StringCodec.INSTANCE).set("mskim");
        return "save";
    }

    @GetMapping("get")
    public String get() {
        return redissonClient.<String>getBucket("name:redisson", StringCodec.INSTANCE).get();
    }

    @GetMapping("getKeys")
    public String getAll() {
        RKeys rKeys = redissonClient.getKeys();
        Stream<String> keysStream = rKeys.getKeysStream();

        // scan 명령어를 통해 실제 쿼리가 발생하는 시점 (default scan size = 10)
        return keysStream.collect(Collectors.joining(","));
    }

    private String createId() {
        var random = new SplittableRandom();
        return String.valueOf(random.nextInt(1, 1000000000));
    }
}
