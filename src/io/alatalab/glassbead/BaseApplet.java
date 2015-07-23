package io.alatalab.glassbead;
import gab.opencv.*;

import java.util.*;

import processing.core.*;
import processing.video.Capture;
import themidibus.ControlChange;
import themidibus.MidiBus;
import processing.video.*;


public abstract class BaseApplet extends PApplet {
	

	
	public Capture cam;
	public OpenCV opencv;
	public MidiBus speedBus = new MidiBus(this,0,1);
	public MidiBus complicationBus = new MidiBus(this,1,2);
	public MidiBus newMoveBus = new MidiBus(this,2,3);
	public  MidiBus resultBus = new MidiBus(this,3,4);
	

	 Queue<Integer> lines_played = new LinkedList<Integer>();  
	


   public abstract void stopAll();
   public abstract void startAll();
   public abstract void muteRow(int i);
	public  void sendMidiMessage(MidiBus myBus,int channel,int number,int value){
		myBus.sendControllerChange(new ControlChange(channel, number, value));
	}
	
	public  void sendMidiMessage(MidiBus myBus,int channel,int number,int value,int delay){
		
		myBus.sendControllerChange(new ControlChange(channel, number, value));
		delay(delay);
	
	}
	public void delay(int time) {
		  int current = millis();
		  while (millis () < current+time) Thread.yield();
		}

	@Override
	public void start() {
		stopAll();
		System.out.println("starting");
		startAll();
		super.start();
	}
	
	
@Override
public void destroy() {
	stopAll();
	super.destroy();
	
	
}

}
