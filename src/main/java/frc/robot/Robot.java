/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.Compressor;

import frc.robot.HatchIntake.HatchState;

import edu.wpi.first.cameraserver.*;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  private static final String kDefaultAuto = "Default";
  private static final String kCustomAuto = "My Auto";
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();

  Joystick guitar = new Joystick(0);
  Joystick vroomStick = new Joystick(1);

  DifferentialDrive myRobot = new DifferentialDrive(new Spark(0), new Spark(2));

  Elevator elevator = new Elevator(4, 6);
  CargoIntake cargoIntake = new CargoIntake(9);
  HatchIntake hatchIntake = new HatchIntake(0, 1);

  Compressor pressorOfCom = new Compressor();

  HatchState hatchState = HatchState.IN;

  /**
   * This function is run when the robot is first started up and should be used
   * for any initialization code.
   */
  @Override
  public void robotInit() {
    CameraServer.getInstance().startAutomaticCapture();
  }

  /**
   * This function is called every robot packet, no matter the mode. Use this for
   * items like diagnostics that you want ran during disabled, autonomous,
   * teleoperated and test.
   *
   * <p>
   * This runs after the mode specific periodic functions, but before LiveWindow
   * and SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
    pressorOfCom.start();
  }

  /**
   * This autonomous (along with the chooser code above) shows how to select
   * between different autonomous modes using the dashboard. The sendable chooser
   * code works with the Java SmartDashboard. If you prefer the LabVIEW Dashboard,
   * remove all of the chooser code and uncomment the getString line to get the
   * auto name from the text box below the Gyro
   *
   * <p>
   * You can add additional auto modes by adding additional comparisons to the
   * switch structure below with additional strings. If using the SendableChooser
   * make sure to add them to the chooser code above as well.
   */
  @Override
  public void autonomousInit() {
    initStuffs();
  }

  /**
   * This function is called periodically during autonomous.
   */
  @Override
  public void autonomousPeriodic() {
    periodicStuffs();
  }

  @Override
  public void teleopInit(){
    initStuffs();
  }

  /**
   * This function is called periodically during operator control.
   */
  @Override
  public void teleopPeriodic() {
    periodicStuffs();
  }

  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
  }

  private void periodicStuffs(){
    double zSpeed = 0.0;
    double ySpeed = 0.0;

    double magicSpeed = 0.6;

    if(guitar.getRawAxis(1) == 1.0){
      zSpeed = magicSpeed;
    }
    else if(guitar.getRawAxis(1) == -1.0){
      zSpeed = -magicSpeed;
    }
    else{
      zSpeed = 0.0;
    }

    if(guitar.getRawButton(1)){
      ySpeed = -magicSpeed;
    }
    else if(guitar.getRawButton(6)){
      ySpeed = magicSpeed;
    }
    else{
      ySpeed = 0.0;
    }

    myRobot.arcadeDrive(zSpeed, ySpeed);

    // Hatch stuff
    if(guitar.getRawAxis(3) > 0.5){
      hatchState = HatchState.OUT;
      hatchIntake.set(hatchState);
    }
    else{
      hatchState = HatchState.IN;
      hatchIntake.set(hatchState);
    }
    SmartDashboard.putBoolean("hatchOut", hatchState == HatchState.OUT ? true : false);

    // Cargo
    if(guitar.getRawButton(4)){
      cargoIntake.outtake();
    }
    else if(guitar.getRawButton(2)){
      cargoIntake.intake();
    }
    else{
      cargoIntake.normal();
    }

    // Elevator
    if(guitar.getRawButton(3)){
      elevator.moveUp();
    }
    else if(guitar.getRawButton(5)){
      elevator.moveDown();
    }
    else{
      elevator.stop();
    }
  }

  private void initStuffs(){
    hatchState = HatchState.IN;
    hatchIntake.set(hatchState);

    SmartDashboard.putBoolean("hatchOut", false);
  }
}
