package poopswoop.appdata;


import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;

import android.os.Bundle;
import android.os.Handler;
import android.view.SurfaceView;

public class Ponder extends Activity { 
	public void onCreate(Bundle savedInstanceState) { 
		super.onCreate(savedInstanceState);     
		setContentView(R.layout.ponderlogo);	// sets the layout
	
		Thread logoThread = new Thread() {
			public void run() {  
			}    
		};
		logoThread.start();
		Timer  t= new Timer();
		   t.schedule(new TimerTask() {

		     @Override
		     public void run() {
		    	 finish();
		    	 Intent i = new Intent();
		 		i.setClassName("poopswoop.appdata",
		 				"poopswoop.appdata.TitleScreen");
		 		startActivity(i);
		     }
		   }, 3000);

	}
}

