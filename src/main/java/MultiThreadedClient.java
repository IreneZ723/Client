import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class MultiThreadedClient {
  final static private int NUMTHREADS = 10; // Number of threads in the initialization phase

  private int threadGroupSize;
  private int numThreadGroups;
  private int delaySeconds;
  private String IPAddr;
  private int totalRequests = 0;

  public MultiThreadedClient(int threadGroupSize, int numThreadGroups, int delaySeconds, String IPAddr) {
    this.threadGroupSize = threadGroupSize;
    this.numThreadGroups = numThreadGroups;
    this.delaySeconds = delaySeconds;
    this.IPAddr = IPAddr;
  }

  public void start() throws InterruptedException {
    ExecutorService executorService = Executors.newFixedThreadPool(threadGroupSize);

    long startTime = System.currentTimeMillis();

    for (int threadGroup = 0; threadGroup < numThreadGroups; threadGroup++) {
      for (int i = 0; i < threadGroupSize; i++) {
        executorService.submit(new ApiCaller());
      }

      try {
        TimeUnit.SECONDS.sleep(delaySeconds);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

    executorService.shutdown();

    try {
      executorService.awaitTermination(1, TimeUnit.HOURS);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    long endTime = System.currentTimeMillis();

    System.out.println("Program completed in " + (endTime - startTime) + " milliseconds");
    System.out.println("Total requests made: " + totalRequests);
  }

  private class ApiCaller implements Runnable {
    @Override
    public void run() {
      for (int i = 0; i < 100; i++) { // Perform 100 POST and 100 GET API requests
       new CallGetApi();
        new CallPostApi();
        totalRequests += 2; // Increment the total request count
      }
    }
  }

  public static void main(String[] args) throws InterruptedException {
    if (args.length != 4) {
      System.err.println("Usage: java MultiThreadedClient <threadGroupSize> <numThreadGroups> <delay> <IPAddr>");
      System.exit(1);
    }

    int threadGroupSize = Integer.parseInt(args[0]);
    int numThreadGroups = Integer.parseInt(args[1]);
    int delaySeconds = Integer.parseInt(args[2]);
    String IPAddr = args[3];

    MultiThreadedClient client = new MultiThreadedClient(threadGroupSize, numThreadGroups, delaySeconds, IPAddr);
    client.start();
  }
}
