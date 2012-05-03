package com.google.code.fontcreator;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class DrawActivity extends Activity implements OnClickListener {

	private ImageButton straightLineToolButton, freeDrawToolButton,
	curvedLineToolButton, clearToolButton, 
	undoButton, redoButton;
	private Button currentLetterDisplayButton, prevButton, saveButton,
	nextButton;

	private FontManager fontManager;

	private DrawPanel drawPanel;

	//private FontManager fontManager;

	AlphabetIterator ai;

	public enum DrawingTools {
		straightLine, freeDraw, curvedLine
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
		undoButton.setOnClickListener(this);
		redoButton.setOnClickListener(this);
		currentLetterDisplayButton.setOnClickListener(this);
		prevButton.setOnClickListener(this);
		saveButton.setOnClickListener(this);
		nextButton.setOnClickListener(this);
		currentTool = DrawingTools.straightLine;
		drawPanel.setCurrentTool(DrawingTools.straightLine);
		//fontManager = new FontManager();
		ai = new AlphabetIterator();
		fontManager = new FontManager(this);
		updateToolHighlight();
		ViewTreeObserver vto = drawPanel.getViewTreeObserver();
		vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				drawPanel.loadGlyph(ai.getCurrent(), fontManager);
			}
		});
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
			drawPanel.checkClear();
			break;
		case R.id.undoButton:
			drawPanel.undo();
			break;
		case R.id.redoButton:
			drawPanel.redo();
			break;
		case R.id.currentLetterDisplayButton:
			final AlertDialog viewDialog;
			AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());

			LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
			View dialogView = li.inflate(R.layout.letter_selector_popup, null); 

			builder.setView(dialogView);
			viewDialog = builder.create();

			final EditText letterselect = (EditText)dialogView.findViewById(R.id.select_letter_edit);
			Button selectButton = (Button)dialogView.findViewById(R.id.select_letter_button);
			selectButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View view) {
					String abc = letterselect.getText().toString();
					updateGlyph(abc);
					currentLetterDisplayButton.setText(abc);
					ai.setCurrent(abc);
					viewDialog.cancel();
				}
			});
			Button cancelButton = (Button)dialogView.findViewById(R.id.cancel_select_letter_button);
			cancelButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View view) {
					viewDialog.cancel();
				}
			});
			viewDialog.show();
			break;
		case R.id.prevButton:
			ai.prev();
			if(drawPanel.needSave())
				saveGlyphDialog(-1);
			else {
				drawPanel.clear();
				drawPanel.loadGlyph(ai.getCurrent(), fontManager);
				currentLetterDisplayButton.setText(ai.getCurrent());
			}
			break;
		case R.id.saveButton:
			saveGlyphDialog(0);
			break;
		case R.id.nextButton:
			ai.next();
			if(drawPanel.needSave())
				saveGlyphDialog(1);
			else {
				drawPanel.clear();
				drawPanel.loadGlyph(ai.getCurrent(), fontManager);
				currentLetterDisplayButton.setText(ai.getCurrent());
			}
			break;
		}
	}

	@Override
	protected void onPostResume() {
		super.onResume();
		drawPanel.loadGlyph(ai.getCurrent(), fontManager);
	}

	private void updateToolHighlight() {
		straightLineToolButton.setBackgroundResource(R.color.transparent);
		freeDrawToolButton.setBackgroundResource(R.color.transparent);
		curvedLineToolButton.setBackgroundResource(R.color.transparent);
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
		}
	}

	private void saveGlyphDialog(final int saveCase){
		final AlertDialog viewDialog;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
		View dialogView = li.inflate(R.layout.save_dialog, null); 

		builder.setView(dialogView);
		viewDialog = builder.create();

		Button noButton = (Button)dialogView.findViewById(R.id.dont_save_glyph_button);
		noButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				viewDialog.cancel();				
			}
		});

		Button saveButton = (Button)dialogView.findViewById(R.id.save_glyph_button);
		saveButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				fontManager = drawPanel.save(ai.getCurrent(), fontManager);
				drawPanel.clear();
				Toast toast = Toast.makeText(getApplicationContext(), "Letter saved!", Toast.LENGTH_LONG);
				toast.show();
				viewDialog.cancel();
			}
		});
		Button cancelButton = (Button)dialogView.findViewById(R.id.cancel_save_glyph_button);
		cancelButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				if(saveCase==-1){
					drawPanel.clear();
					ai.next();
				}
				else if(saveCase==1) {
					drawPanel.clear();
					ai.prev();
				}
				viewDialog.cancel();
			}
		});
		viewDialog.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				currentLetterDisplayButton.setText(ai.getCurrent());
			}
		});
		viewDialog.show();
	}

	private void updateGlyph(String glyphCharacter) {
		//Glyph g = fontManager.getGlyph(glyphCharacter);
		//TODO: Set drawing table to glyph
	}
}
