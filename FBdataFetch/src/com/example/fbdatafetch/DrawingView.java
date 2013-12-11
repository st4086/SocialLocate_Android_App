package com.example.fbdatafetch;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class DrawingView extends View {

	public int width;
	public int height;
	private Bitmap mBitmap;
	private Canvas mCanvas;
	private Path mPath;
	private Paint mBitmapPaint;
	Context context;
	private String[] Users_Name;
	private double[] x, y;
	private int[][] A;
	private int num;
	private Paint mPaint;
	private Paint circlePaint;
	private Paint TextPaint;
	private int User_id;

	public DrawingView(Context c, AttributeSet attrs) {
		super(c, attrs);
		context = c;
		mPath = new Path();
		mBitmapPaint = new Paint(Paint.DITHER_FLAG);
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		mPaint.setColor(Color.BLUE);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setStrokeWidth(4f);
		circlePaint = new Paint();
		circlePaint.setAntiAlias(true);
		circlePaint.setColor(Color.GREEN);
		circlePaint.setStyle(Paint.Style.STROKE);
		circlePaint.setStrokeJoin(Paint.Join.MITER);
		circlePaint.setStrokeWidth(4f);
	}

	public void SetPos(double[] x, double[] y) {
		double[] Min = new double[2];
		double[] Max = new double[2];
		this.x = x;
		this.y = y;

		for (int i = 0; i < x.length; i++) {
			x[i] = 2 * (x[i] + 100);
			y[i] = 2 * (y[i] + 100);
		}

		Min[0] = x[0];
		Max[0] = x[0];
		Min[1] = y[0];
		Max[1] = y[0];

		for (int i = 1; i < x.length; i++) {
			if (x[i] > Max[0])
				Max[0] = x[i];
			else if (x[i] < Min[0])
				Min[0] = x[i];
			if (y[i] > Max[1])
				Max[1] = y[i];
			else if (y[i] < Min[1])
				Min[1] = y[i];
		}

		for (int i = 0; i < x.length; i++) {
			this.x[i] = ((x[i] - Min[0]) / (Max[0] - Min[0])) * 500 + 100;
			this.y[i] = ((y[i] - Min[1]) / (Max[1] - Min[1])) * 800 + 100;
		}
		// invalidate();
	}

	public void SetA(int[][] A, int num, int User_id) {
		this.A = A;
		this.num = num;
		this.User_id = User_id;
	}

	public void SetUsersName(String[] Users_Name) {
		this.Users_Name = Users_Name;
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);

		mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		mCanvas = new Canvas(mBitmap);

	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
		// mPath.moveTo(x, y);
		// mPath.lineTo(2*x, 2*y);
		// canvas.drawPath(mPath, mPaint);
		if (x != null) {
			for (int i = 0; i < x.length; i++) {
				if (i == User_id) {
					circlePaint.setColor(Color.RED);
					canvas.drawCircle((float) x[i], (float) y[i], 75,
							circlePaint);
					circlePaint.setColor(Color.BLACK);
					circlePaint.setTextSize(50);
					canvas.drawText(Users_Name[i], (float) x[i], (float) y[i],
							circlePaint);
				} else {
					circlePaint.setColor(Color.GREEN);
					canvas.drawCircle((float) x[i], (float) y[i], 50,
							circlePaint);
					circlePaint.setColor(Color.GRAY);
					circlePaint.setTextSize(50);
					canvas.drawText(Users_Name[i], (float) x[i], (float) y[i],
							circlePaint);
				}
			}
			for (int i = 0; i < num - 1; i++) {
				for (int j = i + 1; j < num; j++) {
					if (A[i][j] != 0) {
						Log.e("show A", "A: " + i + ", " + j);
						canvas.drawLine((float) x[i], (float) y[i],
								(float) x[j], (float) y[j], mPaint);
					}
				}
			}
		}
		// canvas.drawPath(circlePath, circlePaint);
		Log.e("Drawing", "in onDraw");
	}

}
