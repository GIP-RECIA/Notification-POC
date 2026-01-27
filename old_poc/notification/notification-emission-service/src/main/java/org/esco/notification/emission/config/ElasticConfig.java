package org.esco.notification.emission.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Elastic search client configuration.
 */
@Configuration
public class ElasticConfig {
    @Autowired
    private ElasticProperties properties;

    @Bean
    public RestHighLevelClient elasticSearchClient() {
        String hostUris = properties.getHostUris();

        List<HttpHost> httpHosts = Arrays.stream(hostUris.split(";"))
                .map(h -> {
                    try {
                        return new URI(h);
                    } catch (URISyntaxException e) {
                        throw new Error(e);
                    }
                })
                .map(uri -> new HttpHost(uri.getHost(), uri.getPort(), uri.getScheme()))
                .collect(Collectors.toList());


        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(httpHosts.toArray(new HttpHost[httpHosts.size()])));
        return client;
    }
}
