/**
 * @author Itai Rivkin-Fish
 * @version 9/12/17
 * This is the minesweeper class, which holds data for a single minesweeper game
 */

import java.util.Random;
import java.util.Scanner;

public class Minesweeper {
	static int numXcells = 40;
	static int numYcells = 20;
	static int numBombs = 60;
	
	final int GRID_WIDTH,GRID_HEIGHT,NUM_MINES;
	private int unflaggedMines;
	final static String FLAG = "\u2691";
	final static String BOMB = "\u2620";//"\uD83D\uDCA3";
	private Cell cellArray[][];
	
	/**
	 * 
	 * @param width - width of the minesweeper game (in number of cells)
	 * @param height - height of the minesweeper game (in number of cells)
	 * @param numMines - number of mines in the game
	 */
	public Minesweeper(int width, int height, int numMines) {
		GRID_WIDTH = width;
		GRID_HEIGHT = height;
		NUM_MINES = numMines;
		unflaggedMines = numMines;
		
		cellArray = new Cell[GRID_HEIGHT][GRID_WIDTH];
		
		for(int i = 0; i < GRID_HEIGHT; i++) {	
			for(int j = 0; j < GRID_WIDTH; j++) {
				cellArray[i][j] = new Cell(' ');
			}
		}
	}
	/**
	 * Reveals the minefield, used when the player loses
	 */
	public void revealEntireMinefield() {
		for(int i = 0; i < GRID_HEIGHT; i++) {	
			for(int j = 0; j < GRID_WIDTH; j++) {
				if(cellArray[i][j].getContainsBomb()) {
					cellArray[i][j].setCellContent(BOMB.charAt(0));
				}else {
					cellArray[i][j].setCellContent(String.valueOf(calculateMinesAroundCell(j,i)).charAt(0));
				}
			}
		}
	}
	/**
	 * 
	 * @param cellX - X cell to check
	 * @param cellY - Y cell to check
	 * @return Returns an integer for the number of mines around a cell
	 */
	public int calculateMinesAroundCell(int cellX, int cellY) {
		int numBombs = 0;
		for(int xOffset = -1; xOffset <= 1; xOffset++) {
			for(int yOffset = -1; yOffset <= 1; yOffset++) {
				
				int checkX = cellX + xOffset;
				int checkY = cellY + yOffset;
				
				if(xOffset != 0 || yOffset != 0) {//if there is some sort of offset (aka dont check yourself silly). Strictly, this check isn't required since theoretically a cell with a bomb would never need to display surrounding bombs, but w/e
					if(checkX >= 0 && checkX < GRID_WIDTH && checkY >=0 && checkY < GRID_HEIGHT) {//if X and Y being checked is within the board
						if(cellArray[checkY][checkX].getContainsBomb()) {//if Cell being checked contains bomb
							numBombs++;
						}
					}
				}
			}
		}
		return numBombs;
	}
	/**
	 *	Recursive function that reveals the current cell (displays the number of mines around it) and if that number is 0, also reveals the surrounding cells 
	 * @param cellX - X cell to reveal
	 * @param cellY - Y cell to reveal
	 */
	public void revealCell(int cellX, int cellY) {
		Cell currentCell = cellArray[cellY][cellX];
		if(currentCell.getContainsBomb() || currentCell.getCellContent() != ' ') {
			return;
		}else {	
			currentCell.setCellContent(String.valueOf(calculateMinesAroundCell(cellX,cellY)).charAt(0) );
			if(currentCell.getCellContent() == '0') {//if this cell is 0, reveal surrounding cells
				
				for(int xOffset = -1; xOffset <= 1; xOffset++) {
					for(int yOffset = -1; yOffset <= 1; yOffset++) {
						int modifiedX = cellX + xOffset;
						int modifiedY = cellY + yOffset;

						if(modifiedY >=0 && modifiedY < GRID_HEIGHT && modifiedX >= 0 && modifiedX < GRID_WIDTH && cellArray[modifiedY][modifiedX].getCellContent() == ' ') {
							revealCell(modifiedX,modifiedY);
						}
					}
				}				
			}
		}
		
	}
	
	
	/***Setters and Getters****/
	public Cell[][] getCellArray() {
		return cellArray;
	}
	public void setUnflaggedMines(int n) {
		unflaggedMines = n;
	}
	public int getUnflaggedMines() {
		return unflaggedMines;
	}
	/*************************/
	
		
	
	public static void main(String[] args) {
		
		
		//Initialize game and game controller
		Minesweeper game = new Minesweeper(numXcells,numYcells,numBombs);
		Game gameController = new Game(numXcells*Cell.cellSize + 30,numYcells*Cell.cellSize + Game.minefieldOffsetY + 50, game);
		
		//Find mine positions and place in game
		boolean[][] minePositions = new boolean[game.GRID_HEIGHT][game.GRID_WIDTH];//array containing the positions of mines to disable duplicates
		Random positionGenerator = new Random();
		int xTest,yTest;
		
		for(int i = 0; i < game.NUM_MINES; i++) {
			do {
				xTest = positionGenerator.nextInt(game.GRID_WIDTH);
				yTest = positionGenerator.nextInt(game.GRID_HEIGHT);
				
			} while(minePositions[yTest][xTest]);//keep getting new positions until there is no mine in the random position
			
			minePositions[yTest][xTest] = true;
			game.getCellArray()[yTest][xTest].setContainsBomb(true);
		}

	}
	/**
	 * Resets the game
	 */
	public void reset() {
		Game.gameStartTime = System.nanoTime();
		for(int i = 0; i < GRID_HEIGHT; i++) {
			for(int j = 0; j < GRID_WIDTH; j++) {
				cellArray[i][j].setCellContent(' ');
				cellArray[i][j].setContainsBomb(false);
			}
		}
		//Find mine positions and place in game
		boolean[][] minePositions = new boolean[GRID_HEIGHT][GRID_WIDTH];//array containing the positions of mines to disable duplicates
		Random positionGenerator = new Random();
		int xTest,yTest;
		for(int i = 0; i < NUM_MINES; i++) {
			do {
				xTest = positionGenerator.nextInt(GRID_WIDTH);
				yTest = positionGenerator.nextInt(GRID_HEIGHT);
				
			} while(minePositions[yTest][xTest]);//keep getting new positions until there is no mine in the random position
			
			minePositions[yTest][xTest] = true;
			getCellArray()[yTest][xTest].setContainsBomb(true);
		}
		unflaggedMines = NUM_MINES;
	}
}
