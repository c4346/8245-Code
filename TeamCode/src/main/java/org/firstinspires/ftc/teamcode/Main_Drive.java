/* Copyright (c) 2017 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import java.lang.Math;
import java.util.HashMap;


/**
 * This file contains an minimal example of a Linear "OpMode". An OpMode is a 'program' that runs in either
 * the autonomous or the teleop period of an FTC match. The names of OpModes appear on the menu
 * of the FTC Driver Station. When an selection is made from the menu, the corresponding OpMode
 * class is instantiated on the Robot Controller and executed.
 *
 * This particular OpMode just executes a basic Tank Drive Teleop for a two wheeled robot
 * It includes all the skeletal structure that all linear OpModes contain.
 *
 * Use Android Studios to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this opmode to the Driver Station OpMode list
 */

@TeleOp(name="Basic: Linear OpMode", group="Linear Opmode")
//@Disabled
public class Main_Drive extends LinearOpMode {

    // Declare OpMode members.
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor leftDrive;
    private DcMotor rightDrive;
    private DcMotor elevation;
    private DcMotor pusher;

    private double maxPower = 100d;
    private double interceptPower = 100d;

    private double leftBias = 0d;
    private double rightBias = 0d;
    private double elevationBias = 0d;
    private double pushBias = 0d;

    // Bias curve function modified from 7:36 of https://www.youtube.com/watch?v=lctXaT9pxA0.
    // A graph of this function can be found at https://www.geogebra.org/graphing/wqbhnntr.
    // DON'T FEED IN NEGATIVE BIAS IT WILL BREAK (will only output values greater than 1).
    private double biasCurve(double x, double bias, double max, double intercept) {
        /*
        x: variable plugged into the equation, e.g. the value of a gamestick.
        bias: the bias value, how fast x approaches max.
        max: the maximum value x will reach.
        intercept: the value at which x = max.
         */

        // IDK what this does, think it makes bias value appear more exponential.
        double k = Math.pow( (1 - bias), 3d );
        // Biased values mapped between 0 and max, with max value being reached at intercept.
        // Returns value, inverts if x < 0.
        if (x >= 0) {
            return ((x * k) / (x * k - x + intercept)) * max;
        }
        else {
            return ((-x * k) / (-x * k + x + intercept)) * -max;
        }
    }

    @Override
    public void runOpMode() {
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        // Initialize the hardware variables. Note that the strings used here as parameters
        // to 'get' must correspond to the names assigned during the robot configuration
        // step (using the FTC Robot Controller app on the phone).
        leftDrive  = hardwareMap.get(DcMotor.class, "f left");
        rightDrive = hardwareMap.get(DcMotor.class, "f right");
        elevation = hardwareMap.get(DcMotor.class, "elevation");
        pusher = hardwareMap.get(DcMotor.class, "push");

        // Most robots need the motor on one side to be reversed to drive forward
        // Reverse the motor that runs backwards when connected directly to the battery
        leftDrive.setDirection(DcMotor.Direction.FORWARD);
        rightDrive.setDirection(DcMotor.Direction.REVERSE);

        // Wait for the game to start (driver presses PLAY)
        waitForStart();
        runtime.reset();

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {

            // Setup a variable for each drive wheel to save power level for telemetry
            // Tank Mode uses one stick to control each wheel.
            // This requires no math, but it is hard to drive forward slowly and keep straight.
            double leftPower = gamepad1.left_stick_y;
            double rightPower = gamepad1.right_stick_y;
            double elevationPower = gamepad2.right_stick_y * -1;
            double pushPower = gamepad2.left_stick_y * -1;

            // Send calculated power to wheels
            leftDrive.setPower( biasCurve(leftPower, leftBias, maxPower, interceptPower));
            rightDrive.setPower(biasCurve(rightPower, rightBias, maxPower, interceptPower));
            elevation.setPower(biasCurve(elevationPower, elevationBias, maxPower, interceptPower));
            pusher.setPower(biasCurve(pushPower, pushBias, maxPower, interceptPower));


            // Show the elapsed game time and wheel power.
            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.addData("Motors", "left (%.2f), right (%.2f)", leftPower, rightPower);
            telemetry.addData("gamepad", "left(%.2f), right (.2f)" , gamepad1.left_stick_y, gamepad1.right_stick_y);
            telemetry.addData("elv and push", "elc (%.2f), right (%.2f)", elevationPower, pushPower);
            telemetry.update();
        }
    }
}
