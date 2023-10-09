package Part2;

import java.io.IOException;

public class LoadTest {
    public static void main(String[] args) throws IOException {
        new CalculateResponseTime("latency_records-1.csv").printResult();
        new CalculateResponseTime("latency_records-2.csv").printResult();
        new CalculateResponseTime("latency_records-3.csv").printResult();
    }
}
