package com.google.code.fontcreator;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;


public class UseActivity extends Activity {
	
	private Spinner fontSpinner;
	
	private EditText noteEditText;

	private Button sendButton;
	private Button clearButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.usefont);
		initFontSpinner();
		initEditText();
		initButtons();
	}
	
	/**
	 * Initialize the Font Spinner to selct font.
	 */
	private void initFontSpinner(){
		fontSpinner = (Spinner) findViewById(R.id.chooseFontSpinner);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.fonts,
				android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		fontSpinner.setAdapter(adapter);
	}
	/**
	 * Initialize the EditText that lets user write the note
	 */
	private void initEditText(){
		noteEditText = (EditText) findViewById(R.id.noteEditText);
	}
	
	private void initButtons() {
		// create the buttons
		sendButton = (Button) findViewById(R.id.sendButton);
		clearButton = (Button) findViewById(R.id.clearButton);

		// listeners for the buttons
		sendButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				Intent intent = new Intent(UseActivity.this, SendActivity.class);
	        	startActivity(intent);
			}

		});
		
		clearButton.setOnClickListener(new View.OnClickListener() {
			//clears the edit text
			public void onClick(View view) {
				noteEditText.setText("");
			}

		});

		
	}
}
