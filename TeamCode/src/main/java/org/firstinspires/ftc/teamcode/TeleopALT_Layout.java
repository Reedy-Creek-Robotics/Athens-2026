package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@TeleOp(name = "TeleOp ALT no PP")
public class TeleopALT_Layout extends OpMode {

    // Drive motors
    private DcMotor rfDrive, lfDrive, rbdrive, lbdrive;

    // Mechanism motors
    private DcMotor intake;
    private DcMotorEx outtakewheel;

    // Continuous rotation servos
    private CRServo leftIntake, rightIntake, transfer;

    // Speeds
    private double intakespeed = 0.5;
    private double outtakespeed = 1300;

    // Mechanism states: 0 = off, 1 = forward, -1 = reverse
    private int intakeMode = 0;
    private int transferMode = 0;
    private boolean outtake_running = false;

    // Previous button states
    private boolean dpadDownWasPressed = false;
    private boolean dpadUpWasPressed = false;
    private boolean dpadLeftWasPressed = false;
    private boolean dpadRightWasPressed = false;
    private boolean yWasPressed = false;
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

        double frontLeftPower = forward + right + rotate;
        double frontRightPower = forward - right - rotate;
        double backLeftPower = forward - right + rotate;
        double backRightPower = forward + right - rotate;

        double maxPower = Math.max(1.0,
                Math.max(
                        Math.max(Math.abs(frontLeftPower),
                                Math.abs(frontRightPower)),
                        Math.max(Math.abs(backLeftPower),
                                Math.abs(backRightPower))
                ));

        lfDrive.setPower(frontLeftPower / maxPower);
        rfDrive.setPower(frontRightPower / maxPower);
        lbdrive.setPower(backLeftPower / maxPower);
        rbdrive.setPower(backRightPower / maxPower);
    }

    private void manageIntake() {
        // D-pad down: toggle forward intake
        if (gamepad1.dpad_down && !dpadDownWasPressed) {
            intakeMode = (intakeMode == 1) ? 0 : 1;
        }
        dpadDownWasPressed = gamepad1.dpad_down;

        // D-pad up: toggle reverse intake
        if (gamepad1.dpad_up && !dpadUpWasPressed) {
            intakeMode = (intakeMode == -1) ? 0 : -1;
        }
        dpadUpWasPressed = gamepad1.dpad_up;

        intake.setPower(intakeMode * intakespeed);
        leftIntake.setPower(intakeMode);
        rightIntake.setPower(intakeMode);
    }

    private void manageOuttake() {
        // Y: toggle flywheel
        if (gamepad1.y && !yWasPressed) {
            outtake_running = !outtake_running;
        }
        yWasPressed = gamepad1.y;

        // Left bumper: decrease speed by 50
        if (gamepad1.left_bumper && !leftBumperWasPressed) {
            outtakespeed = Math.max(0, outtakespeed - 50);
        }
        leftBumperWasPressed = gamepad1.left_bumper;

        // Right bumper: increase speed by 50
        if (gamepad1.right_bumper && !rightBumperWasPressed) {
            outtakespeed += 50;
        }
        rightBumperWasPressed = gamepad1.right_bumper;

        // B: reverse flywheel while held
        if (gamepad1.b) {
            outtakewheel.setVelocity(-outtakespeed);
        } else if (outtake_running) {
            outtakewheel.setVelocity(outtakespeed);
        } else {
            outtakewheel.setVelocity(0);
        }
    }

    private void manageTransfer() {
        // D-pad left: toggle forward transfer
        if (gamepad1.dpad_left && !dpadLeftWasPressed) {
            transferMode = (transferMode == 1) ? 0 : 1;
        }
        dpadLeftWasPressed = gamepad1.dpad_left;

        // D-pad right: toggle reverse transfer
        if (gamepad1.dpad_right && !dpadRightWasPressed) {
            transferMode = (transferMode == -1) ? 0 : -1;
        }
        dpadRightWasPressed = gamepad1.dpad_right;

        // Preserve the original transfer direction
        transfer.setPower(-transferMode);
    }

    private void manageEmergencyStop() {
        // X: stop drivetrain and all mechanisms
        if (gamepad1.x) {
            intakeMode = 0;
            transferMode = 0;
            outtake_running = false;

            lfDrive.setPower(0);
            rfDrive.setPower(0);
            lbdrive.setPower(0);
            rbdrive.setPower(0);

            intake.setPower(0);
            outtakewheel.setVelocity(0);
            transfer.setPower(0);
            leftIntake.setPower(0);
            rightIntake.setPower(0);
        }
    }

    private void manageTelemetry() {
        telemetry.addData("Working", true);
        telemetry.addData("Intake Mode", intakeMode);
        telemetry.addData("Transfer Mode", transferMode);
        telemetry.addData("Flywheel Running", outtake_running);
        telemetry.addData("Flywheel Target Velocity", outtakespeed);
        telemetry.addData("Reverse Flywheel", gamepad1.b);
        telemetry.update();
    }

    @Override
    public void stop() {
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