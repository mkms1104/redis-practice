package com.example.nftMachine.redisClient;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.redisson.api.*;
import org.redisson.api.listener.MessageListener;
import org.redisson.client.RedisException;
import org.redisson.client.codec.StringCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class RedissonBasicTest {

    @Autowired
    private RedissonClient redissonClient;

    @Test
    @DisplayName("string 자료형 CRUD 동작 테스트 (보통 문자열은 Bucket 메서드를 사용한다.)")
    public void strings() {
        Assertions.assertAll(
                // 데이터 저장한다.
                () -> {
                    redissonClient.getBucket("myBucket").set("a");
                    Assertions.assertEquals("myBucket", redissonClient.getBucket("myBucket").getName());
                },
                // 조회 후 삭제한다.
                () -> {
                    RBucket<String> myBucket = redissonClient.getBucket("myBucket");
                    Assertions.assertEquals("a", myBucket.getAndDelete());
                },
                // 다시 조회한다.
                () -> {
                    RBucket<String> myBucket = redissonClient.getBucket("myBucket");
                    Assertions.assertNull(myBucket.get());
                }
        );

        Assertions.assertAll(
                // 데이터 저장한다.
                () -> {
                    redissonClient.getBucket("myBucket").set("a");
                },
                // Long 타입으로 읽어본다.
                () -> {
                    RBucket<Long> myBucket = redissonClient.getBucket("myBucket");
                    Assertions.assertEquals("a", myBucket.get());
                },
                // Double 타입으로 읽어본다.
                () -> {
                    RBucket<Double> myBucket = redissonClient.getBucket("myBucket");
                    Assertions.assertEquals("a", myBucket.get());
                }
        );
    }

    @Test
    @DisplayName("Set 자료형 CRUD 동작 테스트")
    public void set() {
        RSet<Object> mySet = redissonClient.getSet("mySet");
        mySet.add("a");
        mySet.add("a");
        mySet.add("b");
        mySet.add("c");
        Assertions.assertEquals(3, mySet.size());

        Assertions.assertTrue(mySet.contains("b"));
        Assertions.assertFalse(mySet.contains("B"));
        Assertions.assertFalse(mySet.contains("z"));

        mySet.remove("c");
        Assertions.assertFalse(mySet.contains("c"));
        Assertions.assertEquals(2, mySet.size());
    }

    @Test
    @DisplayName("list 자로형 CRUD 동작 테스트")
    public void list() {
        redissonClient.getList("myList").add("a");
        redissonClient.getList("myList").add("b");
        redissonClient.getList("myList").add("c");

        redissonClient.getBlockingDeque("myBlockingQueue").add("a");
        redissonClient.getBlockingDeque("myBlockingQueue").add("b");
        redissonClient.getBlockingDeque("myBlockingQueue").add("c");

        redissonClient.getDeque("myDeque").add("a");
        redissonClient.getDeque("myDeque").add("b");
        redissonClient.getDeque("myDeque").add("c");

        List<Object> myList = redissonClient.getList("myList").readAll();
        Assertions.assertEquals(3, myList.size());
        Assertions.assertEquals("b", myList.get(1));

        List<Object> blockingQueue = redissonClient.getBlockingDeque("myList").readAll();
        Assertions.assertEquals(3, blockingQueue.size());
        Assertions.assertEquals("b", blockingQueue.get(1));

        List<Object> deque = redissonClient.getDeque("myList").readAll();
        Assertions.assertEquals(3, deque.size());
        Assertions.assertEquals("b", deque.get(1));
    }

    @Test
    @DisplayName("hash 자료형 CRUD 동작 테스트")
    public void hashes() {
        RMap<Object, Object> myMap = redissonClient.getMap("myMap");
        myMap.put("name", "mskim");
        myMap.put("gender", "m");
        myMap.put("email", "mskim@11h11m.com");

        Assertions.assertEquals(3, myMap.keySet().size());
        Assertions.assertEquals(3, myMap.values().size());
        Assertions.assertEquals("mskim", myMap.get("name"));
    }

    @Test
    @DisplayName("pub-sub 동작 테스트")
    public void pubsub() {
        RTopic myTopic = redissonClient.getTopic("myTopic");

        // myTopic 토픽에 리스너 추가한다.
        myTopic.addListener(Object.class, new MessageListener<Object>() {
            @Override
            public void onMessage(CharSequence channel, Object msg) {
                System.out.printf("subscribe -> Channel: %s, Message: %s", channel, msg);
            }
        });

        // myTopic 토픽에 전송한다.
        myTopic.publish("안녕하세요! 여러분~");
    }

    @Test
    @DisplayName("serialize 테스트 (직렬화, 역직렬화 방식이 다르면 조회 실패)")
    public void serialize() {

        // 직렬화 지정하지 않고 저장 및 조회
        // String serialize 지정
        Assertions.assertDoesNotThrow(() -> {
            redissonClient.getSet("mySet").add("1234");

            // serialize 지정하지 않고 조회
            Set<Object> mySet = redissonClient.getSet("mySet").readAll();
            Assertions.assertTrue(mySet.contains("1234"));
        });

        // 다음 테스트를 위해 삭제
        redissonClient.getSet("mySet").delete();

        // 직렬화 지정하여 저장, 조회 시 지정하지 않음
        Assertions.assertThrows(RedisException.class, () -> {
            // String serialize 지정
            redissonClient.getSet("mySet", StringCodec.INSTANCE).add("1234");

            // serialize 지정하지 않고 조회
            redissonClient.getSet("mySet").readAll();
        });
    }
}
