package com.example.WagnerHome;


//import java.io.BufferedReader;
//import java.io.BufferedWriter;
import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
//import java.io.InputStreamReader;
//import java.io.OutputStreamWriter;
//import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.List;
import java.util.Map;

import com.example.WagnerHome.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {
	public final static String EXTRA_MESSAGE="com.example.WagnerHome.MESSAGE";
	private byte run=0;
	//private Socket socket;
	private MySSLSocket thesocket;
	private String line=null;
	private nUpdateButton update;
	private boolean paused=false;
	private SharedPreferences ip_pref;
	private String ip_address;
	private Object mPauseLock;
    private boolean mPaused;
    private Context context=this;
    public SharedPreferences pref;
	//private boolean asyncrunning=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceManager.setDefaultValues(this, R.xml.preference, false);
        
        pref=PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.OnSharedPreferenceChangeListener listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
	        public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
	        	 pref=PreferenceManager.getDefaultSharedPreferences(getBaseContext());
	        }
	        };

        
        if (PreferenceManager.getDefaultSharedPreferences(this).contains("ip_address"))
       	 Log.d("*********","the key at least exists!!!!");
        else
       	 Log.d("********","the key does not exist :(");


        ip_address = pref.getString("ip_address", "65.60.157.67");
        MySSLSocket.setContext(this);
        Preferences.setContext(this);
        send_to_host.setContext(this);
        setContentView(R.layout.activity_main);
        

        update=new nUpdateButton();
        update.setContext(this);
        if(run==0)
        {	update.start();
        	//nHandler.postDelayed(nUpdateButton, 800);
        	run=1;
        //call monitor thread
        }
       
}

	final Handler nHandler = new Handler() {
		@Override
		public void handleMessage(Message message)//this waits for messages, and interprets it.
		{
			Button text = (Button)findViewById(R.id.send_button);
			if ((!(boolean)((String) message.obj).contains("got_it")) && (!(boolean)((String) message.obj).contains("timeout")))
			{
				text.setText((String)message.obj);
			}
			else if(((boolean)((String) message.obj).contains("timeout")))
			{
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				// 2. Chain together various setter methods to set the dialog characteristics
				builder.setMessage("The home ip was invalid")
				       .setTitle("Timeout")
				       .setPositiveButton("OK", new DialogInterface.OnClickListener() {
           public void onClick(DialogInterface dialog, int id) {
               // User clicked OK button
        	   update.onResume();
           }
       })
       					.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
           public void onClick(DialogInterface dialog, int id) {
               // User cancelled the dialog
        	   update.onResume();
           }
       })
       					.setNeutralButton("Change", new DialogInterface.OnClickListener() {
           public void onClick(DialogInterface dialog, int id) {
        	   openSettings();
           }
       });;

				// 3. Get the AlertDialog from create()
				AlertDialog dialog = builder.create();
				dialog.show();
			}
			else if ((boolean)((String) message.obj).contains("got_it"))
			{
				
			}
		}
				
			
		
	};

	protected class nUpdateButton extends Thread implements Runnable {
		private SharedPreferences pref;
		private Context context;
		
		public void setContext(Context mcontext) {
			 if (context == null)
				 context = mcontext;
		 }	
		public void run() {
			mPauseLock = new Object();
	        mPaused = false;
			while(!paused){	
				try {
					Thread.sleep(150);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    	 Message message = nHandler.obtainMessage();
	    	 
			 try {
				message.obj=status();
				
			} catch (KeyManagementException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (CertificateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} //the return on that mother fuckin update
			 catch (UnrecoverableKeyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 if(((boolean)((String) message.obj).contains("timeout")))
			 {
				 nHandler.sendMessage(message);
				 update.onPause();
			 }
			 else
			 {
				 nHandler.sendMessage(message);
			 }
			 synchronized (mPauseLock) {
	                while (mPaused) {
	                    try {
	                        mPauseLock.wait();
	                    } catch (InterruptedException e) {
	                    }
	                }
	            }
			}
		}
	    public void onPause(){	    	
	    		paused=true;  	
	    		Log.d("on pause","app is paused");
	    		synchronized (mPauseLock) {
	    			mPaused = true;
	    		}
	    }
	    public void onResume()
	    {
	    	paused=false;
	    	synchronized (mPauseLock) {
	    		mPaused = false;
	    		mPauseLock.notifyAll();
	   	   	}
	    	Log.d("on resume","app is resumed");
	    	//update.run();
	    	}
	    

	public String status() throws SocketTimeoutException,KeyManagementException, UnknownHostException, NoSuchAlgorithmException, CertificateException, IOException, UnrecoverableKeyException{
    	//	String the_status=null;
		 pref=PreferenceManager.getDefaultSharedPreferences(context);

        ip_address = pref.getString("ip_address", "127.0.0.1");
        if (pref.contains("ip_address"))
       	 Log.d("*********","the key at least exists!!!!");
        else
       	 Log.d("********","the key does not exist :(");
        try {
        	thesocket = new MySSLSocket(ip_address,3130);
        }catch (SocketTimeoutException e1){
        	return "timeout";
        }
        
        	char request[]={'s','t','a','t','u','s','_','m','e','_','b','i','t','c','h','<','>','\n'};
        	Log.d("6", "connected");
        	
        		String result=thesocket.getOutputstream();
        		if (result=="timeout")
        				return result;
        		for (int p=0;p<request.length;p++)
        		{
        			thesocket.append(request[p]);
        		}       	
        		thesocket.flush();
        		thesocket.nullout();
        	
        		thesocket.getInputstream();
        		line = thesocket.readline(); // add first line
        		Log.d("sfsdfsdsd","sdsfdsfds");
        		String [] splitted;
        	
        		while(!line.contains("><"))
        		{
        			line = thesocket.readline();
        		}
        		splitted=line.split("><");
        		line=splitted[0];
        		thesocket.closeInputStream();
        		thesocket.close();
        		Log.d("9","closed the socket");
        		return line;

    }

 
	}
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    public void sendMessage(View view){
    	 send_to_host toast = new send_to_host(this);
         toast.execute("bullshit");
    }
    public void changeButton (String new_text){
    	Log.d("ssfds","44twr");
	     Button text = (Button)findViewById(R.id.send_button);
	     if(!new_text.contains("got_it"))
	     {
	    	 text.setText(new_text);
	     }
    }

   @Override
    protected void onPause(){
    	paused=true;
    	super.onPause();
    	update.onPause();
    	
    }
    @Override
    protected void onResume()
    {
    	paused=false;
    	super.onResume();
    	update.onResume();	
    }
    @Override
    protected void onStart() {
        super.onStart();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
    			openSettings();
                return true;
        }
    void openSettings(){
    	startActivity(new Intent(getApplicationContext(), Preferences.class));
    }
    
    
    public static class PrefsFragment extends PreferenceFragment {
    	 
        @Override
        public void onCreate(Bundle savedInstanceState) {
                    super.onCreate(savedInstanceState);
                    addPreferencesFromResource(R.xml.preference);
                    
        }
}
    public static class Preferences extends PreferenceActivity {
    	public static final String KEY_PREF_IP = "ip_address";
    	private static Context context;
        
    	@Override
        public void onCreate(Bundle savedInstanceState) {
        	super.onCreate(savedInstanceState);
            getFragmentManager().beginTransaction().replace(android.R.id.content, new PrefsFragment()).commit();
        } 
     /*   public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals(KEY_PREF_IP)) {
                Preference ip_add;
                // Set summary to be the user-description for the selected value
            }
        }*/
        public static void setContext(Context mcontext) {
   		 if (context == null)
   			 context = mcontext;
   	 }
    }
    
}
