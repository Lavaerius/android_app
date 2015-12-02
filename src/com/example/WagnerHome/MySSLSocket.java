package com.example.WagnerHome;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

//import javax.net.ssl.KeyManagerFactory;
//import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
//import javax.net.ssl.TrustManagerFactory;

import org.apache.http.conn.ssl.SSLSocketFactory;

import com.example.WagnerHome.R;

import android.content.Context;
import android.util.Log;

public class MySSLSocket  {
	private SSLSocket socket;
	private BufferedWriter out;
	private InputStreamReader inputStreamReader;
	private BufferedReader bufferedReader;
	private Boolean connected=false;
	
	private static Context context;

	 public static void setContext(Context mcontext) {
		 if (context == null)
			 context = mcontext;
	 }	
	    
public MySSLSocket (String address,int port) throws SocketTimeoutException, UnknownHostException, IOException, KeyManagementException, NoSuchAlgorithmException, CertificateException, UnrecoverableKeyException {
	if (connected !=true)
	{
		try {
		
			Log.d("before","create the socket");
			KeyStore trustStore = KeyStore.getInstance("BKS");
			//KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
			InputStream trustStoreStream = context.getResources().openRawResource(R.raw.androidclientkey);
			trustStore.load(trustStoreStream,"passwordsuckit".toCharArray());
			KeyStore keyStore = KeyStore.getInstance("BKS");
			InputStream keyStoreStream = context.getResources().openRawResource(R.raw.androidkeypair);
			keyStore.load(keyStoreStream,"passwordsuckit".toCharArray());
			//TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			//trustManagerFactory.init(trustStore);
			// SSLSocketFactory ssf = new SSLSocketFactory();
			Log.d("during","making the socket");
			SSLSocketFactory factory =new SSLSocketFactory(trustStore);
			factory.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			InetSocketAddress remote =  new InetSocketAddress(address,port);
			socket = (SSLSocket)factory.createSocket();
			socket.connect(remote,10000);
			//socket = (SSLSocket) factory.createSocket(new Socket(address,port),address,port,false);
			
			Log.d("initiate","handshake");
			socket.startHandshake();
			Log.d("after","hand shook");
			connected=true;
			//String [] allowed= testsocket.getEnabledCipherSuites();
			//testsocket.setEnabledCipherSuites(allowed);
		} catch (KeyStoreException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		  catch (SocketTimeoutException e1){
			  
		  }
		//SSLSocketFactory f=(SSLSocketFactory) SSLSocketFactory.getDefault();
	
		Log.d("rhino","weiner");
		//Log.d("poop",socket.getLocalSocketAddress().toString());
	}
}

public void connect (){
	//try {
		Log.d("poop","wanger");
		//socket.startHandshake();
		Log.d("poop","whollop");
	//} catch (IOException e) {
		// TODO Auto-generated catch block
		//e.printStackTrace();
	}
public String getOutputstream(){
	try {
		out=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()), 2048);
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		return "timeout";
	} 
	return "";

}

public void closeOutputStream(){
	try {
		out.close();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}

public void append(char output){
	try {
		out.append(output);
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
}
public void flush(){
	try {
		out.flush();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}
public void nullout(){
	out=null;
}
public void getInputstream(){
	
	try {
		inputStreamReader = new InputStreamReader(socket.getInputStream());
		bufferedReader = new BufferedReader(inputStreamReader); 
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
}

public String readline(){
	try {
		String buff = bufferedReader.readLine();
		return buff;
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		return "timeout";	
	}
}

public void close(){
	try {
		socket.close();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
}

public void closeInputStream() {
	try {
		inputStreamReader.close();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
}
}
//}
