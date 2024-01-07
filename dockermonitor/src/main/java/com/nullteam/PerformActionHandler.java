package com.nullteam;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.BlockingQueue;

public class PerformActionHandler implements HttpHandler {
    private final BlockingQueue<ActionRequest> actionQueue;
    private final Gson gson;
    public PerformActionHandler(BlockingQueue<ActionRequest> actionQueue, Gson gson) {
        this.actionQueue = actionQueue;
        this.gson = gson;
    }
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            // Read the JSON payload from the request body
            InputStream requestBody = exchange.getRequestBody();
            byte[] payloadBytes = requestBody.readAllBytes();
            String payload = new String(payloadBytes, StandardCharsets.UTF_8);

            // Deserialize the JSON payload to ActionRequest
            ActionRequest actionRequest = gson.fromJson(payload, ActionRequest.class);

            // Enqueue the ActionRequest for processing by the ExecutorThread
            actionQueue.offer(actionRequest);

            // Send a response
            String response = "Action request enqueued successfully";
            exchange.sendResponseHeaders(200, response.length());
            exchange.getResponseBody().write(response.getBytes());

        } catch (Exception e) {
            e.printStackTrace();
            exchange.sendResponseHeaders(500, 0);
        } finally {
            exchange.close();
        }
    }
}

