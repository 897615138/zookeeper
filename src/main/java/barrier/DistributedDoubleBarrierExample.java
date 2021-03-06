package barrier;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.barriers.DistributedDoubleBarrier;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.test.TestingServer;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author JillW
 * @date 2020/10/22
 */
@SuppressWarnings("ALL")
public class DistributedDoubleBarrierExample {
    private static final int QTY = 5;
    private static final String PATH = "/examples/zookeeper.barrier";

    public static void main(String[] args) throws Exception {
        try (TestingServer server = new TestingServer()) {
            ExecutorService service;
            try (CuratorFramework client = CuratorFrameworkFactory.newClient(server.getConnectString(), new ExponentialBackoffRetry(1000, 3))) {
                client.start();
                service = new ScheduledThreadPoolExecutor(QTY,
                        new BasicThreadFactory.Builder().namingPattern("example-schedule-pool-%d").daemon(true).build());
                for (int i = 0; i < QTY; ++i) {
                    DistributedDoubleBarrier barrier = new DistributedDoubleBarrier(client, PATH, QTY);
                    int index = i;
                    Callable<Void> task = () -> {

                        Thread.sleep((long) (3 * Math.random()));
                        System.out.println("Client #" + index + " enters");
                        barrier.enter();
                        System.out.println("Client #" + index + " begins");
                        Thread.sleep((long) (3000 * Math.random()));
                        barrier.leave();
                        System.out.println("Client #" + index + " left");
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
