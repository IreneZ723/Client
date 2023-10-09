package Part1;

import io.swagger.client.model.AlbumsProfile;

import java.io.File;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ClientPart1 {
    final static private int NUMTHREADS = 10; // Number of threads in the initialization phase
    private int threadGroupSize;
    private int numThreadGroups;
    private int delaySeconds;
    private String IPAddr;
    private int totalRequests = 0;
    private int failedRequests = 0;
    public File image = new File("albumImageTest.png");
    public AlbumsProfile exampleProfile = new AlbumsProfile();
    CountDownLatch completed = new CountDownLatch(threadGroupSize * numThreadGroups);

    public ClientPart1(int threadGroupSize, int numThreadGroups, int delaySeconds, String IPAddr) {
        this.threadGroupSize = threadGroupSize;
        this.numThreadGroups = numThreadGroups;
        this.delaySeconds = delaySeconds;
        this.IPAddr = IPAddr;
    }

    synchronized public void inc(int times) {
        this.totalRequests += times;
    }


    public void start() throws InterruptedException {

        //  Initialization phase : hard-coded URI
        CountDownLatch completedStartUp = new CountDownLatch(NUMTHREADS);
        long startUpStartTime = System.currentTimeMillis();


        // lambda runnable creation - interface only has a single method so lambda works fine
        // It's recommended to create an instance of `ApiClient` per thread in a multithreaded environment to avoid any potential issues.


        new OneGroup(100, 10, completedStartUp, image, exampleProfile, IPAddr).run();

        completedStartUp.await();

        long startUpEndTime = System.currentTimeMillis();
        System.out.println("Failed request " + failedRequests);
        System.out.println("[Start Up]: The initialization phase completed in " + (startUpEndTime - startUpStartTime) + " milliseconds");
        System.out.println("[Start UP]: Throughput " + NUMTHREADS * 200 * 1000 / (startUpEndTime - startUpStartTime) + " req/sec");


        // Start step3

        long startTime = System.currentTimeMillis();
        System.out.println("Start Thread Group");
        CountDownLatch completed = new CountDownLatch(numThreadGroups * threadGroupSize);
        for (int threadGroup = 0; threadGroup < numThreadGroups; threadGroup++) {
            // for each threadgroup
            OneGroup oneGroup = new OneGroup(1000, threadGroupSize, completed, image, exampleProfile, IPAddr);

            inc(2000 * threadGroupSize);
            this.failedRequests += oneGroup.getFailedRequestThisThread();

            try {
                TimeUnit.SECONDS.sleep(delaySeconds);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        completed.await();

        long endTime = System.currentTimeMillis();
        double wallTime = (endTime - startTime) / 1000;
        System.out.println("Total Request: " + totalRequests);
        System.out.println("Failed Request: " + failedRequests);
        System.out.println("Wall time " + wallTime + " seconds");
        System.out.println("Throughput: " + totalRequests / wallTime + "/s");


    }

    public static void main(String[] args) throws InterruptedException {
        if (args.length != 4) {
            System.err.println("Usage: java Part1.ClientPart1 <threadGroupSize> <numThreadGroups> <delay> <IPAddr>");
            System.exit(1);
        }

        int threadGroupSize = Integer.parseInt(args[0]);
        int numThreadGroups = Integer.parseInt(args[1]);
        int delaySeconds = Integer.parseInt(args[2]);
        String IPAddr = args[3];
        System.out.println("threadGroupSize :" + threadGroupSize + " numThreadGroups " + numThreadGroups + " delaySeconds " + delaySeconds + " IPAddr " + IPAddr);
        ClientPart1 client = new ClientPart1(threadGroupSize, numThreadGroups, delaySeconds, IPAddr);
        client.start();
    }
}


