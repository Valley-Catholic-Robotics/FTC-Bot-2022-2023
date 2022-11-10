package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.util.Hardware;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.Range;

@TeleOp(name="MecanumDrive: Teleop", group="Mecanum")

public class MecanumDriveTeleop extends LinearOpMode {

    /* Declare OpMode members. */
    HardwareSetUp robot=new HardwareSetUp();   // Use a Pushbot's hardware
   

    @Override
    public void runOpMode() {
        double x1=0; //left/right
        double y1=0; //front/back
        
        double fortyFiveInRads=-Math.PI/4;
        double cosine45 = Math.cos(fortyFiveInRads);
        double sine45 = Math.sin(fortyFiveInRads);
        
        double x2=0;
        double y2=0; 
        /* Initialize the hardware variables.
         * The init() method of the hardware class does all the work here
         */
        double armPosition = robot.ARM HOME; // Servo's position
        //double clawPosition = robot. CLAW HOME; // Servo safe position
        
        final double ARM SPEED=0 ;// sets rate to move servo
        //final double CLAW SPEED = 0.01; // sets rate to move serve
         
        robot.init(hardwareMap);

        // Send telemetry message to signify robot waiting;
        telemetry.addData("Say", "Hello Driver");    //
        telemetry.update();

        // Wait for the game to start (driver presses PLAY)
        waitForStart();

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {
            double spin=gamepad1.right_stick_x;
            
            if (Math.abs(spin) > 0.1) {
                robot.FRDrive.setPower(spin);
                robot.BRDrive.setPower(spin);
                robot.FLDrive.setPower(-spin);
                robot.BLDrive.setPower(-spin);
            }
            
            else {
            x1=-gamepad1.left_stick_x;
            y1=gamepad1.left_stick_y;
            
            //need to rotate 45 degree
            y2 = y1*cosine45 + x1*sine45;
            x2 = x1*cosine45 - y1*sine45;

            // Output the safe vales to the motor drives.
            robot.FLDrive.setPower(x2);
            robot.BRDrive.setPower(x2);
            robot.FRDrive.setPower(y2);
            robot.BLDrive.setPower(y2);
            }
            //Raise and lower the arm
            ARM SPEED=gamepa2.left_stick_y;
            armPosition = ARM SPEED; // add to the servo position so it moves

            armPosition = Range. cLip(armPosition, robot.ARM MIN_RANGE,robot.ARM_MAX_RANGE); 
// make sure the position is valid

            // Send telemetry message to signify robot running;
            telemetry.addData("x1","%.2f",x1);
            telemetry.addData("y1","%.2f",y1);
	telemetry. addData("arm", "%.2f", armPosition); 
            telemetry.update();

            // Pace this loop so jaw action is reasonable speed.
            sleep(50);
            
            

        }
    }
}