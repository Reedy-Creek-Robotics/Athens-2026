package org.firstinspires.ftc.teamcode;


import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

@TeleOp(name = "teleop no pedropathing")
public class Teleop  extends OpMode {
    DcMotor rfDrive, lfDrive,rbdrive, lbdrive, intake;
    DcMotorEx outtakewheel;



    @Override
    public void init() {
        initHardware();
    }



    @Override
    public void loop() {
        manageDriving();
        manageDriving();
    }



    private void initHardware() {
        rfDrive = hardwareMap.get(DcMotor.class, "rfDrive");
        lfDrive = hardwareMap.get(DcMotor.class, "lfdrive");
        lbdrive = hardwareMap.get(DcMotor.class, "lbdrive");
        rbdrive = hardwareMap.get(DcMotor.class, "rbdrive" );



        lfDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rfDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        lbdrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rbdrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        lfDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rfDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        lbdrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rbdrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);



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
