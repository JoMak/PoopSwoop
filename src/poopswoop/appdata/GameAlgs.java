
package poopswoop.appdata;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.Random;
import java.util.Scanner;

import android.util.Log;
// this file contains the non-graphical methods needed to implement the game
public class GameAlgs {
	//--- initial variables ---//
	public static boolean levelup = false;	// did the user level up? or are more birds still generating
	public static int level = 1;			// level number
	public static int lives = 3;			// lives
	public static int points = 0;			// points
	public static int screenW =PoopSwoop.gameTouch.getWidth();	// the width of the screen
	public static int screenH =PoopSwoop.gameTouch.getHeight();	// the height of the screen

	//--------------------------- BIRDS ---------------------------------------//
	public static int birdPos = -PoopSwoop.birdW;	// this variable is needed so that every time birdPos = 0, it triggers the method to generate birds
	private static Random rPos = new Random();	// random position for the bird (top, middle, bottom)
	private static Random rBird = new Random();	// random generation of bird
	public static Random rSpeed = new Random();	// random generation of bird speed
	static int numBirds = 0;					// number of potential columns of birds

	// position of birds
	public final static int TOP_BIRD = 5;
	public final static int MID_BIRD =(PoopSwoop.birdH+5);
	public final static int BOT_BIRD =2*(PoopSwoop.birdH+5);
	public static int birdW = PoopSwoop.birdW;
	// array of birds, there are three layers of birds, each layer
	// with a constant y value
	public static LinkedList<Bird> topBird = new LinkedList<Bird>();
	public static LinkedList<Bird> midBird = new LinkedList<Bird>();
	public static LinkedList<Bird> botBird = new LinkedList<Bird>();
	private final static int POO_CLOSENESS =PoopSwoop.pooW+2; // how close each poo can be
	public static void addBird(){ 
		// this method adds birds (theres a 33% change a bird will be added to that specific y position)
		// the 33% is done to limit the number of birds, or else the game will be way too hard
		int poo_first=rPooPos.nextInt(screenW-(2*birdW))+1*birdW;
		int poo_second=rPooPos.nextInt(screenW-(2*birdW))+1*birdW;//two poo positions
		//Log.v("DEBUG","DEBUG - "+poo_first+" "+poo_second);
		if (rPos.nextInt(3)==0){
			// adds a bird offscreen at its respective y positive
			// also sets the two places it will drop its poo
			// if the poo drop positions are too close, eliminate one of them
			if (Math.abs(poo_first-poo_second)<= POO_CLOSENESS){
				topBird.add(new Bird(-(birdW),TOP_BIRD,rSpeed.nextInt(6)+6,poo_first)); 
				numpoo++;
			}
			else{
				topBird.add(new Bird(-(birdW),TOP_BIRD,rSpeed.nextInt(6)+6,poo_first,poo_second)); 
				numpoo+=2;
			}
		}
		if (rPos.nextInt(3)==1){
			if (Math.abs(poo_first-poo_second)<= POO_CLOSENESS){
				midBird.add(new Bird(-(birdW),MID_BIRD,rSpeed.nextInt(6)+6,poo_first)); 
				numpoo++;
			}
			else{
			    midBird.add(new Bird(-(birdW),MID_BIRD,rSpeed.nextInt(6)+6,poo_first,poo_second)); 
				numpoo+=2;
			}; 
		}
		if (rPos.nextInt(3)==2){
			if (Math.abs(poo_first-poo_second)<= POO_CLOSENESS){
				botBird.add(new Bird(-(birdW),BOT_BIRD,rSpeed.nextInt(6)+6,poo_first)); 
				numpoo++;
			}
			else{
				botBird.add(new Bird(-(birdW),BOT_BIRD,rSpeed.nextInt(6)+6,poo_first,poo_second)); 
				numpoo+=2;
			}
		}
		numBirds++;	// regardless, add one to number of potential birds
	}
	public static void birdGen(){
		// removes birds if they are offscreen
		for(int i =0; i<topBird.size(); i++){
			if (topBird.get(i).x>screenW){	// if the bird's x value is greater than the screen's width
				topBird.remove(i);			// remove it
				i--; // move i back down because one was removed
			}
		}
		for(int i =0; i<midBird.size(); i++){
			if (midBird.get(i).x>screenW){
				midBird.remove(i);
				i--;
			}
		}
		for(int i =0; i<botBird.size(); i++){
			if (botBird.get(i).x>screenW){
				botBird.remove(i);
				i--;	
			}
		}
		if (birdPos>=0 || (topBird.size()==0 && midBird.size()==0&&botBird.size()==0)){
			// if adding the bird is triggered (birdPos>=0),
			// or there are no birds on the screen
			birdPos=-birdW;
			if (numBirds<2*level+10){	// if more birds can still be generated for this level
				// limits number of columns of potential birds to level*level+10
				// therefore, increasing # of birds with each level
				if (rBird.nextInt(level+2)<=level){	
					// generates a random number from 0 to the level number +2
					// if that generated number is less than the level then generate a bird
					// therefore chance of creating a bird increases as levels increase
					addBird();
				}
			}
			else{	// if there should be no more generation of birds, time to level up!
				levelup = true;
			}
		}
		else{// if adding the bird is not triggered, keep progressing the counter
			birdPos+=3;
		}
	}

	//--------------------------- POO ---------------------------------------//
	private static Random rPooPos = new Random();	// random position for the bird poo
	public static Random rPooSpeed = new Random();	// random generation of bird speed
	// array of poo
	// with a constant x value
	public static LinkedList<Poo> poo = new LinkedList<Poo>();
	static int maxPooSpeed  =0;	// the maximum speed the poo can be
	static int minPooSpeed  =1;	// the minumum speed the poo can be
	public static int poo_used=0; // poo that has been swiped
	public static int numpoo=0; // number of poo
	//--------------UPGRADES------------------------------------//
	public  final static int NORMAL = 0;			//87%
	public  final static int ADDLIFE = 1;			//2%
	public  final static int BOMB = 2;				//10%
	public  final static int BIG = 3;				//2%
	//WIPER
	public static boolean wiperBig=false;		//booleans for wiper upgrade
	public static int wiper_duration =100;		//duration of upgrade
	//-----------BOMB---------//
	//PLACES HIT
	public static LinkedList<Integer> bombx=new LinkedList<Integer>();
	public static LinkedList<Integer> bomby=new LinkedList<Integer>();
	public static final int bombduration = 10;
	public static void addPoo(Bird b){ 	
		// this method takes in a bird's current position, and adds poo
		// to that position, with a speed
		// specifies max according to level
		maxPooSpeed = (int) Math.ceil(minPooSpeed+(level/(double)10));// max 5 speed
		int speed;
		if (level<45){
			speed = rPooSpeed.nextInt(maxPooSpeed)+minPooSpeed;
		}
		else{
			speed = rPooSpeed.nextInt(7)+minPooSpeed;
			// for level 45+ give a small probability of speed6
			if (speed ==7){
				speed=6;
			}
			else if(speed==6){
				speed=5;
			}
			else if (speed >=4){
				speed =4;
			}
		}
		// UPGRADES/POWERUPS
		Random r = new Random();
		int type_poo=0;
		int pooprob = r.nextInt(100);
		if (pooprob <87)
			type_poo = NORMAL;
		else if (pooprob <89)
			type_poo = ADDLIFE;
		else if (pooprob <99)
			type_poo = BOMB;
		/*else if (pooprob <100) ////BUG FIXXX
			type_poo = BIG;*/
		poo.add(new Poo(b.x,b.y-speed,speed,type_poo)); // sets a new poo
	}
	public static void pooGen(){
		// generates poo at the specific location
		for (int i = 0; i<topBird.size(); i++){
			if (!topBird.get(i).dropped1&&topBird.get(i).x >= topBird.get(i).drop1  ){	// if the bird's position is at where the drop1 is supposed to be
				addPoo(topBird.get(i));	// adds the poop
				topBird.get(i).dropped1 = true;	// sets the drop to be true (prevents dropping of poo after initial drop)

			}
			//Log.v("DEBUG","DEBUGtop1");
			if (!topBird.get(i).dropped2 &&topBird.get(i).x >= topBird.get(i).drop2 ){// if the bird's position is at where the drop2 is supposed to be
				addPoo(topBird.get(i));
				topBird.get(i).dropped2 = true;
			}
			//Log.v("DEBUG","DEBUGtop2");
		}
		//Log.v("DEBUG","DEBUGtopdone");
		for (int i = 0; i<midBird.size(); i++){
			if (!midBird.get(i).dropped1&& midBird.get(i).x >= midBird.get(i).drop1){
				addPoo(midBird.get(i));
				midBird.get(i).dropped1 = true;

			}
			//Log.v("DEBUG","DEBUGmid1");
			if (!midBird.get(i).dropped2 && midBird.get(i).x >= midBird.get(i).drop2){
				addPoo(midBird.get(i));
				midBird.get(i).dropped2 = true;
			}
			//Log.v("DEBUG","DEBUGmid2");
		}
		//Log.v("DEBUG","DEBUGmiddone");
		for (int i = 0; i<botBird.size(); i++){
			if (!botBird.get(i).dropped1 && botBird.get(i).x >= botBird.get(i).drop1){
				addPoo(botBird.get(i));
				botBird.get(i).dropped1= true;
			}
			//Log.v("DEBUG","DEBUGbot1");
			if (!botBird.get(i).dropped2&&botBird.get(i).x >= botBird.get(i).drop2){
				addPoo(botBird.get(i));
				botBird.get(i).dropped2 = true;
			}
			//Log.v("DEBUG","DEBUGbot2");
		}
		//Log.v("DEBUG","DEBUGbotdone");
	}
	public static void checkLife(){
		// checks if one has lost a life (poo is past the screen)
		for(Poo p :poo){
			if (p.y>screenH-PoopSwoop.pooH && !p.gone){
				p.gone =true;	// will not display poo anymore
				poo_used++;
				if (lives !=0 && p.type ==NORMAL){	// prevents negative life and makes sure items dont kill you
					lives--;
				}		// subtract a life
			}
		}
		for (int i = 0; i<poo.size();i++){ // remove poo from linkedlist
			if (poo.get(i).gone){
				poo.remove(i--);
			}
		}
	}
	static Object LOCK = new Object();	// used to synchronize the methods
	public static void checkHit(int lastx, int x, int y, int pooH, int pooW,int wiperW, int wiperH){
		//	synchronized (LOCK){	// locks it so that this can only be called by one method at a time
		for (int i =0;i<poo.size();i++){
			if (lastx > x){	// if the user is swiping from the right
				if (poo.get(i).x+pooW<=lastx && poo.get(i).x+pooW>=x-wiperW &&
						poo.get(i).y+pooH >= y-wiperH && poo.get(i).y <=y){// if the poo is in range of the wiper
					if (!poo.get(i).gone){	// if the poo isnt gone
						poo.get(i).gone = true;	//poo disappears
						poo_used++;
						//UPGRADES
						if (poo.get(i).type == NORMAL)
							points += poo.get(i).speed;	// increase points
						else if (poo.get(i).type == ADDLIFE)
							lives++;
						else if (poo.get(i).type == BOMB){
							bombx.add(poo.get(i).x);
							bomby.add((int)Math.floor(poo.get(i).y));
							PoopSwoop.bomb_notification_elapsed.add(0); //adds to the timer for the notification
							lives--;
						}
						else if (poo.get(i).type == BIG){
							wiperBig = true;
							PoopSwoop.wiper_elapsed_time=0;
						}

					}
					
				}
			}
			else if (lastx < x){	// if the user is swiping from the left
				if (poo.get(i).x>=lastx && poo.get(i).x<=x+wiperW &&
						poo.get(i).y+pooH >= y-wiperH && poo.get(i).y <=y){// if the poo is in range of the wiper
					if (!poo.get(i).gone){
						if (!poo.get(i).gone){	// if the poo isnt gone
							poo.get(i).gone = true;	//poo disappears
							poo_used++;
							//UPGRADES
							if (poo.get(i).type == NORMAL)
								points += poo.get(i).speed;	// increase points
							else if (poo.get(i).type == ADDLIFE)
								lives++;
							else if (poo.get(i).type == BOMB){
								bombx.add(poo.get(i).x);
								bomby.add((int)Math.floor(poo.get(i).y));
								PoopSwoop.bomb_notification_elapsed.add(0); //adds to the timer for the notification
								lives--;
							}
							else if (poo.get(i).type == BIG){
								wiperBig = true;
								PoopSwoop.wiper_elapsed_time=0;
							}
						}
					}
				}
			}
		}
		//}
	}

	public static boolean pooOnScreen(){
		// checks if poo there is any poo on screen

		return (numpoo==poo_used && numpoo >0);

	}
}
