package com.google.code.fontcreator;

import android.graphics.Paint;
import android.graphics.Path;

public class Stroke {
public Stroke(Path path, Paint paint) {
		super();
		this.path = path;
		this.paint = paint;
	}
public Path getPath() {
	return path;
}
public Paint getPaint() {
	return paint;
}
private Path path;
private Paint paint;
}
