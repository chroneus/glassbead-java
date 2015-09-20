package com.chroneus.atarigo;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashSet;
import java.util.List;

/**
 * this class looks like two-dimension BitSet with constant size
 */
public class BitBoard implements Cloneable {
	 BitSet bitset ;
	 byte xsize = 19, ysize = 19;
	public BitBoard(byte x, byte y) {
		bitset = new BitSet(x*y);
		setXsize(x);
		setYsize(y);
	}
  
	public BitSet getBitset() {
		return bitset;
	}

	public void setBitset(BitSet bitset) {
		this.bitset = bitset;
	}

	public BitBoard() {
		bitset = new BitSet(BoardConstant.SIZE*BoardConstant.SIZE);
	}

	public BitBoard(int size) {
		this((byte) Math.sqrt(size), (byte) Math.sqrt(size));
	}
	public BitBoard(int[] initial) {
		this(BoardConstant.SIZE,BoardConstant.SIZE);
		for (int i = 0; i < initial.length; i++) {
			set(initial[i]);
		}
	}
	public BitBoard(int i, int j) {
		this((byte) i, (byte) j);
	}

	public BitBoard(BitSet bitSet2) {
		this.bitset=bitSet2;
		
	}

	public void set(byte x, byte y) {
		set(x * ysize + y);
	}

	public void set(int x, int y) {
		set(x * ysize + y);
	}

	public boolean get(byte x, byte y) {
		return get(x * ysize + y);
	}

	public void flip(int bit) {
		bitset.flip(bit);
	}

	public void set(int bit) {
		bitset.set(bit);
	}

	public boolean get(int bit) {
		 return bitset.get(bit);
	}

	public void clear(int bit) {
		bitset.clear(bit);
	}

	public void clear() {
		bitset.clear();
	}

	public byte getXsize() {
		// return (byte) ((a1 >> (48)) & 0xff);
		return xsize;
	}

	public byte getYsize() {
		// return (byte) ((a1 >> (56)) & 0xff);
		return ysize;
	}

	public void setXsize(byte x) {
		// a1|= (0xF0FFFFFF & x);
		xsize = x;
	}

	public void setYsize(byte y) {
		// a1|= (0x0FFFFFFF & y);
		ysize = y;
	}

	@Override
	protected Object clone() {
		BitBoard bitboard = new BitBoard();
		bitboard.bitset= (BitSet) bitset.clone();
		bitboard.xsize = xsize;
		bitboard.ysize = ysize;
		return bitboard;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof BitBoard) {
			if (this.ysize == ((BitBoard) obj).ysize && this.bitset.equals(((BitBoard)obj).getBitset())) return true;
		}
		return false;
	}
    
	
	public BitBoard mirror() {
		BitBoard bitBoard = new BitBoard(xsize, ysize);
		for (int i = nextSetBit(0); i >= 0; i = nextSetBit(i + 1)) {
			bitBoard.set(i / ysize, ysize - 1 - i % ysize);
		}
		return bitBoard;
	}

	/**
	 * convert (x,y) to (SIZEY-1-y,x)
	 * 
	 */
	public BitBoard rotate90() {
		BitBoard bitBoard = new BitBoard(ysize, xsize);

		for (int i = nextSetBit(0); i >= 0; i = nextSetBit(i + 1)) {
			bitBoard.set(ysize - 1 - i % ysize, i / ysize);
		}
		return bitBoard;
	}

	public BitBoard moveXY(byte x, byte y, byte wrap_size_x, byte wrap_size_y) {

		BitBoard bitBoard = new BitBoard(wrap_size_x, wrap_size_y);
		for (int i = nextSetBit(0); i >= 0; i = nextSetBit(i + 1)) {
			int x0 = i / ysize, y0 = i % ysize;
			int x1 = x0 + x;
			int y1 = y0 + y;
			if (x1 >= wrap_size_x || y1 >= wrap_size_y) System.out.println("move pattern is out of result");
			bitBoard.set(wrap_size_y * (x0 + x) + y0 + y);
		}
		return bitBoard;
	}

	// TODO Zobrist hashing
	public long hash() {
		return 0L;
	}

	@Override
	public int hashCode() {
		return bitset.hashCode() ;
	}
	

	
	public int nextSetBit(int bit) {
		return bitset.nextSetBit(bit);
		
	}

	public void not() {
		bitset.flip(0, 360);
		
	}

	public void andNot(BitBoard moved_transform) {
		bitset.andNot(moved_transform.bitset);
		
	}

	public boolean isEmpty() {
		return bitset.isEmpty();
	}

	public int cardinality() {
		return bitset.cardinality();
	}

	public int size() {
		return getXsize() * getYsize();
	}

	/**
	 * 
	 */
	public void and(BitBoard moved_transform) {
		bitset.and(moved_transform.bitset);
	}

	public void or(BitBoard moved_transform) {
		bitset.or(moved_transform.bitset);
	}

	public void flipInRange(int i, int j) {
		// slow stub
		for (int bit = i; bit <= j; bit++) {
			flip(bit);
		}
	}
	public  BitBoard[] containsSubBitBoard( BitBoard subset)  {
		List<BitBoard> resultList = new ArrayList<BitBoard>();
		HashSet<BitBoard> subsetTransformed = new HashSet<BitBoard>();
		BitBoard rotate90subset = subset.rotate90();
		BitBoard rotate180subset = rotate90subset.rotate90();
		BitBoard rotate270subset = rotate180subset.rotate90();
		// 8 affinity transformation
		subsetTransformed.add(subset);
		subsetTransformed.add(rotate90subset);
		subsetTransformed.add(rotate180subset);
		subsetTransformed.add(rotate270subset);
		subsetTransformed.add(subset.mirror());
		subsetTransformed.add(rotate90subset.mirror());
		subsetTransformed.add(rotate180subset.mirror());
		subsetTransformed.add(rotate270subset.mirror());

		for (BitBoard subsetTransform : subsetTransformed) {
			for (byte x = 0; x <= this.getXsize() - subsetTransform.getXsize(); x++) {
				for (byte y = 0; y <= this.getYsize() - subsetTransform.getYsize(); y++) {
					BitBoard test = (BitBoard) this.clone();
					BitBoard moved_transform = subsetTransform.moveXY(x, y, this.getXsize(), this.getYsize());
					test.and(moved_transform);
					if (test.equals(moved_transform)) {
						resultList.add(moved_transform);
					}
				}
			}

		}
		BitBoard[] result=new BitBoard[resultList.size()];
		result=resultList.toArray(result);
		return result;
	}

	@Override
	public String toString() {
		char[][] pseudoGraphics = new char[xsize][ysize];
		for (byte x = 0; x < xsize; x++) {
			for (byte y = 0; y < ysize; y++) {
				if (get(x, y))
					pseudoGraphics[x][y] = '*';
				else
					pseudoGraphics[x][y] = '·';
			}
		}

		StringBuffer out = new StringBuffer();
		for (byte x = 0; x < xsize; x++) {
			for (byte y = 0; y < ysize; y++) {
				out.append(" " + pseudoGraphics[x][y]);
			}
			out.append("\n");
		}

		return out.toString();
	}

	public BitBoard shift (int n) {
		
		BitBoard shifted = new BitBoard(this.bitset.get(n, Math.max(n, this.bitset.length())));
		

		return shifted;
	}
	//TODO use long
public BitBoard unshift (int n) {
		
	BitSet result = new BitSet(BoardConstant.SIZE*BoardConstant.SIZE);
	BitSet shifted=this.bitset.get(0, this.bitset.length()-n);
	
	for(int i=n;i<BoardConstant.SIZE*BoardConstant.SIZE;i++){
		if(shifted.get(i-n))
		result.set(i);
	}
		return new BitBoard(result);
	}

public  BitBoard shiftRight( int n) {
	
    if (n < 0)
        throw new IllegalArgumentException("'n' must be >= 0");
    if (n >= 64)
        throw new IllegalArgumentException("'n' must be < 64");

    long[] words = this.bitset.toLongArray();
    if(words.length==0) return (BitBoard) this.clone();
    // Expand array if there will be carry bits
    if ( words[words.length - 1] >>> n > 0) {
        long[] tmp = new long[words.length + 1];
        System.arraycopy(words, 0, tmp, 0, words.length);
        words = tmp;
    }

    // Do the shift
    for (int i = words.length - 1; i > 0; i--) {
        words[i] <<= n; // Shift current word
        words[i] |= words[i - 1] >>> (64 - n); // Do the carry
    }
    words[0] <<= n; // shift [0] separately, since no carry

    return new BitBoard(BitSet.valueOf(words));
}

	/*
	 * also use it for influence
	 */
	public  BitBoard nearest_stones() {
		BitBoard firstresult=this.get_left_nearest_stones();
		firstresult.or(this.get_right_nearest_stones());
		firstresult.or(this.get_top_nearest_stones());
		firstresult.or(this.get_bottom_nearest_stones());
		firstresult.andNot(this);
		return firstresult;
	}
	public  BitBoard diagonal_nearest_stones() {
		BitBoard firstresult=this.get_left_nearest_stones().get_top_nearest_stones();
		firstresult.or(this.get_left_nearest_stones().get_bottom_nearest_stones());
		firstresult.or(this.get_right_nearest_stones().get_top_nearest_stones());
		firstresult.or(this.get_right_nearest_stones().get_bottom_nearest_stones());
		firstresult.andNot(this);
		return firstresult;
	}
	
	
	public BitBoard get_left_top_nearest_stones(){
		
		BitBoard leftresult=(BitBoard) this.clone();
		leftresult.andNot(BoardConstant.LeftBorder);
		leftresult.andNot(BoardConstant.TopBorder);
		return leftresult.shift(20);
	}
	public BitBoard get_right_top_nearest_stones(){
		
		BitBoard leftresult=(BitBoard) this.clone();
		leftresult.andNot(BoardConstant.RightBorder);
		leftresult.andNot(BoardConstant.TopBorder);
		return leftresult.shift(18);
	}	
	public BitBoard get_left_nearest_stones(){
		BitBoard leftresult=(BitBoard) this.clone();
		leftresult.andNot(BoardConstant.LeftBorder);
		
		return leftresult.shift(1);
	}
	public BitBoard get_right_nearest_stones(){
		BitBoard rightresult=(BitBoard) this.clone();
		rightresult.andNot(BoardConstant.RightBorder);
		return rightresult.shiftRight( 1);
	}
	public BitBoard get_top_nearest_stones(){
		BitBoard leftresult=this.shift(19);
		
		//	leftresult.bitset.
			leftresult.andNot(BoardConstant.TopBorder);
			return leftresult;
	}
	public BitBoard get_bottom_nearest_stones() {
		BitBoard bottomresult=(BitBoard) this.clone();
		bottomresult.andNot(BoardConstant.BottomBorder);
		return bottomresult.shiftRight(19);
	}



}
