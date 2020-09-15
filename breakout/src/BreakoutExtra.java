/* 
 * Name: Priya Nigam
 * Section Leader: Brian Zhang
 * Program: breakout.java
 * Function: To create a version of the game breakout using skills discussed in class such as
 * instance variables and mouse events that can be functional even when certain constants are changed.
 * Optional features:
 * 1. Added WaitforClick(); in the reset ball method, etc.
 * 2. Added bounce.au sound whenever ball hits paddle, brick, left wall, right wall, or ceiling.
 * 3. added "Click to play!" button by adding methods for adding click to play, waiting for click,
 *  and removing click to play primarily in the startBoard() and resetBall() methods.
 * 4. Kicker added by using a time instance variable and changing the time between pauses 
 * such that as the ball hits the walls, the pause time or the delay decreases.
 * 5. Paddle changer added by creating a a different sized paddle when ball is reset. This paddle 
 * becomes bigger or smaller depending upon a randomly generated number 
 * that the paddle height and width is divided by.  
 * 6. Ball color changer added by setting the color of the ball to the brick it most recently collided with.
 */

import acm.graphics.*;     // GOval, GRect, etc.
import acm.program.*;      // GraphicsProgram
import acm.util.*;         // RandomGenerator

import java.applet.*;      // AudioClip
import java.awt.*;         // Color
import java.awt.event.*;   // MouseEvent

public class BreakoutExtra extends BreakoutProgram {

/*
 * Function: Create instance variables for paddle, x and y velocity of ball, score label, score,
 * number of turns, and number of bricks so that these values can be used in
 * multiple methods, especially to prevent bugs and to keep track of values.
 */

	private GRect paddle; 
	// For the paddle in the game, created because we needed to utilize mouse events.
	
	private double xVelocity; 
	//For the xVelocity of the ball, created as an instance variable 
	//because it had to be called upon multiple times and has to persist throughout the program.
	
	private double yVelocity; 
	//For the yVelocity of the ball, created as an instance variable 
	//because it had to be called upon multiple times and has to persist throughout the program.
	
	private GLabel scoreLabel; 
	//For the score label  at the top left corner of the screen, created as an instance 
	//variable because it must persist through the program and 
	// otherwise, the ball could hit it and it would disappear.
	
	private int score; 
	//Used to keep track of score, and thus is an instance variable because
	//it changes throughout the program.
	
	private int numberOfTurns; 
	//Used to keep track of number of turns left, and thus is an instance variable because it 
	//changes throughout the program and affects several methods.
	
	private int numberOfBricks; 
	//Used to keep track of number of bricks remaining, and thus is an instance variable 
	// because it changes throughout the program and affects several methods.        
	
	private int time; 
	//Used for kicker method because it is changed and reset with each turn and every collision with the walls. 
	
	private static final double TIME_ADDITION = .99;
	//Used to decrease the pause time as the game continues so that the ball speeds up. 

	public void run() {		
		addScoreTurns();
		startBoard();
		addMouseListeners();
	}
	
	/*
	 * Function: To create the board that is used for playing by producing the paddle,
	 * click to play button, the ball, and the bricks.
	 * This also allows these items to move and checks for winning or losing, 
	 * allowing for the reset of the game. 
	 */
	private void startBoard() {
		
		setSize(APPLICATION_WIDTH, APPLICATION_HEIGHT);
		drawPaddle();
		
		GOval ball = drawBall();
		add(ball);
		completeBricks();
		GLabel clickToPlay = new GLabel("CLICK TO PLAY!");
		
		addClickToPlay(clickToPlay);
		waitForClick();
		removeClickToPlay(clickToPlay);
		
		bounceBall(ball, clickToPlay);
		checkForWinning(ball);
	}
	
	/*
	 * Function: To check for the end of a turn by seeing if the ball is 
	 * below the floor, and then accordingly changing the
	 * score label to reflect that a turn is gone, as well as reseting 
	 * the ball with a new random x velocity.  
	 */
	private void checkEndOfTurn(GOval ball, GLabel clickToPlay) {
		
		if (ballBelowFloor(ball)) {
			//Function: numberOfTurns decreases every time the ball goes below the floor
			numberOfTurns--;
			scoreLabel.setLabel("Score: " + score + "  Turns: " + numberOfTurns);
			resetBall(ball, clickToPlay);
			//Function: randomly generates an x velocity between the min and max and 
			//multiplies it by -1 if (rgen.nextBoolean())
			RandomGenerator rgen = RandomGenerator.getInstance();
			xVelocity = rgen.nextDouble(VELOCITY_MIN , VELOCITY_MAX );
			if (rgen.nextBoolean()) {
				xVelocity *= -1;	
			}	
		}
	}
	
	/*
	 * Function: Adds Click to Play button onto the screen
	 */
	private void addClickToPlay(GLabel clickToPlay) {
		clickToPlay.setFont(SCREEN_FONT);
		double x = (getWidth() / 2.0) - (clickToPlay.getWidth()/2); 
		double y = (getHeight() / 2.0) -   BALL_RADIUS - (clickToPlay.getHeight()/2);
		add(clickToPlay, x, y);
	}
	
	/*
	 * Function: Removes Click to Play button from the screen
	 */
	private void removeClickToPlay(GLabel clickToPlay) {
		remove(clickToPlay);
	}
	
	/*
	 * Function: To reset the ball and create a "click to play" button if 
	 * you have lost a turn but have not lost the game, 
	 * as well as to signal the end of a game if all turns have been used by removing the paddle, 
	 * removing the ball, and creating a new label.
	 */
	private void resetBall(GOval ball, GLabel clickToPlay) {
		if (numberOfTurns > 0) {
			double x = getWidth()/2- BALL_RADIUS;
			double y = getHeight()/2 - BALL_RADIUS;
			ball.setLocation(x, y);
			ball.setColor(Color.BLACK);
			time = 0;
			addClickToPlay(clickToPlay);
			waitForClick();
			removeClickToPlay(clickToPlay);
			//Changes size of paddle based on division of size by randomly generated number 
			RandomGenerator rgen = RandomGenerator.getInstance();
			paddle.setSize(PADDLE_WIDTH / rgen.nextDouble(.75, 3), PADDLE_HEIGHT / rgen.nextDouble(.75,3));
			//Function: resets ball in the middle of the board 
		} else {
			//Function: Signals the end of game
			startGameover(ball);
			
		}
		
	}
	
	/*
	 * Function: To signal the end of the game by creating a game over label,
	 * removing the paddle, and removing the ball. 
	 */
	private void startGameover(GOval ball) {
		GLabel gameover = new GLabel( " GAME OVER " );
		gameover.setFont(SCREEN_FONT);
		double x = (getWidth() / 2.0) - (gameover.getWidth()/2); 
		double y = (getHeight() / 2.0) -  (gameover.getHeight()/2);
		add(gameover, x, y);
		remove(ball);
		remove(paddle);
		scoreLabel.setLabel("Score " + score + "  Turns " + numberOfTurns);
	}
	
	/*
	 * Function: To create the bricks that are needed for the game in 
	 * specific colors, rows, and bricks per row. 
	 * This is implemented using a nested for loop.
	 */
	private void completeBricks() {
		//Function: To determine size of one side of board, which can be used to determine starting x of row.
		double sideSize = NBRICKS_PER_ROW * BRICK_WIDTH + (NBRICKS_PER_ROW - 1) * BRICK_SEP;
		double xStart = (getWidth() / 2.0) - sideSize / 2.0; 
		double yStart = BRICK_Y_OFFSET;
		numberOfBricks = NBRICKS_PER_ROW * NBRICK_ROWS;
		// Function: creates a nested for loop that draws bricks of a certain color in a row for a number of rows.
		for (int i = 0; i < NBRICK_ROWS; i++) { 
			for (int col = 0; col < NBRICKS_PER_ROW; col++) {
				double x = xStart + col * (BRICK_WIDTH + BRICK_SEP); 
				double y = yStart + i * (BRICK_HEIGHT + BRICK_SEP);
				drawBrick(x, y, i);
			}
		}
		
	}
	
	
	
	/*
	 * Function: To add the GLabel that keeps track of the score in the game.
	 */
	private void addScoreTurns() {
		numberOfTurns = NTURNS;
		scoreLabel = new GLabel("Score: " + score + "   Turns: " + numberOfTurns);
		scoreLabel.setFont(SCREEN_FONT);
		double y = scoreLabel.getHeight();
		add(scoreLabel, 0, y);
	}

	
	/*
	 * Function: To draw a single brick that is a certain height and width 
	 * in a particular location with a particular color.
	 */
	private void drawBrick(double x, double y, int index) {
		GRect brick = new GRect(x, y, BRICK_WIDTH, BRICK_HEIGHT); 
		Color brickColor = getColor(index);
		brick.setColor(brickColor);
		brick.setFilled(true);
		add(brick);
	}
	
	/*
	 * Function: To set the color of a brick in the game based on its row 
	 * number such that the order of colors is red, orange, yellow, green, and 
	 * cyan, with the colors repeating after 10 rows and each color having two rows 
	 * each time. 
	 */
	private Color getColor(int index) {
		if (index % 10 == 1 || index % 10 == 0) {
			return Color.RED;
		} else if (index % 10 == 2 || index % 10 == 3) {
			return Color.ORANGE;
		} else if (index % 10 == 4 || index % 10 == 5) {
			return Color.YELLOW;
		} else if (index % 10 == 6 || index % 10 == 7) {
			return Color.GREEN;
		} else {
			return Color.CYAN;
		}
	}

	/*
	 * Function: To draw the paddle used in the game at a location in the bottom of the screen.
	 */
	private void drawPaddle() {
		paddle = new GRect(PADDLE_WIDTH, PADDLE_HEIGHT);
		paddle.setFilled(true);
		//Function: fills paddle in black and sets it's original position
		double x = getWidth() / 2.0 - PADDLE_WIDTH/2.0;
		double y = getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT;
		add(paddle, x, y);
		
	}
	
    
	/*
	 * Function: Allows the paddle to move whenever the mouse moves so that 
	 * it can be used to prevent the ball from hitting the floor.
	 */
	public void mouseMoved(MouseEvent e) {	
		double x = e.getX() - paddle.getWidth()/2;
		double y =  getHeight() - PADDLE_Y_OFFSET - PADDLE_HEIGHT;
		//Function: Makes sure that the paddle does not go off the screen
		if (e.getX() + (paddle.getWidth()/2) <= getWidth() && e.getX() - (paddle.getWidth()/2) >= 0) {
		paddle.setLocation(x, y);
		}
	}
	
	/*
	 * Function: Create the ball that is used in the game with a specific size 
	 * and original location based on the BALL_RADIUS constant.
	 */
	private GOval drawBall() {
		GOval ball = new GOval(getWidth()/2-BALL_RADIUS, getHeight()/2 - BALL_RADIUS, BALL_RADIUS * 2, BALL_RADIUS * 2);
		ball.setFilled(true);
		return(ball);
	}
	
	/*
	 * Function: To allow for the bouncing of the ball off surfaces by creating a 
	 * randomly generated xVelocity, allowing the ball to move in particular directions 
	 * and change velocities based on what it hits, pause to allow us to see the motion,
	 *  and check for collisions or the end of a turn. 
	 */
	private void bounceBall(GOval ball, GLabel clickToPlay) {
		time = 0;
		RandomGenerator rgen = RandomGenerator.getInstance();
		yVelocity = VELOCITY_MAX;
		//Function: randomly generates x velocity between minimum and maximum and multiplies by -1 if rgen.nextBoolean.
		xVelocity = rgen.nextDouble(VELOCITY_MIN , VELOCITY_MAX );
		if (rgen.nextBoolean()) {
			xVelocity *= -1;	
		}
		while (true) {
			
			ball.move(xVelocity, yVelocity);
			
			//Function: adds sound and changes YVelocity to -1 so that it bounces off the wall.
			if((ballAboveCeiling(ball) || 
					ballBelowFloor(ball))) {
				
				AudioClip bounceClip = MediaTools.loadAudioClip("res/bounce.au");
				 bounceClip.play();
				 yVelocity *= -1; 	
				 time += TIME_ADDITION;
			}
			
			//Function: adds sound and changes YVelocity to -1 so that it bounces off the wall.
			if((ballPastRight(ball) || ballPastLeft(ball))) {
				AudioClip bounceClip = MediaTools.loadAudioClip("res/bounce.au");
				 bounceClip.play();
				xVelocity *= -1;
				time += TIME_ADDITION;
			}
			// Function: changes pause time such that each time, the ball appears to speed up.
			double pausetime = DELAY / (time + 1);
			pause(pausetime);
			
			checkForCollisions(ball.getX(), ball.getY(), ball.getX() + 2* BALL_RADIUS, ball.getY(), ball.getX(),
					ball.getY() + 2*BALL_RADIUS, ball.getX() + 2*BALL_RADIUS, ball.getY() + 2*BALL_RADIUS, ball);
			
			
			checkEndOfTurn(ball, clickToPlay);
			
			if (numberOfBricks == 0 || numberOfTurns == 0) {
				break;
			}
		}	
	}
	
	
	/*
	 * Function: Describes when ball above ceiling is true, which can then be used in 
	 * other methods to change the velocity 
	 * of the ball when this occurs so that the ball bounces off the surface. 
	 */
	private boolean ballAboveCeiling(GOval ball) {
		return ball.getY() + ball.getHeight() <= 0;
	}
	
	
	/*
	 * Function: Describes when ball below floor is true, which can then be used in 
	 * other methods to signify that this means a turn has ended.
	 */
	private boolean ballBelowFloor(GOval ball) {
		return ball.getY() + ball.getHeight() >= getHeight();
	}
	
	/*
	 * Function: Describes when location of the ball past the right window edge is 
	 * true, which can then be used in other methods to change the velocity 
	 * of the ball when this occurs so that the ball bounces off the surface. 
	 */
	private boolean ballPastRight(GOval ball) {
		return ball.getX() + ball.getWidth() >= getWidth();
	}
	
	
	
	/*
	 * Function: Describes when location of the ball past the left window edge is 
	 * true, which can then be used in other methods to change the velocity 
	 * of the ball when this occurs so that the ball bounces off the surface. 
	 */
	private boolean ballPastLeft(GOval ball) {
		return ball.getX() + (ball.getWidth() / 2.0) <= 0;
	}

	
	/*
	 * Function: To check for collisions with the bricks and allow the bricks get 
	 * removed in the game; to check for collisions with the paddle and allow the 
	 * ball to bounce off of it; to use the getElementAt function to determine if any
	 * part of the ball has collided with an object and act according to what the 
	 * object is; to increase the score and decrease the number of bricks remaining 
	 * in the count that was established as each brick is collided with and removed; 
	 * to change the color of the ball to the brick it just hit.
	 */
	private void checkForCollisions(double x, double y, double x2, double y2, double x3, double y3, double x4, double y4, GOval ball) {
		GObject collider = getElementAt(x, y, x2, y2, x3, y3, x4, y4);
		if (collider != null && collider != paddle && collider != scoreLabel) {
			AudioClip bounceClip = MediaTools.loadAudioClip("res/bounce.au");
			 bounceClip.play();
			 //Function: To change the color of the ball to the color of the brick it most recently hit.
			 ball.setColor(collider.getColor());
			 remove(collider);
			yVelocity *= -1;
			score++;
			scoreLabel.setLabel("Score: " + score + "  Turns: " + numberOfTurns);
			numberOfBricks--;
		}
		if (collider == paddle)	{
			AudioClip bounceClip = MediaTools.loadAudioClip("res/bounce.au");
			 bounceClip.play();
			//Function: To fix sticky paddle case by ensuring that yVelocity is (-) when it leaves the paddle.
			yVelocity = Math.abs(yVelocity);
			yVelocity *= -1;
			
		}
	}
	
	
	/*
	 * Function: To check if there are no bricks remaining in the game, and if so to present 
	 * a new Glabel as well as the removal of the ball and paddle to signify that the player
	 *  has won the game. 
	 */
	private void checkForWinning(GOval ball) {
		if (numberOfBricks == 0) {
			GLabel youwin = new GLabel( " YOU WON! " );
			youwin.setFont(SCREEN_FONT);
			double x = (getWidth() / 2.0) - (youwin.getWidth()/2); 
			double y = (getHeight() / 2.0) -  (youwin.getHeight()/2);
			add(youwin, x, y);
			remove(ball);
			remove(paddle);
			
		}		
	}

	
}
