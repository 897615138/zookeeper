package lock;


import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.test.TestingServer;
import org.apache.curator.utils.CloseableUtils;

import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 主程序
 *
 * @author JillW
 * @date 2020/10/22
 */
public class InterProcessMutexExample {
    private static final int QTY = 5;
    private static final int REPETITIONS = QTY * 10;
    private static final String PATH = "/examples/locks";

    public static void main(String[] args) throws Exception {
        FakeLimitedResource resource = new FakeLimitedResource();
        ScheduledExecutorService service = new ScheduledThreadPoolExecutor(QTY,
                new BasicThreadFactory.Builder().namingPattern("example-schedule-pool-%d").daemon(true).build());
        TestingServer server = new TestingServer();
        try {
            for (int i = 0; i < QTY; ++i) {
                int index = i;
                Callable<Void> task = () -> {
                    CuratorFramework client = CuratorFrameworkFactory
                            .newClient(server.getConnectString(), new ExponentialBackoffRetry(1000, 3));
                    try {
                        client.start();
                        ExampleClientThatLocks example =
                                new ExampleClientThatLocks(client, InterProcessMutexExample.PATH, resource, "Client " + index);
                        for (int j = 0; j < InterProcessMutexExample.REPETITIONS; ++j) {
                            example.doWork(10, TimeUnit.SECONDS);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        CloseableUtils.closeQuietly(client);
                    }
                    return null;
                };
                service.submit(task);
            }
            service.shutdown();
            service.awaitTermination(10, TimeUnit.MINUTES);
        } finally {
            CloseableUtils.closeQuietly(server);
        }
    }
}
