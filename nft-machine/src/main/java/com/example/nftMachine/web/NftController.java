package com.example.nftMachine.web;

import com.example.nftMachine.service.NftGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("nft")
@RequiredArgsConstructor
public class NftController {
    private final NftGenerator nftGenerator;

//    @RequestMapping("mint")
//    public String publish() throws InterruptedException {
//        String userId = RandomStringUtils.randomAlphabetic(7);
//        log.info(userId);
//        nftGenerator.generate(userId);
//        return "minted";
//    }
}
