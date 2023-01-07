package org.firstinspires.ftc.teamcode.drive.opmode;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.trajectorysequence.TrajectorySequence;

@Autonomous(name = "RoadRuner", group = "Concept")
//@Disabled

public class TestingTrajectories extends LinearOpMode {
    private DcMotor arm1;
    private DcMotor arm2;
    private Servo claw1;
    private Servo claw2;

    public void runOpMode(){
        SampleMecanumDrive drive=new SampleMecanumDrive(hardwareMap);
        arm1=hardwareMap.get(DcMotor.class, "arm1");
        arm2=hardwareMap.get(DcMotor.class, "arm2");
        claw1=hardwareMap.servo.get("claw1");// set equal to name of the servo motor in the phone
        claw2=hardwareMap.servo.get("claw2");// set equal to name of the servo motor in the phone
        arm1.setDirection(DcMotor.Direction.REVERSE);
        arm2.setDirection(DcMotor.Direction.FORWARD);
        arm1.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        arm2.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        arm1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        arm2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        TrajectorySequence traj1 = drive.trajectorySequenceBuilder(new Pose2d(35.5, -60, Math.toRadians(90)))
                .strafeTo(new Vector2d(23.5, -9))
                .UNSTABLE_addTemporalMarkerOffset(-0.5, () -> arm1.setPower(1))
                .waitSeconds(2)
                .back(5)
                .lineToSplineHeading(new Pose2d(59.5, -12, Math.toRadians(0)))
                .waitSeconds(2)
                .lineToSplineHeading(new Pose2d(30.5, -7, Math.toRadians(135)))
                .waitSeconds(2)
                .back(5)
                .lineToSplineHeading(new Pose2d(59.5, -12, Math.toRadians(0)))
                .waitSeconds(2)
                .lineToSplineHeading(new Pose2d(30.5, -7, Math.toRadians(135)))
                .waitSeconds(2)
                .back(5)
                .lineToSplineHeading(new Pose2d(59.5, -12, Math.toRadians(0)))
                .waitSeconds(2)
                .lineToSplineHeading(new Pose2d(30.5, -7, Math.toRadians(135)))
                .waitSeconds(2)
                .back(5)
                .build();

        waitForStart();

        if(isStopRequested()) return;

        drive.followTrajectorySequence(traj1);

    }
}
