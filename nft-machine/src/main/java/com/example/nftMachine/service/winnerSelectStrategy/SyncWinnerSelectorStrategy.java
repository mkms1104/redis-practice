package com.example.nftMachine.service.winnerSelectStrategy;

import com.example.nftMachine.service.NftGeneratorUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class SyncWinnerSelectorStrategy implements WinnerSelector {
    private final NftGeneratorUtil nftGeneratorUtil;

    @Override
    public void winnerSelect(String userId) {
        synchronized (this) {
            defaultWinnerSelector(nftGeneratorUtil, userId);
        }
        ;
    }
}
