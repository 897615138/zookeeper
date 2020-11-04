package framework.kafka.common;

import org.apache.kafka.common.serialization.Deserializer;
import org.springframework.util.SerializationUtils;

/**
 * @author jill
 */
public class ObjectDeserializer implements Deserializer<Object> {

//	@Override
//	public void configure(Map<String, ?> configs, boolean isKey) {
//
//	}

    /**
     * 反序列化
     */
    @Override
    public Object deserialize(String topic, byte[] data) {
        return SerializationUtils.deserialize(data);
    }

//	@Override
//	public void close() {
//
//	}

}
