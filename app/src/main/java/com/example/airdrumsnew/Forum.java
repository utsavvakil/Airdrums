package com.example.airdrumsnew;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.widget.TextView;


public class Forum extends Activity {
	
	TextView tvKausic,tvUtsav,tvAch1,tvAch2,tvChal1,tvChal2;
	Typeface font;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.forum);
		
		font = Typeface.createFromAsset(getAssets(), "Roboto-Thin.ttf");

		tvKausic = (TextView)findViewById(R.id.tvKausic);
		tvKausic.setTypeface(font);

		tvUtsav = (TextView)findViewById(R.id.tvUtsav);
		tvUtsav.setTypeface(font);

		tvAch1 = (TextView)findViewById(R.id.tvAch1);
		tvAch1.setTypeface(font);

		tvAch2 = (TextView)findViewById(R.id.tvAch2);
		tvAch2.setTypeface(font);

		tvChal1 = (TextView)findViewById(R.id.tvChal1);
		tvChal1.setTypeface(font);

		tvChal2 = (TextView)findViewById(R.id.tvChal2);
		tvChal2.setTypeface(font);
		
		android.app.ActionBar bar = this.getActionBar();
		ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#5677fc"));
		bar.setBackgroundDrawable(colorDrawable);

		int titleId = getResources().getIdentifier("action_bar_title", "id",
				"android");
		SpannableString s = new SpannableString("Forum");
		s.setSpan(new TypefaceSpan(this, "Roboto-Thin.ttf"), 0, s.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		
		
		
	}
	
	
	
}