
package poopswoop.appdata;
// Class for the birds
public class Bird {
	int x, y;	// xy positions
	public int speed;	
	public int drop1,drop2; // the two places the bird will drop poo
	public boolean dropped1,dropped2;//check if dropped
	public Bird(int x, int y, int speed, int drop1, int drop2) {
		this.speed = speed;
		this.x = x;
		this.y = y;
		this.drop1 = drop1;
		this.drop2 = drop2;
		dropped1 =false;
		dropped2=false;
	}
	public Bird(int x, int y, int speed, int drop1) {//no second drop (two poo too proxy, eliminate one)
		this.speed = speed;
		this.x = x;
		this.y = y;
		this.drop1 = drop1;
		dropped1 =false;
		dropped2=true;
	}
	
}