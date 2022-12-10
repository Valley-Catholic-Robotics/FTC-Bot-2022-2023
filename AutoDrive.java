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
//69.5in
@Autonomous(name = "AutoDrive", group = "Concept")
//@Disabled
public class AutoDrive extends LinearOpMode {
   // [Declare OpMode members]
   // SYSTEM OpMODE MEMBERS
   private ElapsedTime runtime = new ElapsedTime();
   // DCMOTOR OpMODE MEMBERS               // {MECHANUM DRIVE BASE}
   private DcMotor FRDrive;                //   FR Drive -->  \\_________//  <-- BR Drive
   private DcMotor BRDrive;                //                  |         |
   private DcMotor BLDrive;                //       Front -->  |         |  <-- Rear
   private DcMotor FLDrive;                //                  |_________|
   private DcMotor arm1;                //   FL Drive -->  //         \\  <-- BL Drive
   private DcMotor arm2;
   // SERVO OpMODE MEMBERS                 
   private Servo claw1;
   private Servo claw2;
   // SENSOR OpMODE MEMBERS
   //(None)
   // MOTOR VARIABLES
   private double speed = 0.8;
    private double time=0;
   //TENSORFLOW VARIABLES
   private static final String TFOD_MODEL_ASSET = "Cone.tflite";
   private static final String[] LABEL={
       "Blue",
       "Red",
       "Black"  
   };
   
   private boolean skystoneFound = false;
   private int blockPos= 0;
   private double start=0;
    private double end=1;
    private double middle1=0.55;
    private double middle2=0.4;
    private int stop=0;
   //VUFORIA KEY INITIALIZATION
   private static final String VUFORIA_KEY ="AXiadPX/////AAABmXO/436A10Bir5qRFHntJaAq5Bk5KctD+d5kjBHoKKhrocEamVb/dco5UIeJx52f0a6FUgm3k2t3fTLXrODhEgjXfI+GRg4fehfN53sbZjPxnNZup1TGZbuZuNZ+ZziAKxgfb8hgmnJ6l7WXSc/MGVw+aJCpoQRccXzBexgZSqgHgkPeoEUgPENY7beWczuIpBtRpvEZOcu6qqUgO6AVcBHSz9nLHfd4LiYjpSeRP3nJ8U69yevSBkXsv/rsYTZzNZ56/QBqL1u7+v9E/23B515U7vwD0u5CM++YqTW9sic+MWOAwhis2ORqZMx4gWXO1cTGQ1sR+dWSrIzzi9SPPiH7yRJGxdMVd7aUmjxFpgFV";
   //INSTANTIATE VUFORIA LOCALIZATION ENGINE
   private VuforiaLocalizer vuforia;
   //INSTANTIATE TENSORFLOW OBJECT DETECTION
   private TFObjectDetector tfod;

   @Override
   public void runOpMode() {
       telemetry.addData("System", "online");
       telemetry.update();

       // Initialize the hardware variables. Note that the strings used here as parameters
       // to 'get' must correspond to the names assigned during the robot configuration
       // step (using the FTC Robot Controller app on the phone).
       FRDrive = hardwareMap.get(DcMotor.class, "FR");
       BRDrive = hardwareMap.get(DcMotor.class, "BR");
       BLDrive = hardwareMap.get(DcMotor.class, "BL");
       FLDrive = hardwareMap.get(DcMotor.class, "FL");
       arm1 = hardwareMap.get(DcMotor.class, "arm1");
       arm2 = hardwareMap.get(DcMotor.class, "arm2");

       claw1 = hardwareMap.get(Servo.class, "claw1");
       claw2 = hardwareMap.get(Servo.class, "claw2");
       

       FRDrive.setDirection(DcMotor.Direction.REVERSE);
       BRDrive.setDirection(DcMotor.Direction.REVERSE);
       BLDrive.setDirection(DcMotor.Direction.FORWARD);
       FLDrive.setDirection(DcMotor.Direction.FORWARD);

       arm1.setDirection(DcMotor.Direction.REVERSE);
       arm2.setDirection(DcMotor.Direction.FORWARD);

       

       //Continue using encoders normally
       FRDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
       BRDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
       BLDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
       FLDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
       arm1.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    arm2.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        arm1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        arm2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
       // The TFObjectDetector uses the camera frames from the VuforiaLocalizer, so we create that first.
       //initVuforia();

       //if (ClassFactory.getInstance().canCreateTFObjectDetector()) {
            //initTfod();
      // } else {
          // telemetry.addData("Sorry!", "This device is not compatible with TFOD");
       //}

       //Activate TensorFlow Object Detection before we wait for the start command.
       //Do it here so that the Camera Stream window will have the TensorFlow annotations visible.
     /*  if (tfod != null) {
           tfod.activate();

           tfod.setZoom(1, 16.0/9.0);
       }
        */
        
       waitForStart();
       
       if (opModeIsActive()) {
        claw1.setPosition(middle1);
        claw2.setPosition(middle1);
        /*
             while (opModeIsActive()) {
                if (tfod != null) {
                    // getUpdatedRecognitions() will return null if no new information is available since
                    // the last time that call was made.
                    List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();
                    if (updatedRecognitions != null) {
                        telemetry.addData("# Objects Detected", updatedRecognitions.size());

                        // step through the list of recognitions and display image position/size information for each one
                        // Note: "Image number" refers to the randomized image orientation/number
                        for (Recognition recognition : updatedRecognitions) {
                            double col = (recognition.getLeft() + recognition.getRight()) / 2 ;
                            double row = (recognition.getTop()  + recognition.getBottom()) / 2 ;
                            double width  = Math.abs(recognition.getRight() - recognition.getLeft()) ;
                            double height = Math.abs(recognition.getTop()  - recognition.getBottom()) ;

                            telemetry.addData(""," ");
                            telemetry.addData("Image", "%s (%.0f %% Conf.)", recognition.getLabel(), recognition.getConfidence() * 100 );
                            telemetry.addData("- Position (Row/Col)","%.0f / %.0f", row, col);
                            telemetry.addData("- Size (Width/Height)","%.0f / %.0f", width, height);
                            if (recognition.getLabel().equals("Red")) {
                               Cone = 1;
                               break;
                           }
                           if (recognition.getLabel().equals("Blue")) {
                               Cone = 2;
                               break;
                           }
                           if (recognition.getLabel().equals("Black")) {
                               Cone = 3;
                               break;
                           }

                        }
                        telemetry.update();
                    }
                }
            */
           
            drive(103,"FORWARD");
            drive(33,"RIGHT");
           drive(29,"FORWARD");
            claw1.setPosition(start);
            claw2.setPosition(start);
            sleep(200);
            drive(6,"BACKWARD");
            /*
            drive(3,"LEFT");
            rotate(43,"RIGHT");
            drive(5,"FORWARD");
            drive(6,"BACKWARD");
            drive(67,"RIGHT");
            drive(5,"FORWARD");
            arm1.setPower(-speed);
            arm2.setPower(-speed);
            sleep(240);
            arm1.setPower(0);
            arm2.setPower(0);
            claw1.setPosition(middle1);
            claw2.setPosition(middle2);
            sleep(400);
            arm1.setPower(-speed);
            arm2.setPower(-speed);
            sleep(1300);
            arm1.setPower(0);
            arm2.setPower(0);
            //drive(54.5,"LEFT");
            */
           telemetry.addData("Path", time);
           telemetry.update();

           sleep(1000);

        }
    }



   public void drive(double centimeters, String direction) {
       if (direction.equals("FORWARD")) {
           FRDrive.setPower(speed);
           BRDrive.setPower(speed);
           BLDrive.setPower(speed);
           FLDrive.setPower(speed);
           SmartSleep(centimeters);
       }
       if (direction.equals("BACKWARD")) {
           FRDrive.setPower(-speed);
           BRDrive.setPower(-speed);
           BLDrive.setPower(-speed);
           FLDrive.setPower(-speed);
           SmartSleep(centimeters);
       }
       if (direction.equals("RIGHT")) {
           FRDrive.setPower(-speed);
           BRDrive.setPower(speed);
           BLDrive.setPower(-speed);
           FLDrive.setPower(speed);
           SmartSleep3(centimeters);
       }
       if (direction.equals("LEFT")) {
           FRDrive.setPower(speed);
           BRDrive.setPower(-speed);
           BLDrive.setPower(speed);
           FLDrive.setPower(-speed);
           SmartSleep3(centimeters);
       }
       //Stop wheels
       FRDrive.setPower(0);
       BRDrive.setPower(0);
       BLDrive.setPower(0);
       FLDrive.setPower(0);  
   }
   public void rotate(double degrees, String direction) {
       if (direction.equals("RIGHT")) {
           FRDrive.setPower(-speed);
           BRDrive.setPower(-speed);
           BLDrive.setPower(speed);
           FLDrive.setPower(speed);
           SmartSleep2(degrees);

       }
       if (direction.equals("LEFT")) {
           FRDrive.setPower(speed);
           BRDrive.setPower(speed);
           BLDrive.setPower(-speed);
           FLDrive.setPower(-speed);
           SmartSleep2(degrees);
       }
       FRDrive.setPower(0);
       BRDrive.setPower(0);
       BLDrive.setPower(0);
       FLDrive.setPower(0);
   }
   
   public void arm(double level, double distance) {
     if (level==3){
       arm1.setPower(speed);
       arm2.setPower(speed);
       SmartSleep(armConverter(distance));
     }
     if (level==-3){
       arm1.setPower(-speed);
       arm2.setPower(-speed);
       SmartSleep(armConverter(distance));
     }
      arm1.setPower(0);
      arm2.setPower(0);
   }
   public void claw(double c1,double c2){
      claw1.setPosition(c1);
      claw2.setPosition(c2);
   }
   
   public void SmartSleep(double distance) {
        runtime.reset();
       time+=converter(distance);
       while (opModeIsActive() && runtime.seconds() < converter(distance)) {
       }
   }
   public void SmartSleep2(double degree) {
       runtime.reset();
       time+=turnConverter(degree);
       while (opModeIsActive() && runtime.seconds() < turnConverter(degree)) {
           
       }
   }
   public void SmartSleep3(double distance) {
       runtime.reset();
       time+=turnConverter(distance);
       while (opModeIsActive() && runtime.seconds() < strafeConverter(distance)) {
           
       }
   }
   public double converter(double distance) {
       //0.04316in = 1s
       distance =distance*0.03043;
       return (double)(distance);
   }
   
   public double armConverter(double distance) {
       //28 cm = 288 s
       distance = (distance / 30) * 288;
       return (double)(distance);
   }
   public double strafeConverter(double distance) {
       //28 cm = 288 s
       distance = distance*0.0367;
       return (double)(distance);
   }
   public double turnConverter(double angle) {
       //1.11s = 90 degrees
       angle = (angle / 90) * 1.11;
       return (double)(angle);
   }
   /* 
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
       tfod.loadModelFromAsset(TFOD_MODEL_ASSET, LABELS);
   }
    */

}



