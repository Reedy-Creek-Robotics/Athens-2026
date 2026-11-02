package org.firstinspires.ftc.teamcode;

import com.pedropathing.follower.Follower;
import com.pedropathing.revhub.localizers.PinpointLocalizer;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Bot {
    HardwareMap hardwareMap;

    // Drive motors
    public DcMotor rfDrive, lfDrive, rbdrive, lbdrive;

    // Mechanism motors
    private DcMotor intake;
    private DcMotorEx outtakewheel;

    private double intakeSpeed = .5;


    //Follower
    Follower follower;

    // Continuous rotation servos
    private CRServo leftIntake, rightIntake, transfer;
    private double transferSpeed = 1;
    private boolean intakeRunning= false;


    public Bot(HardwareMap hardwareMap){
        this.hardwareMap = hardwareMap;
        initHardware();

    }

    private void initHardware() {
        rfDrive = hardwareMap.get(DcMotor.class, "rfdrive");
        lfDrive = hardwareMap.get(DcMotor.class, "lfdrive");
        rbdrive = hardwareMap.get(DcMotor.class, "rbdrive");
        lbdrive = hardwareMap.get(DcMotor.class, "lbdrive");

        rfDrive.setDirection(DcMotorSimple.Direction.REVERSE);
        rbdrive.setDirection(DcMotorSimple.Direction.REVERSE);

        lfDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rfDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        lbdrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rbdrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        lfDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rfDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        lbdrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rbdrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        intake = hardwareMap.get(DcMotor.class, "intake");
        intake.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        intake.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        intake.setDirection(DcMotorSimple.Direction.REVERSE);

        outtakewheel = hardwareMap.get(DcMotorEx.class, "outtake");
        outtakewheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        outtakewheel.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        rightIntake = hardwareMap.get(CRServo.class, "rightIntake");
        leftIntake = hardwareMap.get(CRServo.class, "leftIntake");
        transfer = hardwareMap.get(CRServo.class, "transfer");

        transfer.setDirection(DcMotorSimple.Direction.REVERSE);
        leftIntake.setDirection(DcMotorSimple.Direction.REVERSE);




    }


    public void run_outtake(float speed){
        outtakewheel.setVelocity(speed);
    }

    public void run_intake(boolean reverse){
        intake.setPower(reverse? (-intakeSpeed):intakeSpeed);
        intakeRunning=true;
    }

    public void run_transfer(boolean reverse){
        transfer.setPower(reverse? (-transferSpeed):transferSpeed);
    }
    
    public void stop_transfer(){
        transfer.setPower(0);
    }
    public void stop_intake(){
        intake.setPower(0);
        intakeRunning=false;
    }

    public void drive(double forward,  double right, double rotate){
        double frontLeftPower = forward + right + rotate;
        double frontRightPower = forward- right- rotate;
        double backLeftPower = forward- right + rotate;
        double backRightPower = forward + right- rotate;

        lfDrive.setPower(frontLeftPower);
        rfDrive.setPower(frontRightPower);
        lbdrive.setPower(backLeftPower);
        rbdrive.setPower(backRightPower);
    }


    public double getOuttakeSpeed(){
        return outtakewheel.getVelocity();
    }


    public boolean isIntakeRunning() {
        return false;
    }
}
