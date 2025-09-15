package com.defen.fojcodesandbox.config;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class DockerClientConfig {

    private static final Integer maxConnections = 100;
    private static final Integer connectTimeout = 30;
    private static final Integer responseTimeout = 30;

    @Bean
    public DockerClient dockerClient() {
        // 1. 优先读环境变量 DOCKER_HOST
        String dockerHost = System.getenv("DOCKER_HOST");

        // 2. 如果没设置，走 Mac 默认 socket
        if (dockerHost == null || dockerHost.isEmpty()) {
            dockerHost = "unix:///var/run/docker.sock";
        }

        DefaultDockerClientConfig config = DefaultDockerClientConfig
                .createDefaultConfigBuilder()
                .withDockerHost(dockerHost)
                .build();

        DockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
                .dockerHost(config.getDockerHost())
                .sslConfig(config.getSSLConfig())
                .maxConnections(maxConnections)
                .connectionTimeout(Duration.ofSeconds(connectTimeout))
                .responseTimeout(Duration.ofSeconds(responseTimeout))
                .build();

        DockerClient dockerClient = DockerClientImpl.getInstance(config, httpClient);

        return dockerClient;
    }
}