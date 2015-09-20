package io.alatalab.glassbead.state;

import java.util.*;
import java.awt.Robot;
import java.text.DateFormat;

import com.chroneus.atarigo.Board;

import processing.core.PApplet;
import themidibus.ControlChange;
import themidibus.MidiBus;
import themidibus.Note;

public class GameState  {

    
	static long previous_move_time=System.currentTimeMillis();
	static int midichannel, midibusname;
	static PApplet mainApp;
	static MidiBus speedBus = new MidiBus(mainApp,0,1);
	static MidiBus complicationBus = new MidiBus(mainApp,1,2);
	static MidiBus newMoveBus = new MidiBus(mainApp,2,3);
	static MidiBus resultBus = new MidiBus(mainApp,3,4);
	static Robot robot;
	static long previous_difference=10;
	static int bpm = 115;
	
    static{
	try{
		robot = new Robot();
	}
	catch(Exception e){
		e.printStackTrace();
	}
    }
	
	public static void onGameOver(){
		System.out.println("gameover");
		sendMidiMessage(resultBus,0, 13, 127,7000);
		sendMidiMessage(resultBus,0, 14, 127);
		

	}
	
	
	public static void onNewGame(){
		System.out.println("newgame");
	//	sendKey(0x30);
		
		sendMidiMessage(newMoveBus,1, 12, 127);

	}
	
	public static void onMove(int col, int row, int[][] cells){
		getSpeed(System.currentTimeMillis());
		//TODO complication
		int complication=0;
		for (int i = 0; i < cells.length; i++) {
			for (int j = 0; j < cells.length; j++) {
				if(cells[i][j]>0) complication++;
			}
		}
		complication /= 2 ;
		if(complication>16) complication =16;
		newMoveBus.sendControllerChange(new ControlChange(1,complication,  127));
		
	}
	public static int getSpeed(long now){
		System.out.println("next move at:"+DateFormat.getTimeInstance().format(new Date(now)));
		long difference=(now-previous_move_time)/1000;
		previous_move_time = now;
		if(previous_difference> difference )
			bpm+=10;
		
	//		sendMidiMessage(speedBus,0, 10, bpm);
		previous_difference = difference;
		System.out.println("bpm:"+bpm);
		return bpm;
	}
	
	public static void playRandomNote(MidiBus myBus,int channel){
		
		Note note = new Note(channel, (int)Math.random()*88, 100);
		myBus.sendNoteOn(note);
		mainApp.delay(200);
		myBus.sendNoteOff(note);
		
	}
	
	public static int getComplication(int number_of_stones){
		return (int) (3*Math.round(10*Math.log10(number_of_stones)));
	}
	 
	public static int movesToEnd(Board board){
		return 0;
	}
	
	public static void sendMidiMessage(MidiBus myBus,int channel,int number,int value){
		myBus.sendControllerChange(new ControlChange(channel, number, value));
	}
	
	public static void sendMidiMessage(MidiBus myBus,int channel,int number,int value,int delay){
		
		myBus.sendControllerChange(new ControlChange(channel, number, value));
	
	}

public static void sendKey(int key) {	 	   
    robot.keyPress( key);
    robot.delay(100);
}
	public static void sendEval(int bestEval) {
		
		System.out.println(bestEval);
		
	}


	
	
	
	void delay(int time) {
		  int current = mainApp.millis();
		  while (mainApp.millis() < current+time) Thread.yield();
}
}
