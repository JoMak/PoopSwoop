
package poopswoop.appdata;
import java.util.LinkedList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
public class PoopSwoop extends Activity implements OnTouchListener, SurfaceHolder.Callback{
	public static int birdW =35;			// width of bird
	public static int birdH =30;			// height of bird
	public static int pooW = birdW/2;		// width of poo
	public static int pooH = birdH/2;		// height of poo
	public static int wiperW=pooW;			// width of wiper
	public static int wiperH = 3*wiperW;	// height of wiper
	static int flashbg =0;					// needed for flashy background
	GameLoop gameLoop;
	public static SurfaceView gameTouch;
	private SurfaceHolder holder;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);     
		setContentView(R.layout.game_sur);     
		gameTouch =(SurfaceView) findViewById(R.id.gameSur);
		gameTouch.setOnTouchListener(this);	
		holder = gameTouch.getHolder();
		gameTouch.getHolder().addCallback(this);

	}
	public static boolean init = true;	// if the game just started
	//private volatile boolean running = true;
	private class GameLoop extends Thread {
		boolean paused = false;
		public void run() {
			Looper.prepare();
			if (init){	// if the game just started
				// initialize variables
				GameAlgs.topBird.clear();
				GameAlgs.midBird.clear();
				GameAlgs.botBird.clear();
				GameAlgs.poo.clear();
				GameAlgs.points = 0;
				GameAlgs.level = 1;
				GameAlgs.lives = 3;
				GameAlgs.levelup =false;
				GameAlgs.numBirds = 0;
				GameAlgs.birdPos = -birdW;
				//upgrades
				GameAlgs.bombx.clear();
				GameAlgs.bomby.clear();
				bomb_notification_elapsed.clear();
				GameAlgs.wiperBig=false;
				GameAlgs.wiper_duration = 0;
				GameAlgs.poo_used=0;
				GameAlgs.numpoo =0;
				init = false;
			}
			while (GameAlgs.level <51 && GameAlgs.lives >0 && !GameAlgs.levelup) {
				// loops the game
				checkNew();	// checks everything (collision, generation)
				draw();		// draws the pictures/graphics
				//Log.v("DEBUG","DEBUGdraw");
				synchronized (this){
					while(paused){
						try{
							wait();
						}catch(Exception e){}
					}
				}
			}
			if (GameAlgs.level>50){	// if the person beat the game
				gameBeaten();
			}
			else if (GameAlgs.levelup){	// level up
				// finish drawing birds that are on the screen
				while (!(GameAlgs.topBird.size()==0 && GameAlgs.midBird.size()==0 && GameAlgs.botBird.size()==0 && GameAlgs.pooOnScreen())&&GameAlgs.lives>0){	
					synchronized (this){
						while(paused){
							try{
								wait();
							}catch(Exception e){}
						}
					}
					// makes sure nothing is on screen
					checkNew();
					draw();
				}
				if (GameAlgs.lives >0){	// if the person didnt die yet
					GameAlgs.poo.clear();	// clears the array of poo
					GameAlgs.levelup = false;	// level is false
					GameAlgs.bombx.clear();
					GameAlgs.bomby.clear();
					bomb_notification_elapsed.clear(); //clear all notifications for bombs
					GameAlgs.wiperBig=false; // clear wiper upgrade
					GameAlgs.wiper_duration = 0;
					GameAlgs.poo_used=0;
					GameAlgs.numpoo =0;
					draw();					// redraw the screen
					GameAlgs.level ++;		// increase level by one
					GameAlgs.numBirds = 0;	// reset birds to zero
					lvlUpDialog();			// shows level up screen
				}
				else {
					gameOver();
				}
			}
			else{	// lost all lives
				gameOver();
			}
			Looper.loop();   

		}
		public void safeStop() {
			//	running = false;
			paused = false;
			finish();
		}
	}
	private void checkNew(){	
		// generates birds and poo, also checks if one has lost a life (poo falls below screen)
		GameAlgs.birdGen();
		GameAlgs.pooGen();
		GameAlgs.checkLife();
	}
	//------------------------------------------------------------DRAWING-------------------------------------------------------------------//
	public static LinkedList<Integer> bomb_notification_elapsed = new LinkedList<Integer>();
	private void drawNotifications(Canvas c){
		//when a bomb hits
		Drawable BOOM = getResources().getDrawable(R.drawable.boom);
		if (GameAlgs.bombx.size()>0){
			for (int i = 0;i<GameAlgs.bombx.size();i++){
				if (bomb_notification_elapsed.get(i) <GameAlgs.bombduration){ // if the bomb has been hit within the duration. continue showing notification
					BOOM.setBounds(GameAlgs.bombx.get(i)-(birdW), GameAlgs.bomby.get(i), GameAlgs.bombx.get(i)+(birdW)+ birdW, GameAlgs.bomby.get(i)+2*birdH);
					BOOM.draw(c);
					bomb_notification_elapsed.set(i,bomb_notification_elapsed.get(i)+1); //adds one to the timer
				}
				else{//if duration exceed, clear
					GameAlgs.bombx.remove(i);
					GameAlgs.bomby.remove(i);
					bomb_notification_elapsed.remove(i);
					i--; //reduce iteration
				}
			}
		}
	} 
	private void  drawBirds(Canvas c){	// draws the birds
		Drawable bird = getResources().getDrawable(R.drawable.birdl);
		for (Bird b:GameAlgs.topBird){
			b.x+=b.speed;	// makes the bird move
			bird.setBounds(b.x, GameAlgs.TOP_BIRD, b.x+ birdW, b.y+birdH);
			bird.draw(c);
		}
		for (Bird b:GameAlgs.midBird){
			b.x+=b.speed;
			bird.setBounds(b.x, GameAlgs.MID_BIRD, b.x+ birdW, b.y+birdH);
			bird.draw(c);
		}
		for (Bird b:GameAlgs.botBird){
			b.x+=b.speed;
			bird.setBounds(b.x, GameAlgs.BOT_BIRD, b.x+ birdW, b.y+birdH);
			bird.draw(c);
		}
	}
	private void drawPoo(Canvas c){	// draws the poo
		// different types of poo
		Drawable poop = getResources().getDrawable(R.drawable.poo);
		Drawable life = getResources().getDrawable(R.drawable.life);
		Drawable bomb = getResources().getDrawable(R.drawable.bomb);
		Drawable big = getResources().getDrawable(R.drawable.bigwiper);
		for (Poo p:GameAlgs.poo){
			if (!p.gone){
				p.y+=(p.speed);	// drops the poo
				if (p.type == GameAlgs.NORMAL){ //normal
					poop.setBounds( p.x+(birdW/2)-pooW, (int)Math.floor(p.y)+birdH,p.x+(birdW/2),(int)Math.floor(p.y)+birdH+pooH);
					poop.draw(c);
				}
				else if (p.type == GameAlgs.ADDLIFE){//lifepoo
					life.setBounds( p.x+(birdW/2)-pooW, (int)Math.floor(p.y)+birdH,p.x+(birdW/2), (int)Math.floor(p.y)+birdH+pooH);
					life.draw(c);
				}
				else if (p.type == GameAlgs.BOMB){//bomb
					bomb.setBounds( p.x+(birdW/2)-pooW,(int)Math.floor(p.y)+birdH,p.x+(birdW/2), (int)Math.floor(p.y)+birdH+pooH);
					bomb.draw(c);
				}
				else if (p.type == GameAlgs.BIG){//big
					big.setBounds( p.x+(birdW/2)-pooW, (int)Math.floor(p.y)+birdH,p.x+(birdW/2), (int)Math.floor(p.y)+birdH+pooH);
					big.draw(c);
				}
			};
		}

	}
	Drawable bg;//set the background
	private void drawBg(Canvas c){	// draws the background
		if (Selection.bgColour.equals("Sky")){	// if the option is sky
			bg = getResources().getDrawable(R.drawable.game_back);
			bg.setBounds(gameTouch.getLeft(),gameTouch.getTop(), gameTouch.getWidth(),gameTouch.getHeight());
			bg.draw(c);
		}
		else if(Selection.bgColour.equals("Seizure")){// if the option is seizure
			bg = getResources().getDrawable(((flashbg==0)?R.drawable.seizure1:R.drawable.seizure2));
			if(flashbg ==0){flashbg =1;}else{flashbg =0;}
			bg.setBounds(gameTouch.getLeft(),gameTouch.getTop(), gameTouch.getWidth(),gameTouch.getHeight());
			bg.draw(c);
		}
		else if(Selection.bgColour.equals("Troll")){// if the option is Troll
			bg = getResources().getDrawable(R.drawable.black_screen);
			bg.setBounds(gameTouch.getLeft(),gameTouch.getTop(), gameTouch.getWidth(),gameTouch.getHeight());
			bg.draw(c);
		}

	}
	private void drawLevel_Lives_Points(Canvas c){ // displays the level lives and points
		Paint mPaint = new Paint();
		mPaint.setColor(Color.WHITE);
		mPaint.setTextSize(GameAlgs.screenH/10);
		c.drawText(repeat("Level "+GameAlgs.level)+" Lives "+((GameAlgs.lives<=0)?0:GameAlgs.lives)+" Points "+GameAlgs.points ,0, GameAlgs.screenH-10, mPaint); 
	}
	private String repeat(String s){	//offset for level number
		if (s.length()<8){
			s+=" ";
		}
		return s;
	}
	public boolean touch = false;	// checks if the user is touching the screen
	public int lastx =0;
	public int lasty =0;
	public int x=0;
	public int y=0;
	public static int wiper_elapsed_time =0; //how long the wiper has been on the screen
	private void drawWiper(Canvas c){	// draws the wiper
		if (touch){	// if the user is touching the screen
			Drawable wiper = getResources().getDrawable(R.drawable.wiperr);;
			if (lastx > x){	// if the user swipes right to left
				wiper = getResources().getDrawable(R.drawable.wiperr);
			}
			else{// if the user swipes left to right
				wiper = getResources().getDrawable(R.drawable.wiperl);
			}
			//upgrade?
			if (GameAlgs.wiperBig) {//upgrade hit 
				if (wiper_elapsed_time<GameAlgs.wiper_duration){ // if duration still going
					//large wiper x2size
					Log.v("DEBUG","DEBUG-WIPERLARGE");
					wiper.setBounds(x-wiperW,y-wiperH, (x-wiperW)+2*wiperW,(y-wiperH)+2*wiperH);
					wiper_elapsed_time++;
				}
				else{
					GameAlgs.wiperBig =false;
				}
			}
			else{
				//no wiper upgrade
				wiper.setBounds(x-wiperW,y-wiperH/2, (x-wiperW)+wiperW,(y-wiperH/2)+wiperH);
				//GameAlgs.wiperBig = false; //reset upgrade to false
			}
			//drawing
			wiper.draw(c);
		}
	}
	public boolean onTouch(View view, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() ==MotionEvent.ACTION_MOVE) {
			// if the person is touching the screen, check if the wiper is wiping the poo
			touch = true;
			lastx = x;	// the last x value, so that the wiper can change directions
			lasty = y;
			x = (int)event.getX();
			y = (int)event.getY();
			if (Math.abs(lastx-x)<100&&Math.abs(lasty-y)<50){	// used to reduce the lag; 
				//	if the difference between the last touch and the current touch is not very far away
				if (GameAlgs.wiperBig){//if there is an upgrade
					//Log.v("DEBUG","DEBUG-ONTOUCHUPS");
					GameAlgs.checkHit(lastx,x,y,pooH,pooW,wiperW*2,wiperH*2);
				}
				else //no upgrade
					GameAlgs.checkHit(lastx,x,y,pooH,pooW,wiperW,wiperH);
			}
			return true;
		}
		else{
			touch = false;
		}
		return false;
	}

	private void draw() {
		// drawing everything onto screen
		Canvas c = new Canvas();
		try {
			c = holder.lockCanvas();
			if (c != null) {
				drawBg(c);
				drawBirds(c);
				drawPoo(c);
				drawNotifications(c);
				drawWiper(c);
				drawLevel_Lives_Points(c);
			}
		} finally {
			if (c != null) {
				holder.unlockCanvasAndPost(c);
			}
		}
	}

	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		// do nothing
	}

	public void surfaceCreated(SurfaceHolder holder) {
		if (init){
			// starts the game thread
			gameLoop = new GameLoop();
			gameLoop.start();
			gameLoop.setPriority(Thread.MAX_PRIORITY);
		}
		else{
			checkNew();	// checks everything (collision, generation)
			draw();		// draws the pictures/graphics


		}
	}

	public void surfaceDestroyed(SurfaceHolder arg0) {
		// if the surface is somehow destroyed, pause the game
		synchronized (gameLoop) { //pause
			if (!gameLoop.paused){
				gameLoop.paused = true;
				pauseDialog();
			}
		}
	}
	public void onBackPressed() {
		// do something on back.
		synchronized (gameLoop){
			gameLoop.paused = true;
			pauseDialog();
		}
		return;
	}
	public void lvlUpDialog () // dialog box for level up
	{
		gameLoop.paused = true;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(R.drawable.level_up)
		.setTitle("Level Up!")
		.setMessage("You have leveled up! More birds are approaching, Ready?")    
		.setCancelable(false)
		.setPositiveButton("Continue", new DialogInterface.OnClickListener()
		{      
			public void onClick(DialogInterface dialog, int id) 
			{   	
				dialog.cancel();
				gameLoop = new GameLoop();
				gameLoop.start();
			}
		}
		);
		AlertDialog alert = builder.create();
		alert.show();
	}
	public void pauseDialog () // dialog box for pausing
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(R.drawable.pause)
		.setTitle("PAUSED")
		.setMessage("Game Paused. Click to Resume.")    
		.setCancelable(false)
		.setPositiveButton("Resume", new DialogInterface.OnClickListener()
		{      
			public void onClick(DialogInterface dialog, int id) 
			{   	
				dialog.cancel();
				synchronized (gameLoop) { //unpause
					gameLoop.paused = false;
					gameLoop.notify();
				}

			}
		}
		)
		.setNegativeButton("Quit and Main Menu", new DialogInterface.OnClickListener()
		{       
			public void onClick(DialogInterface dialog, int id)
			{   
				dialog.cancel();
				init=true;
				Intent i = new Intent();
				i.setClassName("poopswoop.appdata",
				"poopswoop.appdata.Selection");
				startActivity(i);
				gameLoop.safeStop();
			}   
		}
		);
		AlertDialog alert = builder.create();
		alert.show();
	}
	public void gameOver () 
	{	// game over, go to main menu or play again
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("The farmer's crops have been drenched in poo!") 
		.setTitle("Game Over!")
		.setCancelable(false)
		.setPositiveButton("Main Menu", new DialogInterface.OnClickListener()
		{      
			public void onClick(DialogInterface dialog, int id) 
			{   
				dialog.cancel();
				init=true;
				Intent i = new Intent();
				i.setClassName("poopswoop.appdata",
				"poopswoop.appdata.Selection");
				startActivity(i);
				gameLoop.safeStop();
			}     
		}
		)    
		.setNegativeButton("Play Again", new DialogInterface.OnClickListener()
		{       
			public void onClick(DialogInterface dialog, int id)
			{   
				dialog.cancel();
				init = true;
				gameLoop = new GameLoop();
				gameLoop.start(); 
			}   
		}
		);
		AlertDialog alert = builder.create();
		alert.show();
	}
	public void gameBeaten(){	// when the game is beaten
		// go to main screen, or play again
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Congratulations! You have beaten Poop Swoop! You have no life... " +
				"You might think that by beating this game, you have achieved a lot, " +
				"but in fact, you have not. Good bye. Have fun knowing that you wasted hours trying " +
		"to beat this game! \n") 
		.setTitle("YOU HAVE BEATEN THE GAME!")
		.setCancelable(false)
		.setPositiveButton("I acknowledge that I have wasted " +
				"my life beating this game.", new DialogInterface.OnClickListener()
		{      
			public void onClick(DialogInterface dialog, int id) 
			{   
				dialog.cancel();
				init=true;
				Intent i = new Intent();
				i.setClassName("poopswoop.appdata",
				"poopswoop.appdata.TitleScreen");
				startActivity(i);
				gameLoop.safeStop();//stop the game
			}     
		}
		)    
		.setNegativeButton("Waste more time (Play Again).", new DialogInterface.OnClickListener()
		{       
			public void onClick(DialogInterface dialog, int id)
			{   
				dialog.cancel();
				init = true;
				gameLoop = new GameLoop();
				gameLoop.start(); 
			}   
		}
		);
		AlertDialog alert = builder.create();
		alert.show();
	}
}
