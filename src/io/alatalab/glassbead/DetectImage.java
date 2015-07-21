package io.alatalab.glassbead;

import gab.opencv.*;

import org.opencv.imgproc.Imgproc;
import org.opencv.core.Core;

import java.util.*;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.CvType;
import org.opencv.core.Point;
import org.opencv.core.Size;

import processing.core.PApplet;
import processing.core.PImage;
import processing.video.Capture;

public class DetectImage extends PApplet {
	Capture cam;
	OpenCV opencv;
	int b=0;
	
	PImage  src, dst, markerImg;
	ArrayList<MatOfPoint> contours;
	ArrayList<MatOfPoint2f> approximations;
	ArrayList<MatOfPoint2f> markers;
	

	int camera_width =Integer.valueOf(Props.getValue("camera_width"));
	int camera_height =Integer.valueOf(Props.getValue("camera_height"));
	int threshold_block =Integer.valueOf(Props.getValue("threshold_block"));
	int threshold_substract =Integer.valueOf(Props.getValue("threshold_substract"));
	ArrayList<Line> lines;


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
		    
	//	    opencv.adaptiveThreshold(threshold_block, threshold_substract);
		 
			    opencv.threshold(b);
System.out.println(b++);
if(b==255) b=0;
		    
		    
			image(opencv.getSnapshot(), 0, 0);
	  //  strokeWeight(3);
	   
		    
		    
		    
		    
		    
	}

}
