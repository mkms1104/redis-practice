package com.example.nftMachine.service.nft;

import com.redis.example.nft.service.NftGenerator;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
public class SimpleNftGenerateMachineMyTest {
    private final NftGenerator nftGenerator;
    private final Long PUBLISH_CNT = 100L;

    @Test
    public void clear() {
        nftGenerator.clear();
    }

    @Test
    @DisplayName("관리자는 100개의 NFT 발행한다.")
    public void step01() {
        nftGenerator.clear();
        nftGenerator.load(PUBLISH_CNT);
        assertEquals(PUBLISH_CNT, nftGenerator.remainCnt());
    }

    @Test
    @DisplayName("1000명의 사용자가 동시에 민팅을 시작한다.")
    public void step02() throws InterruptedException {
        final int userCount = 1000;
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        List<Callable<String>> collect = Stream.generate(this::task).limit(userCount).collect(Collectors.toList());
        executorService.invokeAll(collect);
    }

    @Test
    @DisplayName("발행 이후 데이터 상태가 정상인지 확인한다.")
    public void step03() {
        Assertions.assertAll(
                // 남은 NFT 0개
                () -> assertEquals(0, nftGenerator.remainCnt()),

                // 발행된 NFT 수량은 100개
                () -> assertEquals(PUBLISH_CNT, nftGenerator.lastPublishNumber()),

                // 당첨자는 1명
                () -> assertEquals(1, nftGenerator.winnerListCnt())
        );
    }

    private Callable<String> task() {
//        String userId = RandomStringUtils.randomAlphabetic(7);
        return () -> nftGenerator.generate("userId");
    }
}
