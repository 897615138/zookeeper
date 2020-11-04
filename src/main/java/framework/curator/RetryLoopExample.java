package framework.curator;

/**
 * @author JillW
 * @date 2020/10/20
 */
@SuppressWarnings("ALL")
public class RetryLoopExample {
    public static void main(String[] args) {
//        RetryLoop retryLoop = client.newRetryLoop();
//        while ( retryLoop.shouldContinue() )
//        {
//            try
//            {
//                // perform your work
//       ...
//                // it's important to re\-get the ZK instance as there may have been an error and the instance was
//               re\-created
//                ZooKeeper zk = client.getZookeeper();
//                retryLoop.markComplete();
//            }
//            catch ( Exception e )
//            {
//                retryLoop.takeException(e);
//            }
//        }
//    }
//
//    public static void main(String[] args) {
//        RetryLoop.callWithRetry(client, new Callable<Void>()
//        {
//            @Override
//            public Void call() throws Exception
//            {
//                // do your work here - it will get retried if needed
//                return null;
//            }
//        });
    }

}
