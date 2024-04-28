package com.jh.accountmanagement.config;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import redis.embedded.RedisServer;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

@Profile("local")
@Configuration
@Slf4j
public class EmbeddedRedisConfiguration {
    @Value("${spring.data.redis.port}")
    private int redisPort;

    private RedisServer redisServer;

    /*현재 시스템이 ARM 아키텍처인지 확인
      만약 ARM 아키텍처라면,
      RedisServer 클래스를 사용하여 Redis 서버를 생성
      그렇지 않으면, RedisServer.builder()를 사용하여 Redis 서버를 생성*/
    @PostConstruct
    public void startRedis() throws IOException {
        if (isArmArchitecture()) {
            log.info("ARM Architecture");
            redisServer = new RedisServer(Objects.requireNonNull(getRedisServerExecutable()), redisPort);
        } else {
            redisServer = RedisServer.builder()
                    .port(redisPort)
                    .setting("maxmemory 128M")
                    .build();
        }

        redisServer.start();
    }

    @PreDestroy
    public void stopRedis() {
        if (redisServer != null) {
            redisServer.stop();
        }
    }

    private boolean isArmArchitecture() {
        return System.getProperty("os.arch").contains("aarch64");
    }

    private File getRedisServerExecutable() throws IOException {
        try {
            //return  new ClassPathResource("binary/redis/redis-server-linux-arm64-arc").getFile();
            return new File("src/main/resources/binary/redis/redis-server-linux-arm64-arc");
        } catch (Exception e) {
            throw new IOException("Redis Server Executable not found");
        }
    }
}
