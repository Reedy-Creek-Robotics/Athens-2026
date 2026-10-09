package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;


@TeleOp
public class SuperTEST extends LinearOpMode{

    private DcMotor motor0;

    @Override
    public void runOpMode() {

        motor0 = hardwareMap.get(DcMotor.class, "motor0");

        int motorPower;

        waitForStart();

        while (opModeIsActive()){
            if (gamepad1.a){
                motorPower  = 1;
            }
            else {
                motorPower = 0;
            }


            motor0.setPower(motorPower);


        }
    }


}