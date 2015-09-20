package com.chroneus.atarigo;

import io.alatalab.glassbead.BaseApplet;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Queue;
import java.util.Random;

import gab.opencv.Contour;
import gab.opencv.OpenCV;
import processing.event.MouseEvent;
import processing.video.Capture;

public class GoMokuApplet extends BaseApplet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Random rand = new Random();
  static int state =1;
	boolean[][] markerCells;
	Integer[] rows = new Integer[]{1,2,3,4,5,6,7,8,9,10,11};
	public long last_time_played=System.currentTimeMillis();

	public	void setup() {
		
		    size(200,200);
		    youLoose();

		  }
		


	public void draw() {
		    
	}

	@Override
	public void stopAll() {
		sendMidiMessage(newMoveBus, 2, 20, 127);
		
	}

	@Override
	public void startAll() {
	//playColumn(1);
		
	}

	@Override
	public void muteRow(int i) {
		
		sendMidiMessage(newMoveBus, 2, 20+i, 127);
	}

	

	@Override
	public void playColumn(int i) {
		System.out.println("played "+i);
		sendMidiMessage(newMoveBus, 2, i, 127);
		
	}
	
    public void youLoose(){
    	System.out.println("you loose");
    	sendMidiMessage(newMoveBus, 2, 19, 127);
    	stopAll();
    }



	@Override
	public void playRow(int i) {
		// TODO Auto-generated method stub
		
	}
}
