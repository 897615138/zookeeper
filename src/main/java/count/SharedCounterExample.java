package count;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.shared.SharedCount;
import org.apache.curator.framework.recipes.shared.SharedCountListener;
import org.apache.curator.framework.recipes.shared.SharedCountReader;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.test.TestingServer;

import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 使用baseCount来监听计数值(addListener方法)。 任意的SharedCount， 只要使用相同的path，都可以得到这个计数值。
 * 然后我们使用5个线程为计数值增加一个10以内的随机数。
 * 这里我们使用trySetCount去设置计数器。 第一个参数提供当前的VersionedValue,如果期间其它client更新了此计数值， 你的更新可能不成功，
 * 但是这时你的client更新了最新的值，所以失败了你可以尝试再更新一次。
 * 而setCount是强制更新计数器的值。
 *
 * @author JillW
 * @date 2020/10/22
 */
public class SharedCounterExample implements SharedCountListener {
    private static final int QTY = 5;
    private static final String PATH = "/examples/counter";

    public static void main(String[] args) throws Exception {
        Random rand = new Random();
        SharedCounterExample example = new SharedCounterExample();
        try (TestingServer server = new TestingServer()) {
            CuratorFramework client =
                    CuratorFrameworkFactory.newClient(server.getConnectString(), new ExponentialBackoffRetry(1000, 3));
            client.start();

            try (SharedCount baseCount = new SharedCount(client, PATH, 0)) {
                baseCount.addListener(example);
                baseCount.start();

                List<SharedCount> examples = Lists.newArrayList();
                ScheduledExecutorService service = new ScheduledThreadPoolExecutor(QTY,
                        new BasicThreadFactory.Builder().namingPattern("example-schedule-pool-%d").daemon(true).build());

                for (int i = 0; i < QTY; ++i) {
                    SharedCount count = new SharedCount(client, PATH, 0);
                    examples.add(count);
                    Callable<Void> task = () -> {
                        count.start();
                        Thread.sleep(rand.nextInt(10000));
                        System.out.println("Increment:" + count.trySetCount(count.getVersionedValue(),
                                count.getCount() + rand.nextInt(10)));
                        return null;
                    };
                    service.submit(task);
                }


                service.shutdown();
                service.awaitTermination(10, TimeUnit.MINUTES);

                for (int i = 0; i < QTY; ++i) {
                    examples.get(i).close();
                }
            }
        }

    }

    @Override
    public void stateChanged(CuratorFramework arg0, ConnectionState arg1) {
        System.out.println("State changed: " + arg1.toString());
    }

    @Override
    public void countHasChanged(SharedCountReader sharedCount, int newCount) {
        System.out.println("Counter's value is changed to " + newCount);
    }
}
