package com.chroneus.atarigo;

import gab.opencv.Contour;
import gab.opencv.OpenCV;
import io.alatalab.glassbead.BaseApplet;
import io.alatalab.glassbead.Props;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;


public class AtariGoApplet extends BaseApplet{
   static int master_volume = 0;
	public Random rand = new Random();
  static int center_x,center_y, real_contour=0;
	int camera_width =Integer.valueOf(Props.getValue("log_camera_width"));
	int camera_height =Integer.valueOf(Props.getValue("log_camera_height"));
	
  static int state =1;
	boolean[][] markerCells;
	Integer[] rows = new Integer[]{1,2,3,4,5,6,7,8,9,10,11};
	public long last_time_played=System.currentTimeMillis();

	static Queue<Integer> rows_played = new LinkedList<Integer>();
	public	void setup() {
		//size(camera_width, camera_height);
		size (20,20);
		
		    

		  }
		

	public void muteRow() {
		if(!rows_played.isEmpty()){
		sendMidiMessage(newMoveBus, 2, 113+rows_played.poll(), 127);
		}else{
			stopAll();
			System.out.println("stopped");
		}
		
		
	}

	public void draw() {
		
	}

	

	@Override
	public void startAll() {
	//playColumn(1);
		fadeIn(960);
	}
	@Override
	public void muteRow(int i) {
		sendMidiMessage(newMoveBus, 2, 113+i, 127);
		
	}
	@Override
	public void playColumn(int i) {
		sendMidiMessage(newMoveBus, 3, 2+i, 127);
		
	}
	@Override
	public void playRow(int i) {
		rows_played.add(i);
		switch (i) {
		case 1:
			sendMidiMessage(newMoveBus, 2, 0 + rand.nextInt(16), 127);
		case 2:
			sendMidiMessage(newMoveBus, 2, 17 + rand.nextInt( 12), 127);
		case 3:
			sendMidiMessage(newMoveBus, 2, 29 + rand.nextInt( 15), 127);
		case 4:
			sendMidiMessage(newMoveBus, 2, 44 + rand.nextInt(15), 127);
		case 5:
			sendMidiMessage(newMoveBus, 2, 59 + rand.nextInt(20), 127);
		case 8:
			sendMidiMessage(newMoveBus, 2, 80 +rand.nextInt( 5), 127);
		case 9:
			sendMidiMessage(newMoveBus, 2, 85 + rand.nextInt( 8), 127);
		case 10:
			sendMidiMessage(newMoveBus, 2, 94+ rand.nextInt( 5), 127);
		}

	}
	
	public void mute(){
		sendMidiMessage(complicationBus, 2, 2, 0);
		master_volume=0;
	}
	public void fadeIn  (int msec){
		int parts = 16,max=128;
		System.out.println("fadein");
		for(int i=0;i<parts;i++){

		master_volume = i*128/parts;
		sendMidiMessage(complicationBus, 2, 2,  master_volume,msec/parts);
		}
	}
	
	public void fadeOut  (int msec){
		int parts = 16,max=128;
		if(master_volume<1)return;
		for(int i=parts;i>=0;i--){
			master_volume = i*128/parts;
		sendMidiMessage(complicationBus, 2, 2,master_volume,msec/parts);
		}
		System.out.println("fadeout");
	}
	
	public void stopAll() {
		fadeOut(200);
		sendMidiMessage(newMoveBus, 2, 125, 127);
		
	}

    

	   public void youHalfLoose(){
		   sendMidiMessage(resultBus, 2, 1, 127);
	    }
	
	   public void youLoose(){
		   System.out.println("you loose");
		   
	    	sendMidiMessage(resultBus, 1, 1, 127,1000);
	    	stopAll();
	    }
	
}
