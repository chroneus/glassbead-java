package io.alatalab.glassbead.state;

import java.awt.*;

import processing.core.PApplet;

public class GoMokuApplet extends PApplet 
{
  
	private static final long serialVersionUID = -2858978030120895374L;
	
GoMokuBoard board;
   Label score;

   public void init() 
   {
     setBackground( Color.white );
     setLayout( new FlowLayout() );
     Dimension sz = new Dimension(1024, 768);
     setPreferredSize(sz);

     int cellSize = Math.min( sz.width, sz.height )/20;

     board = new GoMokuBoard( 20,20,cellSize,cellSize );
     score = new Label( board.getScore() );

     add( board );
     add( new Button( "New game" ) );
     add( new Button( "Swap positions" ) );
     add( score );
     
     GameState.mainApp=this;
   }
   
   public boolean action( Event evt, Object arg )
   {
      if ("New game".equals( arg ))
      {
         board.newGame();
         score.setText( board.getScore() );
         return true;
      }
      else if ("Swap positions".equals( arg ))
      {
         board.swapPositions();
         score.setText( board.getScore() );
         return true;
      }
      return false;
   }

   public String getAppletInfo() 
   {
      return "Go-Moku. ";
   }
}