package com.google.code.fontcreator;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;

public class DrawActivity extends Activity implements OnClickListener {

	ImageButton straightLineToolButton, freeDrawToolButton,
			curvedLineToolButton, clearToolButton, eraserToolButton,
			undoButton, redoButton;
	Button currentLetterDisplayButton, prevButton, saveButton, nextButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.drawactivitylayout);
		straightLineToolButton = (ImageButton) findViewById(R.id.straightLineToolButton);
		freeDrawToolButton = (ImageButton) findViewById(R.id.freeDrawToolButton);
		curvedLineToolButton = (ImageButton) findViewById(R.id.curvedLineToolButton);
		clearToolButton = (ImageButton) findViewById(R.id.clearToolButton);
		eraserToolButton = (ImageButton) findViewById(R.id.eraserToolButton);
		undoButton = (ImageButton) findViewById(R.id.undoButton);
		redoButton = (ImageButton) findViewById(R.id.redoButton);
		currentLetterDisplayButton = (Button) findViewById(R.id.currentLetterDisplayButton);
		prevButton = (Button) findViewById(R.id.prevButton);
		saveButton = (Button) findViewById(R.id.saveButton);
		nextButton = (Button) findViewById(R.id.nextButton);
		straightLineToolButton.setOnClickListener(this);
		freeDrawToolButton.setOnClickListener(this);
		curvedLineToolButton.setOnClickListener(this);
		clearToolButton.setOnClickListener(this);
		eraserToolButton.setOnClickListener(this);
		undoButton.setOnClickListener(this);
		redoButton.setOnClickListener(this);
		currentLetterDisplayButton.setOnClickListener(this);
		prevButton.setOnClickListener(this);
		saveButton.setOnClickListener(this);
		nextButton.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.layout.drawactivitymenu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;
		switch (item.getItemId()) {
		case R.id.usefontdrawscreenmenuitem:
			intent = new Intent(this, UseActivity.class);
			startActivity(intent);
			return true;
		case R.id.displayfontdrawscreenmenuitem:
			intent = new Intent(this, DisplayActivity.class);
			startActivity(intent);
			return true;
		case R.id.mainmenudrawscreenmenuitem:
			intent = new Intent(this, MainMenuActivity.class);
			startActivity(intent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.straightLineToolButton:
			break;
		case R.id.freeDrawToolButton:
			break;
		case R.id.curvedLineToolButton:
			break;
		case R.id.clearToolButton:
			break;
		case R.id.eraserToolButton:
			break;
		case R.id.undoButton:
			break;
		case R.id.redoButton:
			break;
		case R.id.currentLetterDisplayButton:
			break;
		case R.id.prevButton:
			break;
		case R.id.saveButton:
			break;
		case R.id.nextButton:
			break;
		}
	}
}
