package Part2;

import Part2.ClientPart2;

import java.io.IOException;

public class LoadTest {
    private int threadGroupSize;
    private int numThreadGroups;
    private int delaySeconds;
    private String IPAddr;

    public LoadTest(int threadGroupSize, int numThreadGroups, int delaySeconds, String IPAddr) {
        this.threadGroupSize = threadGroupSize;
        this.numThreadGroups = numThreadGroups;
        this.delaySeconds = delaySeconds;
        this.IPAddr = IPAddr;
    }

    public void callJavaServer() throws InterruptedException, IOException {

        System.out.format("Load test for Java server with ThreadGroup size of %d, numThreadGroups of %d delaySeconds of %d \n", threadGroupSize, numThreadGroups, delaySeconds);
        ClientPart2 client = new ClientPart2(threadGroupSize, numThreadGroups, delaySeconds, IPAddr);
        client.start();
    }

    public void callGoServer() throws InterruptedException, IOException {
        System.out.format("Load test for Go server with ThreadGroup size of %d, numThreadGroups of %d delaySeconds of %d \n", threadGroupSize, numThreadGroups, delaySeconds);
        ClientPart2 client = new ClientPart2(threadGroupSize, numThreadGroups, delaySeconds, IPAddr);
        client.start();
    }

    public static void main(String[] args) {
        LoadTest loadTest1 = new LoadTest(10, 10, 2, "http://35.87.44.22:8080/javaServlet_war");
        LoadTest loadTest2 = new LoadTest(10, 20, 2, "http://35.87.44.22:8080/javaServlet_war");
        LoadTest loadTest3 = new LoadTest(10, 30, 2, "http://35.87.44.22:8080/javaServlet_war");

        // Java Server
        try {
            loadTest1.callJavaServer();
            new CalculateResponseTime("latency-records-1").printResult();
            loadTest2.callJavaServer();
            new CalculateResponseTime("latency-records-2").printResult();
            loadTest3.callJavaServer();
            new CalculateResponseTime("latency-records-3").printResult();

        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }
}
