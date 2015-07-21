package io.alatalab.glassbead.state;
/**
 * GoMokuBoard.java
 *
 * Copyright (c) 1997, Dmitry Azovtsev, http://www.azovtsev.com
 *
 * This is free software. You are permitted to copy, distribute, or modify
 * it under the terms of the MIT license, see
 * http://www.opensource.org/licenses/mit-license.php
 */
import java.awt.*;
import java.util.*;

/**
 * GoMoku, also known as GoBang, or Five-in-a-Row ('go' means 'five' in
 * Japanese) is a strategy game where players alternate in placing crosses and
 * zeros on a piece of graph paper (originally it was played with black and
 * white stones on a 20x20 go board). The winner is the first player to get an
 * unbroken row of five.
 * 
 * The first player wins with perfect play (this has been formally proven for a
 * 15x15 board, but likely to be true for larger boards as well). However, this
 * implementation does not try to play perfectly. Instead, we use a simple
 * heuristics based on the value of each empty cell defined by the potential for
 * building lines in horizontal, vertical, or diagonal directions. The idea for
 * the algorithm is taken from an old Turbo Pascal textbook.
 */
public class GoMokuBoard extends Canvas {

	/**
	 * We represent the board as a two-dimensional array of cells, each one is
	 * either EMPTY, or has CROSS or ZERO in it. BORDER is a special value used
	 * to account for board borders. It is not stored in cells.
	 */
	int cells[][];

	static final int EMPTY = 0;
	static final int CROSS = 1;
	static final int ZERO = 2;
	static final int BORDER = 3;

	// Board size, in cells
	int nCols;
	int nRows;

	// Game state
	int userPiece = CROSS;
	int turnNum;
	int winner;

	// When either side wins, we store coordinates of an unbroken row
	// of five pieces
	int winRow1;
	int winCol1;
	int winRow2;
	int winCol2;

	// Win counts for each side
	int crossesScore = 0;
	int zerosScore = 0;

	// Rendering info
	int x0;
	int y0;
	int cellWidth;
	int cellHeight;
	Color bgColor;
	Color fgColor;
	Dimension minDimension=new Dimension(1024, 768);

	// Cursor location
	int currCol;
	int currRow;

	/**
	 * Create a board.
	 * 
	 * @param nCols
	 *            the number of columns on the board
	 * @param nRows
	 *            the number of rows on the board
	 * @param cellWidth
	 *            individual cell width in pixels
	 * @param cellHeight
	 *            individual cell height in pixels
	 */
	public GoMokuBoard(int nCols, int nRows, int cellWidth, int cellHeight) {

		this.nCols = nCols;
		this.nRows = nRows;
		this.cellWidth = cellWidth;
		this.cellHeight = cellHeight;
		minDimension = new Dimension(nCols * cellWidth + 1, nRows * cellHeight
				+ 1);

		bgColor = (getBackground() == null) ? Color.white : getBackground();
		fgColor = (getForeground() == null) ? Color.black : getForeground();

		newGame();
	}

	/**
	 * Start new game
	 */
	public void newGame() {
		cells = new int[nCols][nRows];
		turnNum = 0;
		winner = EMPTY;

		currCol = nCols / 2;
		currRow = nRows / 2;

		if (userPiece == ZERO) {
			nextTurn();
		}
		GameState.onNewGame();	
		repaint();
	}

	/**
	 * Swap positions and have computer make the next turn for the user. If
	 * called repeatedly, computer will play with itself.
	 */
	public void swapPositions() {
		userPiece = (userPiece == CROSS) ? ZERO : CROSS;
		crossesScore = zerosScore = 0;
		nextTurn();
		repaint();
	}

	/**
	 * Set the user piece at the current cursor position and calculate the
	 * response.
	 */
	public void nextTurn() {
		if (winner != EMPTY){
			GameState.onGameOver();
			return; // game is already over
		}
		int bestRow = -1;
		int bestCol = -1;
		int bestEval = -1;

		if (turnNum == 0) {
			// start in the center
			bestRow = nRows / 2;
			bestCol = nCols / 2;
		} else {
			for (int i = nCols - 1; i >= 0; i--)
				for (int j = nRows - 1; j >= 0; j--)
					if (cells[i][j] == EMPTY) {
						int eval = getCellValue(i, j);
						if (eval > bestEval) {
							bestEval = eval;
							bestCol = i;
							bestRow = j;
						}
					}
		}
		GameState.sendEval(bestEval);
		
		setCell(bestCol, bestRow, userPiece == ZERO ? CROSS : ZERO);
	}

	/**
	 * Process mouse event
	 */
	public boolean mouseDown(Event event, int x, int y) {
		if (winner != EMPTY){
			GameState.onGameOver();
			return false; // game is already over
		}
		int i = x / cellWidth;
		int j = y / cellHeight;

		if (cells[i][j] == EMPTY) {
			setCell(i, j, userPiece);
			nextTurn();
		}

		return false;
	}

	/**
     * Minimum and preferred sizes are equal to the board width and height in 
     * pixels.
     */
	public Dimension minimumSize() {
		return minDimension;
	}

	/**
     * Minimum and preferred sizes are equal to the board width and height in 
     * pixels.
     */
	public Dimension preferredSize() {
		return minDimension;
	}

	/**
	 * Draw the board
	 */
	public void paint(Graphics g1d) {
	    Graphics2D g = (Graphics2D) g1d.create();
	    g.setStroke(new BasicStroke(3.0f));
		int width = nCols * cellWidth;
		int height = nRows * cellHeight;

		// Draw grid
		g.setColor(Color.blue);
		for (int i = nCols; i >= 0; i--)
			g.drawLine(x0 + i * cellWidth, y0, x0 + i * cellWidth, y0 + height);
		for (int j = nRows; j >= 0; j--)
			g.drawLine(x0, y0 + j * cellHeight, x0 + width, y0 + j * cellHeight);

		// Draw pieces
		g.setColor(fgColor);
		for (int i = nCols - 1; i >= 0; i--)
			for (int j = nRows - 1; j >= 0; j--)
				switch (cells[i][j]) {
				case EMPTY:
					// int eval = evaluate( i,j );
					// g.drawString( ""+eval, i*cellWidth + 1, j*cellHeight -9
					// );
					break;

				case CROSS:
					g.drawLine(i * cellWidth + 2 + x0, j * cellHeight + 2 + y0,
							(i + 1) * cellWidth - 2 + x0, (j + 1) * cellHeight
									- 2 + y0);
					g.drawLine((i + 1) * cellWidth - 2 + x0, j * cellHeight + 2
							+ y0, i * cellWidth + 2 + x0, (j + 1) * cellHeight
							- 2 + y0);
					break;

				case ZERO:
					g.drawOval(i * cellWidth + 2 + x0, j * cellHeight + 2 + y0,
							cellWidth - 4, cellHeight - 4);
					break;

				default:
					// error!
					g.fillRect(i * cellWidth + 1 + x0, j * cellHeight + 1 + y0,
							cellWidth - 1, cellHeight - 1);
				}

		// Draw winner
		if (winner != EMPTY) {
			g.setColor(Color.red);
			g.drawLine(winCol1 * cellWidth + cellWidth / 2, winRow1
					* cellHeight + cellHeight / 2, winCol2 * cellWidth
					+ cellWidth / 2, winRow2 * cellHeight + cellHeight / 2);
			g.drawLine(winCol1 * cellWidth + cellWidth / 2 + 1, winRow1
					* cellHeight + cellHeight / 2, winCol2 * cellWidth
					+ cellWidth / 2 + 1, winRow2 * cellHeight + cellHeight / 2);
			g.drawLine(winCol1 * cellWidth + cellWidth / 2, winRow1
					* cellHeight + cellHeight / 2 + 1, winCol2 * cellWidth
					+ cellWidth / 2, winRow2 * cellHeight + cellHeight / 2 + 1);

		}
	}

	/**
	 * Set cell at the given row and column to the given piece, check whether
	 * we've got a winner, and update game state.
	 */
	protected void setCell(int col, int row, int val) {
		turnNum++;
		cells[col][row] = val;
		if (checkWinner(col, row)) {
			GameState.onGameOver();
			repaint(); 
		} else {
			GameState.onMove(col,row,cells);
			repaint(col * cellWidth + 1, row * cellHeight + 1, cellWidth - 1,
					cellHeight - 1);
		}
	}

	// Delta coordinates for S, SE, E, and SW directions.
	int dx[] = { 0, -1, -1, -1 };
	int dy[] = { -1, -1, 0, 1 };

	/**
	 * Get cell values for a line of ten pieces - five on each side from the
	 * given row and column, oriented in a given direction. There are four
	 * possible directions: horizontal, vertical, and two diagonals. To
	 * distinguish between diagonals, we'll call directions S, SE, E, and SW
	 * (think of an azimuth).
	 * 
	 */
	int[] getLine(int col, int row, int direction) {
		int line[] = new int[10];

		// First get the south side of the line
		int i = col;
		int j = row;

		for (int k = 4; k >= 0; k--) {
			i += dx[direction];
			j += dy[direction];

			if (i >= 0 && j >= 0 && i < nCols && j < nRows) // are we on board?
				line[k] = cells[i][j];
			else
				line[k] = BORDER;
		}

		// Then get the north side
		i = col;
		j = row;

		for (int k = 5; k <= 9; k++) {
			i -= dx[direction];
			j -= dy[direction];

			if (i >= 0 && j >= 0 && i < nCols && j < nRows) // are we on board?
				line[k] = cells[i][j];
			else
				line[k] = BORDER;
		}

		return line;
	}

	/**
	 * Calculate line value for a given player.
	 * 
	 * @param line
	 *            the line, as returned by getLine
	 * @param playerPiece
	 *            whether we're calculating for CROSSes or ZEROs
	 * @return weight value
	 */
	int getLineValue(int[] line, int playerPiece) {
		int best = 0;
		int first = 0;
		int last = 0;
		int gaps = 0;
		boolean near = false;

		for (int i = 1; i < 6; i++) {
			first = 0;
			last = 0;
			gaps = 0;
			near = false;
			int s = i;

			while (gaps < 4 && s < i + 4) {
				if (line[s] == EMPTY) {
					gaps++;
				} else if (line[s] == playerPiece) {
					last = s;
					if (first == 0)
						first = s;
					near |= (s == 4 || s == 5);
				} else {
					gaps = 4;
				}

				s++;

			} // while

			int dg = 4 - gaps;
			int eval = dg * dg;

			if (near) // next cell is occupied with own piece
				eval++;

			if (last - first < dg) // line has no gaps
				eval++;

			if (line[i - 1] != userPiece
					&& // not blocked
					line[i + 4] != userPiece && line[i - 1] != BORDER
					&& line[i + 4] != BORDER)
				eval++;

			if (eval > best)
				best = eval;

		} // for

		return best;
	}

	/**
	 * Allocate an array to hold weights for different directions in the
	 * evaluate method (so that we don't have to realloc it every time)
	 */
	int weights[] = new int[4];

	/**
	 * Evaluate the value of a given cell
	 */
	int getCellValue(int col, int row) {
		for (int dir = 0; dir < 4; dir++) {
			int[] line = getLine(col, row, dir);
			int myWeight = getLineValue(line, userPiece == ZERO ? CROSS : ZERO) + 2;
			int hisWeight = getLineValue(line, userPiece);
			weights[dir] = Math.max(myWeight, hisWeight) - 2;
		}
		Arrays.sort(weights);
		return weights[3] * 64 + weights[2] * 16 + weights[1] * 4 + weights[0];
	}

	/**
	 * Check if the last player won the game and update scores.
	 * 
	 * @param col
	 *            last played column
	 * @param row
	 *            last played row
	 */
	boolean checkWinner(int col, int row) {
		int lastPlayed = cells[col][row];
		int dir = 0;
		int k;

		do {
			int line[] = getLine(col, row, dir);
			winCol1 = col;
			winRow1 = row;
			winCol2 = col;
			winRow2 = row;
			k = 1;

			for (int p = 4; p >= 0; p--)
				if (line[p] == lastPlayed) {
					winCol2 += dx[dir];
					winRow2 += dy[dir];
					k++;
				} else
					break;

			for (int p = 5; p <= 9; p++)
				if (line[p] == lastPlayed) {
					winCol1 -= dx[dir];
					winRow1 -= dy[dir];
					k++;
				} else
					break;

			dir++;
		} while (dir < 4 && k < 5);

		if (k < 5)
			return false; // no winner

		winner = lastPlayed;

		if (winner == CROSS)
			crossesScore++;
		else
			zerosScore++;

		return true;
	}

	/**
	 * Get score string
	 */
	String getScore() {
		return "Score: You (" + (userPiece == CROSS ? 'X' : 'O') + "): "
				+ (userPiece == CROSS ? crossesScore : zerosScore)
				+ ", Computer (" + (userPiece != CROSS ? 'X' : 'O') + "): "
				+ (userPiece != CROSS ? crossesScore : zerosScore);
	}
};