package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.hardware.rev.Rev2mDistanceSensor;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer.CameraDirection;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;

import java.util.List;

@Autonomous(name = "AutoDrive", group = "Concept")
//@Disabled
public class SkystoneTesting extends LinearOpMode {
   // [Declare OpMode members]
   // SYSTEM OpMODE MEMBERS
   private ElapsedTime runtime = new ElapsedTime();
   // DCMOTOR OpMODE MEMBERS               // {MECHANUM DRIVE BASE}
   private DcMotor FRDrive;                //   FR Drive -->  \\_________//  <-- BR Drive
   private DcMotor BRDrive;                //                  |         |
   private DcMotor BLDrive;                //       Front -->  |         |  <-- Rear
   private DcMotor FLDrive;                //                  |_________|
   private DcMotor arm;                //   FL Drive -->  //         \\  <-- BL Drive
   // SERVO OpMODE MEMBERS                 
   private Servo claw;
   
   // SENSOR OpMODE MEMBERS
   //(None)
   // MOTOR VARIABLES
   private double FPLeftUp = 1.0;
   private double FPRightUp = 0.0;
   private double FPLeftDown = 0.0;
   private double FPRightDown = 1.0;
   private double speed = 0.9;

   //TENSORFLOW VARIABLES
   private static final String TFOD_MODEL_ASSET = "Skystone.tflite";
   private static final String LABEL_FIRST_ELEMENT = "Stone";
   private static final String LABEL_SECOND_ELEMENT = "Skystone";
   private boolean skystoneFound = false;
   private int blockPos= 0;

   //VUFORIA KEY INITIALIZATION
   private static final String VUFORIA_KEY =
           "AQicf7n/////AAABmRyml1/1m000nhFYuPY4fv9jx2C/APw5+KXt1Y2pwdh3nl+Qx07i2B27J+VDaiu2ym9K5hIESTPVZbsIwk+WS7tgERaeuIC6S48XD2Ypls1IXQDmYfuDoUQEfdtAFXHBa/AcLrZA0u7bvVzS1hIXx3KM4oz4/e8MG9xOWr3UDyx5O/ch3PBc9KtEQY+nGg3kkEF7V+1HOF6e1++u5qjbNrPJoS87W3NIXpCnVCBkVmM02hHhxd8O2N7xt8Zk6ss4fwMZxSi4wb3Qyss3nZcs/BnpJDBnbzo1UPh+0xE2t0zeSnTIdzYvdxaOiu74Bfia27vu3pQ0+gOqIJyduCvJd7PnarLPoIzRwSmZfqGvjRt3";
   //INSTANTIATE VUFORIA LOCALIZATION ENGINE
   private VuforiaLocalizer vuforia;
   //INSTANTIATE TENSORFLOW OBJECT DETECTION
   private TFObjectDetector tfod;

   @Override
   public void runOpMode() {
       telemetry.addData("System", "Online");
       telemetry.update();

       // Initialize the hardware variables. Note that the strings used here as parameters
       // to 'get' must correspond to the names assigned during the robot configuration
       // step (using the FTC Robot Controller app on the phone).
       FRDrive = hardwareMap.get(DcMotor.class, "FRDrive");
       BRDrive = hardwareMap.get(DcMotor.class, "BRDrive");
       BLDrive = hardwareMap.get(DcMotor.class, "BLDrive");
       FLDrive = hardwareMap.get(DcMotor.class, "FLDrive");
       arm = hardwareMap.get(DcMotor.class, "arm");
       claw = hardwareMap.get(Servo.class, "claw");
       

       FRDrive.setDirection(DcMotor.Direction.REVERSE);
       BRDrive.setDirection(DcMotor.Direction.REVERSE);
       BLDrive.setDirection(DcMotor.Direction.FORWARD);
       FLDrive.setDirection(DcMotor.Direction.FORWARD);

       arm.setDirection(DcMotor.Direction.REVERSE);
       claw.setPosition(0);

       // The TFObjectDetector uses the camera frames from the VuforiaLocalizer, so we create that first.
       initVuforia();

       if (ClassFactory.getInstance().canCreateTFObjectDetector()) {
           initTfod();
       } else {
           telemetry.addData("Sorry!", "This device is not compatible with TFOD");
       }

       //Activate TensorFlow Object Detection before we wait for the start command.
       //Do it here so that the Camera Stream window will have the TensorFlow annotations visible.
       if (tfod != null) {
           tfod.activate();
       }

       waitForStart();

       if (opModeIsActive()) {
           arm.setPosition(1.0);
           claw.setPosition(0.2);
           drive(25, "FORWARD");
           //Track distance motors traveled to determine which combination the blocks are in
           FLDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
           FLDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
           FRDrive.setPower(-0.4);
           BRDrive.setPower(0.4);
           BLDrive.setPower(-0.4);
           FLDrive.setPower(0.4);
           while (opModeIsActive() && skystoneFound == false) {
               if (tfod != null) {
                   // getUpdatedRecognitions() will return null if no new information is available since
                   // the last time that call was made.
                   List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();
                   if (updatedRecognitions != null) {
                       telemetry.addData("Objects Detected", updatedRecognitions.size());

                       // step through the list of recognitions and display boundary info.
                       int i = 0;
                       for (Recognition recognition : updatedRecognitions) {
                           telemetry.addData(String.format("label (%d)", i), recognition.getLabel());
                           telemetry.addData(String.format("  left,top (%d)", i), "%.03f , %.03f",
                                   recognition.getLeft(), recognition.getTop());
                           telemetry.addData(String.format("  right,bottom (%d)", i), "%.03f , %.03f",
                                   recognition.getRight(), recognition.getBottom());
                           if (recognition.getLabel().equals("Skystone")) {
                               skystoneFound = true;
                               break;
                           }
                       }
                       telemetry.update();
                   }
               }
           }
           if (FLDrive.getCurrentPosition() >= strafeConverter(32)) {
               blockPos = 3;
           }
           else if (FLDrive.getCurrentPosition() >= strafeConverter(10) && FLDrive.getCurrentPosition() < strafeConverter(32)) {
               blockPos = 2;
           }
           else if (FLDrive.getCurrentPosition() < strafeConverter(10)) {
               blockPos = 1;
           }
           else {
               blockPos = 1;
           }
           drive(6, "RIGHT");
           drive(50, "FORWARD");
           claw.setPosition(0.7);
           drive(22, "BACKWARD");
           rotate(90, "LEFT");
           if (blockPos == 3) {
               drive(160, "FORWARD");
           }
           if (blockPos == 2) {
               drive(150, "FORWARD");
           }
           if (blockPos == 1) {
               drive(120, "FORWARD");
           }
           claw.setPosition(0.2);
           drive(190, "BACKWARD");
           rotate(100, "RIGHT");
           drive(50, "RIGHT");
           if (blockPos == 3) {
               drive(15, "LEFT");
               rotate(20, "RIGHT");
           }
           if (blockPos == 2) {
               drive(5, "LEFT");
           }
           if (blockPos == 1) {
               drive(25, "LEFT");
           }
           drive(30, "FORWARD");
           claw.setPosition(0.7);
           drive(10, "BACKWARD");
           rotate(90, "LEFT");
           drive(200, "FORWARD");
           claw.setPosition(0.2);
           drive(30, "BACKWARD");
       }

       if (tfod != null) {
           tfod.shutdown();
       }
   }

   public void drive(double centimeters, String direction) {
       //Reset encoders
       FRDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
       BRDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
       BLDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
       FLDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
       if (direction.equals("FORWARD")) {
           //Initialize target position
           FRDrive.setTargetPosition(converter(centimeters));
           BRDrive.setTargetPosition(converter(centimeters));
           BLDrive.setTargetPosition(converter(centimeters));
           FLDrive.setTargetPosition(converter(centimeters));
           //Initialize encoders
           FRDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
           BRDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
           BLDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
           FLDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
           //Set power to wheels
           FRDrive.setPower(speed);
           BRDrive.setPower(speed);
           BLDrive.setPower(speed);
           FLDrive.setPower(speed);
           while (opModeIsActive()
                   && FRDrive.isBusy()
                   && BRDrive.isBusy()
                   && BLDrive.isBusy()
                   && FLDrive.isBusy()) {
               telemetry.addData("System", "Driving forward...");
               telemetry.update();
           }
       }
       if (direction.equals("BACKWARD")) {
           //Initialize target position
           FRDrive.setTargetPosition(-converter(centimeters));
           BRDrive.setTargetPosition(-converter(centimeters));
           BLDrive.setTargetPosition(-converter(centimeters));
           FLDrive.setTargetPosition(-converter(centimeters));
           //Initialize encoders
           FRDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
           BRDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
           BLDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
           FLDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
           //Set power to wheels
           FRDrive.setPower(-speed);
           BRDrive.setPower(-speed);
           BLDrive.setPower(-speed);
           FLDrive.setPower(-speed);
           while (opModeIsActive()
                   && FRDrive.isBusy()
                   && BRDrive.isBusy()
                   && BLDrive.isBusy()
                   && FLDrive.isBusy()) {
               telemetry.addData("System", "Driving backward...");
               telemetry.update();
           }
       }
       if (direction.equals("RIGHT")) {
           //Initialize target position
           FRDrive.setTargetPosition(-strafeConverter(centimeters));
           BRDrive.setTargetPosition(strafeConverter(centimeters));
           BLDrive.setTargetPosition(-strafeConverter(centimeters));
           FLDrive.setTargetPosition(strafeConverter(centimeters));
           //Initialize encoders
           FRDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
           BRDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
           BLDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
           FLDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
           //Set power to wheels
           FRDrive.setPower(-speed);
           BRDrive.setPower(speed);
           BLDrive.setPower(-speed);
           FLDrive.setPower(speed);
           while (opModeIsActive()
                   && FRDrive.isBusy()
                   && BRDrive.isBusy()
                   && BLDrive.isBusy()
                   && FLDrive.isBusy()) {
               telemetry.addData("System", "Driving right...");
               telemetry.update();
           }
       }
       if (direction.equals("LEFT")) {
           //Initialize target position
           FRDrive.setTargetPosition(strafeConverter(centimeters));
           BRDrive.setTargetPosition(-strafeConverter(centimeters));
           BLDrive.setTargetPosition(strafeConverter(centimeters));
           FLDrive.setTargetPosition(-strafeConverter(centimeters));
           //Initialize encoders
           FRDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
           BRDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
           BLDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
           FLDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
           //Set power to wheels
           FRDrive.setPower(speed);
           BRDrive.setPower(-speed);
           BLDrive.setPower(speed);
           FLDrive.setPower(-speed);
           while (opModeIsActive()
                   && FRDrive.isBusy()
                   && BRDrive.isBusy()
                   && BLDrive.isBusy()
                   && FLDrive.isBusy()) {
               telemetry.addData("System", "Driving left...");
               telemetry.update();
           }
       }
       //Stop wheels
       FRDrive.setPower(0);
       BRDrive.setPower(0);
       BLDrive.setPower(0);
       FLDrive.setPower(0);
       //Continue using encoders normally
       FRDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
       BRDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
       BLDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
       FLDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
   }
   public void rotate(double degrees, String direction) {
       //Reset encoders
       FRDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
       BRDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
       BLDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
       FLDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
       if (direction.equals("RIGHT")) {
           //Initialize target position
           FRDrive.setTargetPosition(-turnConverter(degrees));
           BRDrive.setTargetPosition(-turnConverter(degrees));
           BLDrive.setTargetPosition(turnConverter(degrees));
           FLDrive.setTargetPosition(turnConverter(degrees));
           //Initialize encoders
           FRDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
           BRDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
           BLDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
           FLDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
           //Set power to wheels
           FRDrive.setPower(-speed);
           BRDrive.setPower(-speed);
           BLDrive.setPower(speed);
           FLDrive.setPower(speed);
           while (opModeIsActive()
                   && FRDrive.isBusy()
                   && BRDrive.isBusy()
                   && BLDrive.isBusy()
                   && FLDrive.isBusy()) {
               telemetry.addData("System", "Turning right...");
               telemetry.update();
           }
       }
       if (direction.equals("LEFT")) {
           //Initialize target position
           FRDrive.setTargetPosition(turnConverter(degrees));
           BRDrive.setTargetPosition(turnConverter(degrees));
           BLDrive.setTargetPosition(-turnConverter(degrees));
           FLDrive.setTargetPosition(-turnConverter(degrees));
           //Initialize encoders
           FRDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
           BRDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
           BLDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
           FLDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
           //Set power to wheels
           FRDrive.setPower(speed);
           BRDrive.setPower(speed);
           BLDrive.setPower(-speed);
           FLDrive.setPower(-speed);
           while (opModeIsActive()
                   && FRDrive.isBusy()
                   && BRDrive.isBusy()
                   && BLDrive.isBusy()
                   && FLDrive.isBusy()) {
               telemetry.addData("System", "Turning left...");
               telemetry.update();
           }
       }
       FRDrive.setPower(0);
       BRDrive.setPower(0);
       BLDrive.setPower(0);
       FLDrive.setPower(0);
       FRDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
       BRDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
       BLDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
       FLDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
   }
   public void halt(double seconds) {
       runtime.reset();
       while (opModeIsActive() && runtime.seconds() < seconds) {
           FRDrive.setPower(0);
           BRDrive.setPower(0);
           BLDrive.setPower(0);
           FLDrive.setPower(0);
       }
   }
   public int converter(double distance) {
       //40 cm = 288 ticks
       distance = (distance / 40) * 288;
       return (int)(distance);
   }
   public int strafeConverter(double distance) {
       //28 cm = 288 ticks
       distance = (distance / 30) * 288;
       return (int)(distance);
   }
   public int turnConverter(double angle) {
       //288 ticks = 90 degrees
       angle = (angle / 80) * 288;
       return (int)(angle);
   }

   // Initialize the Vuforia localization engine.
   private void initVuforia() {

       // Configure Vuforia by creating a Parameter object, and passing it to the Vuforia engine.

       VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();

       parameters.vuforiaLicenseKey = VUFORIA_KEY;
       parameters.cameraDirection = CameraDirection.BACK;

       // Instantiate the Vuforia engine
       vuforia = ClassFactory.getInstance().createVuforia(parameters);

       // Loading trackables is not necessary for the TensorFlow Object Detection engine.
   }

   // Initialize the TensorFlow Object Detection engine.
   private void initTfod() {
       int tfodMonitorViewId = hardwareMap.appContext.getResources().getIdentifier(
           "tfodMonitorViewId", "id", hardwareMap.appContext.getPackageName());
       TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters(tfodMonitorViewId);
       tfodParameters.minimumConfidence = 0.8;
       tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia);
       tfod.loadModelFromAsset(TFOD_MODEL_ASSET, LABEL_FIRST_ELEMENT, LABEL_SECOND_ELEMENT);
   }
}


