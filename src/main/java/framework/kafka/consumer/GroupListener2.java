package framework.kafka.consumer;

import framework.kafka.model.DemoObj;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;


/**
 * 消息消费者（group2）
 *
 * @author zifangsky
 */
@Component("groupListener2")
public class GroupListener2 {
    private static final Logger logger = LoggerFactory.getLogger(GroupListener2.class);

    @KafkaListener(topics = {"topic-test2"}, groupId = "group2")
    public void listenTopic2(DemoObj data) {
        System.out.println("Group2收到消息：" + data);
        logger.info(MessageFormat.format("Group2收到消息：{0}", data));
    }

//	@KafkaListener(topics={"topic-test2"},groupId="group2")
//	public void listenTopic2_2(DemoObj data){
//		System.out.println("Group2_2收到消息：" + data);
//		logger.info(MessageFormat.format("Group2_2收到消息：{0}", data));
//	}

}
