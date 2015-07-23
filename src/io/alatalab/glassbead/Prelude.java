package io.alatalab.glassbead;

import io.alatalab.glassbead.*;
import gab.opencv.*;

import org.opencv.imgproc.Imgproc;
import org.opencv.core.Core;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.*;

import processing.core.*;
import processing.video.Capture;
import themidibus.ControlChange;
import themidibus.MidiBus;
import processing.video.*;


public class Prelude extends BaseApplet {
	Movie video;//

	
	

	int camera_width =Integer.valueOf(Props.getValue("camera_width"));
	int camera_height =Integer.valueOf(Props.getValue("camera_height"));
	

	boolean[][] markerCells;

	
	@Override
	public void stop() {
		super.stop();
	}
	
	public void movieEvent(Movie m) {//
		  m.read();//
		}
	
	public	void setup() {
		
		
		size(camera_width, camera_height);
			
		    cam = new Capture(this, camera_width, camera_height, Props.getValue("camera_name"), 30);
		    video = new Movie(this, "/Users/chro/Desktop/1-2-3e.mov");//
		    opencv = new OpenCV(this, camera_width, camera_height);
		    
		    opencv.startBackgroundSubtraction(5, 3, 0.5);
		    
		    video.loop();
		    video.play();
		   
		    

		  }

   void personEntered(int number_of_person){

	   System.out.println(number_of_person);
	   if(number_of_person>0){
		   play1Line();
		   if(number_of_person>1)
			   play2Line();
		   if(number_of_person>2)
			   play3Line();
		   if(number_of_person>3)
			   play4Line();
		   if(number_of_person>5)
			   play5Line();
		   if(number_of_person>7)
			   play8Line();
		   if(number_of_person>10)
			   play9Line();
	  // sendMidiMessage(newMoveBus, 2, number_of_person, 127);
	   }
   }

 
 
   void play1Line(){
	   sendMidiMessage(newMoveBus, 2, (int)Math.random()*16, 127);
   }
   void play2Line(){
	   sendMidiMessage(newMoveBus, 2,16+ (int)Math.random()*16, 127);
   }
   void play3Line(){
	   sendMidiMessage(newMoveBus, 2, 31+(int)Math.random()*5, 127);
   }
   void play4Line(){
	   sendMidiMessage(newMoveBus, 2, 36+(int)Math.random()*5, 127);
   }
   void play5Line(){
	   sendMidiMessage(newMoveBus, 2, 45+(int)Math.random()*5, 127);
   }
   void play8Line(){
	   sendMidiMessage(newMoveBus, 2, 50, 127);
   }
   void play9Line(){
	   sendMidiMessage(newMoveBus, 2, 50, 127);
   }
   
   void makeGlitch(int value){
	   sendMidiMessage(complicationBus, 1, 1, value);

   }
   
	public void draw() {
	/*	 if (cam.available() == true) {
			    cam.read();
			  }*/

		  image(video, 0, 0);  

		  
		  opencv.loadImage(video);
		  opencv.adaptiveThreshold(11, 5);
		  opencv.updateBackground();
		  
		  opencv.dilate();
		  opencv.erode();

		  noFill();
		  stroke(255, 0, 0);
		 strokeWeight(1);
		 
		 
		ArrayList <Contour> contours=  opencv.findContours(true, true);
		 System.out.println(contours.size());
		  for (Contour contour : contours) {
		    contour.getPolygonApproximation().draw();
		  }
		  personEntered(contours.size());
		  
		//    opencv = new OpenCV(this, cam);
		//  delay(10*1000);
		 // play3Line();
	//personEntered(1);

		//	image(opencv.getSnapshot(), 0, 0);
	 
	   
		/*    for(int i=10;i<127;i++){
		    	personEntered(i);
		    	delay(20*1000);
		    	System.out.println(i);
		    }
		    
		 */   
		    
		    
	}
@Override
public void startAll() {
	sendMidiMessage(newMoveBus,  2, 72, 127);
	
}
	@Override
	public void stopAll() {
		makeGlitch(0);
		sendMidiMessage(newMoveBus, 2, 73, 127);
		System.out.println("stopped");
	}

	@Override
	public void muteRow(int i) {
		sendMidiMessage(newMoveBus, 2, 73+i, 127);
		
	}

}
