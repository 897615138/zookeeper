package framework.kafka.controller;


import framework.kafka.model.DemoObj;
import framework.kafka.producer.SimpleProducer;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


/**
 * @author JillW
 */
@RestController
@RequestMapping("/kafka")
public class TestKafkaController {
    @Resource(name = "simpleProducer")
    private SimpleProducer producer;

    @RequestMapping("/send/text")
    public String send(String data) {
        //测试使用topic
        String topic = "topic-test1";
        producer.sendMessage(topic, data);

        return "发送数据【" + data + "】成功！Topic:" + topic;
    }

    @RequestMapping("/send/object")
    public String send2(DemoObj demoObj) {
        //测试使用topic
        String topic = "topic-test2";
        producer.sendObjectMessage(topic, demoObj);

        return "发送数据【" + demoObj + "】成功！Topic:" + topic;
    }

}
