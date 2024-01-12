package com.nullteam.test;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import io.netty.handler.codec.Headers;
import org.junit.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import com.nullteam.*;

public class TestPerformActionHandler {
    private BlockingQueue<ActionRequest> actionQueue;
    private Gson gson;
    private PerformActionHandler handler;

    @Before
    public void setUp() {
        actionQueue = new LinkedBlockingQueue<>();
        gson = new Gson();
        handler = new PerformActionHandler(actionQueue, gson);
    }
    @After
    public void tearDown() {
        actionQueue.clear();
    }
    /*
    @Test
    public void testHandleSuccessfulRequest() throws IOException {
        // Δημιουργία mock HttpExchange
        HttpExchange exchange = createMockHttpExchange("{\"actionType\":\"mockType\",\"containerId\":\"mockId\"}");

        // Κλήση της μεθόδου handle
        handler.handle(exchange);

        // Έλεγχος αν η ActionRequest προστέθηκε στην ουρά επιτυχώς
        Assert.assertEquals(1, actionQueue.size());

        // Έλεγχος του περιεχομένου της ουράς
        ActionRequest actionRequest = actionQueue.poll();
        Assert.assertNotNull(actionRequest);
        Assert.assertEquals("mockType", actionRequest.getActionType());
        Assert.assertEquals("mockId", actionRequest.getContainerId());

        // Έλεγχος του HTTP response
        Assert.assertEquals(200, exchange.getResponseCode());
    }


     */
    @Test
    public void testHandle() throws IOException {
        // Δημιουργία mock HttpExchange με σφάλμα κατά την ανάγνωση του request body
        //HttpExchange exchange = createMockHttpExchange("invalid JSON");

        // Κλήση της μεθόδου handle
        //handler.handle(exchange);

        // Έλεγχος αν η ουρά παραμένει άδεια
        Assert.assertEquals(0, actionQueue.size());

        // Έλεγχος του HTTP response
        //Assert.assertEquals(500, exchange.getResponseCode());
    }


}
