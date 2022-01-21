package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;

@Autonomous(name = "AutonomousTestJava")
public class AutonomousTestJava extends LinearOpMode {
    private DcMotor RightDrive;
    private DcMotor LeftDrive;
    private DcMotor Lift;
    private DcMotor Pusher;

    //Convert from the counts per revolution of the encoder to counts per inch
    static final double HD_COUNTS_PER_REV = 28;
    static final double DRIVE_GEAR_REDUCTION = 40;
    static final double WHEEL_CIRCUMFERENCE_MM = 90 * Math.PI;
    static final double DRIVE_COUNTS_PER_MM = (HD_COUNTS_PER_REV * DRIVE_GEAR_REDUCTION) / WHEEL_CIRCUMFERENCE_MM;
    static final double DRIVE_COUNTS_PER_IN = DRIVE_COUNTS_PER_MM * 25.4;

    //Create elapsed time variable and an instance of elapsed time
    private ElapsedTime runtime = new ElapsedTime();

    // Drive function with 3 parameters
    private void drive(double power, double leftInches, double rightInches) {
        int rightTarget;
        int leftTarget;

        if (opModeIsActive()) {
            // Create target positions
            rightTarget = RightDrive.getCurrentPosition() + (int)(rightInches * DRIVE_COUNTS_PER_IN);
            leftTarget  = LeftDrive.getCurrentPosition() + (int)(leftInches * DRIVE_COUNTS_PER_IN);

            // set target position
            LeftDrive.setTargetPosition(leftTarget);
            RightDrive.setTargetPosition(rightTarget);

            //switch to run to position mode
            LeftDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            RightDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            //run to position at the desiginated power
            LeftDrive.setPower(power);
            RightDrive.setPower(power);

            // wait until both motors are no longer busy running to position
            while (opModeIsActive() && (LeftDrive.isBusy() || RightDrive.isBusy())) {
            }

            // set motor power back to 0
            RightDrive.setPower(0);
            LeftDrive.setPower(0);
        }
    }

    private void lift(double power, double position) {
        int Target;

        if (opModeIsActive()) {
            // Create target positions
            Target = Lift.getCurrentPosition() + (int)(position);
            // set target position
            Lift.setTargetPosition(Target);

            //switch to run to position mode
            Lift.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            //run to position at the desiginated power
            Lift.setPower(power);

            // wait until both motors are no longer busy running to position
            while (opModeIsActive() && (Lift.isBusy() )) {
            }

            // set motor power back to 0
            Lift.setPower(0);
        }
    }
    @Override
    public void runOpMode() {

        RightDrive = hardwareMap.get(DcMotor.class, "RightDrive");
        LeftDrive = hardwareMap.get(DcMotor.class, "LeftDrive");
        Lift = hardwareMap.get(DcMotor.class, "Lift");
        Pusher = hardwareMap.get(DcMotor.class, "Pusher");


        LeftDrive.setDirection(DcMotorSimple.Direction.REVERSE);
        //RightDrive.setDirection(DcMotorSimple.Direction.REVERSE);

        waitForStart();
        if (opModeIsActive()) {
            telemetry.addData("LMotorPosition", LeftDrive.getCurrentPosition());
            telemetry.addData("RMotorPosition", RightDrive.getCurrentPosition());
            //segment 1
            drive(-0.7, -110, -110);
            lift(.3, 1);
            runtime.reset(); // reset elapsed time timer


        }
    }
}