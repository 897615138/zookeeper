package framework.kafka.common;

import org.apache.kafka.common.serialization.Serializer;
import org.springframework.util.SerializationUtils;

/**
 * @author JillW
 */
public class ObjectSerializer implements Serializer<Object> {

    /**
     * 序列化
     */
    @Override
    public byte[] serialize(String topic, Object data) {
        return SerializationUtils.serialize(data);
    }

//	@Override
//	public void close() {
//
//	}

}
