package com.google.code.fontcreator;

import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.Log;

public class Stroke {

	private Point start, end, control;
	
	private Path path;
	
	private boolean isComponentWise;

	public Point getStart() {
		return start;
	}

	public Point getEnd() {
		return end;
	}
	
	public Point getControl() {
		return control;
	}

	public Stroke(Point start, Point control, Point end, Paint paint) {
		Log.v("WTF",start+" " + control + " " + end);
		this.start = new Point(start);
		this.end = new Point(end);
		this.control = new Point(control);
		this.paint = paint;
		
		path = new Path();
		path.moveTo(start.x, start.y);
		path.quadTo(control.x, control.y, end.x, end.y);
		isComponentWise = false;
	}
	
	public Stroke( Path path, Paint paint) {
		this.path = path;
		this.paint = paint;
		isComponentWise = true;
	}

	public Path getPath() {
		return path;
	}

	public Paint getPaint() {
		return paint;
	}
	
	public boolean isComponentWisePath() {
		return isComponentWise;
	}
	
	private Paint paint;
}
