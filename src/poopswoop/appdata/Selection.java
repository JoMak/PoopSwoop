
// the Selection Menu (Play, Options, Instructions)
package poopswoop.appdata;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Toast;

public class Selection extends Activity implements OnTouchListener {
	SurfaceView select;
	static Thread selectionThread;
	static String bgColour = "Sky";	// background colour
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);     
		setContentView(R.layout.selection);     
		select =(SurfaceView) findViewById(R.id.selectionSur);
		select.setOnTouchListener(this);
		selectionThread = new Thread() {    
			public void run() {
			}   
		};  
		selectionThread.start();
	}
	// this dialog box is not used, but could potentially be used later on
	// so it is not deleted
	/*public void instruct1 ()
	{
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Your goal is to protect your limo from the endless "
				+"rain of bird droppings. Your limo can only withstand 3 hits from these "+
				"poisonous balls of poop. After being hit 3 times, the game ends. You are armed "+
				"with a windshield wiper, which can be used by moving your finger across the screen. "+
				"This will create a path for your wiper, which can be linear, curved, any shape you can think "+
				"of. (With the only restriction being that you cannot head in the direction you started at. Birds "+
				"will appear at the top of the screen, randomly dropping poop. As time progresses, the number of birds "+
		"and amount of poop dropped will increase. Aim for a high score!")
		.setCancelable(false)    
		.setNegativeButton("Close", new DialogInterface.OnClickListener()
		{       
			public void onClick(DialogInterface dialog, int id)
			{             
				dialog.cancel();   
			}   
		}
		);
		AlertDialog alert = builder.create();
		alert.show();

	}*/
	public void onBackPressed() {
		// exit
		finish();
		return;
	}
	public void endExplanation()
	{ // last dialog of the instruction explanation
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("The farmer's crops can only survive 3 hits from the bird poop. Don't let "+
		"that happen! Good luck, save those crops!")
		.setCancelable(false)
		.setIcon(R.drawable.bird)
		.setTitle("Lives")
		.setPositiveButton("Cancel", new DialogInterface.OnClickListener()
		{      
			public void onClick(DialogInterface dialog, int id) 
			{    		
				dialog.cancel();
			}     
		}
		)    
		.setNegativeButton("Play", new DialogInterface.OnClickListener()
		{       
			public void onClick(DialogInterface dialog, int id)
			{             
				finish();
				dialog.cancel();
				Intent i = new Intent();
				i.setClassName("poopswoop.appdata",
				"poopswoop.appdata.PoopSwoop");
				startActivity(i);
				
			}   
		}
		);
		AlertDialog alert = builder.create();
		alert.show();

	}

	public void levelExplanation()
	{	// explains how levels work
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("As time passes by, you will level up. More birds will appear "+
		"and poo will be dropped more frequently. A message will pop up, notifying you of your level up.")
		.setCancelable(false)
		.setIcon(R.drawable.level_up)
		.setTitle("Levels")
		.setPositiveButton("Back", new DialogInterface.OnClickListener()
		{      
			public void onClick(DialogInterface dialog, int id) 
			{    
				wiperExplanation();

			}     
		}
		)    
		.setNegativeButton("Next", new DialogInterface.OnClickListener()
		{       
			public void onClick(DialogInterface dialog, int id)
			{             
				endExplanation();   
			}   
		}
		);
		AlertDialog alert = builder.create();
		alert.show();

	}

	public void wiperExplanation()
	{	// explains how wipers work
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Move your finger across the screen to create a windshield wiper. "+
				"Use it to wipe the poo before it reaches the farmer's crops! "+
		"Hint: Wiping slowly will be more effective than wiping too quickly.")
		.setCancelable(false)
		.setIcon(R.drawable.wiperl)
		.setTitle("The Wiper")
		.setPositiveButton("Back", new DialogInterface.OnClickListener()
		{      
			public void onClick(DialogInterface dialog, int id) 
			{    
				pooExplanation();

			}     
		}
		)    
		.setNegativeButton("Next", new DialogInterface.OnClickListener()
		{       
			public void onClick(DialogInterface dialog, int id)
			{             
				levelExplanation();
			}   
		}
		);
		AlertDialog alert = builder.create();
		alert.show();

	}

	public void pooExplanation()
	{	// explains what birds will do in game
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(	"Sometimes the poo will be items instead. Wiping is a star adds one to your life, but be careful, don't touch the bombs!")   
		.setCancelable(false)
		.setIcon(R.drawable.poo)
		.setTitle("Poo!")
		.setPositiveButton("Back", new DialogInterface.OnClickListener()
		{      
			public void onClick(DialogInterface dialog, int id) 
			{    
				birdExplanation();
				dialog.cancel();

			}     
		}
		)    
		.setNegativeButton("Next", new DialogInterface.OnClickListener()
		{       
			public void onClick(DialogInterface dialog, int id)
			{             
				wiperExplanation();   
			}   
		}
		);
		AlertDialog alert = builder.create();
		alert.show();
	}
	public void birdExplanation()
	{	// explains what birds will do in game
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Birds will fly across the screen nonstop, dropping poo as they travel.")   
		.setCancelable(false)
		.setIcon(R.drawable.bird)
		.setTitle("Birds!")
		.setPositiveButton("Back", new DialogInterface.OnClickListener()
		{      
			public void onClick(DialogInterface dialog, int id) 
			{    
				//backgroundStory();
				dialog.cancel();

			}     
		}
		)    
		.setNegativeButton("Next", new DialogInterface.OnClickListener()
		{       
			public void onClick(DialogInterface dialog, int id)
			{             
				pooExplanation();   
			}   
		}
		);
		AlertDialog alert = builder.create();
		alert.show();
	}

	/*public void backgroundStory ()
	{	// storyline
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(R.drawable.icon)
		.setTitle("The Story")
		.setMessage("You are a wizard named Murdoc. You have been hired "+
				"by a farmer to protect his crops from an incoming swarm "+
				"of diseased birds that have extremely corrosive poo. After seeing "+
				"what the birds have done to his crops, you have decided to use your "+
				"most valuable weapon: A WINDSHIELD WIPER!\n")    
		
		.setCancelable(false)
		.setIcon(R.drawable.bird)
		.setPositiveButton("Close", new DialogInterface.OnClickListener()
		{      
			public void onClick(DialogInterface dialog, int id) 
			{    
				dialog.cancel();

			}     
		}
		)    
		.setNegativeButton("Next", new DialogInterface.OnClickListener()
		{       
			public void onClick(DialogInterface dialog, int id)
			{             
				birdExplanation();  
			}   
		}
		);
		AlertDialog alert = builder.create();
		alert.show();
	}*/

	
		
	
	
	public void options ()
	{// options box to change background colour	
		final CharSequence[] items = {"Troll", "Seizure", "Sky"};
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Pick a background color")
	
		.setNegativeButton("Ok", new DialogInterface.OnClickListener()
		{       
			public void onClick(DialogInterface dialog, int id)
			{             
				dialog.cancel();   
			}   
		}
		)
			.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener()
		{    
			public void onClick(DialogInterface dialog, int item)
			{        Toast.makeText(getApplicationContext(), items[item], Toast.LENGTH_SHORT).show(); 
					if(item==0)
					{
						bgColour = "Troll";
					}
					else if (item==1)
					{
						bgColour = "Seizure";
					}
					else if (item == 2)
					{
						bgColour = "Sky";
					}
			}
		}
		);
		AlertDialog alert = builder.create();
		alert.show();
}


public boolean onTouch(View v, MotionEvent event) {
	if (event.getAction() == MotionEvent.ACTION_DOWN) {
		// depending on the x/y coordinates the touch is, relative to the screen width,
		// play, instructions, or options will show
		double x = (double)event.getX();
		double y = (double)event.getY();
		if (x/select.getWidth()>0.29 &&x/select.getWidth()<0.93&&y/select.getHeight()>0.71&&y/select.getHeight()<0.9)
		{// instructions
			birdExplanation();
		}
		else if (x/select.getWidth()>0.50 && x/select.getWidth()<0.9&&y/select.getHeight()>0.37&&y/select.getHeight()<0.63)
		{// options
			options();
		}
		else if (x/select.getWidth()>0.03 && x/select.getWidth()<0.49&&y/select.getHeight()>0.1&&y/select.getHeight()<0.4)
		{// game
			Intent i = new Intent();
			i.setClassName("poopswoop.appdata",
			"poopswoop.appdata.PoopSwoop");
			startActivity(i);// start the game
			finish();
			return true;
		}
	}
	return false;
}
}