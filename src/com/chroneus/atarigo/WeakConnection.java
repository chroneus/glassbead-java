package com.chroneus.atarigo;
import java.util.*;
public class WeakConnection {
  BitBoard a, b;
  BitBoard cutting_points;
  
  public WeakConnection(BitBoard a, BitBoard b, BitBoard cutting_points) {
	super();
	this.a = a;
	this.b = b;
	this.cutting_points = cutting_points;
}

@Override
public int hashCode() {
	return a.hashCode()^b.hashCode()+cutting_points.hashCode();
}
@Override
	public boolean equals(Object obj) {
		
		return (obj instanceof WeakConnection) && this.hashCode()==((WeakConnection)obj).hashCode();
	}
@Override
public String toString() {
	return toBoard().toString();
}

public Board toBoard() {
	Board print_board=new Board();
	print_board.black.or(a);
	print_board.black.or(b);
	print_board.white.or(cutting_points);
	return print_board;
}

static Set<WeakConnection>  checkWeakConnection(BitBoard a,BitBoard b,BitBoard other_stones){
	 Set<WeakConnection> result =new HashSet<WeakConnection>();
	 
	  
	  return result;
	  
  }



private static BitBoard getKosumiInnerStones(BitBoard kosumi) {
	int a=kosumi.nextSetBit(0);
	int b=kosumi.nextSetBit(a+1);
	BitBoard internal=new BitBoard();
	internal.set(a+Board.SIZE);
	internal.set(b-Board.SIZE);
	return internal;
}

static BitBoard getBambooInnerStones(BitBoard bamboo){
	  int a=bamboo.nextSetBit(0);
	  int b=bamboo.nextSetBit(a+1);
	  int c=bamboo.nextSetBit(b+1);
	  int d=bamboo.nextSetBit(c+1);
	  BitBoard internal=new BitBoard();
	  if(b==a+1) {
		  internal.set(a+Board.SIZE);
		  internal.set(a+Board.SIZE+1);	  
	  }else{
		  internal.set(a+1);
		  internal.set(a+Board.SIZE+1);	
	  }
	  return internal;
	  
  }
}
