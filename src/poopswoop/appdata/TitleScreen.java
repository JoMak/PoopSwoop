
// Title Screen/Splash Screen
package poopswoop.appdata;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
public class TitleScreen extends Activity implements OnTouchListener { 
	public void onCreate(Bundle savedInstanceState) { 
		super.onCreate(savedInstanceState);     
		setContentView(R.layout.title_screen);	// sets the layout
		SurfaceView title = (SurfaceView) findViewById(R.id.titleSurface);
		title.setOnTouchListener(this);
		Thread titleThread = new Thread() {
			public void run() {  
			}    
		};
		titleThread.start();
	}
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			// if the screen is touched, start selection menu
			finish();
			Intent i = new Intent();
			i.setClassName("poopswoop.appdata",
					"poopswoop.appdata.Selection");
			startActivity(i);
			return true;
		}
		return false;
	}
}
