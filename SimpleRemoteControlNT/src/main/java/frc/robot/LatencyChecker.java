package frc.robot;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

/**
 * The robot side of the latency checking widget
 */
public class LatencyChecker {

    LatencyChecker() {
        NetworkTableInstance defaultInstance = NetworkTableInstance.getDefault();
        NetworkTableEntry requestEntry = defaultInstance.getEntry("/LatencyChecker/Ping");
        NetworkTableEntry responseEntry = defaultInstance.getEntry("/LatencyChecker/Ping~");
        requestEntry.setBoolean(false);
        responseEntry.setBoolean(false);

        requestEntry.addListener(
                (event) -> {
                    //
                     responseEntry.setBoolean(event.value.getBoolean());
                    System.out.println("got request");
//                    responseEntry.setBoolean(true);
                },
                63);
    }
}
