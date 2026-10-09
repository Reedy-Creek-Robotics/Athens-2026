package org.firstinspires.ftc.teamcode;


import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;

@TeleOp(name = "teleop no pedropathing")
public class Teleop  extends OpMode {
    DcMotor rfDrive, lfDrive,rbdrive, lbdrive, intake;
    DcMotorEx outtakewheel;

    ElapsedTime buttonDebounce;

    boolean aWasClicked = false;
    double intakespeed = 1.0;
    private boolean yWasClicked = false;


    @Override
    public void init() {
        initHardware();
        buttonDebounce  = new ElapsedTime(ElapsedTime.Resolution.MILLISECONDS);
        buttonDebounce.reset();
    }



    @Override
    public void loop() {
        manageDriving();
        manageTelemetry();
    }



    private void initHardware() {
        rfDrive = hardwareMap.get(DcMotor.class, "rfDrive");
        lfDrive = hardwareMap.get(DcMotor.class, "lfdrive");
        lbdrive = hardwareMap.get(DcMotor.class, "lbdrive");
        rbdrive = hardwareMap.get(DcMotor.class, "rbdrive" );

        lfDrive.setDirection(DcMotorSimple.Direction.REVERSE);
        lbdrive.setDirection(DcMotorSimple.Direction.REVERSE);

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


        outtakewheel = hardwareMap.get(DcMotorEx.class, "outtake");
        outtakewheel.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        outtakewheel.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
    }



    private void manageTelemetry(){
        telemetry.addData("working", true);
        telemetry.update();
    }

    private void manageDriving(){
        double forward =-gamepad1.left_stick_y;
        double right = gamepad1.left_stick_x;
        double rotate = gamepad1.right_stick_x;

        driveBotCentric(forward, right, rotate);

        if(gamepad1.a&&!aWasClicked&&buttonDebounce.milliseconds()>250){
            intake.setPower(intakespeed);
            buttonDebounce.reset();
            aWasClicked = true;
        } else if (gamepad1.a&&aWasClicked&&buttonDebounce.milliseconds()>250) {
            intake.setPower(0);
            buttonDebounce.reset();
            aWasClicked = false;
        }

        if(gamepad1.dpad_up&&buttonDebounce.milliseconds()>250){
            intakespeed+=.05;
            buttonDebounce.reset();
        } else if(gamepad1.dpad_down&&buttonDebounce.milliseconds()>250){
            intakespeed-=.05;
            buttonDebounce.reset();
        }

        if(gamepad1.y&&yWasClicked&&buttonDebounce.milliseconds()>250){
            intake.setPower(intakespeed);
            buttonDebounce.reset();
            yWasClicked=true;
        } else if (gamepad1.y&&yWasClicked&&buttonDebounce.milliseconds()>250) {
            intake.setPower(0);
            buttonDebounce.reset();
            yWasClicked=false;
        }

        if(gamepad1.right_bumper&&buttonDebounce.milliseconds()>250){
            intakespeed+=.05;
            buttonDebounce.reset();
        } else if(gamepad1.left_bumper&&buttonDebounce.milliseconds()>250){
            intakespeed-=.05;
            buttonDebounce.reset();
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
