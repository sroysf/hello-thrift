package com.force;

import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) throws Exception {
        EdgeServer server = new EdgeServer();
        EdgeClient client = new EdgeClient();

        new Thread(new Runnable() {
            @Override
            public void run() {
                server.start();
            }
        }).start();

        TimeUnit.SECONDS.sleep(2);

        Thread clientThread = new Thread(new Runnable() {
            @Override
            public void run() {
                client.start();
            }
        });

        clientThread.start();
        clientThread.join();
    }
}
