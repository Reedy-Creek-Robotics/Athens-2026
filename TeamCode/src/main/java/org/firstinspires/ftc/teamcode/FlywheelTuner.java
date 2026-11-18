package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.PIDCoefficients;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

@TeleOp(name = "Flywheel Tuner")
public class FlywheelTuner extends OpMode {
    public DcMotorEx flywheel;

    public double highVelocity = 1380 ;
    public double lowVelocity=1360;

    public double curTargetVelocity =highVelocity;

    double F=11.643;
    double P=170;


    double[] stepsizes = {10.0,1.0, 0.1,0.01,0.001};


    public DcMotor intake;
    CRServo transfer;

    int stepIndex = 1;
    @Override
    public void init() {
        flywheel = hardwareMap.get(DcMotorEx.class, "outtake");
        flywheel.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        PIDFCoefficients pidf = new PIDFCoefficients(P, 0,0, F);
        flywheel.setPIDFCoefficients(DcMotorEx.RunMode.RUN_USING_ENCODER, pidf);

        transfer = hardwareMap.get(CRServo.class, "transfer");
        intake = hardwareMap.get(DcMotor.class,"intake");
        transfer.setDirection(DcMotorSimple.Direction.REVERSE);
        telemetry.addLine("init COmplete");
    }

    @Override
    public void loop() {
        if(gamepad1.yWasPressed()){
            if(curTargetVelocity==highVelocity){
                curTargetVelocity = lowVelocity;
            }else{
                curTargetVelocity = highVelocity;
            }

        }

        if (gamepad1.bWasPressed()) {
            stepIndex = (stepIndex+1) % stepsizes.length;
        }

        if (gamepad1.dpadLeftWasPressed()){
            F-=stepsizes[stepIndex];
        }
        if (gamepad1.dpadRightWasPressed()){
            F+=stepsizes[stepIndex];
        }
        if (gamepad1.dpadUpWasPressed()){
            P+=stepsizes[stepIndex];
        }
        if (gamepad1.dpadDownWasPressed()){
            P-=stepsizes[stepIndex];
        }

        if (gamepad1.x){
            intake.setPower(-1);
            transfer.setPower(-1);
        }else{
            intake.setPower(0);
            transfer.setPower(0);
        }


        // new pidf
        PIDFCoefficients pidf = new PIDFCoefficients(P, 0,0, F);
        flywheel.setPIDFCoefficients(DcMotorEx.RunMode.RUN_USING_ENCODER, pidf);

        //set velocity

        flywheel.setVelocity(curTargetVelocity);

        double curVelocity = flywheel.getVelocity();
        double error = curTargetVelocity- curVelocity;


        telemetry.addData("Target Velocity", curTargetVelocity);
        telemetry.addData("Current Velocity", "%.2f",curVelocity);
        telemetry.addData("Error", "%.2f",error);
        telemetry.addData("Error", "%.2f", error);
        telemetry.addLine("-----------------------------");
        telemetry.addData("P", "%.4f", P);
        telemetry.addData("F", "%.4f", F);
        telemetry.addData("Step Size", "%.4f", stepsizes[stepIndex]);
        telemetry.update();
    }
}
