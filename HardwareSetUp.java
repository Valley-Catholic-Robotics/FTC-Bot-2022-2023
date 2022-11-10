package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;


public class HardwareSetUp
{
    /* Public OpMode members. */
    public DcMotor FLDrive;
    public DcMotor FRDrive;
    public DcMotor BLDrive;
    public DcMotor BRDrive;

    public DcMotor arm;
    public Servo claw;

    public final static double CLAW_HOME = 0.0; // Starting position for Servo Claw
    public final static double CLAW_MIN_RANGE = 0.0; // Smallest number value allowed for servo position
    public final static double CLAW_MAX_RANGE = 1.0; // Largest number value allowed for servo position

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
        
        arm=hwMap.get(DcMotor.class, "arm");
        claw = hwMap.servo.get("claw");// set equal to name of the servo motor in the phone
        // setPosition actually sets the servo's position and moves it


        
        FLDrive.setDirection(DcMotor.Direction.REVERSE); // Set to REVERSE if using AndyMark motors
        FRDrive.setDirection(DcMotor.Direction.FORWARD);// Set to FORWARD if using AndyMark motors
        BLDrive.setDirection(DcMotor.Direction.REVERSE); // Set to REVERSE if using AndyMark motors
        BRDrive.setDirection(DcMotor.Direction.FORWARD);// Set to FORWARD if using AndyMark motors
        
        arm.setDirection(DcMotor.Direction.FORWARD);
        claw.setPosition(CLAW_HOME);
         
        // Set all motors to zero power
        FLDrive.setPower(0);
        FRDrive.setPower(0);
        BLDrive.setPower(0);
        BRDrive.setPower(0);
        
        arm.setPower(0);

        // Set all motors to run without encoders.
        // May want to use RUN_USING_ENCODERS if encoders are installed.
        FLDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        FRDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        BLDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        BRDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        
        arm.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        //Define and initialize ALL installed servos.
        /*leftClaw  = hwMap.get(Servo.class, "left_hand");
        rightClaw = hwMap.get(Servo.class, "right_hand");
        leftClaw.setPosition(MID_SERVO);
        rightClaw.setPosition(MID_SERVO);
        */
    }
}