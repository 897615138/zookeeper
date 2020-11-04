package framework.kafka.common;

import org.apache.kafka.common.serialization.Deserializer;
import org.springframework.util.SerializationUtils;

/**
 * @author jill
 */
public class ObjectDeserializer implements Deserializer<Object> {


    /**
     * 反序列化
     *
     * @param topic 主题
     * @param data  数据
     * @return return
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
