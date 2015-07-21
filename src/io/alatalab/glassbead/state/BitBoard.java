package io.alatalab.glassbead.state;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * this class looks like two-dimension BitSet with constant size
 */
public class BitBoard implements Cloneable {
	long a0 = 0, a1 = 0;// max 128 bit
	 long a2=0,a3=0,a4=0,a5=0; // for 19*19
	 int xsize = 19, ysize = 19;
	static final long mask=Long.parseLong("11111111111111111", 2);

	public BitBoard(int x, int y) {
		setXsize(x);
		setYsize(y);
	}

	public BitBoard() {
	}

	public BitBoard(int size) {
		this((int) Math.sqrt(size), (int) Math.sqrt(size));
	}
	public BitBoard(int[] initial) {
		this(BoardConstant.SIZE,BoardConstant.SIZE);
		for (int i = 0; i < initial.length; i++) {
			set(initial[i]);
		}
	}

	public void set(int x, int y) {
		set(x * ysize + y);
	}


	public boolean get(int x, int y) {
		return get(x * ysize + y);
	}

	public void flip(int bit) {
		if (bit < 64)
			a0 ^= 1L << bit;
		else
			a1 ^= 1L << (bit - 64);
	}

	public void set(int bit) {
		if (bit < 64)
			a0 |= 1L << bit;
		else
			a1 |= 1L << (bit - 64);
	}

	public boolean get(int bit) {
		if (bit < 64)
			return ((a0 & (1L << bit)) != 0);
		else
			return ((a1 & (1L << (bit - 64))) != 0);
	}

	public void clear(int bit) {
		if (bit < 64)
			a0 &= ~(1L << bit);
		else
			a1 &= ~(1L << (bit - 64));
	}

	public void clear() {
		int x = getXsize();
		int y = getYsize();
		a0 = 0;
		a1 = 0;
		setXsize(x);
		setYsize(y);
	}

	public int getXsize() {
		// return (int) ((a1 >> (48)) & 0xff);
		return xsize;
	}

	public int getYsize() {
		// return (int) ((a1 >> (56)) & 0xff);
		return ysize;
	}

	public void setXsize(int x) {
		// a1|= (0xF0FFFFFF & x);
		xsize = x;
	}

	public void setYsize(int y) {
		// a1|= (0x0FFFFFFF & y);
		ysize = y;
	}

	@Override
	protected Object clone() {
		BitBoard bitboard = new BitBoard();
		bitboard.a0 = a0;
		bitboard.a1 = a1;
		bitboard.xsize = xsize;
		bitboard.ysize = ysize;
		return bitboard;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof BitBoard) {
			if (this.ysize == ((BitBoard) obj).ysize && this.a0 == ((BitBoard) obj).a0
					&& this.a1 == ((BitBoard) obj).a1) return true;
		}
		return false;
	}
    
	public boolean intersects(BitBoard another) {
		  return ((this.a0 & another.a0) != 0) ||((this.a1 & another.a1) != 0);
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

	public BitBoard moveXY(int x, int y, int wrap_size_x, int wrap_size_y) {

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
		return (int)(a0 ^ (a0>> 32) ^ a1 ^ (a1 >> 32)) ;
	}
	

	
	public int nextSetBit(int bit) {
		if (bit < 64) {
			int trail_a0 = Long.numberOfTrailingZeros(a0 & (-1L << bit));
			if (trail_a0 < 64)
				return trail_a0;
			else {
				int trail_a1 = Long.numberOfTrailingZeros(a1);
				if (trail_a1 < 64)
					return trail_a1 + 64;
				else
					return -1;
			}
		}
		else {
			int trail_a1 = Long.numberOfTrailingZeros(a1 & (-1L << (bit - 64)));
			if (trail_a1 < 17)
				return trail_a1 + 64;
			else
				return -1;
		}
	}

	public void not() {
		a0 = ~a0;
		a1 = ~a1 &mask | (a1&~mask);
		
	}

	public void andNot(BitBoard moved_transform) {
		a0 &= ~moved_transform.a0;
		a1 &= (~moved_transform.a1&mask)|(a1&~mask);
		
	}

	public boolean isEmpty() {
		return a0 == 0 && (a1&mask) == 0;
	}

	public int cardinality() {
		return Long.bitCount(a0) + Long.bitCount(a1&mask);
	}

	public int size() {
		return getXsize() * getYsize();
	}

	/**
	 * 
	 */
	public void and(BitBoard moved_transform) {
		a0 &= moved_transform.a0;
		a1 &= moved_transform.a1;
	}

	public void or(BitBoard moved_transform) {
		a0 |= moved_transform.a0;
		a1 |= moved_transform.a1;
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
			for (int x = 0; x <= this.getXsize() - subsetTransform.getXsize(); x++) {
				for (int y = 0; y <= this.getYsize() - subsetTransform.getYsize(); y++) {
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
		for (int x = 0; x < xsize; x++) {
			for (int y = 0; y < ysize; y++) {
				if (get(x, y))
					pseudoGraphics[x][y] = '*';
				else
					pseudoGraphics[x][y] = '·';
			}
		}

		StringBuffer out = new StringBuffer();
		for (int x = 0; x < xsize; x++) {
			for (int y = 0; y < ysize; y++) {
				out.append(" " + pseudoGraphics[x][y]);
			}
			out.append("\n");
		}

		return out.toString();
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
	
	
	public BitBoard get_left_nearest_stones(){
		BitBoard leftresult=(BitBoard) this.clone();
		leftresult.andNot(BoardConstant.LeftBorder);
		leftresult.a0>>>=1;
		leftresult.a0 |= ((leftresult.a1&1L) << 63);
		leftresult.a1=((leftresult.a1>>>1)&mask)|(leftresult.a1&~mask);	
		return leftresult;
	}
	public BitBoard get_right_nearest_stones(){
		BitBoard rightresult=(BitBoard) this.clone();
		rightresult.andNot(BoardConstant.RightBorder);
		rightresult.a1=((rightresult.a1<<1)&mask)|(rightresult.a1&~mask);
		rightresult.a1|=((rightresult.a0&-1L)>>>63);
		rightresult.a0<<=1;
		return rightresult;
	}
	public BitBoard get_top_nearest_stones(){
		BitBoard topresult=(BitBoard) this.clone();
		topresult.andNot(BoardConstant.TopBorder);
		topresult.a0>>>=9;
		topresult.a0 |=( (topresult.a1&511L) << 55);
		topresult.a1=((topresult.a1>>>9)&mask)|(topresult.a1&~mask);
		return topresult;
	}
	public BitBoard get_bottom_nearest_stones(){
		BitBoard bottomresult=(BitBoard) this.clone();
		bottomresult.andNot(BoardConstant.BottomBorder);
		bottomresult.a1=((bottomresult.a1<<9)&mask)|(bottomresult.a1&~mask);
		bottomresult.a1|=((bottomresult.a0&-1L)>>>55);
		bottomresult.a0<<=9;
		return bottomresult;
	}
	


}
