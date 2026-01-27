package org.esco.notification.emission.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.esco.notification.data.Notification;
import org.esco.notification.emission.component.UserDetailsAccessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.stereotype.Service;

import java.io.IOError;
import java.io.IOException;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ElasticServiceImpl implements ElasticService {
    @Autowired
    private RestHighLevelClient client;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private UserDetailsAccessor userDetailsAccessor;

    @Override
    public List<Notification> findNotifications(Principal principal, String... medias) throws IOException {
        String userUuid = userDetailsAccessor.getUserUuid((OAuth2Authentication) principal);

        SearchRequest searchRequest = new SearchRequest("logstash-notification-*").types("notification");

        BoolQueryBuilder userFilter = QueryBuilders.boolQuery()
                .should(QueryBuilders.matchQuery("header.userEvent.userUuid", userUuid))
                .should(QueryBuilders.matchQuery("header.userEvent.userUuid", principal.getName()));

        BoolQueryBuilder query = QueryBuilders.boolQuery().must(userFilter);

        if (medias.length > 0) {
            BoolQueryBuilder webMediaFilter = QueryBuilders.boolQuery();

            for (String media : medias) {
                webMediaFilter.should(QueryBuilders.matchQuery("header.media", media));
            }

            query.must(webMediaFilter);
        }

        FieldSortBuilder sort = SortBuilders.fieldSort("@timestamp").order(SortOrder.DESC);

        searchRequest.source().query(query).sort(sort);

        SearchResponse search = client.search(searchRequest);

        try {
            List<Notification> notifications = Arrays.stream(search.getHits().getHits())
                    .map(SearchHit::getSourceAsString)
                    .map(this::mapToNotification).collect(Collectors.toList());

            return notifications;
        } catch (IOError e) {
            throw (IOException) e.getCause();
        }
    }

    private Notification mapToNotification(String source) {
        try {
            return mapper.readValue(source, Notification.class);
        } catch (IOException e) {
            throw new IOError(e);
        }
    }
}
