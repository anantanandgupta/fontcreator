package com.google.code.fontcreator;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;


public class UseActivity extends Activity {
	
	private Spinner fontSpinner;
	
	private EditText noteEditText;
	
	private Button clearButton;
	
	private String fontName;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.usefont);
		if (getIntent().getExtras().containsKey(FontDefaults.FILENAMEKEY))
			fontName = getIntent().getExtras().getString(FontDefaults.FILENAMEKEY);
		else 
			fontName = FontDefaults.DEFAULTFILENAME;
		initFontSpinner();
		initEditText();
		initButtons();
		setFont();
	}
	
	/**
	 * Initialize the Font Spinner to selct font.
	 */
	private void initFontSpinner(){
		fontSpinner = (Spinner) findViewById(R.id.chooseFontSpinner);
		final String fontFiles[] = FontUtils.getFonts(this);
		int i;
		for (i = 0 ; i < fontFiles.length; i++) {
			if (fontFiles[i].equalsIgnoreCase(fontName)) {
				break;
			}
		}
		ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, fontFiles);
		spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
		fontSpinner.setAdapter(spinnerArrayAdapter);
		fontSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				fontName = (String)arg0.getItemAtPosition(arg2);
				setFont();
			}

			

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {}
		});
		fontSpinner.setSelection(i);
	}
	/**
	 * Initialize the EditText that lets user write the note
	 */
	private void initEditText(){
		noteEditText = (EditText) findViewById(R.id.noteEditText);
	}
	
	private void setFont() {
		noteEditText.setTypeface(FontUtils.getTypeface(this, fontName));
	}
	
	private void initButtons() {
		// create the buttons
		
		clearButton = (Button) findViewById(R.id.clearButton);

		// listeners for the buttons

		
		clearButton.setOnClickListener(new View.OnClickListener() {
			//clears the edit text
			public void onClick(View view) {
				noteEditText.setText("");
			}

		});

		
	}
}
