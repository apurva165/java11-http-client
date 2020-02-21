package httpclient.demo;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.CountDownLatch;

public class WebSocketExample {
    public static void main(String[] args) throws InterruptedException {
        int msgcount = 5;
        CountDownLatch recieveLatch = new CountDownLatch(msgcount);

        CompletableFuture<WebSocket> weFuture = HttpClient.newHttpClient()
                .newWebSocketBuilder()
                .connectTimeout(Duration.ofSeconds(3))
                .buildAsync(URI.create("wss://echo.websocket.org"), new EchoListener(recieveLatch));

        weFuture.thenAccept(webSocket -> {
            webSocket.request(msgcount);
            for (int i = 0; i < msgcount; i++) {
                webSocket.sendText("Message : " + i, true);
            }
        });

        recieveLatch.await();
    }

    static class EchoListener implements WebSocket.Listener {

        CountDownLatch recieveLatch;

        public EchoListener(CountDownLatch recieveLatch) {
            this.recieveLatch = recieveLatch;
        }

        @Override
        public void onOpen(WebSocket webSocket) {
            System.out.println("websocket open");
        }

        @Override
        public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
            System.out.println("on Text :" + data);
            recieveLatch.countDown();
            return null;
        }
    }
}
