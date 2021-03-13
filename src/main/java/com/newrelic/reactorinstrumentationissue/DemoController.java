package com.newrelic.reactorinstrumentationissue;

import io.netty.handler.timeout.ReadTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static java.util.Collections.singletonMap;

@Slf4j
@RestController
public class DemoController {
    private final WebClient clientWith10MsTimeout;
    private final WebClient client;

    public DemoController() {
        HttpClient httpClient = HttpClient.create()
                .tcpConfiguration(tcpClient ->
                        tcpClient.doOnConnected(conn ->
                                conn.addHandlerLast(new ReadTimeoutHandler(10, TimeUnit.MILLISECONDS))));
        ReactorClientHttpConnector connector =
                new ReactorClientHttpConnector(httpClient);
        clientWith10MsTimeout = WebClient.builder()
                .clientConnector(connector)
                .build();
        client = WebClient.builder()
                .build();
    }

    @GetMapping("/error")
    public Mono<ResponseEntity<String>> error() {
        log.info("in /error handler");
        return clientWith10MsTimeout.get()
                .uri("http://www.google.com")
                .retrieve()
                .bodyToMono(String.class)
                .map(s -> ResponseEntity.ok()
                        .contentType(MediaType.TEXT_HTML)
                        .body(s))
                .doOnError(e -> log.info("in doOnError"));
    }

    @GetMapping("/success")
    public Mono<ResponseEntity<String>> success() {
        log.info("in /success handler");
        return client.get()
                .uri("http://www.google.com")
                .retrieve()
                .bodyToMono(String.class)
                .map(s -> ResponseEntity.ok()
                        .contentType(MediaType.TEXT_HTML)
                        .body(s))
                .doOnSuccess(s -> log.info("in doOnSuccess"));
    }
}
