package io.alatalab.glassbead;

import gab.opencv.*;

import org.opencv.imgproc.Imgproc;
import org.opencv.core.Core;

import java.awt.Rectangle;
import java.util.*;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.CvType;
import org.opencv.core.Point;
import org.opencv.core.Size;

import processing.core.*;
import processing.video.Capture;


public class DetectImage extends PApplet {
	Capture cam;
	OpenCV opencv;
	int b=0;
	
	PImage  src, dst, markerImg;
	ArrayList<MatOfPoint2f> approximations;
	ArrayList<MatOfPoint2f> markers;
	

	int camera_width =Integer.valueOf(Props.getValue("camera_width"));
	int camera_height =Integer.valueOf(Props.getValue("camera_height"));
	int threshold_block =Integer.valueOf(Props.getValue("threshold_block"));
	int threshold_substract =Integer.valueOf(Props.getValue("threshold_substract"));
	int threshold_black =Integer.valueOf(Props.getValue("threshold_black"));
	int threshold_white =Integer.valueOf(Props.getValue("threshold_white"));
    ArrayList<Contour> black_contours, white_contours;



	boolean[][] markerCells;

	public	void setup() {
		size(camera_width, camera_height);
           
		    cam = new Capture(this, camera_width, camera_height, Props.getValue("camera_name"), 30);
		    
		    // Start capturing the images from the camera
		    cam.start();
		    
		    

		  }

		





	public void draw() {
		 if (cam.available() == true) {
			    cam.read();
			  }
		
		    opencv = new OpenCV(this, cam);
		    
	/*	    opencv.adaptiveThreshold(threshold_block, threshold_substract);
		   
			    opencv.threshold(threshold_black);
System.out.println(threshold_black++);
if(threshold_black>255) threshold_black=0;
	/**		    black_contours = opencv.findContours();
System.out.println("black:"+black_contours.size());
Rectangle rectangle = new Rectangle(camera_width-400, camera_height-400);
rectangle.setLocation(100, 100);

for (Contour contour : black_contours) {
	if(contour.area()>5 &&contour.area()<10 && contour.getBoundingBox().intersects(rectangle))
	System.out.println(contour.area());
  stroke(0, 255, 0);
  contour.draw();
  
  stroke(255, 0, 0);
  beginShape();
  for (PVector point : contour.getPolygonApproximation().getPoints()) {
    vertex(point.x, point.y);
  }
  endShape();
}
	/*	    opencv.threshold(threshold_white);
		  

		    white_contours = opencv.findContours();
System.out.println("white:"+white_contours.size());
*/
			image(opencv.getSnapshot(), 0, 0);
	 
	   
		    
		    
		    
		    
		    
	}

}
