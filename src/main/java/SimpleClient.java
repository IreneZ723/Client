import java.util.concurrent.CountDownLatch;

// initilization phase
public class SimpleClient {
  final static private int NUMTHREADS = 10;
  private int count = 0;

  synchronized public void inc() {
    count++;
  }

  public int getVal() {
    return this.count;
  }

  public static void main(String[] args) throws InterruptedException {
    final SimpleClient counter = new SimpleClient();
    CountDownLatch  completed = new CountDownLatch(NUMTHREADS);
    long startTime = System.currentTimeMillis();
    for (int i = 0; i < NUMTHREADS; i++) {
      // lambda runnable creation - interface only has a single method so lambda works fine
      Runnable thread =  () -> {
        new CallPostApi();
        for(int j = 0 ; j < 10; j++){
          new CallGetApi();
        }
        counter.inc();
        completed.countDown();
      };

      new Thread(thread).start();
    }

    completed.await();
    long endTime = System.currentTimeMillis();
    System.out.println("Value should be equal to " + NUMTHREADS + " It is: " + counter.getVal());
    System.out.println("The initialization phase completed in " + (endTime - startTime) + " milliseconds");
  }
}
