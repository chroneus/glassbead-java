package com.chroneus.atarigo;

import java.util.*;

public class GomokuBoard extends Board {

	public GomokuBoard(char[] charArray) {
		super( charArray);
	}
	public GomokuBoard() {
		super( );
	}
	@Override
	public boolean is_terminal() {
		 
		return (getLongestLineLenghtLeft(black) == 5) || (getLongestLineLenghtLeft(white) ==5)
				|| (getLongestLineLenghtLeftTop(black) == 5)||(getLongestLineLenghtLeftTop(white) == 5)
				|| (getLongestLineLenghtRightTop(black) == 5)||(getLongestLineLenghtRightTop(white) == 5)
				|| (getLongestLineLenghtTop(black) == 5)||(getLongestLineLenghtTop(white) == 5);
		
	}

	private BitBoard getLongestLine(BitBoard black) {
		
		int max_length = 0;
		BitBoard longest_found = new BitBoard();
		
		for (BitBoard b: getAllLines(black)) {
			
			if (b.cardinality() > max_length) {
				longest_found = b;
				max_length = b.cardinality();
			}
		}
		
		return longest_found;				
	}
	private int getLongestLineLenghtLeft(BitBoard black) {
		BitBoard board =(BitBoard) black.clone();
		board.and(board.get_left_nearest_stones());
		if(board.cardinality()==0) return 1;
		board.and(board.get_left_nearest_stones());
		if(board.cardinality()==0) return 2;
		board.and(board.get_left_nearest_stones());
		if(board.cardinality()==0) return 3;
		board.and(board.get_left_nearest_stones());
		if(board.cardinality()==0) return 4;
		return 5;		
	}
	
	
	private int getLongestLineLenghtLeftTop(BitBoard black) {
		BitBoard board =(BitBoard) black.clone();
		board.and(board.get_left_top_nearest_stones());
		if(board.cardinality()==0) return 1;
		board.and(board.get_left_top_nearest_stones());
		if(board.cardinality()==0) return 2;
		board.and(board.get_left_top_nearest_stones());
		if(board.cardinality()==0) return 3;
		board.and(board.get_left_top_nearest_stones());
		if(board.cardinality()==0) return 4;
		return 5;		
	}
	
	private int getLongestLineLenghtTop(BitBoard black) {
		BitBoard board =(BitBoard) black.clone();
		board.and(board.get_top_nearest_stones());
		if(board.cardinality()==0) return 1;
		board.and(board.get_top_nearest_stones());
		if(board.cardinality()==0) return 2;
		board.and(board.get_top_nearest_stones());
		if(board.cardinality()==0) return 3;
		board.and(board.get_top_nearest_stones());
		if(board.cardinality()==0) return 4;
		return 5;		
	}
	
	private int getLongestLineLenghtRightTop(BitBoard black) {
		BitBoard board =(BitBoard) black.clone();
		board.and(board.get_right_top_nearest_stones());
		if(board.cardinality()==0) return 1;
		board.and(board.get_right_top_nearest_stones());
		if(board.cardinality()==0) return 2;
		board.and(board.get_right_top_nearest_stones());
		if(board.cardinality()==0) return 3;
		board.and(board.get_right_top_nearest_stones());
		if(board.cardinality()==0) return 4;
		return 5;		
	}
	
	
	private HashSet<BitBoard> getAllLines(BitBoard black) {
		
		HashSet<BitBoard> list = new HashSet<BitBoard>();
		
//		for (int i = 0; i < black.length; i++) {
//			
//		}
		
		return list;
	}
	
	public static void main(String[] args) {
		BitBoard b= new BitBoard();
		b.set(200);
		b.set(120);
		b.set(140);
		b.set(160);
		b.set(180);
		System.out.println(b);
		//b.shift(1);
		System.out.println(b.nearest_stones());
		
		
		GomokuBoard board = new GomokuBoard();
		board.black=b;
		//System.out.println(board.is_terminal());
		
	}
	
	

}
