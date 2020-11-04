package lock;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;

import java.util.concurrent.TimeUnit;

/**
 * 请求锁， 使用资源，释放锁
 *
 * @author JillW
 * @date 2020/10/22
 */
public class ExampleClientThatLocks {
    private final InterProcessMutex lock;
    private final FakeLimitedResource resource;
    private final String clientName;

    public ExampleClientThatLocks(CuratorFramework client, String lockPath, FakeLimitedResource resource,
                                  String clientName) {
        this.resource = resource;
        this.clientName = clientName;
        //InterProcessMutex换成不可重入锁InterProcessSemaphoreMutex
        //线程被阻塞在第二个acquire上 是不可重入的
        lock = new InterProcessMutex(client, lockPath);
    }

    public void doWork(long time, TimeUnit unit) throws Exception {
        if (!lock.acquire(time, unit)) {
            throw new IllegalStateException(clientName + " could not acquire the zookeeper.lock");
        }
        System.out.println(clientName + " has the zookeeper.lock");
        if (!lock.acquire(time, unit)) {
            throw new IllegalStateException(clientName + " could not acquire the zookeeper.lock");
        }
        System.out.println(clientName + " has the zookeeper.lock again");

        try {
            resource.use(); //access resource exclusively
        } finally {
            System.out.println(clientName + " releasing the zookeeper.lock");
            lock.release(); // always release the zookeeper.lock in a finally block
            lock.release();

        }
    }
}
