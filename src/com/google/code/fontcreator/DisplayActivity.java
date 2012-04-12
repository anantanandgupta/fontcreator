package com.google.code.fontcreator;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class DisplayActivity extends Activity{
	private TextView fontDisplayTextView;
	private Button mainMenuButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.displayfont);
		initFontDisplay();
		initButtons();
	}
	
	private void initFontDisplay(){
		fontDisplayTextView = (TextView) findViewById(R.id.fontDisplayTextView);
		fontDisplayTextView.setText(getString(R.string.UCLetters)
				+"\n" +getString(R.string.LCLetters)+ "\n" + getString(R.string.symbols));
	}
	
	private void initButtons() {
		// create the mainMenuButton
		mainMenuButton = (Button) findViewById(R.id.menuButton);

		// listener for the button
		mainMenuButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				Intent intent = new Intent(DisplayActivity.this, MainMenuActivity.class);
	        	startActivity(intent);
			}

		});

		
	}
}
