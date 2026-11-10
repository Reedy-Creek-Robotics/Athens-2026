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

import com.pedropathing.api.PoseFactory;
import com.pedropathing.follower.Follower;
import com.pedropathing.ivy.Command;
import com.pedropathing.ivy.Scheduler;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.pedro.Constants;

public class Auto extends OpMode {
    Bot robot;

    int targetOuttake = 1500;

    private final PoseFactory poseFactory = PoseFactory.degrees();

    private final Pose start = poseFactory.of(56, 8, 270);
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

    @Override
    public void init() {
        Scheduler.reset();
        follower = Constants.create(hardwareMap);
        follower.setPose(start);
        follower.update();
        robot = new Bot(hardwareMap, follower);
    }

    @Override
    public void start() {
        schedule(autoRoutine());
    }

    @Override
    public void loop() {
        follower.update();
        Scheduler.execute();
        telemetry.addData("x", follower.pose().x());
        telemetry.addData("y", follower.pose().y());
        telemetry.addData("heading", follower.pose().heading());
        telemetry.addData("outtake speed", robot.getOuttakeSpeed());
        telemetry.addData("outtake deviation", Math.abs(robot.getOuttakeSpeed()-targetOuttake));

        if (follower.currentPath() != null) {
            telemetry.addData("Current path distance remaining", follower.distanceToEndpoint());
            telemetry.addData("Path number", follower.pathIndex());
        }
        telemetry.update();
    }

    public Path pickupSeg1() {
        return line(start, pickupSeg1).heading(piecewise()
                .until(0.2045, linear(start, pickupSeg1Segment1End))
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
                .setDone(()->Math.abs(robot.getOuttakeSpeed()-speed) < 40)
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
                runTransfer(),
                waitMs(5000),
                instant(()->robot.stop_transfer()),
                instant(()->robot.run_outtake(0))
        );
    }

    public Command autoRoutine(){
        return sequential(
                outtake(),
                follow(follower, pickupSeg1()),
                manageIntake(),
                follow(follower,pickupSeg2()),
                waitMs(1000),
                manageIntake(),
                follow(follower, launch2()),
                outtake()
        ).requiring(follower, robot);
    }
}
