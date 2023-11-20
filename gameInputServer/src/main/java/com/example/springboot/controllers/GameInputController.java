package com.example.springboot.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


@RestController
public class GameInputController {
    private WebClient webClient;
    private String leaderboardURL;

    @Autowired
    public GameInputController(WebClient.Builder webClientBuilder, @Value("${leaderboard.server.url}") String leaderboardURL) {
        this.webClient = webClientBuilder.baseUrl(leaderboardURL).build();
        this.leaderboardURL = leaderboardURL;
    }

    @PostMapping("/submitScore")
    public Mono<ResponseEntity<String>> submitScore(@RequestParam String playerId, @RequestParam int newScore, @RequestParam int leaderboardId) {
        String leaderboardEndPoint = "/setScore";

        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("playerId", playerId);
        requestBody.add("newScore", String.valueOf(newScore));
        requestBody.add("leaderboardId", String.valueOf(leaderboardId));

        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        return webClient.post()
                .uri(leaderboardEndPoint)
                .headers(httpHeaders -> httpHeaders.addAll(headers))
                .body(BodyInserters.fromFormData(requestBody))
                .retrieve()
                .toEntity(String.class)
                .map(responseEntity -> ResponseEntity.status(responseEntity.getStatusCode()).body(responseEntity.getBody()));
    }

}
