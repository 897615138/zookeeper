package lock;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.framework.recipes.locks.InterProcessReadWriteLock;

import java.util.concurrent.TimeUnit;

/**
 * 请求了一个写锁， 然后降级成读锁。 执行业务处理，然后释放读写锁。
 *
 * @author JillW
 * @date 2020/10/22
 */
public class ExampleClientReadWriteLocks {
    private final InterProcessMutex readLock;
    private final InterProcessMutex writeLock;
    private final FakeLimitedResource resource;
    private final String clientName;

    public ExampleClientReadWriteLocks(CuratorFramework client, String lockPath, FakeLimitedResource resource,
                                       String clientName) {
        this.resource = resource;
        this.clientName = clientName;
        InterProcessReadWriteLock lock = new InterProcessReadWriteLock(client, lockPath);
        readLock = lock.readLock();
        writeLock = lock.writeLock();
    }

    static void operation(long time, TimeUnit unit, InterProcessMutex writeLock, String clientName, String s, String s2, InterProcessMutex readLock, String s3, String s4, FakeLimitedResource resource) throws Exception {
        if (!writeLock.acquire(time, unit)) {
            throw new IllegalStateException(clientName + s);
        }
        System.out.println(clientName + s2);

        if (!readLock.acquire(time, unit)) {
            throw new IllegalStateException(clientName + s3);
        }
        System.out.println(clientName + s4);

        try {
            resource.use(); //access resource exclusively
        } finally {
            System.out.println(clientName + " releasing the zookeeper.lock");
            readLock.release(); // always release the zookeeper.lock in a finally block
            writeLock.release(); // always release the zookeeper.lock in a finally block
        }
    }

    public void doWork(long time, TimeUnit unit) throws Exception {
        operation(time, unit, writeLock, clientName, " could not acquire the writeLock", " has the writeLock", readLock, " could not acquire the readLock", " has the readLock too", resource);
    }
}
