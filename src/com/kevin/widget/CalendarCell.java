
package com.kevin.widget;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;


public class CalendarCell {
	private Boolean isPriceCell = false;
	private String str_pirce;
	private float f_Scale;
	
	protected Rect mBound = null;
	protected int year;
	protected int month;
	protected int mDayOfMonth = 1;	// from 1 to 31
	protected int weekOfDay;
	protected Paint bgPaint = new Paint();
	protected Paint mPaint = new Paint(Paint.SUBPIXEL_TEXT_FLAG
            |Paint.ANTI_ALIAS_FLAG);
	int dx, dy;
	public CalendarCell(int year, int month, int dayOfMon,int week, Rect rect, float textSize, boolean bold) {
		this.year = year;
		this.month = month;
		mDayOfMonth = dayOfMon;
		weekOfDay = week;
		mBound = rect;
		mPaint.setTextSize(textSize);
		mPaint.setColor(Color.rgb(126, 126, 126));
		bgPaint.setColor(Color.rgb(255, 255, 255));
		if(bold) mPaint.setFakeBoldText(true);
		
		dx = (int) mPaint.measureText(String.valueOf(mDayOfMonth)) / 2;
		dy = (int) (-mPaint.ascent() + mPaint.descent()) / 2;
	}
	
	public CalendarCell(int year, int month, int dayOfMon,int week, Rect rect, float textSize) {
		this(year, month, dayOfMon,week, rect, textSize, false);
	}
	
	protected void draw(Canvas canvas) {
		Paint borderPaint = new Paint();
		borderPaint.setStrokeWidth(2);
		borderPaint.setColor(Color.rgb(204, 204, 204));
		if(isPriceCell){
			canvas.drawLine(mBound.left+4, mBound.top+5, mBound.right-4, mBound.top+5, borderPaint);//上
			canvas.drawLine(mBound.left+4, mBound.bottom-5, mBound.right-4, mBound.bottom-5, borderPaint);//下
			canvas.drawLine(mBound.left+5, mBound.top+6, mBound.left+5, mBound.bottom-6, borderPaint);//左
			canvas.drawLine(mBound.right-5, mBound.top+6, mBound.right-5, mBound.bottom-6, borderPaint);//右
			canvas.drawRect(mBound.left+6, mBound.top+6, mBound.right-6, mBound.bottom-6, bgPaint);
			canvas.drawText(String.valueOf(mDayOfMonth), mBound.centerX() - dx, mBound.centerY() - dy + 6*f_Scale, mPaint);
			Paint pricePaint =  new Paint(Paint.SUBPIXEL_TEXT_FLAG |Paint.ANTI_ALIAS_FLAG);
			pricePaint.setTextSize(mPaint.getTextSize()-4);
			pricePaint.setColor(Color.rgb(255,144,0));
			canvas.drawText(String.valueOf(str_pirce), mBound.centerX() - (int) pricePaint.measureText(String.valueOf(str_pirce)) / 2, mBound.centerY() + dy + 5*f_Scale, pricePaint);
			
		}else{
			canvas.drawLine(mBound.left+4, mBound.top+5, mBound.right-4, mBound.top+5, borderPaint);//上
			canvas.drawLine(mBound.left+4, mBound.bottom-5, mBound.right-4, mBound.bottom-5, borderPaint);//下
			canvas.drawLine(mBound.left+5, mBound.top+6, mBound.left+5, mBound.bottom-6, borderPaint);//左
			canvas.drawLine(mBound.right-5, mBound.top+6, mBound.right-5, mBound.bottom-6, borderPaint);//右
			canvas.drawRect(mBound.left+6, mBound.top+6, mBound.right-6, mBound.bottom-6, bgPaint);
			canvas.drawText(String.valueOf(mDayOfMonth), mBound.centerX() - dx, mBound.centerY() + dy -5, mPaint);
		}
	}
	
	public int getYear() {
		return year;
	}

	public int getMonth() {
		return month;
	}

	public int getDayOfMonth() {
		return mDayOfMonth;
	}
	
	public int getWeekOfDay(){
		return weekOfDay;
	}
	
	public boolean hitTest(int x, int y) {
		return mBound.contains(x, y); 
	}
	
	public Rect getBound() {
		return mBound;
	}
	
	public String toString() {
		return String.valueOf(mDayOfMonth)+"("+mBound.toString()+")";
	}

	public void putPrice(String pirce, float scale) {
		// TODO Auto-generated method stub
		isPriceCell = true;
		str_pirce = pirce;
		f_Scale  = scale;
	}
	
}

