package com.example.nftMachine.service.winnerSelectStrategy;

import com.example.nftMachine.service.NftGeneratorUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;

import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
public class LockWinnerSelectorStrategy implements WinnerSelector {
    private final NftGeneratorUtil nftGeneratorUtil;

    @Override
    public void winnerSelect(String userId) {
        RLock lock = nftGeneratorUtil.rLock();
        try {
            if (!lock.tryLock(1, 1, TimeUnit.SECONDS)) {
                throw new InterruptedException("Lock Exception");
            }
            defaultWinnerSelector(nftGeneratorUtil, userId);

        } catch (InterruptedException e) {
            log.info(e.getMessage());

        } finally {
            lock.unlock();
        }
    }
}
