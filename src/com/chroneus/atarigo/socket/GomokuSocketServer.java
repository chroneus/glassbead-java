package com.chroneus.atarigo.socket;

import io.alatalab.glassbead.BaseApplet;
import io.alatalab.glassbead.GoMoku;

import java.io.*;
import java.net.*;

import com.chroneus.atarigo.Board;
import com.chroneus.atarigo.GoMokuApplet;
import com.chroneus.atarigo.GomokuBoard;

public class GomokuSocketServer {

	static ServerSocket sc = null;
	static GomokuBoard previous_board = new GomokuBoard();
    static GoMokuApplet base;
    static int state=0,current_state;
	public static void main(String[] args) {
		base = new GoMokuApplet();
		base.main(new String[] { "--present", "MyProcessingSketch" });
		base.stopAll();
		try {
			sc = new ServerSocket(7777);
			socketAccept(sc);
			// Board b = new Board();
			// b.play_move(160);
			// b.play_move(161);
			// b.play_move(162);
			// b.play_move(163);
			// b.play_move(161+19);
			// b.play_move(10);
			// b.play_move(161-19);
			//
			// System.out.println(b.is_white_next);
			// System.out.println(b.is_terminal());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void beginRecording(String text){
		if(text==null) text = "/Users/chro/Desktop/"+System.currentTimeMillis()+"-atarigo.mp3";
		try {
			 Runtime.getRuntime().exec(new String[]{"/usr/local/bin/sox", "-t", "coreaudio", "Soundflower (2ch)", "-q" , text});
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void stopRecording(){
		try {
			 Runtime.getRuntime().exec(new String[]{"killall", "sox", "-SIGINT"} );
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public static void socketAccept(ServerSocket sc) {
		Socket socket = null;
		try {
			socket = sc.accept();
			System.out.println("Got a client ");
			System.out.println();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					socket.getInputStream()));
			String line = null;
			GomokuBoard board;
			while (true) {
				line = reader.readLine();
				//System.out.println(line);
				if (line != null && !line.isEmpty() && line.length() == 361) {

					 board = new GomokuBoard(line.toCharArray());
				
					if (!board.isEmpty()) {
						if (Math.abs(board.black.cardinality()
								- board.white.cardinality()) > 2
								/*|| Math.abs(board.black.cardinality()
										+ board.white.cardinality()
										- previous_board.black.cardinality()
										- previous_board.white.cardinality())>4*/
											) {
							System.err.println("failed detection "+ board+"\n "
											
									);

							board = previous_board; //failed detection
						}

					}
					if(board.cardinality()!=previous_board.cardinality())
						proceedGomokuBoard((GomokuBoard)board);
					previous_board = board;
				}
			
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (socket != null)
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			socketAccept(sc);
		}
	}

	private static void proceedGomokuBoard(GomokuBoard board) {
		System.out.println(board + " \n");
		
		if(board.isEmpty()) {
			base.stopAll();
			return;
		}
		if(board.is_terminal()) {
			base.youLoose();
			stopRecording();
			return;
		}
		if(board.cardinality()<2 ) {
			current_state=1;
			beginRecording(null);
		}
		else if(board.cardinality()<4 ) current_state=2;
		else if(board.cardinality()<7 ) current_state=3;
		else if(state==18) current_state=base.rand.nextInt(12);
		else
		current_state++;
        System.out.println(current_state);
		//if(state!=current_state) {
			state=current_state;
			base.playColumn(state);
		//}
	}
}
