package framework.curator.discovery;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.test.TestingServer;


/**
 * @author Jill W
 * @date 2020/11/04
 */
public class DiscoveryUtil {
    public static String[] operation1(String command) {
        String[] parts = command.split("\\s");
        //   continue;
        if (parts.length == 0) {
            System.out.println("continue");
        }
        return parts;
    }

    public static CuratorFramework operation2(TestingServer server) {
        CuratorFramework client = CuratorFrameworkFactory.newClient(server.getConnectString(), new ExponentialBackoffRetry(1000, 3));
        client.getCuratorListenable().addListener(
                (client1, event) -> System.out.println("CuratorEvent: " + event.getType().name()));
        client.start();
        return client;
    }
}
