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

	private ImageButton straightLineToolButton, freeDrawToolButton,
			curvedLineToolButton, clearToolButton, eraserToolButton,
			undoButton, redoButton;
	private Button currentLetterDisplayButton, prevButton, saveButton,
			nextButton;

	private DrawPanel drawPanel;

	public enum DrawingTools {
		straightLine, freeDraw, curvedLine, eraser
	}

	private DrawingTools currentTool;

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
		drawPanel = (DrawPanel) findViewById(R.id.drawPanel);
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
		currentTool = DrawingTools.straightLine;
		drawPanel.setCurrentTool(DrawingTools.straightLine);
		updateToolHighlight();
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
			currentTool = DrawingTools.straightLine;
			updateToolHighlight();
			drawPanel.setCurrentTool(DrawingTools.straightLine);
			break;
		case R.id.freeDrawToolButton:
			currentTool = DrawingTools.freeDraw;
			updateToolHighlight();
			drawPanel.setCurrentTool(DrawingTools.freeDraw);

			break;
		case R.id.curvedLineToolButton:
			currentTool = DrawingTools.curvedLine;
			updateToolHighlight();
			drawPanel.setCurrentTool(DrawingTools.curvedLine);

			break;
		case R.id.clearToolButton:
			drawPanel.clear();
			break;
		case R.id.eraserToolButton:
			currentTool = DrawingTools.eraser;
			updateToolHighlight();
			drawPanel.setCurrentTool(DrawingTools.eraser);

			break;
		case R.id.undoButton:
			drawPanel.undo();
			break;
		case R.id.redoButton:
			drawPanel.redo();
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

	private void updateToolHighlight() {
		straightLineToolButton.setBackgroundResource(R.color.transparent);
		freeDrawToolButton.setBackgroundResource(R.color.transparent);
		curvedLineToolButton.setBackgroundResource(R.color.transparent);
		eraserToolButton.setBackgroundResource(R.color.transparent);
		switch (currentTool) {
		case straightLine:
			straightLineToolButton
					.setBackgroundResource(R.color.transparentbuttonselected);
			break;
		case freeDraw:
			freeDrawToolButton
					.setBackgroundResource(R.color.transparentbuttonselected);
			break;
		case curvedLine:
			curvedLineToolButton
					.setBackgroundResource(R.color.transparentbuttonselected);
			break;
		case eraser:
			eraserToolButton
					.setBackgroundResource(R.color.transparentbuttonselected);
			break;
		}
	}
}
