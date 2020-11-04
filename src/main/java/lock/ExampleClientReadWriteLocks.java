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

    public void doWork(long time, TimeUnit unit) throws Exception {
        if (!writeLock.acquire(time, unit)) {
            throw new IllegalStateException(clientName + " could not acquire the writeLock");
        }
        System.out.println(clientName + " has the writeLock");

        if (!readLock.acquire(time, unit)) {
            throw new IllegalStateException(clientName + " could not acquire the readLock");
        }
        System.out.println(clientName + " has the readLock too");

        try {
            resource.use(); //access resource exclusively
        } finally {
            System.out.println(clientName + " releasing the zookeeper.lock");
            readLock.release(); // always release the zookeeper.lock in a finally block
            writeLock.release(); // always release the zookeeper.lock in a finally block
        }
    }
}
