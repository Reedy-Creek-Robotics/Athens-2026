package org.firstinspires.ftc.teamcode.pedro;

import com.pedropathing.algorithm.Foresight;
import com.pedropathing.algorithm.ForesightConfig;
import com.pedropathing.controllers.Controller;
import com.pedropathing.follower.Follower;
import com.pedropathing.math.Matrix;
import com.pedropathing.math.Vector2D;
import com.pedropathing.revhub.drivetrains.Mecanum;
import com.pedropathing.revhub.drivetrains.MecanumConfig;
import com.pedropathing.revhub.localizers.PinpointConfig;
import com.pedropathing.revhub.localizers.PinpointLocalizer;
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class Constants {
    public static Follower create(HardwareMap h) {
        return new Follower(
                new PinpointLocalizer(h, localizerConfig),
                new Mecanum(h, drivetrainConfig),
                new Foresight(foresightConfig)
        );
    }


    public static MecanumConfig drivetrainConfig = new MecanumConfig(c -> {
        c.frontLeftName.set("lfdrive");
        c.frontRightName.set("rfdrive");
        c.backLeftName.set("lbdrive");
        c.backRightName.set("rbdrive");
        c.frontLeftDirection.set(DcMotorSimple.Direction.FORWARD);
        c.frontRightDirection.set(DcMotorSimple.Direction.REVERSE);
        c.backLeftDirection.set(DcMotorSimple.Direction.FORWARD);
        c.backRightDirection.set(DcMotorSimple.Direction.REVERSE);
    });

    public static PinpointConfig localizerConfig = new PinpointConfig(c -> {
        c.name.set("pinpoint");
        c.podType.set(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD);
        c.xPodOffset.set(-2.7432965856837477);
        c.yPodOffset.set(6.834185742956447);
        c.xPodDirection.set(GoBildaPinpointDriver.EncoderDirection.FORWARD);
        c.yPodDirection.set(GoBildaPinpointDriver.EncoderDirection.REVERSED);
        c.globalDistanceUnit.set(DistanceUnit.INCH);
        c.offsetUnits.set(DistanceUnit.INCH);
    });

    public static ForesightConfig foresightConfig = new ForesightConfig(
            c -> {
                Controller primaryTranslationalForward = Controller.proportional(0.22745637048958595);
                Controller secondaryTranslationalForward = Controller.proportional(0.08403905114974707);
                Controller primaryTranslationalLateral = Controller.proportional(0.3395736601845693);
                Controller secondaryTranslationalLateral = Controller.proportional(0.12546339386288777);

                c.forwardTranslational.set(Controller.piecewise(secondaryTranslationalForward).put(2.5, primaryTranslationalForward));
                c.strafeTranslational.set(Controller.piecewise(secondaryTranslationalLateral).put(2.5, primaryTranslationalLateral));

                c.coast.set(Controller.proportionalFeedforward(0.017638949636479676));
                c.brake.set(Controller.proportionalFeedforward(0.014993107191007724));

                c.headingFeedback.set(Controller.proportional(4.416645498602919));
                c.headingBrakeCoefficients.set(Vector2D.cartesian(0.056666928408123575, 0.004263104864253936));

                c.linearBrakeCoefficients.set(Matrix.diag(0.0859850425513847, 0.07285978401737159));
                c.quadraticBrakeCoefficients.set(Matrix.diag(8.16995367866692E-4, 0.0014973711814392505));

                c.maxAchievableForwardVelocity.set(58.82455637676143);
                c.maxAchievableStrafeVelocity.set(52.34335735237096);
                c.naturalForwardDeceleration.set(29.707374568238738);
                c.naturalStrafeDeceleration.set(55.483546100737286);
            }
    );
}
