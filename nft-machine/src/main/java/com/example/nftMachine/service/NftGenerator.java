package com.example.nftMachine.service;

import com.example.nftMachine.service.winnerSelectStrategy.WinnerSelector;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RQueue;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class NftGenerator {
    private final WinnerSelector winnerSelector;
    private final NftGeneratorUtil nftGeneratorUtil;

    public void load(Long pubCnt) {
        // NFT 발행 수 0 초기화
        nftGeneratorUtil.rPublishNumber().set(0);

        // NFT N개 생성 후 큐에 저장
        RQueue<String> generatedNftAddresses = nftGeneratorUtil.rNftQueue();
        for (int i = 0; i < pubCnt; i++) {
//            String nftAddress = RandomStringUtils.randomAlphanumeric(7);
//            generatedNftAddresses.add();
        }
    }

    public String generate(String userId) throws InterruptedException {
        String genNft = nftGeneratorUtil.rNftQueue().poll();
        if (Objects.isNull(genNft)) return null; // 모두 발행되었다.

        winnerSelector.winnerSelect(userId);
        return genNft;
    }

    public long remainCnt() {
        return nftGeneratorUtil.rNftQueue().size();
    }

    public long lastPublishNumber() {
        return nftGeneratorUtil.rPublishNumber().get();
    }

    public long winnerListCnt() {
        return nftGeneratorUtil.rWinnerList().size();
    }

    public void clear() {
        nftGeneratorUtil.delete(
                nftGeneratorUtil.rNftQueue(),
                nftGeneratorUtil.rPublishNumber(),
                nftGeneratorUtil.rWinnerList()
        );
    }

}
