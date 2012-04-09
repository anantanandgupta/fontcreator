package com.google.code.fontcreator;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;


public class UseActivity extends Activity {
	
	private Spinner fontSpinner;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.usefont);
		initFontSpinner();
	}
	
	private void initFontSpinner(){
		fontSpinner = (Spinner) findViewById(R.id.chooseFontSpinner);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.fonts,
				android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		fontSpinner.setAdapter(adapter);
	}
}
