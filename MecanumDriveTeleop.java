package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
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
        double power=1.5;
        double x1=0; //left/right
        double y1=0; //front/back
    
        double fortyFiveInRads=-Math.PI/4;
        double cosine45 = Math.cos(fortyFiveInRads);
        double sine45 = Math.sin(fortyFiveInRads);

        double x2=0;
        double y2=0; 
        
        
        double armSp=0;
        double arm_position=0;
        double clawPosition=robot.CLAW_HOME; // Servo position
        final double CLAW_SPEED = 0.01; // sets rate to move serve
        
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
        robot.arm.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        robot.arm.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        
        while (opModeIsActive()) {
            double spin=0;
            if (gamepad2.left_stick_y>0){
                armSp=-Math.pow(gamepad2.left_stick_y, power);
            }
            else{
                armSp=Math.pow(Math.abs(gamepad2.left_stick_y), power);
            }
            
            arm_position=0;
            
            if (gamepad1.left_stick_x>0){
                spin=Math.pow(gamepad1.left_stick_x, power);
                robot.FRDrive.setPower(spin);//1
                robot.BRDrive.setPower(spin);//1
                robot.FLDrive.setPower(-spin);//-1
                robot.BLDrive.setPower(-spin);//-1  
            }
            else if(gamepad1.left_stick_x<0){
                spin=-Math.pow(Math.abs(gamepad1.left_stick_x), power);
                robot.FRDrive.setPower(spin);//1
                robot.BRDrive.setPower(spin);//1
                robot.FLDrive.setPower(-spin);//-1
                robot.BLDrive.setPower(-spin);//-1  
            }
            
            else {
                if(gamepad1.right_stick_x<0||gamepad1.right_stick_y<0){
                    if(gamepad1.right_stick_x<0){
                        x1=Math.pow(-gamepad1.right_stick_x, power);
                    }
                    if(gamepad1.right_stick_y<0){
                        y1=-Math.pow(Math.abs(gamepad1.right_stick_y), power);  
                    }
                }   
                else{
                    x1=-Math.pow(gamepad1.right_stick_x, power);
                    y1=Math.pow(Math.abs(gamepad1.right_stick_y), power);  
                }
                
                
                //need to rotate 45 degree
                x2 = x1*cosine45 - y1*sine45;
                y2 = y1*cosine45 + x1*sine45;
                
                
                robot.FLDrive.setPower(x2);
                robot.BRDrive.setPower(x2);
                robot.FRDrive.setPower(y2);
                robot.BLDrive.setPower(y2);
            }
            
            if (gamepad2.a){ // if the "a" button is pressed on the gamepad, do this next line of code
                robot.claw.setPosition(1); 
               
            }
            else if (gamepad2.y){ // if the "y" button is pressed, then do the next Line
                robot.claw.setPosition(0); 
               
            }
        

            
            //Raise and lower the arm
            
           robot.arm.setPower(armSp); 


            // Send telemetry message to signify robot running;
            telemetry.addData("x1","%.2f",x1);
            telemetry.addData("y1","%.2f",y1);
            telemetry.addData("arm", "%.2f",armSp);
            telemetry.addData("claw", "%.2f",clawPosition);
            telemetry.addData("arm_position", "%.2f",arm_position);
            telemetry.addData("spin", "%.2f",spin);
            telemetry.addData("Robot Power", "%.2f",power);
            telemetry.update();

            // Pace this loop so jaw action is reasonable speed.
            sleep(50);

        }
    }
}
