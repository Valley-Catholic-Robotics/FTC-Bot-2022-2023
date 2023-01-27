package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.hardware.DcMotor;
import org.firstinspires.ftc.robotcore.external.navigation.Position;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.Range;

@TeleOp(name="MecanumDrive: Teleop", group="Mecanum")

public class MecanumDriveTeleop extends LinearOpMode {

    /* Declare OpMode members. */
    HardwareSetUp robot=new HardwareSetUp(); 
    // Use a Pushbot's hardware
    
    @Override
    
    public void runOpMode() {
        double power=1;
        
        double armSp=0;
        double start=0;
        double end=1;
        int stop=0;
        double FLPower = 0;
        double BLPower = 0;
        double FRPower = 0;
        double BRPower = 0;
        int mode=1;
        
        /* Initialize the hardware variables.
         * The init() method of the hardware class does all the work here
         */
    
       
        robot.init(hardwareMap);
       
        // Send telemetry message to signify robot waiting;
       
        telemetry.addData("Say", "Hello Driver");    //
        telemetry.update();
        // Wait for the game to start (driver presses PLAY)
        waitForStart();
        // run until the end of the match (driver presses STOP
    
        if(opModeIsActive()){
            while (opModeIsActive()) {
                if (gamepad2.left_stick_y>0){
                    armSp=Math.pow(gamepad2.left_stick_y, power);
                }
                if(gamepad2.left_stick_y<=0 && robot.Touch.getState() == false){
                    armSp=0;
                }
                else if(gamepad2.left_stick_y<=0){
                    armSp=-Math.pow(Math.abs(gamepad2.left_stick_y), power);
                }

                if (gamepad1.b){ // if the "a" button is pressed on the gamepad, do this next line of code
                    mode=1; 
                }
                else if (gamepad1.a){ // if the "y" button is pressed, then do the next Line
                    mode=0; 
                }
                double y = gamepad1.right_stick_y; // Remember, this is reversed!
                double x = -gamepad1.right_stick_x * 1.1; // Counteract imperfect strafing
                double rx = gamepad1.left_stick_x;

                // Read inverse IMU heading, as the IMU heading is CW positive
                double botHeading = -robot.imu.getAngularOrientation().firstAngle;

                double rotX = x * Math.cos(botHeading) - y * Math.sin(botHeading);
                double rotY = x * Math.sin(botHeading) + y * Math.cos(botHeading);

                // Denominator is the largest motor power (absolute value) or 1
                // This ensures all the powers maintain the same ratio, but only when
                // at least one is out of the range [-1, 1]
                double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1);
                if(mode==0){
                    FLPower = (rotY + rotX - rx) / denominator;
                    BLPower = (rotY - rotX - rx) / denominator;
                    FRPower = (rotY - rotX + rx) / denominator;
                    BRPower = (rotY + rotX + rx) / denominator; 
                }
                else{
                    FLPower = (y + x - rx) / denominator;
                    BLPower = (y - x - rx) / denominator;
                    FRPower = (y - x + rx) / denominator;
                    BRPower = (y + x + rx) / denominator;

                }

                robot.FLDrive.setPower(FLPower);
                robot.BLDrive.setPower(BLPower);
                robot.FRDrive.setPower(FRPower);
                robot.BRDrive.setPower(BRPower);
                    
                if (gamepad1.x){
                    robot.FLDrive.setPower(1 );
                    robot.BRDrive.setPower(-1);
                    robot.FRDrive.setPower(-1);
                    robot.BLDrive.setPower(1);
                    sleep(945);
                    robot.FLDrive.setPower(0);
                    robot.BRDrive.setPower(0);
                    robot.FRDrive.setPower(0);
                    robot.BLDrive.setPower(0);
                    
                }
                

                    
                if (gamepad2.b){ // if the "a" button is pressed on the gamepad, do this next line of code
                    robot.claw.setPosition(end); 
                   
                }
                else if (gamepad2.a){ // if the "y" button is pressed, then do the next Line
                    robot.claw.setPosition(start); 
                    
                }
            
                
                
                    //Raise and lower the arm
                
               robot.arm1.setPower(armSp); 
               robot.arm2.setPower(armSp); 
        
        
        
                // Send telemetry message to signify robot running;
                telemetry.addData("x2","%.2f",x);
                telemetry.addData("y2","%.2f",y);
                telemetry.addData("arm", "%.2f",armSp);
                telemetry.addData("Robot Power", "%.2f",power);
                telemetry.update();
        
                // Pace this loop so jaw action is reasonable speed.
                sleep(100);
            }
        }
    }
}
