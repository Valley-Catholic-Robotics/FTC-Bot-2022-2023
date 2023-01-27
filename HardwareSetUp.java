package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.util.ElapsedTime;



public class HardwareSetUp
{
    /* Public OpMode members. */
    public DcMotor FLDrive;
    public DcMotor FRDrive;
    public DcMotor BLDrive;
    public DcMotor BRDrive;

    public DcMotor arm1;
    public DcMotor arm2;
    public Servo claw;
    BNO055IMU imu;
    DigitalChannel Touch;
    

    /* local OpMode members. */
    HardwareMap hwMap;
    
    
    /* Constructor */


    /* Initialize standard Hardware interfaces */
    public void init(HardwareMap ahwMap) {
        // Save reference to Hardware map
        hwMap = ahwMap;

        // Define and Initialize Motors
        FLDrive=hwMap.get(DcMotor.class, "FL");
        FRDrive=hwMap.get(DcMotor.class, "FR");
        BLDrive=hwMap.get(DcMotor.class, "BL");
        BRDrive=hwMap.get(DcMotor.class, "BR");
        
        arm1=hwMap.get(DcMotor.class, "arm1");
        arm2=hwMap.get(DcMotor.class, "arm2");
        claw=hwMap.servo.get("claw1");// set equal to name of the servo motor in the phone

        // setPosition actually sets the servo's position and moves it

        Touch = hwMap.get(DigitalChannel.class, "Touch");
        
        FLDrive.setDirection(DcMotor.Direction.REVERSE); // Set to REVERSE if using AndyMark motors
        FRDrive.setDirection(DcMotor.Direction.FORWARD);// Set to FORWARD if using AndyMark motors
        BLDrive.setDirection(DcMotor.Direction.REVERSE); // Set to REVERSE if using AndyMark motors
        BRDrive.setDirection(DcMotor.Direction.FORWARD);// Set to FORWARD if using AndyMark motors
        
        arm1.setDirection(DcMotor.Direction.REVERSE);
        arm2.setDirection(DcMotor.Direction.FORWARD);

         
        // Set all motors to zero power
        FLDrive.setPower(0);
        FRDrive.setPower(0);
        BLDrive.setPower(0);
        BRDrive.setPower(0);
        


        // Set all motors to run without encoders.
        // May want to use RUN_USING_ENCODERS if encoders are installed.
        //FLDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        //FRDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        //BLDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        //BRDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        
        FLDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        FRDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        BLDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        BRDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        
        arm1.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        arm2.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        FLDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        //FRDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        //BLDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        BRDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        
        //FLDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        FRDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        BLDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        //BRDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        

        arm1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        arm2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

       Touch.setMode(DigitalChannel.Mode.INPUT);
       
        imu = hwMap.get(BNO055IMU.class, "imu");
        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        // Technically this is the default, however specifying it is clearer
        parameters.angleUnit = BNO055IMU.AngleUnit.RADIANS;
        // Without this, data retrieving from the IMU throws an exception
        imu.initialize(parameters);
       

        // Retrieve the IMU from the hardware map
        

    }
 }
