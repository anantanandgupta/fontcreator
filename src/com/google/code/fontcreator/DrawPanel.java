package com.google.code.fontcreator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.code.fontcreator.DrawActivity.DrawingTools;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class DrawPanel extends SurfaceView implements SurfaceHolder.Callback {
	private TutorialThread drawingThread;
	private List<Stroke> pathList, redoHistory, contourList,
			contourRedoHistory;
	private boolean initialDrawPress = false, editingControlPoint = false,
			draggingControlPoint = false, drawingContinuous = false,
			inContour = false;
	private Point startPoint = null, controlPointHandle = null,
			endPoint = null, lastContourEnd = null, contourStart = null;
	private Path currentPath;
	private Paint defaultPaint = null, continuousPaint = null, contourPaint = null;
	private int lastDownX, lastDownY, lastContX, lastContY;
	private DrawActivity.DrawingTools currentTool;

	public DrawPanel(Context context, AttributeSet attribs) {
		super(context, attribs);
		pathList = Collections.synchronizedList(new ArrayList<Stroke>());
		contourList = Collections.synchronizedList(new ArrayList<Stroke>());
		redoHistory = new ArrayList<Stroke>();
		contourRedoHistory = new ArrayList<Stroke>();
		defaultPaint = new Paint();
		defaultPaint.setColor(Color.BLUE);
		defaultPaint.setStyle(Paint.Style.STROKE);
		defaultPaint.setStrokeWidth(1.0f);
		defaultPaint.setStrokeMiter(1.0f);
		defaultPaint.setStrokeJoin(Paint.Join.MITER);
		defaultPaint.setStrokeCap(Cap.SQUARE);
		defaultPaint.setAntiAlias(true);
		continuousPaint = new Paint();
		continuousPaint.setColor(Color.BLUE);
		continuousPaint.setStyle(Paint.Style.STROKE);
		continuousPaint.setStrokeWidth(1.0f);
		continuousPaint.setStrokeMiter(1.0f);
		continuousPaint.setStrokeJoin(Paint.Join.ROUND);
		continuousPaint.setStrokeCap(Cap.ROUND);
		continuousPaint.setAntiAlias(true);
		contourPaint = new Paint();
		contourPaint.setStyle(Paint.Style.FILL_AND_STROKE);
		contourPaint.setStrokeWidth(1.0f);
		contourPaint.setStrokeMiter(1.0f);
		contourPaint.setStrokeJoin(Paint.Join.ROUND);
		contourPaint.setStrokeCap(Cap.ROUND);
		contourPaint.setAntiAlias(true);
		contourPaint.setShader(new LinearGradient(0, 0, 0, getHeight(), Color.BLACK, Color.BLACK, Shader.TileMode.REPEAT));
		getHolder().addCallback(this);
		drawingThread = new TutorialThread(getHolder(), this);
		setFocusable(true);
		setZOrderOnTop(true);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		synchronized (drawingThread.getSurfaceHolder()) {
			switch (currentTool) {
			case straightLine:
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					clearRedoHistory();
					if (!inContour) {
						startPoint = new Point((int) event.getX(),
								(int) event.getY());
						lastDownX = (int) event.getX();
						lastDownY = (int) event.getY();
						initialDrawPress = true;
						contourStart = startPoint;
						inContour = true;
					} else {
						startPoint = lastContourEnd;
						lastDownX = (int) event.getX();
						lastDownY = (int) event.getY();
						initialDrawPress = true;
					}
				} else if (initialDrawPress
						&& event.getAction() == MotionEvent.ACTION_UP) {
					Point end = new Point((int) event.getX(),
							(int) event.getY());
					lastContourEnd = end;
					Path out = new Path();
					out.moveTo(startPoint.x, startPoint.y);
					out.quadTo((end.x - startPoint.x) / 2 + startPoint.x,
							(end.y - startPoint.y) / 2 + startPoint.y, end.x,
							end.y);
					Stroke stroke = new Stroke(startPoint, end, end,
							defaultPaint);
					synchronized (pathList) {
						pathList.add(stroke);
					}
					redoHistory.clear();
					startPoint = null;
					initialDrawPress = false;
					checkClosePath();
				} else if (initialDrawPress
						&& event.getAction() == MotionEvent.ACTION_MOVE) {
					lastDownX = (int) event.getX();
					lastDownY = (int) event.getY();
				} else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
					initialDrawPress = false;
					startPoint = null;
				}
				return true;
			case curvedLine:
				if (!editingControlPoint
						&& event.getAction() == MotionEvent.ACTION_DOWN) {
					clearRedoHistory();
					if (!inContour) {
						startPoint = new Point((int) event.getX(),
								(int) event.getY());
						lastDownX = (int) event.getX();
						lastDownY = (int) event.getY();
						initialDrawPress = true;
						contourStart = startPoint;
						inContour = true;
					}
					else {
						startPoint = lastContourEnd;
						lastDownX = (int) event.getX();
						lastDownY = (int) event.getY();
						initialDrawPress = true;
					}
				} else if (editingControlPoint
						&& event.getAction() == MotionEvent.ACTION_DOWN) {
					Point curr = new Point((int) event.getX(),
							(int) event.getY());
					if (distBetween(curr, controlPointHandle) > 50) {
						Path out = new Path();
						out.moveTo(startPoint.x, startPoint.y);
						out.quadTo(controlPointHandle.x, controlPointHandle.y,
								endPoint.x, endPoint.y);
						Stroke stroke = new Stroke(startPoint,
								controlPointHandle, endPoint, defaultPaint);
						synchronized (pathList) {
							pathList.add(stroke);
						}
						editingControlPoint = false;
						controlPointHandle = null;
						redoHistory.clear();
						startPoint = null;
						initialDrawPress = false;
						endPoint = null;
						checkClosePath();
					} else {
						draggingControlPoint = true;
						lastDownX = (int) event.getX();
						lastDownY = (int) event.getY();
					}
				} else if (initialDrawPress
						&& event.getAction() == MotionEvent.ACTION_UP) {
					endPoint = new Point((int) event.getX(), (int) event.getY());
					controlPointHandle = new Point((endPoint.x - startPoint.x)
							/ 2 + startPoint.x, (endPoint.y - startPoint.y) / 2
							+ startPoint.y);
					editingControlPoint = true;
					initialDrawPress = false;
					lastContourEnd = endPoint;
				} else if (draggingControlPoint
						&& event.getAction() == MotionEvent.ACTION_UP) {
					draggingControlPoint = false;
				} else if (!draggingControlPoint
						&& event.getAction() == MotionEvent.ACTION_MOVE) {
					lastDownX = (int) event.getX();
					lastDownY = (int) event.getY();
				} else if (draggingControlPoint
						&& event.getAction() == MotionEvent.ACTION_MOVE) {
					lastDownX = (int) event.getX();
					lastDownY = (int) event.getY();
					controlPointHandle.set(lastDownX, lastDownY);
				} else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
					initialDrawPress = false;
					startPoint = null;
					controlPointHandle = null;
					draggingControlPoint = false;
					initialDrawPress = false;
					endPoint = null;
				}
				return true;
			case freeDraw:
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					clearRedoHistory();
					if (!inContour) {
						startPoint = new Point((int) event.getX(),
								(int) event.getY());
						currentPath = new Path();
						currentPath.moveTo(startPoint.x, startPoint.y);
						lastContX = startPoint.x;
						lastContY = startPoint.y;
						drawingContinuous = true;
						contourStart = startPoint;
						inContour = true;
					}
					else {
						startPoint = lastContourEnd;
						currentPath = new Path();
						currentPath.moveTo(startPoint.x, startPoint.y);
						lastContX = startPoint.x;
						lastContY = startPoint.y;
						drawingContinuous = true;
					}
				} else if (drawingContinuous
						&& event.getAction() == MotionEvent.ACTION_UP) {
					Point end = new Point((int) event.getX(),
							(int) event.getY());
					currentPath.lineTo(end.x, end.y);
					Stroke stroke = new Stroke(currentPath,
							continuousPaint);
					synchronized (pathList) {
						pathList.add(stroke);
					}
					currentPath = null;
					redoHistory.clear();
					drawingContinuous = false;
					startPoint = null;
					lastContourEnd = end;
					checkClosePath();
				} else if (drawingContinuous
						&& event.getAction() == MotionEvent.ACTION_MOVE) {
					currentPath.lineTo((int) event.getX(), (int) event.getY());
					lastContX = (int) event.getX();
					lastContY = (int) event.getY();
				} else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
					currentPath = null;
					drawingContinuous = false;
				}
				return true;
			case eraser:
				break;
			}
			return false;
		}
	}

	private void checkClosePath() {
		if (distBetween(contourStart, lastContourEnd) < 30) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
			builder.setMessage("Close contour?")
					.setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									finalizeContour();
								}
							})
					.setNegativeButton("No",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									;
								}
							}).show();
		}
	}

	private void finalizeContour() {
		Path contour = new Path();
		contour.moveTo(contourStart.x, contourStart.y);
		Point mid, end;
		synchronized (pathList) {
			for (Stroke s : pathList) {
				if (s.isComponentWisePath()){
					contour.addPath(s.getPath());
				}
				else {
					mid = s.getControl();
					end = s.getEnd();
					contour.quadTo(mid.x, mid.y, end.x, end.y);
				}
				
			}
		}
		contour.close();

		synchronized (contourList) {
			contourList.add(new Stroke(contour, contourPaint));
		}
		synchronized (pathList) {
			pathList.clear();
		}
		inContour = false;
		contourStart = null;
		lastContourEnd = null;
		clearRedoHistory();

	}
	
	public void checkClear() {
		AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
		builder.setMessage("Clear all contours? This cannot be undone.")
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int id) {
								clear();
							}
						})
				.setNegativeButton("No",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int id) {
								;
							}
						}).show();
	}

	private static int distBetween(Point p1, Point p2) {
		int dx = p1.x - p2.x, dy = p1.y - p2.y;
		return (int)Math.sqrt((dx * dx) + (dy * dy));
	}

	@Override
	public void onDraw(Canvas canvas) {
		canvas.drawColor(Color.WHITE);
		synchronized (contourList) {
			for (Stroke stroke : contourList) {
				canvas.drawPath(stroke.getPath(), stroke.getPaint());
			}
		}
		synchronized (pathList) {
			for (Stroke stroke : pathList) {
				canvas.drawPath(stroke.getPath(), stroke.getPaint());
			}
		}
		if (initialDrawPress) {
			Point end = new Point(lastDownX, lastDownY);
			Path out = new Path();
			out.moveTo(startPoint.x, startPoint.y);
			out.quadTo(end.x, end.y, end.x, end.y);
			canvas.drawPath(out, defaultPaint);
		} else if (editingControlPoint) {
			Path out = new Path();
			out.moveTo(startPoint.x, startPoint.y);
			out.quadTo(controlPointHandle.x, controlPointHandle.y, endPoint.x,
					endPoint.y);
			canvas.drawPath(out, defaultPaint);
			canvas.drawCircle(controlPointHandle.x, controlPointHandle.y, 50,
					defaultPaint);
		} else if (drawingContinuous) {
			canvas.drawPath(currentPath, continuousPaint);
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		drawingThread.setRunning(true);
		drawingThread.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// simply copied from sample application LunarLander:
		// we have to tell thread to shut down & wait for it to finish, or else
		// it might touch the Surface after we return and explode
		boolean retry = true;
		drawingThread.setRunning(false);
		while (retry) {
			try {
				drawingThread.join();
				retry = false;
			} catch (InterruptedException e) {
				// we will try it again and again...
			}
		}
	}

	public void undo() {
		finalizeDanglingControlPoints();
		boolean undoPath = false;
		synchronized (pathList) {
			if (pathList.size() > 0){
				Stroke s = pathList.remove(pathList.size() - 1);
				lastContourEnd = s.getStart();
				redoHistory.add(s);
			}
			else{
				undoPath = true;
			}
		}
		if (undoPath) {
			synchronized (contourList) {
				if (contourList.size() > 0) 
					contourRedoHistory.add(contourList.remove(contourList.size()-1));
			}
		}
	}

	public void redo() {
		finalizeDanglingControlPoints();
		boolean undoPath = false;
		synchronized (pathList) {
			if (redoHistory.size() > 0){
				Stroke s = redoHistory.remove(redoHistory.size() - 1);
				lastContourEnd = s.getEnd();
				pathList.add(s);
			}
			else {
				undoPath = true;
			}
		}
		if (undoPath) {
			synchronized (contourList) {
				if (contourRedoHistory.size() > 0) 
					contourList.add(contourRedoHistory.remove(contourRedoHistory.size()-1));
			}
		}
	}
	
	private void clearRedoHistory() {
		contourRedoHistory.clear();
		redoHistory.clear();
	}

	private void finalizeDanglingControlPoints() {
		if (editingControlPoint) {
			editingControlPoint = false;
			synchronized (pathList) {
				Path out = new Path();
				out.moveTo(startPoint.x, startPoint.y);
				out.quadTo(controlPointHandle.x, controlPointHandle.y,
						endPoint.x, endPoint.y);
				pathList.add(new Stroke(startPoint, controlPointHandle,
						endPoint, defaultPaint));
				lastContourEnd = endPoint;
			}
			checkClosePath();
		}
	}

	public DrawActivity.DrawingTools getCurrentTool() {
		return currentTool;
	}

	public void setCurrentTool(DrawActivity.DrawingTools currentTool) {
		finalizeDanglingControlPoints();
		this.currentTool = currentTool;
	}

	public void clear() {
		synchronized (pathList) {
			pathList.clear();
		}
		synchronized (contourList) {
			contourList.clear();
		}
		clearRedoHistory();
		draggingControlPoint = false;
		editingControlPoint = false;
		initialDrawPress = false;
		startPoint = null;
		endPoint = null;
		controlPointHandle = null;
		inContour = false;
	}

	private class TutorialThread extends Thread {
		private SurfaceHolder _surfaceHolder;
		private DrawPanel _panel;
		private boolean _run = false;

		public TutorialThread(SurfaceHolder surfaceHolder, DrawPanel panel) {
			_surfaceHolder = surfaceHolder;
			_panel = panel;
		}

		public void setRunning(boolean run) {
			_run = run;
		}

		public SurfaceHolder getSurfaceHolder() {
			return _surfaceHolder;
		}

		@Override
		public void run() {
			Canvas c;
			while (_run) {
				c = null;
				try {
					c = _surfaceHolder.lockCanvas(null);
					synchronized (_surfaceHolder) {
						_panel.onDraw(c);
					}
				} finally {
					// do this in a finally so that if an exception is thrown
					// during the above, we don't leave the Surface in an
					// inconsistent state
					if (c != null) {
						_surfaceHolder.unlockCanvasAndPost(c);
					}
				}
			}
		}

	}

}
