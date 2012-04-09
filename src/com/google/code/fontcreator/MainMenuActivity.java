package com.google.code.fontcreator;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class MainMenuActivity extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mainmenu);

		final Button existingFontButton = (Button)findViewById(R.id.existing_font_button);
		existingFontButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Dialog viewDialog = new Dialog(v.getContext()); 
				viewDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND, WindowManager.LayoutParams.FLAG_BLUR_BEHIND); 
				viewDialog.setTitle("Select Font"); 

				LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
				View dialogView = li.inflate(R.layout.existingfontdialogue, null); 
				viewDialog.setContentView(dialogView); 
				viewDialog.show(); 
				Spinner spinner = (Spinner) dialogView.findViewById(R.id.chooseExistingFontSpinner); 
				ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(v.getContext(), R.array.fonts, android.R.layout.simple_spinner_item); 
				adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); 
				spinner.setAdapter(adapter); 
			}
		});
	}

}
