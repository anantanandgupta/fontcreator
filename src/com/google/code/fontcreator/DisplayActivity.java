package com.google.code.fontcreator;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class DisplayActivity extends Activity{
	private TextView fontDisplayTextView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.displayfont);
		initFontDisplay();
	}
	
	private void initFontDisplay(){
		fontDisplayTextView = (TextView) findViewById(R.id.fontDisplayTextView);
		fontDisplayTextView.setText(getString(R.string.UCLetters)
				+"\n" +getString(R.string.LCLetters)+ "\n" + getString(R.string.symbols));
	}
}
