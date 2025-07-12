package com.example.demo.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.springframework.data.elasticsearch.support.HttpHeaders;


@Configuration
public class ElasticConfig extends ElasticsearchConfiguration {

    @Override
    public ClientConfiguration clientConfiguration() {
        return ClientConfiguration.builder()
                .connectedTo("https://localhost:9200")
                .usingSsl()
                .withHeaders(() -> {
                    HttpHeaders headers = new HttpHeaders();
                    headers.add(HttpHeaders.AUTHORIZATION, "U2RGWHU1Y0JRNVhpZ0FCRXZRck06eVptOFBJSUIwU0Eyc1FDQnIyQTVUQQ==");
                    return headers;
                })
                .build();
    }
}
