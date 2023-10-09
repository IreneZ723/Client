package Part2;

import Part2.CalculateResponseTime;
import Part2.OneGroup2;
import io.swagger.client.model.AlbumsProfile;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ClientPart2 {
    final static private int NUMTHREADS = 10; // Number of threads in the initialization phase
    private int threadGroupSize;
    private int numThreadGroups;
    private int delaySeconds;
    private String IPAddr;
    private int totalRequests = 0;
    private int failedRequests = 0;
    public File image = new File("albumImageTest.png");
    public AlbumsProfile exampleProfile = new AlbumsProfile();
    CSVPrinter csvPrinter;
    static String filename;

    public ClientPart2(int threadGroupSize, int numThreadGroups, int delaySeconds, String IPAddr) throws IOException {
        this.threadGroupSize = threadGroupSize;
        this.numThreadGroups = numThreadGroups;
        this.delaySeconds = delaySeconds;
        this.IPAddr = IPAddr;
        filename = String.format("latency_records-%d.csv", numThreadGroups);
        this.csvPrinter = new CSVPrinter(new FileWriter(filename, false), CSVFormat.DEFAULT);
    }

    synchronized public void inc(int times) {
        this.totalRequests += times;
    }


    public void start() throws InterruptedException, IOException {

        //  Initialization phase : hard-coded URI
        CountDownLatch completedStartUp = new CountDownLatch(NUMTHREADS);
        long startUpStartTime = System.currentTimeMillis();


        // lambda runnable creation - interface only has a single method so lambda works fine
        // It's recommended to create an instance of `ApiClient` per thread in a multithreaded environment to avoid any potential issues.


        new OneGroup2(100, 10, completedStartUp, image, exampleProfile, IPAddr, csvPrinter).run();

        completedStartUp.await();
        csvPrinter.close();
        long startUpEndTime = System.currentTimeMillis();
        System.out.println("[Start Up]: The initialization phase completed in " + (startUpEndTime - startUpStartTime) + " milliseconds");
        System.out.println("[Start UP]: Throughput " + NUMTHREADS * 200 * 1000 / (startUpEndTime - startUpStartTime) + " req/sec");


        // Start step3

        long startTime = System.currentTimeMillis();
        System.out.println("Start Thread Group");
        CountDownLatch completed = new CountDownLatch(numThreadGroups * threadGroupSize);
        for (int threadGroup = 0; threadGroup < numThreadGroups; threadGroup++) {
            // for each threadgroup
            OneGroup2 oneGroup = new OneGroup2(10, threadGroupSize, completed, image, exampleProfile, IPAddr, csvPrinter);

            inc(2000 * threadGroupSize);
            this.failedRequests += oneGroup.getFailedRequestThisThread();

            try {
                TimeUnit.SECONDS.sleep(delaySeconds);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        completed.await();
        csvPrinter.close();
        long endTime = System.currentTimeMillis();
        double wallTime = (endTime - startTime) / 1000;
        System.out.println("Total Request: " + totalRequests);
        System.out.println("Failed Request: " + failedRequests);
        System.out.println("Wall time " + wallTime + " seconds");
        System.out.println("Throughput: " + totalRequests / wallTime + " req/s");


    }

    public static void main(String[] args) throws InterruptedException, IOException {
        if (args.length != 4) {
            System.err.println("Usage: java Part2.ClientPart2 <threadGroupSize> <numThreadGroups> <delay> <IPAddr>");
            System.exit(1);
        }

        int threadGroupSize = Integer.parseInt(args[0]);
        int numThreadGroups = Integer.parseInt(args[1]);
        int delaySeconds = Integer.parseInt(args[2]);
        String IPAddr = args[3];
        System.out.println("threadGroupSize :" + threadGroupSize + " numThreadGroups " + numThreadGroups + " delaySeconds " + delaySeconds + " IPAddr " + IPAddr);
        ClientPart2 client = new ClientPart2(threadGroupSize, numThreadGroups, delaySeconds, IPAddr);
        client.start();
    }
}


