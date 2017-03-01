package com.example.airdrumsnew;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;



public class BluetoothTest extends Activity
{
	TextView myLabel,tvBass,tvHiHat,tvSnare,tvTomMid,tvRide,tvCrash;
	ImageView ivBass,ivHiHat,ivSnare,ivTomMid,ivRide,ivCrash;
	EditText myTextbox;
	BluetoothAdapter mBluetoothAdapter;
	BluetoothSocket mmSocket;
	BluetoothDevice mmDevice;
	OutputStream mmOutputStream;
	InputStream mmInputStream;


	//Extract the data




	Thread workerThread;
	Button openButton,sendButton,closeButton;
	Button btCalibrate;
	byte[] readBuffer;
	int readBufferPosition;
	int counter,n=0;
	volatile boolean stopWorker;
	char charHiHat='a';
	char charBass = 'b';
	char charTomMid = 'q';
	char charCrash = 'w';
	char charSnare = 'r';
	char charRide = 'e';
	Typeface font;


	MediaPlayer mpBass,mpHiHat,mpTomMid,mpCrash,mpSnare,mpRide;

	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);


		//Extract the data�


		openButton = (Button)findViewById(R.id.open);

		closeButton = (Button)findViewById(R.id.close);

		btCalibrate =(Button)findViewById(R.id.btCalibrate);

		myLabel = (TextView)findViewById(R.id.label);

		font = Typeface.createFromAsset(getAssets(), "Roboto-Thin.ttf");
		android.app.ActionBar bar = this.getActionBar();
		ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#5677fc"));
		bar.setBackgroundDrawable(colorDrawable);

		SpannableString s = new SpannableString("Let's Play!");
		s.setSpan(new TypefaceSpan(this, "Roboto-Thin.ttf"), 0, s.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		tvBass = (TextView)findViewById(R.id.tvBass);
		tvBass.setTypeface(font);
		tvHiHat = (TextView)findViewById(R.id.tvHiHat);
		tvHiHat.setTypeface(font);
		tvSnare = (TextView)findViewById(R.id.tvSnare);
		tvSnare.setTypeface(font);
		tvTomMid = (TextView)findViewById(R.id.tvTomMid);
		tvTomMid.setTypeface(font);
		tvCrash = (TextView)findViewById(R.id.tvCrash);
		tvCrash.setTypeface(font);
		tvRide = (TextView)findViewById(R.id.tvRide);
		tvRide.setTypeface(font);

		ivBass = (ImageView)findViewById(R.id.ivBass);
		ivHiHat = (ImageView)findViewById(R.id.ivHiHat);
		ivSnare = (ImageView)findViewById(R.id.ivSnare);
		ivTomMid = (ImageView)findViewById(R.id.ivTomMid);
		ivCrash = (ImageView)findViewById(R.id.ivCrash);
		ivRide = (ImageView)findViewById(R.id.ivRide);

		mpBass = MediaPlayer.create(this, R.raw.bass);
		mpHiHat = MediaPlayer.create(this, R.raw.hihatopenclose);
		mpTomMid = MediaPlayer.create(this, R.raw.tommid);
		mpCrash = MediaPlayer.create(this, R.raw.crash);
		mpSnare = MediaPlayer.create(this, R.raw.snare);
		mpRide = MediaPlayer.create(this, R.raw.ride);



			tvSnare.setVisibility(View.GONE);
			ivSnare.setVisibility(View.GONE);

			tvCrash.setVisibility(View.GONE);
			ivCrash.setVisibility(View.GONE);
			tvRide.setVisibility(View.GONE);
			ivRide.setVisibility(View.GONE);



		//Open Button
		openButton.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				try 
				{
					findBT();
					openBT();
				}
				catch (IOException ex) { }
			}
		});



		//Close button
		closeButton.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				try 
				{
					closeBT();
				}
				catch (IOException ex) { }
			}
		});

		btCalibrate.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				try {
					sendData('c');
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}

	void findBT()
	{
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if(mBluetoothAdapter == null)
		{
			myLabel.setText("No bluetooth adapter available");
		}

		if(!mBluetoothAdapter.isEnabled())
		{
			Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBluetooth, 0);
		}

		Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
		if(pairedDevices.size() > 0)
		{
			for(BluetoothDevice device : pairedDevices)
			{
				if(device.getName().equals("HC-05")) 
				{
					mmDevice = device;
					break;
				}
			}
		}
		myLabel.setText("Bluetooth Device Found");
	}

	void openBT() throws IOException
	{
		UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"); //Standard SerialPortService ID
		mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);        
		mmSocket.connect();
		mmOutputStream = mmSocket.getOutputStream();
		mmInputStream = mmSocket.getInputStream();

		beginListenForData();

		myLabel.setText("Bluetooth Opened");
	}

	void beginListenForData()
	{
		final Handler handler = new Handler(); 
		final byte delimiter = 10; //This is the ASCII code for a newline character

		stopWorker = false;
		readBufferPosition = 0;
		readBuffer = new byte[1024];
		workerThread = new Thread(new Runnable()
		{
			public void run()
			{                
				while(!Thread.currentThread().isInterrupted() && !stopWorker)
				{
					try 
					{
						int bytesAvailable = mmInputStream.available();                        
						if(bytesAvailable > 0)
						{
							byte[] packetBytes = new byte[bytesAvailable];
							mmInputStream.read(packetBytes);
							for(int i=0;i<bytesAvailable;i++)
							{
								byte b = packetBytes[i];
								if(b == delimiter)
								{
									byte[] encodedBytes = new byte[readBufferPosition];
									System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
									final String data = new String(encodedBytes, "US-ASCII");
									readBufferPosition = 0;

									handler.post(new Runnable()
									{
										public void run()
										{
											myLabel.setText(data);

											System.out.println(""+data.length());
											char c = data.charAt(0);


												switch (c)
												{
												case 'a':System.out.println(c);
												mpHiHat.start();
												tvHiHat.setTextColor(Color.YELLOW);
												tvBass.setTextColor(Color.WHITE);
												tvCrash.setTextColor(Color.WHITE);
												tvRide.setTextColor(Color.WHITE);
												tvSnare.setTextColor(Color.WHITE);
												tvTomMid.setTextColor(Color.WHITE);
												mpHiHat.setOnCompletionListener(new OnCompletionListener() {

													@Override
													public void onCompletion(MediaPlayer mp) {
														// TODO Auto-generated method stub
														tvHiHat.setTextColor(Color.WHITE);	
													}
												});
												break;
												case 'b': System.out.println(c);
												mpBass.start();
												tvBass.setTextColor(Color.YELLOW);
												tvHiHat.setTextColor(Color.WHITE);
												tvCrash.setTextColor(Color.WHITE);
												tvRide.setTextColor(Color.WHITE);
												tvSnare.setTextColor(Color.WHITE);
												tvTomMid.setTextColor(Color.WHITE);
												mpBass.setOnCompletionListener(new OnCompletionListener() {

													@Override
													public void onCompletion(MediaPlayer mp) {
														// TODO Auto-generated method stub
														tvBass.setTextColor(Color.WHITE);	
													}
												});
												break;
												case 'q': System.out.println(c);
												mpTomMid.start();
												tvTomMid.setTextColor(Color.YELLOW);
												tvBass.setTextColor(Color.WHITE);
												tvCrash.setTextColor(Color.WHITE);
												tvRide.setTextColor(Color.WHITE);
												tvSnare.setTextColor(Color.WHITE);
												tvHiHat.setTextColor(Color.WHITE);
												mpTomMid.setOnCompletionListener(new OnCompletionListener() {

													@Override
													public void onCompletion(MediaPlayer mp) {
														// TODO Auto-generated method stub
														tvTomMid.setTextColor(Color.WHITE);	
													}
												});
												break;


												

											
												}
										}
									});
								}
								else
								{
									readBuffer[readBufferPosition++] = b;
								}
							}
						}
					} 
					catch (IOException ex) 
					{
						stopWorker = true;
					}
				}
			}
		});

		workerThread.start();
	}


	void sendData(Character msg) throws IOException
	{


		mmOutputStream.write(msg);
		myLabel.setText("Data Sent");
	}

	void closeBT() throws IOException
	{
		stopWorker = true;
		mmOutputStream.close();
		mmInputStream.close();
		mmSocket.close();
		myLabel.setText("Bluetooth Closed");
	}
}