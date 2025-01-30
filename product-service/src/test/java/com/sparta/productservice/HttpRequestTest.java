package com.sparta.productservice;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpRequestTest {
    private static final int TOTAL_REQUESTS = 10000; // 총 요청 개수
    private static final String API_URL = "http://localhost:8083/performances/1/seats"; // 좌석 화면 진입 API
    private static final String RESULT_FILE = "C:/Users/MADCAHN/Desktop/E-Commerce/logs/test_results.txt"; // 결과 저장 파일

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(100); // 100개 스레드 사용
        int[] successCount = {0};
        int[] failureCount = {0};

        try {
            for (int i = 0; i < TOTAL_REQUESTS; i++) {
                executorService.execute(() -> {
                    try {
                        URL url = new URL(API_URL);
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("GET");
                        int responseCode = connection.getResponseCode();

                        synchronized (HttpRequestTest.class) { //여러 스레드에서 동시 접근 방지
                            if (responseCode == 200) {
                                successCount[0]++;
                            } else if (responseCode == 403) { // 20% 확률로 탈락
                                failureCount[0]++;
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        executorService.shutdown();
        while (!executorService.isTerminated()) {
            // 모든 스레드가 종료될 때까지 대기
        }

        // 파일에 최종 결과 저장
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(RESULT_FILE, false))) { // 기존 내용 덮어쓰기 (false)
            writer.write("Total SUCCESS: " + successCount[0] + "\n");
            writer.write("Total FAILED: " + failureCount[0] + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 콘솔에도 출력
        System.out.println("Total SUCCESS: " + successCount[0]);
        System.out.println("Total FAILED: " + failureCount[0]);
        System.out.println("Test completed! Results saved in " + RESULT_FILE);
    }
}