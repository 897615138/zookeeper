package framework.kafka.config;


import framework.kafka.common.ObjectDeserializer;
import framework.kafka.common.ObjectSerializer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ContainerProperties;

import java.util.HashMap;
import java.util.Map;


/**
 * Kafka配置
 *
 * @author zifangsky
 */
@Configuration
@EnableKafka
public class KafkaConfig {
    /**
     * bootstrap.servers
     * 用于建立到Kafka集群的初始连接的主机/端口对列表。
     * 客户机将使用所有服务器，而不管这里为bootstrapping&mdash指定了哪些服务器;
     * 此列表只影响用于发现完整服务器集的初始主机。
     * 该列表的形式应该是<code>host1:port1,host2:port2，…</code>。
     * 由于这些服务器仅用于初始连接，以发现完整的集群成员关系(可能会动态更改)，因此此列表不需要包含完整的服务器集
     * (不过，如果服务器宕机，您可能需要多个服务器)。
     */
    @Value("${kafka.producer.bootstrapServers}")
    private String producerBootstrapServers;
    /**
     * retries
     * 设置一个大于零的值将导致客户端重新发送任何发送失败并可能出现瞬态错误的记录。
     * 此重试与客户端在接收到错误后重新发送记录没有区别。
     * 允许重试而不将MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION设置为1可能会改变记录的顺序，
     * 因为如果将两个批发送到单个分区，并且第一个批失败并重试，但是第二个批成功，那么第二个批中的记录可能会首先出现。
     * 另外注意，如果DELIVERY_TIMEOUT_MS_CONFIG配置的超时在成功确认之前首先过期，那么在重试次数耗尽之前，生成请求将失败。
     * 用户通常倾向于不设置此配置，而使用DELIVERY_TIMEOUT_MS_CONFIG delivery.timeout.ms来控制重试行为。
     */
    @Value("${kafka.producer.retries}")
    private String producerRetries; //生产者重试次数
    /**
     * batch.size
     * 每当多个记录被发送到同一个分区时，生成器将尝试将记录批处理成更少的请求。
     * 这有助于提高客户机和服务器的性能。
     * 此配置以字节为单位控制默认批处理大小。
     * 不会尝试对大于此大小的记录进行批处理。
     * 发送到代理的请求将包含多个批处理，每个分区都有一个批处理，其中有可供发送的数据。
     * 较小的批处理大小将使批处理不那么常见，并可能降低吞吐量(批处理大小为零将完全禁用批处理)。
     * 非常大的批处理可能会更加浪费内存，因为我们总是会分配指定批处理大小的缓冲区，以预期会有更多的记录。
     */
    @Value("${kafka.producer.batchSize}")
    private String producerBatchSize;
    /**
     * linger.ms
     * 制作人将在请求传输之间到达的任何记录分组为单个批处理请求。
     * 通常情况下，只有当记录到达的速度比发送的速度快时，才会发生这种情况。
     * 然而，在某些情况下，即使在中等负载下，客户端也可能希望减少请求的数量。
     * 此设置通过添加少量的人工延迟来实现这一点，
     * 也就是说，不是立即发送一个记录，生产者将等待到给定的延迟以允许发送其他记录，
     * 以便发送的记录可以成批地一起发送。这可以看作类似于TCP中的Nagle算法。
     * 这个设置了上限配料的延迟:
     * 一旦我们得到BATCH_SIZE_CONFIG值得记录的分区将会立即寄出不管这个设置,
     * 但是如果我们有不到这么多字节积累对于这个分区我们将“徘徊”在指定的时间等待更多的记录。
     * 此设置的默认值为0(即没有延迟)。
     * 例如，设置LINGER_MS_CONFIG =5可以减少发送的请求数量，但是在没有负载的情况下，发送的记录的延迟将增加到5ms。
     */
    @Value("${kafka.producer.lingerMs}")
    private String producerLingerMs;

    /**
     * buffer.memory
     * 生成器可用于缓冲等待发送到服务器的记录的内存总字节。
     * 如果记录发送的速度比它们能够发送到服务器的速度快，
     * 那么生成器将阻塞MAX_BLOCK_MS_CONFIG，然后抛出一个异常。
     * 这个设置应该大致对应于生成器将使用的总内存，但不是硬性限制，
     * 因为生成器使用的所有内存都用于缓冲。一些额外的内存将用于压缩(如果启用了压缩)以及维护动态请求。
     */
    @Value("${kafka.producer.bufferMemory}")
    private String producerBufferMemory;

    @Value("${kafka.consumer.bootstrapServers}")
    private String consumerBootstrapServers;
    /**
     * group.id
     * 标识此使用者所属的使用者组的唯一字符串。
     * 如果使用者通过使用subscribe(topic)或基于kafka的偏移量管理策略使用组管理功能，则需要此属性。
     */
    @Value("${kafka.consumer.groupId}")
    private String consumerGroupId;

    /**
     * enable.auto.commit
     * 如果为真，消费者的偏移量将定期在后台提交。
     */
    @Value("${kafka.consumer.enableAutoCommit}")
    private String consumerEnableAutoCommit;

    /**
     * auto.commit.interval.ms
     * 如果为真，那么消费者偏移量自动提交给Kafka的频率(以毫秒为单位)
     */
    @Value("${kafka.consumer.autoCommitIntervalMs}")
    private String consumerAutoCommitIntervalMs;

    /**
     * session.timeout.ms
     * 使用Kafka的组管理工具时用于检测客户端故障的超时。
     * 客户端定期向代理发送心跳来表示其活动。
     * 如果在此会话超时过期之前代理没有收到心跳，则代理将从组中删除此客户机并启动重新平衡。
     * 注意，该值必须在代理配置中按group.min.session.timeout配置的允许范围内。
     */
    @Value("${kafka.consumer.sessionTimeoutMs}")
    private String consumerSessionTimeoutMs;

    /**
     * max.poll.records
     * 对poll()的一次调用中返回的最大记录数
     */
    @Value("${kafka.consumer.maxPollRecords}")
    private String consumerMaxPollRecords;

    /**
     * auto.offset.reset
     * 当没有初始偏移卡夫卡或如果当前偏移量不存在服务器上的任何更多的(例如,因为数据已被删除):
     * 最早:自动重置抵消最早抵消最新:自动重置抵消最新抵消没有:
     * 抛出异常的消费者如果没有找到以前的抵消消费者集团其他:
     * 消费者抛出例外。
     */
    @Value("${kafka.consumer.autoOffsetReset}")
    private String consumerAutoOffsetReset;

    /**
     * ProducerFactory
     *
     * @return ProducerFactory<Object, Object>
     */
    @Bean
    public ProducerFactory<Object, Object> producerFactory() {
        Map<String, Object> configs = new HashMap<>();

        configs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, producerBootstrapServers);
        configs.put(ProducerConfig.RETRIES_CONFIG, producerRetries);
        configs.put(ProducerConfig.BATCH_SIZE_CONFIG, producerBatchSize);
        configs.put(ProducerConfig.LINGER_MS_CONFIG, producerLingerMs);
        configs.put(ProducerConfig.BUFFER_MEMORY_CONFIG, producerBufferMemory);
        configs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
//		configs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,StringSerializer.class);
//		configs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, ObjectSerializer.class);
        configs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ObjectSerializer.class);

        return new DefaultKafkaProducerFactory<>(configs);
    }

    /**
     * KafkaTemplate
     */
    @Bean
    public KafkaTemplate<Object, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory(), true);
    }

    /**
     * ConsumerFactory
     *
     * @return ConsumerFactory<Object, Object>
     */
    @Bean
    public ConsumerFactory<Object, Object> consumerFactory() {
        Map<String, Object> configs = new HashMap<>(); //参数
        configs.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, consumerBootstrapServers);
        configs.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroupId);
        configs.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, consumerEnableAutoCommit);
        configs.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, consumerAutoCommitIntervalMs);
        configs.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, consumerSessionTimeoutMs);
        configs.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, consumerMaxPollRecords); //批量消费数量
        configs.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, consumerAutoOffsetReset);
        configs.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
//		configs.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,StringDeserializer.class);
//		configs.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ObjectDeserializer.class);
        configs.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                ObjectDeserializer.class); //需要把原来的消息删掉，不然会出现反序列化失败的问题

        return new DefaultKafkaConsumerFactory<>(configs);
    }

    /**
     * 添加KafkaListenerContainerFactory，用于批量消费消息
     *
     * @return KafkaListenerContainerFactory<?>
     */
    @Bean
    public KafkaListenerContainerFactory<?> batchContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<Object, Object> containerFactory =
                new ConcurrentKafkaListenerContainerFactory<>();
        containerFactory.setConsumerFactory(consumerFactory());
        containerFactory.setConcurrency(4);
        containerFactory.setBatchListener(true); //批量消费
        containerFactory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);

        return containerFactory;
    }

}
