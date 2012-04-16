package com.google.code.fontcreator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class DrawPanel extends SurfaceView implements SurfaceHolder.Callback {
	private TutorialThread drawingThread;
	private List<Stroke> pathList, redoHistory;
	private boolean initialDrawPress = false, editingControlPoint = false,
			draggingControlPoint = false, drawingContinuous = false;
	private Point startPoint = null, controlPointHandle = null,
			endPoint = null;
	private Path currentPath;
	private Paint defaultPaint = null, continuousPaint = null;
	private int lastDownX, lastDownY, lastContX, lastContY;
	private DrawActivity.DrawingTools currentTool;

	public DrawPanel(Context context, AttributeSet attribs) {
		super(context, attribs);
		pathList = Collections.synchronizedList(new ArrayList<Stroke>());
		redoHistory = new ArrayList<Stroke>();
		defaultPaint = new Paint();
		defaultPaint.setStyle(Paint.Style.STROKE);
		defaultPaint.setStrokeWidth(1.0f);
		defaultPaint.setStrokeMiter(1.0f);
		defaultPaint.setStrokeJoin(Paint.Join.MITER);
		defaultPaint.setStrokeCap(Cap.SQUARE);
		defaultPaint.setAntiAlias(true);
		continuousPaint = new Paint();
		continuousPaint.setStyle(Paint.Style.STROKE);
		continuousPaint.setStrokeWidth(1.0f);
		continuousPaint.setStrokeMiter(1.0f);
		continuousPaint.setStrokeJoin(Paint.Join.ROUND);
		continuousPaint.setStrokeCap(Cap.ROUND);
		continuousPaint.setAntiAlias(true);
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
					startPoint = new Point((int) event.getX(),
							(int) event.getY());
					lastDownX = (int) event.getX();
					lastDownY = (int) event.getY();
					initialDrawPress = true;
				} else if (initialDrawPress
						&& event.getAction() == MotionEvent.ACTION_UP) {
					Point end = new Point((int) event.getX(),
							(int) event.getY());
					Path out = new Path();
					out.moveTo(startPoint.x, startPoint.y);
					out.quadTo((end.x - startPoint.x) / 2 + startPoint.x,
							(end.y - startPoint.y) / 2 + startPoint.y, end.x,
							end.y);
					Stroke stroke = new Stroke(out, defaultPaint);
					synchronized (pathList) {
						pathList.add(stroke);
					}
					redoHistory.clear();
					startPoint = null;
					initialDrawPress = false;
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

					startPoint = new Point((int) event.getX(),
							(int) event.getY());
					lastDownX = (int) event.getX();
					lastDownY = (int) event.getY();
					initialDrawPress = true;
				} else if (editingControlPoint
						&& event.getAction() == MotionEvent.ACTION_DOWN) {
					Point curr = new Point((int) event.getX(),
							(int) event.getY());
					if (distBetween(curr, controlPointHandle) > 50) {
						Path out = new Path();
						out.moveTo(startPoint.x, startPoint.y);
						out.quadTo(controlPointHandle.x, controlPointHandle.y,
								endPoint.x, endPoint.y);
						Stroke stroke = new Stroke(out, defaultPaint);
						synchronized (pathList) {
							pathList.add(stroke);
						}
						editingControlPoint = false;
						controlPointHandle = null;
						redoHistory.clear();
						startPoint = null;
						initialDrawPress = false;
						endPoint = null;
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
					currentPath = new Path();
					currentPath.moveTo((int) event.getX(), (int) event.getY());
					lastContX = (int) event.getX();
					lastContY = (int) event.getY();
					drawingContinuous = true;
				} else if (drawingContinuous
						&& event.getAction() == MotionEvent.ACTION_UP) {
					currentPath.lineTo((int) event.getX(), (int) event.getY());
					Stroke stroke = new Stroke(currentPath, continuousPaint);
					synchronized (pathList) {
						pathList.add(stroke);
					}
					currentPath = null;
					redoHistory.clear();
					drawingContinuous = false;
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

	private static int distBetween(Point p1, Point p2) {
		int dx = p1.x - p2.x, dy = p1.y - p2.y;
		return (int) Math.abs(Math.sqrt(Math.pow(dx, 2.0) + Math.pow(dy, 2.0)));
	}

	@Override
	public void onDraw(Canvas canvas) {
		canvas.drawColor(Color.WHITE);
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
		}
		else if (drawingContinuous) {
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
		synchronized (pathList) {
			if (pathList.size() > 0)
				redoHistory.add(pathList.remove(pathList.size() - 1));
		}
	}

	public void redo() {
		finalizeDanglingControlPoints();
		synchronized (pathList) {
			if (redoHistory.size() > 0)
				pathList.add(redoHistory.remove(redoHistory.size() - 1));
		}
	}

	private void finalizeDanglingControlPoints() {
		if (editingControlPoint) {
			editingControlPoint = false;
			synchronized (pathList) {
				Path out = new Path();
				out.moveTo(startPoint.x, startPoint.y);
				out.quadTo(controlPointHandle.x, controlPointHandle.y,
						endPoint.x, endPoint.y);
				pathList.add(new Stroke(out, defaultPaint));
			}
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
		redoHistory.clear();
		draggingControlPoint = false;
		editingControlPoint = false;
		initialDrawPress = false;
		startPoint = null;
		endPoint = null;
		controlPointHandle = null;
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
