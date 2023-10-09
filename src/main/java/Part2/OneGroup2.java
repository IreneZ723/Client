package Part2;

import io.swagger.client.ApiException;
import io.swagger.client.api.DefaultApi;
import io.swagger.client.model.AlbumsProfile;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class OneGroup2<csvPrinter> implements Runnable {
    private static final int MAXTRIES = 5;
    private int requestPerThread;
    private int threadNum;
    private CountDownLatch threadLatch;
    private File image;
    private AlbumsProfile exampleProfile;
    private String IPAddr;
    private int failedRequestThisThread = 0;
    private CSVPrinter csvPrinter;

    public int getFailedRequestThisThread() {
        return failedRequestThisThread;
    }

    public void setFailedRequestThisThread(int failedRequestThisThread) {
        this.failedRequestThisThread = failedRequestThisThread;
    }

    public OneGroup2(int requestPerThread, int threadNum, CountDownLatch threadLatch, File image, AlbumsProfile exampleProfile, String IPAddr, CSVPrinter csvPrinter) throws IOException {
        this.requestPerThread = requestPerThread;
        this.threadNum = threadNum;
        this.threadLatch = threadLatch;
        this.image = image;
        this.exampleProfile = exampleProfile;
        this.IPAddr = IPAddr;
        this.csvPrinter = csvPrinter;

    }

    synchronized public void incFailedReq() {
        this.failedRequestThisThread++;
    }

    @Override
    public void run() {
        for (int i = 0; i < threadNum; i++) {
            Runnable thread = () -> {

                DefaultApi apiInstance = new DefaultApi();
                apiInstance.getApiClient().setBasePath(IPAddr);
                //TODO: Make the number right
                for (int j = 0; j < requestPerThread; j++) {
                    int currentTry = 0;
                    int maxRetries = 5;
                    int statusCode = -1;
                    long startTime = System.currentTimeMillis();
                    while (currentTry < maxRetries) {
                        try {
                            startTime = System.currentTimeMillis();
                            statusCode = apiInstance.newAlbumWithHttpInfo(image, exampleProfile).getStatusCode();
                            if (statusCode / 100 == 2) {
                                long endTime = System.currentTimeMillis();
                                recordRequestInfo(startTime, "GET", endTime - startTime, statusCode);
                                break;

                            }
                            if (statusCode >= 400 && statusCode <= 599) {
                                // 4XX or 5XX response, retry the request
                                System.out.println("Retry POST Requets");
                                currentTry++;
                                continue;
                            }
                        } catch (ApiException e) {
                            currentTry++;
                            e.printStackTrace();
                            System.out.println("current Try" + currentTry);
                            if (currentTry >= maxRetries) {
                                // Retry limit reached
                                incFailedReq();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    currentTry = 0;
                    while (currentTry < MAXTRIES) {
                        try {
                            startTime = System.currentTimeMillis();
                            statusCode = apiInstance.getAlbumByKeyWithHttpInfo("3").getStatusCode();
                            if (statusCode / 100 == 2) {

                                long endTime = System.currentTimeMillis();
                                recordRequestInfo(startTime, "GET", endTime - startTime, statusCode);
                                break;
                            }
                            if (statusCode >= 400 && statusCode <= 599) {
                                // 4XX or 5XX response, retry the request
                                System.out.println("Retry Get Requets");
                                currentTry++;
                                continue;
                            }
                        } catch (ApiException e) {
                            e.printStackTrace();
                            currentTry++;
                            if (currentTry >= maxRetries) {
                                // Retry limit reached
                                incFailedReq();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                threadLatch.countDown();

            };
            new Thread(thread).start();
        }
    }

    private void recordRequestInfo(long startTime, String requestType, long latency, int responseCode) throws IOException {
        // Record information in CSV format (start time, request type, latency, response code)

        this.csvPrinter.printRecord(startTime, requestType, latency, responseCode);
    }
}
