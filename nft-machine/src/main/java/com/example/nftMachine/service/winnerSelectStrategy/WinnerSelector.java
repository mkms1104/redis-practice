package com.example.nftMachine.service.winnerSelectStrategy;

import com.example.nftMachine.service.NftGeneratorUtil;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RList;

public interface WinnerSelector {
    Long PICK_NUMBER = 10L;

    void winnerSelect(String userId);

    default void defaultWinnerSelector(NftGeneratorUtil nftGeneratorUtil, String userId) {
        RAtomicLong rPublishNumber = nftGeneratorUtil.rPublishNumber();
        long publishNumber = rPublishNumber.get();

        if (PICK_NUMBER.equals(publishNumber)) {
            RList<String> winnerList = nftGeneratorUtil.rWinnerList();
            winnerList.add(userId);
        }

        rPublishNumber.set(publishNumber + 1);
    }
}
