package com.google.code.fontcreator;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ManageFontActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.managefont);
		
		final Button editButton = (Button)findViewById(R.id.edit_button);
		editButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent myIntent = new Intent(v.getContext(), DrawActivity.class);
                startActivityForResult(myIntent, 0);				
			}
		});
		
		final Button exportButton = (Button)findViewById(R.id.export_button);
		exportButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent myIntent = new Intent(v.getContext(), ExportActivity.class);
                startActivityForResult(myIntent, 0);				
			}
		});
		
		final Button displayButton = (Button)findViewById(R.id.display_button);
		displayButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent myIntent = new Intent(v.getContext(), DisplayActivity.class);
                startActivityForResult(myIntent, 0);				
			}
		});
		
		final Button useButton = (Button)findViewById(R.id.use_button);
		useButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent myIntent = new Intent(v.getContext(), UseActivity.class);
                startActivityForResult(myIntent, 0);				
			}
		});
	}
}
