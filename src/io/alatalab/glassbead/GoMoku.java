package io.alatalab.glassbead;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Queue;
import java.util.Random;

import gab.opencv.Contour;
import gab.opencv.OpenCV;
import processing.event.MouseEvent;
import processing.video.Capture;

public class GoMoku extends BaseApplet {
	Capture cam;
	public OpenCV opencv;
	Random rand = new Random();
	int x1,y1,x2,y2,x3,y3,x4,y4;
	int[][] bounds = new int[4][2];
	java.awt.Polygon p = new java.awt.Polygon();
  static int center_x,center_y, real_contour=0;
	int camera_width =Integer.valueOf(Props.getValue("log_camera_width"));
	int camera_height =Integer.valueOf(Props.getValue("log_camera_height"));
	
	int deltax=180,deltay=180,cellx=30,celly=40;
  static int state =1;
	boolean[][] markerCells;
	Integer[] rows = new Integer[]{1,2,3,4,5,6,7,8,9,10,11};
	public long last_time_played=System.currentTimeMillis();

	public	void setup() {
		size(camera_width, camera_height);

		    cam = new Capture(this, camera_width, camera_height, "USB Camera", 30);
			opencv = new OpenCV(this,camera_width, camera_height);

			opencv.startBackgroundSubtraction(15, 3, 0.5);
		
		    // Start capturing the images from the camera
		    cam.start();
		    
		    

		  }
		

@Override
public void mouseClicked(MouseEvent event) {
	
	super.mouseClicked(event);
	if(x1==0){
		x1=event.getX();
		y1=event.getY();
	}else if(x2==0){
		x2=event.getX();
		y2=event.getY();
	}else if(x4==0){
		x4=event.getX();
		y4=event.getY();
	}else if(x3==0){
		x3=event.getX();
		y3=event.getY();
	}
	p.addPoint(x1, y1);
	p.addPoint(x2, y2);
	p.addPoint(x3, y3);
	p.addPoint(x4, y4);
	
}



	public void draw() {
		 if (cam.available() == true) {
			    cam.read();
			  }

		    image(cam,0,0);
		    
			opencv.loadImage(cam);
			
			
			opencv.updateBackground();
			
		 line(x1,y1,x2,y2);
		 line(x1,y1,x3,y3);
		 line(x2,y2,x4,y4);
		 line(x3,y3,x4,y4);
		 /** 
		 for(int i=0;i<19;i++){
			 line((int)(x1+(x3-x1)*(i)/18),(int)( y1+(y3-y1)*(i)/18),(int)(x2+(x4-x2)*(i)/18), (int)(y2+(y4-y2)*(i)/18));
			 line((int)(x1+(x2-x1)*(i)/18),(int)( y1+(y2-y1)*(i)/18),(int)(x3+(x4-x3)*(i)/18), (int)(y3+(y4-y3)*(i)/18));
				
				//cam.get(x, y);
			
		 }*/
		// System.out.println( cam.get(10,10));
		 	opencv.threshold(20);
			opencv.dilate();
			opencv.erode();

			noFill();
			stroke(255, 0, 0);
			strokeWeight(1);
			real_contour=0;

			ArrayList<Contour> contours = opencv.findContours();
		
			for (Contour contour : contours) {
				contour.getBoundingBox();
				contour = contour.getConvexHull();
				 Rectangle box= contour.getBoundingBox();
			if (contour.area() > 100  && contour.area() < 1000.0 && p.contains(box)) {
				   real_contour++;
					System.out.println(contour.area());
					contour.draw();
					if(box.width>5 && box.height>5 &&p.contains(box) ){
						center_x = box.x+box.width/2;
						center_y = box.y+box.height/2;
					
						// contour.draw();
						 last_time_played=System.currentTimeMillis();
					}
				}
			}
			if(real_contour==0 && center_x!=0){
				Color c = new Color(cam.get(center_x, center_y));
				if(state==18) state=3+rand.nextInt(12);
				playColumn(state++);
				System.out.println("last contour at "+ center_x +":" + center_y+" "+c.getBlue()+" " +c.getGreen()+" "+c.getRed() );
				center_x=0;center_y=0;
			}
		
		    long now=System.currentTimeMillis();
		    if(now - last_time_played>3000){
		    	System.out.println("mute");
		    	last_time_played=now;
		    	muteRow(rand.nextInt(rows.length));
		    }
		    
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
	public void playRow(int i) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void playColumn(int i) {
		System.out.println("play row "+i);
		sendMidiMessage(newMoveBus, 2, i, 127);
		
	}
	
    public void youLoose(){
    	sendMidiMessage(newMoveBus, 2, 20, 127);
    }
}
