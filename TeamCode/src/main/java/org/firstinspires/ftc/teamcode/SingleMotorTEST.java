package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

@TeleOp
public class SingleMotorTEST extends LinearOpMode {

    private DcMotor motor0;

    private ElapsedTime debounceTimer = new ElapsedTime();
    private static final double debounceTime = 0.05;


    private boolean motorOn = false;
    private double powerLevel = 0.5;

    private boolean lastA = false;

    private boolean lastB = false;
    private boolean lastUp = false;
    private boolean lastDown = false;

    private boolean direction = true;

    @Override
    public void runOpMode() {
        motor0 = hardwareMap.get(DcMotor.class, "motor0");
        motor0.setDirection(DcMotor.Direction.FORWARD);
        motor0.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            boolean currentA = gamepad1.a;
            boolean currentUp = gamepad1.dpad_up;
            boolean currentDown = gamepad1.dpad_down;
            boolean currentB = gamepad1.b;

            if (currentA && !lastA && debounceTimer.seconds() > debounceTime) {
                motorOn = !motorOn;
                debounceTimer.reset();
            }
            if (currentA && !lastB && debounceTimer.seconds() > debounceTime) {
                if (direction){
                    motor0.setDirection(DcMotorSimple.Direction.REVERSE);
                    direction = false;
                }
                else {
                    motor0.setDirection(DcMotorSimple.Direction.FORWARD);
                    direction = true;

                }
                debounceTimer.reset();
            }

            if (currentUp && !lastUp && debounceTimer.seconds() > debounceTime) {
                powerLevel += 0.1;
                debounceTimer.reset();
            }
            if (currentDown && !lastDown && debounceTimer.seconds() > debounceTime) {
                powerLevel -= 0.1;
                debounceTimer.reset();
            }
            powerLevel = Range.clip(powerLevel, 0.0, 1.0);

            if (motorOn) {
                motor0.setPower(powerLevel);
            } else {
                motor0.setPower(0);
            }

            lastA = currentA;
            lastUp = currentUp;
            lastDown = currentDown;
            lastB = currentB;

            telemetry.addData("Motor", motorOn ? "ON" : "OFF");
            telemetry.addData("Power Level", "%.1f", powerLevel);
            telemetry.addData("Direction", direction);
            telemetry.update();
        }
    }
}