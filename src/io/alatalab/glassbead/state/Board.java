package io.alatalab.glassbead.state;

import java.text.SimpleDateFormat;
import java.util.*;


public class Board implements Cloneable {
	static final boolean BLACK=false;
	static final boolean WHITE=true;
	BitBoard black = new BitBoard();
	BitBoard white = new BitBoard();

	public boolean is_white_next = false;

	public static byte SIZE = 9;

	/**
	 * Init board from @param toString like
	 * 
	 * 
	 */
	public Board(String toString) {
		String lines[] = toString.split("\n");
		for (int x = 0; x < SIZE; x++) {
			String[] chars = lines[x].trim().split(" ");
			for (int y = 0; y < SIZE; y++) {
				chars[y].trim();
				if (chars[y].equalsIgnoreCase("W")) white.set(x, y);
				if (chars[y].equalsIgnoreCase("B")) black.set(x, y);
			}
		}
		is_white_next = black.cardinality() > white.cardinality();

	}

	public Board() {

	}

	/**
	 * clear board or init
	 */
	public void clear() {
		black.clear();
		white.clear();
		is_white_next = false;
	}

	/**
	 * in GTP there is no I line
	 */
	public static String convertToGTPMove(int position) {
		char first_letter = 'A';
		first_letter += position % SIZE;
		if (first_letter >= 'I') first_letter += 1;
		int second_number = SIZE - position / SIZE;
		return "" + first_letter + second_number;
	}

	public static int convertFromGTPMove(String move) {
		char first_letter = move.charAt(0);
		if (first_letter >= 'I') first_letter -= 1;
		int horizontal_position = first_letter - 'A';
		return horizontal_position + (SIZE - Integer.parseInt(move.substring(1))) * SIZE;
	}

	public static String convertToSGFMove(int position) {
		char first_letter = 'a';
		first_letter += position % SIZE;
		char second_letter = 'a';
		second_letter += position / SIZE;
		return "[" + first_letter + second_letter + "]";
	}

	public static int convertFromSGFMove(String position) {

		int x = position.charAt(0) - 'a';
		int y = position.charAt(1) - 'a';
		return x + y * SIZE;
	}

	public static int distance(int a, int b) {
		int a0 = a % SIZE, a1 = a / SIZE;
		int b0 = b % SIZE, b1 = b / SIZE;
		return Math.max(Math.abs(a0 - b0), Math.abs(a1 - b1));
	}

	/**
	 * play GTP move
	 */
	public void play_move(boolean is_white, String move) {
		if (move.equalsIgnoreCase("resign")) {
			return;
		}
		if (move.equalsIgnoreCase("pass")) {
			return;
		}
		if (is_white) {
			white.set(convertFromGTPMove(move));
		}
		else {
			black.set(convertFromGTPMove(move));
		}
		this.is_white_next = !is_white;
	}

	public void play_move(String move) {
		play_move(is_white_next, move);
	}

	public void play_move(boolean is_white, int move) {
		if (is_white) {
			white.set(move);
		}
		else {
			black.set(move);
		}
		this.is_white_next = !is_white_next;
	}

	public void play_move(int move) {
		play_move(this.is_white_next, move);
	}

	/**
	 * undo move
	 */
	public void undo_move(int move) {

		if (!is_white_next) {
			white.clear(move);
		}
		else {
			black.clear(move);
		}
		this.is_white_next = !this.is_white_next;
	}

	// in real Go consider killed stones
	public String toSGFString() {
		StringBuffer sb = new StringBuffer();
		sb.append(";FF[4]CA[UTF-8]GM[1]");
		sb.append("SZ[" + SIZE + ']' + '\n');
		Date date = new Date();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		sb.append("DT[" + df.format(date) + "]\n");
		for (int i = black.nextSetBit(0); i >= 0; i = black.nextSetBit(i + 1)) {
			sb.append(";B" + convertToSGFMove(i));
		}
		for (int i = white.nextSetBit(0); i >= 0; i = white.nextSetBit(i + 1)) {
			sb.append(";W" + convertToSGFMove(i));
		}
		return '(' + sb.toString() + ')';
	}

	public void loadSGFLine(String line) {
		String moves[] = line.trim().split(";");
		for (String move : moves) {
			move = move.trim();
			if (!move.isEmpty()) {
				if (move.charAt(0) == 'B') {
					play_move(false, convertFromSGFMove(move.substring(2)));
				}
				if (move.charAt(0) == 'W') {
					play_move(true, convertFromSGFMove(move.substring(2)));
				}
			}
		}

	}
    
	public Board fillBoardWithNearestStones(int depth){
		Board board = (Board) this.clone();
		while (depth!=0 && 
				board.white.cardinality() + board.black.cardinality() < SIZE * SIZE) {
			depth--;
			if (board.is_white_next) {
				BitBoard whiteaddon = board.white.nearest_stones();
				whiteaddon.andNot(board.black);
				board.white.or(whiteaddon);
				BitBoard blackaddon = board.black.nearest_stones();
				blackaddon.andNot(board.white);
				board.black.or(blackaddon);
			} else {
				BitBoard blackaddon = board.black.nearest_stones();
				blackaddon.andNot(board.white);
				board.black.or(blackaddon);
				BitBoard whiteaddon = board.white.nearest_stones();
				whiteaddon.andNot(board.black);
				board.white.or(whiteaddon);
			}
		}
		return board;
	}
	
	public Board getMoyo(BitBoard testgroup){
		Board board=(Board) this.clone();
		boolean is_white=this.isWhite(testgroup.nextSetBit(0));
		if(is_white){
			testgroup.andNot(board.black);
			testgroup.andNot(board.black.nearest_stones());
			board.white.or(testgroup);
		}else{
			testgroup.andNot(board.white);
			testgroup.andNot(board.white.nearest_stones());
			board.black.or(testgroup);
		}

		return board;
	}
	
	public BitBoard growGroupFromSeed(BitBoard seed) {
		BitBoard test = (BitBoard) seed.clone();
		test.and(white);
		boolean is_black = false;
		if (test.isEmpty()) is_black = true;
		BitBoard result = (BitBoard) seed.clone();
		BitBoard bitboard;
		do {
			bitboard = result.nearest_stones();
			if (is_black)
				bitboard.and(black);
			else
				bitboard.and(white);
			result.or(bitboard);
		} while (!bitboard.isEmpty() && !bitboard.equals(result));
		return result;
	}

	public BitBoard getLiberties(BitBoard stones) {
		BitBoard result = new BitBoard(stones.getXsize(), stones.getYsize());
		result = stones.nearest_stones();
		result.andNot(black);
		result.andNot(white);
		return result;
	}

	public int minLiberties(BitBoard[] connected) {
		int minLiberties = 255;
		for (int i = 0; i < connected.length; i++) {
			minLiberties = Math.min(minLiberties, getLiberties(connected[i]).cardinality());
		}
		return minLiberties;
	}

	public boolean is_terminal() {
		return (minLiberties(true) == 0 || minLiberties(false) == 0);
	}

	public int minLiberties(boolean is_white) {
		BitBoard[] connected = connectedGroup(is_white);
		return minLiberties(connected);
	}

	/**
	 * list of connected groups
	 */
	public BitBoard[] connectedGroup(boolean is_white) {

		if (is_white) {
			return connectedGroup(this.white);
		}
		else {
			return connectedGroup(this.black);
		}

	}

	/**
	 * list of connected groups divided from existing stones
	 */
	public Set<WeakConnection> getWeakConnections(BitBoard[] groups, BitBoard opponent) {
        Set<WeakConnection> connections=new HashSet<WeakConnection>(); 
		if(groups==null || groups.length==0) return connections;

		for(BitBoard test_group:groups)
			for(BitBoard other_group:groups){
				if(other_group.equals(test_group)||test_group.intersects(other_group)) continue;
				connections.addAll(WeakConnection.checkWeakConnection(test_group,other_group,opponent));
		    	
		}
		return connections;
	}
	/**
	 * list of connected groups divided from existing stones
	 */
	public BitBoard[] connectedGroup(BitBoard stones) {
		BitBoard stoneslocal = (BitBoard) stones.clone();
		BitBoard eyes= getEyes(stones);
		LinkedList<BitBoard> groups = new LinkedList<BitBoard>();
		int element;
		while (!stoneslocal.isEmpty()) {
			element = stoneslocal.nextSetBit(0);
			BitBoard group = new BitBoard(SIZE * SIZE);
			addElementToGroup(group, stoneslocal, element);
			groups.add(group);
		}

		
		BitBoard[] result = new BitBoard[groups.size()];
		result = groups.toArray(result);
		return result;
	}
	
    
	/**
	 * list of connected groups divided from existing stones
	 */
	public BitBoard[] connectedGroupAddElement(BitBoard existing,BitBoard[]result_for_existing,BitBoard new_one) {
		BitBoard diff=(BitBoard) new_one.clone();		diff.andNot(existing);
		if(diff.isEmpty()) return result_for_existing;
		List<BitBoard> resultList=new ArrayList<BitBoard>(Arrays.asList(result_for_existing));
		for(int cell=diff.nextSetBit(0);cell!=-1;cell=diff.nextSetBit(cell+1)){
			BitBoard extra=new BitBoard();
			extra.set(cell);
			for(BitBoard board:result_for_existing){
				if(board.nearest_stones().get(cell)){
					extra.or(board);
					resultList.remove(board);
				}
			resultList.add(extra);	
			}
		}
		BitBoard[] result = new BitBoard[resultList.size()];
		result = resultList.toArray(result);
		return result;
	}
	
	/**
	 * get @param element and it's connected from @param all to @param group
	 * TODO improveme with nearest_stone call
	 */
	private void addElementToGroup(BitBoard group, BitBoard all, int element) {
		group.set(element);
		all.clear(element);
		int near = element + 1;
		if (near % SIZE != 0 && all.get(near)) {
			addElementToGroup(group, all, near);
		}
		near = element - 1;
		if (element % SIZE != 0 && all.get(near)) {
			addElementToGroup(group, all, near);
		}
		near = element + SIZE;
		if (near < SIZE * SIZE && all.get(near)) {
			addElementToGroup(group, all, near);
		}
		near = element - SIZE;
		if (near > 0 && all.get(near)) {
			addElementToGroup(group, all, near);
		}
	}

	/**
	 * 
	 * @param move
	 * @return
	 */
	@SuppressWarnings("unused")
	private boolean isWhite(int move) {
		return white.get(move);
	}

	@SuppressWarnings("unused")
	private boolean isBlack(int move) {
		return black.get(move);
	}


	@Override
	protected Object clone() {

		Board board = new Board();
		board.black = (BitBoard) this.black.clone();
		board.white = (BitBoard) this.white.clone();
		board.is_white_next = this.is_white_next;
		return board;
	}

	public String toString() {
		char[][] pseudoGraphics = new char[SIZE][SIZE];
		for (int x = 0; x < pseudoGraphics.length; x++) {
			for (int y = 0; y < pseudoGraphics.length; y++) {
				pseudoGraphics[x][y] = '·';
			}
		}
		for (int i = 0; i < black.size(); i++) {
			if (black.get(i)) pseudoGraphics[i / SIZE][i % SIZE] = 'B';
		}
		for (int j = 0; j < white.size(); j++) {
			if (white.get(j)) pseudoGraphics[j / SIZE][j % SIZE] = 'W';
		}
		StringBuffer out = new StringBuffer();
		for (int x = 0; x < pseudoGraphics.length; x++) {
			for (int y = 0; y < pseudoGraphics.length; y++) {
				out.append(" " + pseudoGraphics[x][y]);
			}
			out.append("\n");
		}

		return out.toString();
	}

	
	
	
	public BitBoard getEyes(BitBoard test_group) {
		BitBoard test_white = (BitBoard) test_group.clone();
		test_white.and(white);
		boolean am_i_white = test_white.cardinality() > 0;
		BitBoard eyes = test_group.nearest_stones();
		eyes.andNot(black);
		eyes.andNot(white);
		BitBoard open_eyes = eyes.nearest_stones();
		open_eyes.andNot(black);
		open_eyes.andNot(white);
		eyes.andNot(open_eyes.nearest_stones());
		BitBoard first_line_cut = (BitBoard) BoardConstant.FirstLine.clone();//test it with one possible cut
		first_line_cut.and(eyes);
		BitBoard diagonal_first_line = first_line_cut.diagonal_nearest_stones();
		if (am_i_white)
			diagonal_first_line.andNot(white);//or and(black)
		else
			diagonal_first_line.andNot(black);
		eyes.andNot(diagonal_first_line.diagonal_nearest_stones());
	    BitBoard non_first_line = (BitBoard) eyes.clone();//test it with two diagonal cuts
	    non_first_line.andNot(first_line_cut);
	    BitBoard diagonal_non_first_line=non_first_line.diagonal_nearest_stones();
		if (am_i_white)
			diagonal_non_first_line.andNot(white);//or and(black)
		else
			diagonal_non_first_line.andNot(black);
	    diagonal_non_first_line.diagonal_nearest_stones().and(non_first_line);//possible cut
		for (int i = diagonal_non_first_line.nextSetBit(0); i >= 0; i = diagonal_non_first_line.nextSetBit(i + 1)) {
			BitBoard check_i=new BitBoard();
			check_i.set(i);
			check_i=check_i.diagonal_nearest_stones();
			if (am_i_white)
				check_i.andNot(white);//or and(black)
			else
				check_i.andNot(black);
			if(check_i.cardinality()>1) eyes.clear(i);
		}		
		return eyes;
	}

	
	@Override
	/**
	 * do it in language without gc
	 */
	protected void finalize() throws Throwable {
		this.black = null;
		this.white = null;
		super.finalize();

	}

	public boolean isClear(byte i, byte j) {
		return !black.get(i, j)&&!white.get(i, j);
	}

	public void or(Board board) {
		this.black.or(board.black);
		this.white.or(board.white);
	}

}
