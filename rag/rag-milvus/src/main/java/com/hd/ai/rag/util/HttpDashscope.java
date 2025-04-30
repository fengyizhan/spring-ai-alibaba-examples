package com.hd.ai.rag.util;


import com.alibaba.fastjson2.JSON;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

public final class HttpDashscope {
    public static void main(String[] args) {
        String apiKey = System.getenv("DASHSCOPE_API_KEY");
        if (apiKey == null) {
            System.out.println("DASHSCOPE_API_KEY not found in environment variables");
            return;
        }
        String baseUrl = "https://dashscope.aliyuncs.com/compatible-mode/v1/embeddings";
        HttpClient client = HttpClient.newHttpClient();

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "text-embedding-v3");
        requestBody.put("input", "风急天高猿啸哀，渚清沙白鸟飞回，无边落木萧萧下，不尽长江滚滚来");
        requestBody.put("dimensions", 1024);
        requestBody.put("encoding_format", "float");

        try {
            String requestBodyString = JSON.toJSONString(requestBody);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(requestBodyString))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                System.out.println("Response: " + response.body());
            } else {
                System.out.printf("Failed to retrieve response, status code: %d, response: %s%n", response.statusCode(), response.body());
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
