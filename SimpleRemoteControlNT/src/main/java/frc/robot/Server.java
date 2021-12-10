package frc.robot;

import edu.wpi.first.networktables.NetworkTableInstance;

public class Server {

    private long startTime;

    public static void main(String... args) throws InterruptedException {
        new Server().run();

    }

    private void run() throws InterruptedException {
        var nt = NetworkTableInstance.getDefault();
        nt.startClient("localhost");
        var ping = nt.getEntry("/LatencyChecker/Ping");
        var pong = nt.getEntry("/LatencyChecker/Ping~");

        startTime = System.currentTimeMillis();

        pong.addListener(entryNotification -> {
            System.out.println("pong: " + (System.currentTimeMillis() - startTime));
        }, 63);

        var v = false;
        while (true) {
            System.out.println("ping" + v);
            startTime = System.currentTimeMillis();
            ping.setBoolean(v);
            v = !v;
            Thread.sleep(1000);
        }
    }
}
