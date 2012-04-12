package com.google.code.fontcreator;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
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
		final Button newFontButton = (Button)findViewById(R.id.new_font_button);
		newFontButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent myIntent = new Intent(v.getContext(), DrawActivity.class);
                startActivityForResult(myIntent, 0);
			}
		});
		final Button existingFontButton = (Button)findViewById(R.id.existing_font_button);
		existingFontButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				final AlertDialog viewDialog;
				AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
			
				LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
				View dialogView = li.inflate(R.layout.existingfontdialogue, null); 
				
				builder.setView(dialogView);
				viewDialog = builder.create();
				
				Spinner spinner = (Spinner) dialogView.findViewById(R.id.chooseExistingFontSpinner); 
				ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(v.getContext(), R.array.fonts, android.R.layout.simple_spinner_item); 
				adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); 
				spinner.setAdapter(adapter); 
				
				final Button okButton = (Button)dialogView.findViewById(R.id.ok_button);
				okButton.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						Intent myIntent = new Intent(v.getContext(), ManageFontActivity.class);
		                startActivityForResult(myIntent, 0);	
		                //Will put code for specific font?
					}
				});
				final Button cancelButton = (Button)dialogView.findViewById(R.id.cancel_button);
				cancelButton.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						viewDialog.cancel();
					}
				});
				
				viewDialog.show();
				
				
				/*final Dialog viewDialog = new Dialog(v.getContext()); 
				viewDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND, WindowManager.LayoutParams.FLAG_BLUR_BEHIND); 

				LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
				View dialogView = li.inflate(R.layout.existingfontdialogue, null); 
				viewDialog.setContentView(dialogView); 
				viewDialog.show(); 
				Spinner spinner = (Spinner) dialogView.findViewById(R.id.chooseExistingFontSpinner); 
				ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(v.getContext(), R.array.fonts, android.R.layout.simple_spinner_item); 
				adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); 
				spinner.setAdapter(adapter); 
				
				final Button okButton = (Button)dialogView.findViewById(R.id.ok_button);
				okButton.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						Intent myIntent = new Intent(v.getContext(), ManageFontActivity.class);
		                startActivityForResult(myIntent, 0);	
		                //Will put code for specific font?
					}
				});
				final Button cancelButton = (Button)dialogView.findViewById(R.id.cancel_button);
				cancelButton.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						viewDialog.cancel();
					}
				});*/
			}
		});
		final Button helpButton = (Button)findViewById(R.id.help_button);
		helpButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent myIntent = new Intent(v.getContext(), HelpActivity.class);
                startActivityForResult(myIntent, 0);
			}
		});
	}

}
