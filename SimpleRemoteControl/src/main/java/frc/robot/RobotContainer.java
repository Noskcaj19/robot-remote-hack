package frc.robot;

import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.PWMSparkMax;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.RunCommand;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import frc.robot.commands.ExampleCommand;
import frc.robot.subsystems.ExampleSubsystem;

import java.util.HashMap;

abstract class RemoteHID extends GenericHID {
    public RemoteHID(int port) {
        super(port);
    }
}

class RemoteXboxController extends RemoteHID {
    private final NetworkKVServer networkKv;
    private final String prefix;
    private final HashMap<Integer, XboxController.Button> buttonMap = new HashMap<>();
    private final HashMap<Integer, XboxController.Axis> axisMap = new HashMap<>();

    public RemoteXboxController(int port) {
        super(port);
        networkKv = NetworkKVServer.getInstance();

        for (var button : XboxController.Button.values()) {
            buttonMap.put(button.value, button);
        }
        for (var axis : XboxController.Axis.values()) {
            axisMap.put(axis.value, axis);
        }

//        table = NetworkTableInstance.getDefault().getTable("RemoteXbox." + this.getPort());

        prefix = "/RemoteXbox." + this.getPort() + "/";
        for (var button : XboxController.Button.values()) {
//            NetworkTableEntry entry = table.getEntry("Button." + button.name());
//            NetworkTableEntry responseEntry = table.getEntry("Button." + button.name() + "~");
//            entry.setBoolean(false);
//            entry.addListener(
//                    entryNotification ->
//                            responseEntry.setBoolean(entryNotification.value.getBoolean()),
//                    63);
            networkKv.put(prefix + "Button." + button.name(), false);
            networkKv.addListener(prefix + "Button." + button.name(), update -> {
                networkKv.put(prefix + "Button." + button.name() + "~", update.value.booleanValue());
            });
        }
        for (var axis : XboxController.Axis.values()) {
//            NetworkTableEntry entry = table.getEntry("Axis." + axis);
//            NetworkTableEntry responseEntry = table.getEntry("Axis." + axis + "~");
//            entry.setDouble(0);
//            entry.addListener(
//                    entryNotification ->
//                            responseEntry.setDouble(entryNotification.value.getDouble()),
//                    63);
            networkKv.put(prefix + "Axis." + axis.name(), false);
            networkKv.addListener(prefix + "Axis." + axis.name(), update -> {
                networkKv.put(prefix + "Axis." + axis.name() + "~", update.value.doubleValue());
            });

        }
    }

    @Override
    public boolean getRawButton(int button) {
//        return table.getEntry("Button." + buttonMap.get(button).name()).getBoolean(false);
        return networkKv.getBoolean("Button" + buttonMap.get(button).name());
    }

    @Override
    public boolean getRawButtonPressed(int button) {
        System.out.println("getRawButtonPressed");
        return false;
    }

    @Override
    public boolean getRawButtonReleased(int button) {
        System.out.println("getRawButtonReleased");
        return false;
    }

    @Override
    public double getRawAxis(int axis) {
//        return table.getEntry("Axis." + axisMap.get(axis).name()).getDouble(0);
        return networkKv.getDouble("Axis." + axisMap.get(axis).name());
    }

    @Override
    public int getPOV(int pov) {
        System.out.println("getPOV");
        return 0;
    }

    @Override
    public int getAxisCount() {
        return XboxController.Axis.values().length;
    }

    @Override
    public int getPOVCount() {
        System.out.println("getPOVCount");
        return 0;
    }

    @Override
    public int getButtonCount() {
        return XboxController.Button.values().length;
    }

    @Override
    public boolean isConnected() {
        System.out.println("isConnected");
        return false;
    }

    @Override
    public HIDType getType() {
        return HIDType.kHIDGamepad;
    }

    @Override
    public String getName() {
        System.out.println("getName");
        return null;
    }

    @Override
    public int getAxisType(int axis) {
        System.out.println("getAxisType");
        return 0;
    }

    @Override
    public void setOutput(int outputNumber, boolean value) {
        System.out.println("setOutput");
    }

    @Override
    public void setOutputs(int value) {
        System.out.println("setOutputs");
    }

    @Override
    public void setRumble(RumbleType type, double value) {
        System.out.println("setRumble");
    }

    @Override
    public double getX(Hand hand) {
        if (hand.equals(Hand.kLeft)) {
            return getRawAxis(XboxController.Axis.kLeftX.value);
        } else {
            return getRawAxis(XboxController.Axis.kRightX.value);
        }
    }

    @Override
    public double getY(Hand hand) {
        if (hand.equals(Hand.kLeft)) {
            return getRawAxis(XboxController.Axis.kLeftY.value);
        } else {
            return getRawAxis(XboxController.Axis.kRightY.value);
        }
    }
}

/**
 * This class allows two "controllers" to both drive a robot, while allowing the "pilot" to take
 * over complete control at any time
 */
class CopilotController extends GenericHID {
    private final XboxController pilot;
    private final RemoteXboxController copilot;
    private final double deadband = .5;
    private boolean overriding = false;

    public CopilotController(XboxController pilot, RemoteXboxController copilot) {
        super(0);
        this.pilot = pilot;
        this.copilot = copilot;


        var overrideEntry = NetworkTableInstance.getDefault().getEntry("/Copilot/Override");
        overrideEntry.setBoolean(true);
        overrideEntry.addListener(update -> {
            overriding = update.value.getBoolean();
        }, 0);
//        networkKv.put("/Copilot/Override", true);
//        networkKv.addListener("/Copilot/Override", (update) -> {
//            overriding = update.value.booleanValue();
//        });
    }

    public boolean isOverriding() {
        return overriding;
    }

    @Override
    public double getX(Hand hand) {
        double pilotValue = pilot.getX(hand);
//        overriding = overriding || Math.abs(pilotValue) > deadband;
        //        if (overriding) {
        //            return pilotValue;
        //        } else {
        if (overriding) {
            return pilotValue;
        }
        return pilotValue + copilot.getX(hand);
        //        }
    }

    @Override
    public double getY(Hand hand) {
        double pilotValue = pilot.getY(hand);
//        overriding = overriding || Math.abs(pilotValue) > deadband;
        //        if (overriding) {
        //            return pilotValue;
        //        } else {
        if (overriding) {
            return pilotValue;
        }
        return pilotValue + copilot.getY(hand);
        //        }
    }
}

/**
 * This class is where the bulk of the robot should be declared. Since Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in the {@link Robot}
 * periodic methods (other than the scheduler calls). Instead, the structure of the robot (including
 * subsystems, commands, and button mappings) should be declared here.
 */
public class RobotContainer {
    // The robot's subsystems and commands are defined here...
    private final ExampleSubsystem m_exampleSubsystem = new ExampleSubsystem();

    private final ExampleCommand m_autoCommand = new ExampleCommand(m_exampleSubsystem);
    private final CopilotController controller =
            new CopilotController(new XboxController(0), new RemoteXboxController(0));
    //    private final XboxController controller = new XboxController(0);
    private final LatencyChecker latencyChecker = new LatencyChecker(NetworkKVServer.getInstance());
    private final PWMSparkMax motor = new PWMSparkMax(0);


    /**
     * The container for the robot. Contains subsystems, OI devices, and commands.
     */
    public RobotContainer() {
        // Configure the button bindings
        m_exampleSubsystem.setDefaultCommand(
                new RunCommand(
                        () -> {
                            double y = controller.getY(GenericHID.Hand.kRight);
                            motor.set(y);
                        },
                        m_exampleSubsystem));

        configureButtonBindings();
    }

    /**
     * Use this method to define your button->command mappings. Buttons can be created by
     * instantiating a {@link GenericHID} or one of its subclasses ({@link
     * edu.wpi.first.wpilibj.Joystick} or {@link XboxController}), and then passing it to a {@link
     * edu.wpi.first.wpilibj2.command.button.JoystickButton}.
     */
    private void configureButtonBindings() {
        new JoystickButton(controller, XboxController.Button.kA.value)
                .whenPressed(
                        () -> {
                            System.out.println("Heyo");
                            motor.set(1);
                        })
                .whenReleased(
                        () -> {
                            System.out.println("stopped");
                            motor.set(0);
                        });
    }

    /**
     * Use this to pass the autonomous command to the main {@link Robot} class.
     *
     * @return the command to run in autonomous
     */
    public Command getAutonomousCommand() {
        // An ExampleCommand will run in autonomous
        return m_autoCommand;
    }
}
