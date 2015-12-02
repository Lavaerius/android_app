package com.example.WagnerHome;


import java.io.BufferedReader;
//import java.io.BufferedWriter;
import java.io.IOException;
//import java.io.InputStreamReader;
//import java.io.OutputStreamWriter;
//import java.net.Socket;
//import javax.net.ssl.SSLSocket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import android.content.Context;
import com.example.WagnerHome.MainActivity.Preferences;

import android.content.SharedPreferences;
//import org.apache.http.conn.ssl.SSLSocketFactory;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;




public class send_to_host extends AsyncTask<String, Void, String> {
	private MySSLSocket socket;
	public SharedPreferences ip_pref;
	public String ip_address;
	private MainActivity activity;
	private static Context context;

	public static void setContext(Context mcontext) {
			 if (context == null)
				 context = mcontext;
		 }	
	  public send_to_host(MainActivity activity){
		  this.activity = activity;
	  }
	 
	    

	@Override
	public String doInBackground(String... urls){
		String line=null;
        try {
        	String the_line="open_command<>\n";
        	char[] the_line_array=the_line.toCharArray();    
             SharedPreferences pref=PreferenceManager.getDefaultSharedPreferences(context);
             ip_address = pref.getString("ip_address", "127.0.0.1");
             if (pref.contains("ip_address"))
            	 Log.d("*********","the key at least exists!!!!");
             else
            	 Log.d("********","the key does not exist :(");
        	 socket = new MySSLSocket(ip_address,3130);
        	// socket= new MySSLSocket("10.20.32.56",3130);
        	socket.getOutputstream();
        	for(int p=0;p<the_line_array.length;p++)
        	{	
        		socket.append(the_line_array[p]);
        	}        	
        	socket.flush();
        	socket.nullout();
        	socket.getInputstream(); 
        	//InputStreamReader inputStreamReader = new InputStreamReader(socket.getInputStream());
        	//BufferedReader bufferedReader = new BufferedReader(inputStreamReader); 
        	
        	line = socket.readline(); // add first line
        	String [] splitted;
        	//while(!line.contains("><"))
        	//{
        	//	line = socket.readline();
        	//}
        	splitted=line.split("><");
    		line=splitted[0];
        	socket.closeInputStream();
        	socket.close();
        	Log.d("9","closed the socket");
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyManagementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnrecoverableKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CertificateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 		
		Log.d("332423","more success");
        return line;
	}
	@Override
	protected void onPostExecute(String result){
		
		if(!result.contains("got_it"))
		{
				activity.changeButton(result);
		}
		Log.d("fantastic","how do i pass this");
	}
	

}
