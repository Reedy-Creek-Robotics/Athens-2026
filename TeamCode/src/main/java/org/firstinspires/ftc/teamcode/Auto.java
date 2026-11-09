package org.firstinspires.ftc.teamcode;

import static com.pedropathing.api.Paths.curve;
import static com.pedropathing.api.Paths.line;
import static com.pedropathing.ivy.Scheduler.schedule;
import static com.pedropathing.ivy.commands.Commands.conditional;
import static com.pedropathing.ivy.commands.Commands.instant;
import static com.pedropathing.ivy.commands.Commands.waitMs;
import static com.pedropathing.ivy.groups.Groups.sequential;
import static com.pedropathing.ivy.pedro.PedroCommands.follow;
import static com.pedropathing.paths.interpolator.Interpolator.constant;
import static com.pedropathing.paths.interpolator.Interpolator.linear;
import static com.pedropathing.paths.interpolator.Interpolator.piecewise;

import com.pedropathing.api.Paths;
import com.pedropathing.api.PoseFactory;
import com.pedropathing.follower.Follower;
import com.pedropathing.ivy.Command;
import com.pedropathing.ivy.CommandBuilder;
import com.pedropathing.ivy.Scheduler;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;
import com.pedropathing.paths.interpolator.Interpolator;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.robot.Robot;

import org.firstinspires.ftc.teamcode.pedro.Constants;

public class Auto extends OpMode {
    Bot robot;

    int targetOuttake = 1500;

    private final PoseFactory poseFactory = PoseFactory.degrees();

    private final Pose start = poseFactory.of(56, 8, 90);
    private final Pose launch1 = poseFactory.of(56, 36, 90);
    private final Pose pickupSeg1 = poseFactory.of(8.7, 14.8469, -90);
    private final Pose pickupSeg1Segment1Start = poseFactory.of(8.7, 14.8469, 90);
    private final Pose pickupSeg1Segment1End = poseFactory.of(8.7, 14.8469, 270);
    private final Pose pickupSeg1Segment2Heading = poseFactory.of(8.7, 14.8469, -90);
    private final Pose pickupSeg2 = poseFactory.of(8.7, 9.3, 270);
    private final Pose launch2Start = poseFactory.of(8.7, 9.3, 270);
    private final Pose launch2 = poseFactory.of(56, 108, 270);
    private final Pose launch2Control1 = poseFactory.of(78, 7.5, 0);
    private Follower follower;

    private int  autoState = 0;


    @Override
    public void init() {
        robot = new Bot(hardwareMap);
        Scheduler.reset();
        follower = Constants.create(hardwareMap);
        follower.setPose(start);
        follower.update();
    }

    @Override
    public void start() {
        schedule(autoroutine());
    }

    @Override
    public void loop() {
        Scheduler.execute();
        follower.update();
        telemetry.addData("x", follower.pose().x());
        telemetry.addData("y", follower.pose().y());
        telemetry.addData("heading", follower.pose().heading());

        if (follower.currentPath() != null) {
            telemetry.addData("Current path distance remaining", follower.distanceToEndpoint());
            telemetry.addData("Path number", follower.pathIndex());
        }
        telemetry.update();
    }

    public Path launch1() {
        return line(start, launch1).linear(start, launch1);
    }

    public Path pickupSeg1() {
        return line(launch1, pickupSeg1).heading(piecewise()
                .until(0.2045, linear(pickupSeg1Segment1Start, pickupSeg1Segment1End))
                .until(1, constant(pickupSeg1Segment2Heading)));
    }

    public Path pickupSeg2() {
        return line(pickupSeg1, pickupSeg2).constant(pickupSeg2);
    }

    public Path launch2() {
        return curve(launch2Start, launch2Control1, launch2).linear(launch2Start, launch2);
    }

    public Command runOuttakeWheel(int speed){
        return Command.build()
                .setStart(()->
                        robot.run_outtake(speed)
                )
                .setDone(()->Math.abs(robot.getOuttakeSpeed()-speed) < 10)
                .requiring(robot);
    }

    public Command manageIntake(){
        return conditional(
                () -> robot.isIntakeRunning(),
                instant(() -> robot.stop_intake()),
                instant(()-> robot.run_intake(false))
        );

    }
    public Command runTransfer(){
        return Command.build()
                .setStart(()->
                        robot.run_transfer(false)
                )
                .setDone(()->true)
                .requiring(robot);
    }

    public Command outtake() {
        return sequential(
                runOuttakeWheel(targetOuttake),
                runTransfer()
        );
    }


    public Command autoroutine(){
        return sequential(
                follow(follower, launch1()),
                outtake(),
                waitMs(5000),
                instant(()->robot.stop_transfer()),
                instant(()->robot.run_outtake(0)),
                follow(follower, pickupSeg1()),
                manageIntake(),
                follow(follower,pickupSeg2()),
                waitMs(1000),
                manageIntake(),
                follow(follower, launch2()),
                outtake(),
                waitMs(5000),
                instant(()->robot.stop_transfer()),
                instant(()->robot.run_outtake(0))
        ).requiring(follower);
    }

}
