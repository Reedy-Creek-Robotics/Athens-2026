package org.firstinspires.ftc.teamcode;


import com.pedropathing.follower.Follower;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.pedro.Constants;

@TeleOp(name = "teleop no pedropathing")
public class Teleop  extends OpMode {
    //defining motors and such
    DcMotor rfDrive, lfDrive,rbdrive, lbdrive, intake;
    DcMotorEx outtakewheel;

    CRServo leftIntake, rightIntake, transfer;

    ElapsedTime buttonDebounce;

    boolean aWasClicked = false;
    double intakespeed = 1.0, outtakespeed =2800;

    boolean intake_running = false, outtake_running = false, transfer_running = false;

    private boolean yWasClicked = false;
    private double transfer_power = 0;
    private boolean DpaddDownBefore=false;
    Bot robot;
    Follower follower;


    @Override
    public void init() {
        follower = Constants.create(hardwareMap);

        robot = new Bot(hardwareMap,follower);
        buttonDebounce  = new ElapsedTime(ElapsedTime.Resolution.MILLISECONDS);
        buttonDebounce.reset();
    }



    @Override
    public void loop() {
        manageDriving();
        manageTelemetry();
    }


    private void manageTelemetry(){
        telemetry.addData("working", true);
        telemetry.addData("Intake Speed", intakespeed);
        telemetry.addData("Outtake Speed", robot.getOuttakeSpeed());
        telemetry.addData("Target Outtake", outtakespeed);
        telemetry.update();
    }

    private void manageDriving() {
        double forward = -gamepad1.left_stick_y;
        double right = gamepad1.left_stick_x;
        double rotate = gamepad1.right_stick_x;

        robot.drive(forward, right, rotate);



        if(gamepad1.dpad_down&&buttonDebounce.milliseconds()>250){
            outtakewheel.setVelocity(0);
            transfer.setPower(0);
            intake.setPower(0);
            buttonDebounce.reset();
        }

    }

    private void manageTransfer() {
        if(gamepad1.b){
            transfer_running = true;
            transfer.setPower(-1);
        }else if(gamepad1.y){
            transfer.setPower(1);
            transfer_running=false;
        }else{
            transfer.setPower(0);
        }



    }

    private void manageOuttake() {
        if(gamepad1.a&&buttonDebounce.milliseconds()>250){
            outtake_running = !outtake_running;
            buttonDebounce.reset();
        }
        if(gamepad1.dpadLeftWasPressed()){
            outtakewheel.setVelocity(-outtakespeed);
        }
        if(gamepad1.dpadLeftWasReleased()){
            outtakewheel.setVelocity(0);
        }
        if(gamepad1.left_bumper&&buttonDebounce.milliseconds()>200){
            outtakespeed-=300;
            buttonDebounce.reset();
        }
        if (gamepad1.right_bumper&&buttonDebounce.milliseconds()>200) {
            outtakespeed+=300;
            buttonDebounce.reset();
        }
    }

    private void manageIntake() {
        if(gamepad1.dpad_down&&buttonDebounce.milliseconds()>200&&!DpaddDownBefore){
            intake_running = !intake_running;
            buttonDebounce.reset();
        }
        if(gamepad1.dpadDownWasPressed()){
            intake.setPower(-1);
        } else if (gamepad1.dpadDownWasReleased()) {
            intake.setPower(0);
        }
    }

    private void driveBotCentric(double forward,  double right, double rotate) {
        double frontLeftPower = forward + right + rotate;
        double frontRightPower = forward- right- rotate;
        double backLeftPower = forward- right + rotate;
        double backRightPower = forward + right- rotate;

        lfDrive.setPower(frontLeftPower);
        rfDrive.setPower(frontRightPower);
        lbdrive.setPower(backLeftPower);
        rbdrive.setPower(backRightPower);
    }

}
