package io.alatalab.glassbead;


import processing.core.*;
import processing.video.*;
import themidibus.*;
public class CaptureVideo extends PApplet {
  //	An array of stripes
	MidiBus myBus;
	Capture cam;

	public void setup() {
	  size(640, 480);
	  MidiBus.list();
	  myBus = new MidiBus(this, -1, "Java Sound Synthesizer"); 
	  String[] cameras = Capture.list();
	  
	  if (cameras.length == 0) {
	    println("There are no cameras available for capture.");
	    exit();
	  } else {
	    println("Available cameras:");
	    for (int i = 0; i < cameras.length; i++) {
	      println(cameras[i]);
	    }
	    
	    // The camera can be initialized directly using an 
	    // element from the array returned by list():
	    cam = new Capture(this, cameras[0]);
	    cam.start();     
	  }      
	}

	public void draw() {
	  if (cam.available() == true) {
	    cam.read();
	  }
	//  image(cam, 0, 0);
	  PImage pi = cam.get();
	  image(pi, 1, 1);
	  // The following does the same, and is faster when just drawing the image
	  // without any additional resizing, transformations, or tint.
	  //set(0, 0, cam);
	}

  
  
  
  
}
