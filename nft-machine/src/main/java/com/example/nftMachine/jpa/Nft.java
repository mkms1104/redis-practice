package com.example.nftMachine.jpa;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@ToString
@EqualsAndHashCode
@RedisHash("nft")
public class Nft {

    @Id
    private Long id;
    private String name;
    private Long price;
    private int tokenAddress;
    private boolean isMint;

    public Nft(Long id, String name, Long price) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.tokenAddress = this.hashCode();
    }

    public String getHashKey() {
        return "nft:" + this.id;
    }
}

