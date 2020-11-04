package framework.kafka.consumer;

import framework.kafka.model.DemoObj;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.List;

/**
 * 消息消费者（group1）
 *
 * @author zifangsky
 */
@Component("groupListener1")
public class GroupListener1 {
    private static final Logger logger = LoggerFactory.getLogger(GroupListener1.class);

    @KafkaListener(topics = {"topic-test"}, groupId = "group1", containerFactory = "batchContainerFactory")
    public void listenTopic1(List<String> data) {
        System.out.println("Group1收到消息：" + data);
        logger.info(MessageFormat.format("Group1收到消息：{0}", data));
    }

    @KafkaListener(topics = {"topic-test2"}, groupId = "group1")
    public void listenTopic2(DemoObj data) {
        System.out.println("Group1收到消息：" + data);
        logger.info(MessageFormat.format("Group1收到消息：{0}", data));
    }

}
