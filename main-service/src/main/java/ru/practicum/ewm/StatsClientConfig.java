package ru.practicum.ewm;

import org.springframework.web.client.RestClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Value;
import ru.practicum.ewm.stats.client.Client;

@Configuration
public class StatsClientConfig {

    @Value("${stats-server.url:http://stats-server:9090}")
    private String statsServerUrl;

    @Bean
    public RestClient restClient() {
        return RestClient.builder()
                .baseUrl(statsServerUrl)
                .build();
    }

    @Bean
    public Client statsClient(RestClient restClient) {
        return new Client(restClient);
    }
}
