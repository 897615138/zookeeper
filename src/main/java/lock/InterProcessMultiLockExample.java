package lock;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessLock;
import org.apache.curator.framework.recipes.locks.InterProcessMultiLock;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.framework.recipes.locks.InterProcessSemaphoreMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.test.TestingServer;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * 新建一个InterProcessMultiLock， 包含一个重入锁和一个非重入锁。
 * 调用acquire后可以看到线程同时拥有了这两个锁。
 * 调用release看到这两个锁都被释放了。
 *
 * @author JillW
 * @date 2020/10/22
 */
public class InterProcessMultiLockExample {
    private static final String PATH1 = "/examples/locks1";
    private static final String PATH2 = "/examples/locks2";
    private static final int TEN = 10;

    public static void main(String[] args) throws Exception {
        FakeLimitedResource resource = new FakeLimitedResource();
        try (TestingServer server = new TestingServer()) {
            CuratorFramework client =
                    CuratorFrameworkFactory.newClient(server.getConnectString(), new ExponentialBackoffRetry(1000, 3));
            client.start();

            InterProcessLock lock1 = new InterProcessMutex(client, PATH1);
            InterProcessLock lock2 = new InterProcessSemaphoreMutex(client, PATH2);

            InterProcessMultiLock lock = new InterProcessMultiLock(Arrays.asList(lock1, lock2));
            if (!lock.acquire(TEN, TimeUnit.SECONDS)) {
                throw new IllegalStateException("could not acquire the zookeeper.lock");
            }
            System.out.println("has the zookeeper.lock");

            System.out.println("has the lock1: " + lock1.isAcquiredInThisProcess());
            System.out.println("has the lock2: " + lock2.isAcquiredInThisProcess());

            try {
                resource.use(); //access resource exclusively
            } finally {
                System.out.println("releasing the zookeeper.lock");
                lock.release(); // always release the zookeeper.lock in a finally block
            }
            System.out.println("has the lock1: " + lock1.isAcquiredInThisProcess());
            System.out.println("has the lock2: " + lock2.isAcquiredInThisProcess());
        }
    }
}
