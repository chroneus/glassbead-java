package io.alatalab.glassbead;


import processing.core.*;
import processing.video.*;
import themidibus.*;
public class MidiBusSender extends PApplet {
	

	MidiBus bus;
	int velocity;
	int note;

	public void setup()
	{
	  size(256, 256);
	  background(0);

	  //list the available MIDI devices
	  MidiBus.list(); 

	  //select the MIDI in and out to use from the list above
	  bus= new MidiBus(this, 0, 1);
	}

	public void draw()
	{
	}


	public void mousePressed()
	{
	  note= mouseX;
	  velocity= mouseY;
	  bus.sendNoteOn(0, note, velocity);
	 
	}

	public void mouseReleased()
	{
	  bus.sendNoteOff(0, note, velocity);
	}
  
  
  
}
