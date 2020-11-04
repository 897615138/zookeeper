package count;

import com.google.common.collect.Lists;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.atomic.AtomicValue;
import org.apache.curator.framework.recipes.atomic.DistributedAtomicLong;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.test.TestingServer;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 使用5个线程对计数器进行加一操作，如果成功，将操作前后的值打印出来
 *
 * @author JillW
 * @date 2020/10/22
 */
public class DistributedAtomicLongExample {
    private static final int QTY = 5;
    private static final String PATH = "/examples/counter";

    public static void main(String[] args) throws Exception {
        try (TestingServer server = new TestingServer()) {
            ExecutorService service;
            try (CuratorFramework client = CuratorFrameworkFactory.newClient(server.getConnectString(), new ExponentialBackoffRetry(1000, 3))) {
                client.start();
                List<DistributedAtomicLong> examples = Lists.newArrayList();
                service = Executors.newFixedThreadPool(QTY);
                for (int i = 0; i < QTY; ++i) {
                    DistributedAtomicLong count = new DistributedAtomicLong(client, PATH, new RetryNTimes(10, 10));

                    examples.add(count);
                    Callable<Void> task = () -> {
                        try {

                            //Thread.sleep(rand.nextInt(1000));
                            AtomicValue<Long> value = count.increment();
                            //AtomicValue<Long> value = zookeeper.count.decrement();
                            //AtomicValue<Long> value = zookeeper.count.add((long)rand.nextInt(20));
                            System.out.println("succeed: " + value.succeeded());
                            if (value.succeeded()) {
                                System.out.println("Increment: from " + value.preValue() + " to " + value.postValue());
                            }
                        } catch (
                                Exception e) {
                            e.printStackTrace();
                        }
                        return null;
                    };
                    service.submit(task);
                }
            }
            service.shutdown();
            service.awaitTermination(10, TimeUnit.MINUTES);
        }
    }
}
