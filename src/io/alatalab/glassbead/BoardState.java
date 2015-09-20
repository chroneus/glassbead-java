package io.alatalab.glassbead;

import java.util.BitSet;

public class BoardState {
	BitSet black = new BitSet(361);
	BitSet white = new BitSet(361);
	public BoardState() {
		
	}
	boolean isEmpty(){
		return black.isEmpty() && white.isEmpty();
	}
	
	int cardinality(){
		return black.cardinality()+white.cardinality();
	}
	
	/*static BitSet nearestBitSet(BitSet bitset){
		BitSet newset = new BitSet();
		for (int i = bitset.nextSetBit(0); i >= 0; i = bitset.nextSetBit(i+1)) {
		   //  if(i>18)
		}
	}*/

}
