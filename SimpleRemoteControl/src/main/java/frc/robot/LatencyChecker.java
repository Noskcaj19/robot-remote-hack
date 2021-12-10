package frc.robot;

public class LatencyChecker {
    public LatencyChecker(NetworkKV networkKV) {
        networkKV.addListener("/LatencyChecker/Ping",
                networkKVUpdate -> networkKV.put("/LatencyChecker/Ping~", true));
    }
}
