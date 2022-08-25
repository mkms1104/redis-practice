package com.example.nftMachine.config;

import com.example.nftMachine.service.NftGeneratorUtil;
import com.example.nftMachine.service.winnerSelectStrategy.SyncWinnerSelectorStrategy;
import com.example.nftMachine.service.winnerSelectStrategy.WinnerSelector;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
public class CommonBeanConfig {

    @Value("${spring.redis.host}")
    private String host;

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();

        if (host.equals("localhost")) config.useSingleServer().setAddress("redis://" + host + ":6379");
        else config.useClusterServers().addNodeAddress("rediss://" + host + ":6379");

        return Redisson.create(config);
    }

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
                .useSsl().and()
                .commandTimeout(Duration.ofSeconds(2))
                .shutdownTimeout(Duration.ZERO)
                .build();

        return new LettuceConnectionFactory(new RedisStandaloneConfiguration(host, 6379), clientConfig);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        var redisTemplate = new RedisTemplate<String, Object>();
        redisTemplate.setEnableTransactionSupport(true);
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setDefaultSerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericToStringSerializer<>(Object.class));
        redisTemplate.setHashValueSerializer(new GenericToStringSerializer<>(Object.class));
        return redisTemplate;
    }


    @Bean
    public WinnerSelector winnerSelector(NftGeneratorUtil nftGeneratorUtil) {
        return new SyncWinnerSelectorStrategy(nftGeneratorUtil);
    }
}
