
package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@TeleOp(name = "TeleOp No PedroPathing")
public class teleopGemini extends OpMode {

    // Drive motors
    private DcMotor rfDrive, lfDrive, rbdrive, lbdrive;

    // Mechanism motors
    private DcMotor intake;
    private DcMotorEx outtakewheel;

    // Continuous rotation servos
    private CRServo leftIntake, rightIntake, transfer;

    // Speeds
    private double intakespeed = 0.5;
    private double outtakespeed = 2800;

    // Toggle states
    private boolean intake_running = false;
    private boolean outtake_running = false;

    // Previous button states for detecting button presses
    private boolean aWasPressed = false;
    private boolean dpadUpWasPressed = false;
    private boolean leftBumperWasPressed = false;
    private boolean rightBumperWasPressed = false;

    @Override
    public void init() {
        initHardware();
        telemetry.addLine("TeleOp initialized");
        telemetry.update();
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

    @Override
    public void loop() {
        manageDriving();
        manageIntake();
        manageOuttake();
        manageTransfer();
        manageEmergencyStop();
        manageTelemetry();
    }

    private void manageDriving() {
        double forward = -gamepad1.left_stick_y;
        double right = gamepad1.left_stick_x;
        double rotate = gamepad1.right_stick_x;

        driveBotCentric(forward, right, rotate);
    }

    private void driveBotCentric(
            double forward, double right, double rotate) {

        double frontLeftPower = forward + right + rotate;
        double frontRightPower = forward - right - rotate;
        double backLeftPower = forward - right + rotate;
        double backRightPower = forward + right - rotate;

        // Normalize powers to keep them within [-1, 1]
        double maxPower = Math.max(
                1.0,
                Math.max(
                        Math.max(Math.abs(frontLeftPower),
                                Math.abs(frontRightPower)),
                        Math.max(Math.abs(backLeftPower),
                                Math.abs(backRightPower))
                )
        );

        lfDrive.setPower(frontLeftPower / maxPower);
        rfDrive.setPower(frontRightPower / maxPower);
        lbdrive.setPower(backLeftPower / maxPower);
        rbdrive.setPower(backRightPower / maxPower);
    }

    private void manageIntake() {
        // D-pad up toggles the intake on/off
        if (gamepad1.dpad_up && !dpadUpWasPressed) {
            intake_running = !intake_running;
        }
        dpadUpWasPressed = gamepad1.dpad_up;

        // D-pad down reverses the intake while held
        if (gamepad1.dpad_down) {
            intake.setPower(-0.5);
        } else if (intake_running) {
            intake.setPower(intakespeed);
            leftIntake.setPower(1);
            rightIntake.setPower(1);
        } else {
            intake.setPower(0);
            leftIntake.setPower(0);
            rightIntake.setPower(0);
        }
    }

    private void manageOuttake() {
        // A toggles the outtake on/off
        if (gamepad1.a && !aWasPressed) {
            outtake_running = !outtake_running;
        }
        aWasPressed = gamepad1.a;

        // Bumpers adjust the target velocity once per press
        if (gamepad1.left_bumper && !leftBumperWasPressed) {
            outtakespeed = Math.max(0, outtakespeed - 150);
        }
        leftBumperWasPressed = gamepad1.left_bumper;

        if (gamepad1.right_bumper && !rightBumperWasPressed) {
            outtakespeed += 150;
        }
        rightBumperWasPressed = gamepad1.right_bumper;

        // D-pad left reverses the outtake while held
        if (gamepad1.dpad_left) {
            outtakewheel.setVelocity(-outtakespeed);
        } else if (outtake_running) {
            outtakewheel.setVelocity(outtakespeed);
        } else {
            outtakewheel.setVelocity(0);
        }
    }

    private void manageTransfer() {
        // B runs transfer while held; Y stops it
        if (gamepad1.b && !gamepad1.y) {
            transfer.setPower(-1.0);
        } else if(gamepad1.y){
            transfer.setPower(1.0);

        } else {
            transfer.setPower(0);
        }
    }

    private void manageEmergencyStop() {
        // X stops all mechanisms and clears toggle states
        if (gamepad1.x) {
            intake_running = false;
            outtake_running = false;

            intake.setPower(0);
            outtakewheel.setVelocity(0);
            transfer.setPower(0);
            leftIntake.setPower(0);
            rightIntake.setPower(0);
        }
    }

    private void manageTelemetry() {
        telemetry.addData("Working", true);
        telemetry.addData("Intake Running", intake_running);
        telemetry.addData("Intake Speed", intakespeed);
        telemetry.addData("Outtake Running", outtake_running);
        telemetry.addData("Outtake Target Velocity", outtakespeed);
        telemetry.addData("Transfer Active",
                gamepad1.b && !gamepad1.y);
        telemetry.update();
    }

    @Override
    public void stop() {
        // Stop all motors and servos when OpMode ends
        lfDrive.setPower(0);
        rfDrive.setPower(0);
        lbdrive.setPower(0);
        rbdrive.setPower(0);

        intake.setPower(0);
        outtakewheel.setVelocity(0);

        leftIntake.setPower(0);
        rightIntake.setPower(0);
        transfer.setPower(0);
    }
}
