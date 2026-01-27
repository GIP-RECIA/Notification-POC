package org.esco.notification.randombeans;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.thedeanda.lorem.LoremIpsum;
import org.esco.notification.randombeans.configuration.PublishConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class RandomBeanEventsGenerator implements Generator {
    static Logger log = LoggerFactory.getLogger(RandomBeanEventsGenerator.class.getName());

    private final Connection connection;

    private final Channel channel;
    private final ObjectMapper objectMapper;

    private final List<RandomBean> beans = new ArrayList<>();
    private final Random random = new Random();
    private final PublishConfiguration configuration;

    public RandomBeanEventsGenerator(Connection connection, PublishConfiguration configuration) throws IOException {
        this.connection = connection;

        this.channel = this.connection.createChannel();
        this.configuration = configuration;
        this.configuration.configure(this.channel);

        this.objectMapper = new ObjectMapper(new JsonFactory());
        this.objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    private RandomBean createRandomBean() {
        RandomBean bean = new RandomBean();
        bean.setTitle(LoremIpsum.getInstance().getWords(1, 3));
        return randomizeBean(bean, false);
    }

    private RandomBean randomizeBean(RandomBean bean) {
        return randomizeBean(bean, true);
    }

    private RandomBean randomizeBean(RandomBean bean, boolean updateLastUpdate) {
        if (updateLastUpdate) {
            bean.setLastUpdateDate(new Date());
        }
        bean.setDescription(LoremIpsum.getInstance().getWords(3, 8));
        bean.setRandom(random.nextInt(1000));
        return bean;
    }

    @Override
    public void generate() throws GeneratorException {
        int randInt = random.nextInt(10);

        if (randInt < beans.size()) {
            RandomBean existingBean = beans.get(randInt);

            boolean update = random.nextBoolean();
            if (update) {
                existingBean = randomizeBean(existingBean);
                publish(existingBean, MutationEventType.UPDATED);
            } else {
                RandomBean deletedBean = beans.remove(randInt);
                publish(deletedBean, MutationEventType.DELETED);
            }
        } else {
            RandomBean newBean = createRandomBean();
            beans.add(newBean);
            publish(newBean, MutationEventType.CREATED);
        }
    }

    protected void publish(RandomBean bean, MutationEventType eventType) throws GeneratorException {
        try {
            byte[] serialized = this.objectMapper.writeValueAsBytes(bean);
            AMQP.BasicProperties properties = new AMQP.BasicProperties.Builder()
                    .contentType("application/json")
                    .type(eventType.toString())
                    .build();

            this.configuration.publish(channel, properties, serialized, bean);
            log.debug(String.format("Event %s published for %s", eventType, bean));
        } catch (IOException e) {
            throw new GeneratorException(String.format("Can't publish %s event for %s", eventType, bean), e);
        }
    }
}
