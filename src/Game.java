import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferStrategy;

/**
 * @author Itai Rivkin-Fish
 * @version 9/12/17
 * 
 *  This class does all of the game and GUI handling
 */ 
public class Game extends Canvas implements Runnable{
	private static final long serialVersionUID = 1L;
	
	public static final int minefieldOffsetX = 10;
	public static final int minefieldOffsetY = 160;
	
	final private static int resetButtonX = 380;
	final private static int resetButtonY = 70;
	final private static int resetButtonWidth = 100;
	final private static int resetButtonHeight = 30;
	
	static long gameStartTime;
	private boolean isRunning = false;
	private Thread thread;
	
	private final int xSize,ySize;
	
	private Minesweeper game;
	/**
	 * 
	 * @param xSize - Screen size X
	 * @param ySize - Screen size Y
	 * @param game - Minesweeper game to access
	 */
	public Game(int xSize, int ySize, Minesweeper game) {
		this.xSize = xSize;
		this.ySize = ySize;
		this.game = game;
		
		new Window(xSize,ySize,"Minesweeper",this);
		
		gameStartTime = System.nanoTime();
		//Mouse listener to handle mouse clicks
		addMouseListener(
			new MouseAdapter() { 
	          public void mousePressed(MouseEvent me) { 
	            handleMouse(me); 
	          } 
			}
		); 
		
		start();
	}
	/**
	 * 
	 * @param me - mouse event to handle
	 * this function checks mouse clicks and deals with them
	 */
	protected void handleMouse(MouseEvent me) {
		if(me.getButton() == 1) {//Left Click
			int mouseX = me.getX();
			int mouseY = me.getY();
			
			int convertedMouseX = (int)Math.floor(((double)mouseX-(double)minefieldOffsetX)/(double)Cell.cellSize);
			int convertedMouseY = (int)Math.floor(((double)mouseY-(double)minefieldOffsetY)/(double)Cell.cellSize);
			if(convertedMouseX >= 0 && convertedMouseX < game.GRID_WIDTH && convertedMouseY >= 0 && convertedMouseY < game.GRID_HEIGHT && game.getCellArray()[convertedMouseY][convertedMouseX].getCellContent() == ' ') {
				if(game.getCellArray()[convertedMouseY][convertedMouseX].getContainsBomb() == false) {//if the cell clicked on does not contain a bomb
					game.revealCell(convertedMouseX,convertedMouseY);
				}else {
					game.revealEntireMinefield();
				}
			}else {
				if(mouseX > resetButtonX && mouseX <  resetButtonX+resetButtonWidth && mouseY > resetButtonY & mouseY < resetButtonY+resetButtonHeight) {
					game.reset();
				}
			}
			
		}else if(me.getButton() == 3) {//right click
			int mouseX = me.getX();
			int mouseY = me.getY();
			
			int convertedMouseX = (mouseX-minefieldOffsetX)/Cell.cellSize;
			int convertedMouseY = (mouseY-minefieldOffsetY)/Cell.cellSize;
			if(convertedMouseX >= 0 && convertedMouseX < game.GRID_WIDTH && convertedMouseY >= 0 && convertedMouseY < game.GRID_HEIGHT) {
				Cell cell = game.getCellArray()[convertedMouseY][convertedMouseX];
				if(cell.getCellContent() == ' ') {
					cell.setCellContent(Minesweeper.FLAG.charAt(0));
					game.setUnflaggedMines(game.getUnflaggedMines()-1);
				}else if(cell.getCellContent() == Minesweeper.FLAG.charAt(0)) {
					cell.setCellContent(' ');
					game.setUnflaggedMines(game.getUnflaggedMines()+1);
				}
				
			
			}
			
		}
		
	}
	/**
	 * Starts a thread and sets isRunning to true
	 */
	private void start() {
		isRunning = true;
		thread = new Thread(this);
		thread.start();
	}
	/**
	 * Stops a thread and sets isRunning to false
	 */
	private void stop() {
		isRunning = false;
		try {
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Code that runs constantly- calling the render and tick functions
	 * Borrowed from Zachary Berenger who copied it from Notch who copied it from who knows?
	 */
	public void run() {
		this.requestFocus();
		long lastTime = System.nanoTime();
		double ticksPerSecond = 60.0;
		double nanoSecondsPerTick = Math.pow(10, 9)/ticksPerSecond;
		double ticksToRender = 0;
		long fpsTimer = System.currentTimeMillis();
		int frames = 0;
		while(isRunning) {
			long currentTime = System.nanoTime();
			ticksToRender += (currentTime - lastTime)/nanoSecondsPerTick;
			lastTime = currentTime;
			while(ticksToRender >= 1) {
				render();
				ticksToRender--;
			}
			
			frames++;
			if(System.currentTimeMillis() - fpsTimer > 1000) {
				fpsTimer += 1000;
				frames = 0;
			}
		}
		stop();
	}
	
	public void tick() {
		
	}
	/**
	 * 
	 * @param g - graphics object
	 * @param toDraw - string to draw
	 * @param drawX - x position of draw
	 * @param drawY - y position of draw
	 * @param outlineColor - color to outline
	 */
	public void drawStringOutline(Graphics g,String toDraw,int drawX, int drawY, Color outlineColor) {
		Color savedColor = g.getColor();
		
		g.setColor(outlineColor);
		
		for(int i = -1; i <= 1; i++) {
			for(int j = -1; j <= 1; j++) {
				g.drawString(toDraw, drawX + i, drawY + j);
			}
		}
		
		g.setColor(savedColor);
		
		g.drawString(toDraw, drawX, drawY);
		
	}
	/**
	 * overload of previous function
	 * @param g - graphics object
	 * @param toDraw - string to draw
	 * @param drawX - x position of draw
	 * @param drawY - y position of draw
	 * @param outlineColor - color to outline
	 * @param outlineWidth - Width of outline
	 */
	public void drawStringOutline(Graphics g,String toDraw,int drawX, int drawY, Color outlineColor, int outlineWidth) {
		Color savedColor = g.getColor();
		
		g.setColor(outlineColor);
		
		for(int i = -outlineWidth; i <= outlineWidth; i++) {
			for(int j = -outlineWidth; j <= outlineWidth; j++) {
				g.drawString(toDraw, drawX + i, drawY + j);
			}
		}
		
		g.setColor(savedColor);
		
		g.drawString(toDraw, drawX, drawY);
		
	}
	/**
	 * 
	 * @param g - graphics object
	 * @param drawX - x position to draw
	 * @param drawY - y postion to draw
	 */
	private void drawElapsedTime(Graphics g,int drawX, int drawY) {
		g.setColor(Color.WHITE);
		
		long elapsedTime = (System.nanoTime()-gameStartTime)/(long)Math.pow(10, 9);
		int elapsedHours = (int)(elapsedTime/3600);
		int elapsedMins = (int) ((elapsedTime-(elapsedHours*3600))/60);
		int elapsedSeconds = (int) ((elapsedTime-(elapsedMins*60)));
		
		String elapsedHoursString = "" + elapsedHours;
		if(elapsedHoursString.length() == 1) {
			elapsedHoursString = "0" + elapsedHoursString;
		}
		
		String elapsedMinsString = "" + elapsedMins;
		if(elapsedMinsString.length() == 1) {
			elapsedMinsString = "0" + elapsedMinsString;
		}
		
		String elapsedSecondsString = "" + elapsedSeconds;
		if(elapsedSecondsString.length() == 1) {
			elapsedSecondsString = "0" + elapsedSecondsString;
		}
		
		String elapsedTimeString =  elapsedHoursString + ":" + elapsedMinsString + ":" + elapsedSecondsString;
		
		drawStringOutline(g,elapsedTimeString,drawX,drawY,Color.BLACK,2);
	}
	/**
	 * Draws everything that needs drawing
	 */
	public void render() {
		BufferStrategy bs = this.getBufferStrategy();
		if(bs == null) {
			this.createBufferStrategy(3);
			return;
		}
		Graphics g = bs.getDrawGraphics();
		/***BEGIN DRAW***/
		//draw background
		g.setColor(Color.gray);
		g.fillRect(0, 0, this.xSize, this.ySize);
		//draw title
		g.setColor(Color.orange);
		Font titleFont = new Font("sansserif", Font.ITALIC , 42);
		g.setFont(titleFont);
		drawStringOutline(g, "Fisherswamp's Minesweeper", 10, 50, Color.red);
		//set font for remaining headers
		Font headerFont = new Font("sansserif", Font.PLAIN , 38);
		g.setFont(headerFont);
		//draw elapsed time
		drawElapsedTime(g,10,100);
		//draw remaining mines
		g.setColor(Color.white);
		drawStringOutline(g,"Mines: " + game.getUnflaggedMines(),190,100,Color.BLACK,2);
		//draw reset button
		g.setColor(Color.white);
		g.fillRect(resetButtonX, resetButtonY, resetButtonWidth, resetButtonHeight);
		g.setColor(Color.black);
		g.drawRect(resetButtonX, resetButtonY, resetButtonWidth, resetButtonHeight);
		g.drawString("Reset", resetButtonX, resetButtonY + resetButtonHeight);
		//draw cells
		
		Cell[][] cellArray = game.getCellArray();
		for(int i = 0; i < game.GRID_HEIGHT; i++) {
			for(int j = 0; j < game.GRID_WIDTH; j++) {
				int drawX = minefieldOffsetX + (j*Cell.cellSize); //+ (5*j);
				int drawY = minefieldOffsetY + (i*Cell.cellSize); //+ (5*i);
				
				Color cellColor = cellArray[i][j].getCellContent() == ' ' ? Color.DARK_GRAY : Color.WHITE; 
				g.setColor(cellColor);
				
				g.fillRect(drawX,drawY,Cell.cellSize,Cell.cellSize);
				g.setColor(Color.BLACK);
				g.drawRect(drawX, drawY,Cell.cellSize,Cell.cellSize);
				String toDraw = Character.toString(cellArray[i][j].getCellContent());
				
				Font fnt = new Font("Serif", Font.BOLD, 16);
				g.setFont(fnt);
				
				switch(toDraw) {
					case "0":
						toDraw = " ";
					break;
					case "1":
						g.setColor(Color.blue);
					break;
					case "2":
						g.setColor(Color.GREEN);
					break;
					case "3":
						g.setColor(Color.cyan);
					break;
					case "4":
						g.setColor(Color.pink);
					break;
					case "5":
						g.setColor(Color.orange);
					break;
					case "6":
						g.setColor(Color.RED);
					break;
					case "7":
						g.setColor(Color.YELLOW);
					break;
					case "8":
						g.setColor(Color.white);
					break;
					case Minesweeper.FLAG:
						g.setColor(Color.RED);
					break;
					case Minesweeper.BOMB:
						g.setColor(Color.white);
						g.setFont(new Font("Serif",Font.PLAIN,31));
						drawX -= 11;
						drawY += 7;
					break;
				}
				
				
				drawStringOutline(g,toDraw, drawX + (int)(Cell.cellSize/2)-2, drawY + (int)(Cell.cellSize/2)+4,Color.black);
			}
		}
		
		/***END DRAW***/
		g.dispose();
		bs.show();
	}
}
	