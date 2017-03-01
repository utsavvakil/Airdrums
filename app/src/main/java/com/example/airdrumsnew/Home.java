package com.example.airdrumsnew;





import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class Home extends Activity implements OnClickListener{

	Typeface font;
	TextView tvTitle,tvOption1,tvOption2,tvOption3;
	Button btnDrums, btnForum,btnTut;
	Bundle bundle;
	Intent intent;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);

		android.app.ActionBar bar = this.getActionBar();
		ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#5677fc"));
		bar.setBackgroundDrawable(colorDrawable);

		int titleId = getResources().getIdentifier("action_bar_title", "id",
				"android");
		SpannableString s = new SpannableString("Home");
		s.setSpan(new TypefaceSpan(this, "Roboto-Thin.ttf"), 0, s.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		// Update the action bar title with the TypefaceSpan instance
		ActionBar actionBar = getActionBar();
		actionBar.setTitle(s);

		font = Typeface.createFromAsset(getAssets(), "Roboto-Thin.ttf");

		tvTitle = (TextView)findViewById(R.id.tvTitle);
		tvTitle.setTypeface(font);

		btnDrums = (Button)findViewById(R.id.btnDrums);
		btnDrums.setTypeface(font);
		btnForum = (Button)findViewById(R.id.btnForum);
		btnForum.setTypeface(font);
		btnTut = (Button)findViewById(R.id.btnTut);
		btnTut.setTypeface(font);
		intent = new Intent(Home.this,BluetoothTest.class);

		btnDrums.setOnClickListener(this);
		btnForum.setOnClickListener(this);
		btnTut.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId())
		{
		case R.id.btnDrums:
			startActivity(intent);


		break;
		
		case R.id.btnForum: Intent intent1 = new Intent(Home.this,Forum.class);
		startActivity(intent1);
		break;

		}
		// TODO Auto-generated method stub

	}


}
