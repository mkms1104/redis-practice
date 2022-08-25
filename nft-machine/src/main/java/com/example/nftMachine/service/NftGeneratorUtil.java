package com.example.nftMachine.service;

import lombok.RequiredArgsConstructor;
import org.redisson.api.*;
import org.redisson.client.codec.StringCodec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NftGeneratorUtil {
    private final RedissonClient redissonClient;

    @Value("${nft.name}")
    private String nftName;

    public RQueue<String> rNftQueue() {
        return redissonClient.getQueue(nftName, StringCodec.INSTANCE);
    }

    public RAtomicLong rPublishNumber() {
        return redissonClient.getAtomicLong(nftName + ":publishNumber");
    }

    public RList<String> rWinnerList() {
        return redissonClient.getList(nftName + ":winnerList", StringCodec.INSTANCE);
    }

    public RLock rLock() {
        return redissonClient.getLock("lock:" + nftName);
    }

    public void delete(RObject... rObjects) {
        redissonClient.getKeys().delete(rObjects);
    }
}
