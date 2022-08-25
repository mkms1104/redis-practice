package com.example.nftMachine.redisClient;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.redisson.api.MapOptions;
import org.redisson.api.RedissonClient;
import org.redisson.api.map.MapWriter;
import org.redisson.client.codec.StringCodec;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
public class RedissonTransactionMyTest {
    private final RedissonClient redissonClient;
    private final MemberRepository memberRepository;
    private final PlatformTransactionManager transactionManager;

    @Test
    @DisplayName("레디스에 데이터가 적재될때 마다 외부 저장소에 동기화한다.")
    public void writeThroughStrategy() {
        System.out.println(transactionManager);
        final var writeMode = MapOptions.WriteMode.WRITE_THROUGH;

        var rMap = redissonClient.getMap("test", StringCodec.INSTANCE, mapOptions(writeMode, null));
        rMap.put("name", "mskim");
        rMap.put("age", "30");
    }

    @Test
    @DisplayName("레디스에 데이터가 적재된 후 지정한 delay 시간이 지나거나 batchSize 만큼 쌓였을 경우 외부 저장소에 한꺼번에 동기화한다.")
    public void writeBehindStrategy() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(3);

        final var writeMode = MapOptions.WriteMode.WRITE_BEHIND;
        var rMap = redissonClient.getMap("test", StringCodec.INSTANCE, mapOptions(writeMode, latch));
        rMap.put("name", "mskim");
        rMap.put("age", "30");
        rMap.put("error", "boom");

        latch.await();
    }

    private MapOptions<String, String> mapOptions(MapOptions.WriteMode writeMode, CountDownLatch latch) {
        return MapOptions.<String, String>defaults().writeBehindDelay(5000).writeMode(writeMode).writer(new MapWriter<String, String>() {
            @Override
            public void write(Map<String, String> map) {
                try {
                    map.forEach((key, value) -> {

                        Member member = new Member(value);
                        memberRepository.save(member);
                        if (latch != null) latch.countDown();

                        // 강제 에러 발생
//                        if (key.equals("error")) throw new RuntimeException(value);
                    });

                } catch (Exception e) {
                    System.out.println("롤백 !!!");
                    throw e;
                }
            }

            @Override
            public void delete(Collection<String> keys) {
                throw new UnsupportedOperationException();
            }
        });
    }
}
