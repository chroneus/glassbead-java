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
	PImage src;
	ArrayList<MatOfPoint> contours;
	ArrayList<MatOfPoint2f> approximations;
	ArrayList<MatOfPoint2f> markers;
	int threshold_block =Integer.valueOf(Props.getValue("threshold_block"));
	int threshold_substract =Integer.valueOf(Props.getValue("threshold_substract"));
	ArrayList<Line> lines;


	boolean[][] markerCells;

	public	void setup() {
		size(640, 480);

		    cam = new Capture(this, 640, 480, Props.getValue("camera.Name"), 30);
		    
		    // Start capturing the images from the camera
		    cam.start();
		    
		    

		  }

		





	public void draw() {
		 if (cam.available() == true) {
			    cam.read();
			  }
		
		    opencv = new OpenCV(this, cam);
		    
		    opencv.adaptiveThreshold(threshold_block, threshold_substract);
		
		    
		    
		    
		    
		    opencv.findCannyEdges(20, 75);

		    // Find lines with Hough line detection
		    // Arguments are: threshold, minLengthLength, maxLineGap
		    lines = opencv.findLines(100, 30, 20);

		    
			image(opencv.getSnapshot(), 0, 0);
	  //  strokeWeight(3);
	    
	    for (Line line : lines) {
	      // lines include angle in radians, measured in double precision
	      // so we can select out vertical and horizontal lines
	      // They also include "start" and "end" PVectors with the position
	      if (line.angle >= radians(0) && line.angle < radians(1)) {
	        stroke(0, 255, 0);
	        line(line.start.x, line.start.y, line.end.x, line.end.y);
	      }

	      if (line.angle > radians(89) && line.angle < radians(91)) {
	        stroke(255, 0, 0);
	        line(line.start.x, line.start.y, line.end.x, line.end.y);
	      }
	    }
		    
		    
		    
		    
		    
	}

}
