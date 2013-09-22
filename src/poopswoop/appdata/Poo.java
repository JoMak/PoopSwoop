
package poopswoop.appdata;
import java.util.LinkedList;
import java.util.Random;

import android.util.Log;
//---------------------------- BIRD POO -----------------------------------//
// Class for the poo
public class Poo {
	double y,speed;
	int x,type;
	boolean gone =false;
	public Poo(int x, int y, int speed, int type) {
		this.x = x;
		this.y = y;
		this.speed = speed;
		this.type = type;
		if (type ==GameAlgs.BOMB){// ensures bomb doesnt coincide with other poo
			this.speed-=(speed<=2?-0.5:0.5);
			//Log.v("debug","DEBUG - BOMBSPEED "+this.speed);
		}
	}
}

