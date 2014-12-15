package com.kevin.widget;

import java.util.Calendar;
import java.util.HashMap;

import com.kevin.kandroidwidget.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.MonthDisplayHelper;
import android.view.View;
/**
 * 
 * @author liu.yang
 *
 */
public class CalendarView extends View{
	
	private Boolean isPriceCalendar = false;
//	private String str_json;
	
	public static final int DEFAULT_BOARD_SIZE = 100;
	private static float CELL_TEXT_SIZE;
	
	private int mCellWidth;
	private int mCellHeight;
	
	public static final int CURRENT_MOUNT = 0;
    public static final int NEXT_MOUNT = 1;
    public static final int PREVIOUS_MOUNT = -1;
	private static final String[] weekTitle = {"周日","周一","周二","周三","周四","周五","周六"};
	
	private Calendar mRightNow = null;
	private CalendarCell mToday = null;
	private CalendarCell mTouchedCell = null;
	private CalendarCell[][] mCells;
	private HashMap<String, String> minPrice;
	
	private OnMonthChangeListener monthChangeListener;
	MonthDisplayHelper mHelper;
	
	private Paint mBackgroundColor;
	private Paint today_CellBackground;
	private Paint today_CellTextColor;
	private Paint cellBackgroundColorTouched;
	private Paint mWeekTitle;
	private float scale;
	
	public CalendarView(Context context) {
		this(context, null);
	}
	
	public CalendarView(Context context, AttributeSet attrs){
		super(context, attrs);
		scale = context.getResources().getDisplayMetrics().density;
		initCalendarView();
	}
	
	private void initCalendarView() {
		mRightNow = Calendar.getInstance();
		mHelper = new MonthDisplayHelper(
					mRightNow.get(Calendar.YEAR),
					mRightNow.get(Calendar.MONTH),
					Calendar.SUNDAY
				);
		
		mBackgroundColor = new Paint();
		today_CellBackground = new Paint();
		today_CellTextColor = new Paint(Paint.SUBPIXEL_TEXT_FLAG  |Paint.ANTI_ALIAS_FLAG);
		cellBackgroundColorTouched = new Paint();
		mWeekTitle = new Paint(Paint.SUBPIXEL_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
		
		mBackgroundColor.setColor(getResources().getColor(R.color.background));
		
		
		today_CellBackground.setColor(getResources().getColor(R.color.lightblue_cell));
		today_CellTextColor.setColor(getResources().getColor(R.color.blue_script));
		
		cellBackgroundColorTouched.setColor(getResources().getColor(R.color.darkblue_cell));
		
		mWeekTitle.setColor(Color.rgb(135, 135, 135));
	}
	
	private void initCells() {
		class _calendar {
			public int year;
			public int month;
	    	public int day;
	    	public int whichMonth;  // -1 为上月 1为下月 0为此月
	    	public _calendar(int y, int m, int d, int b) {
	    		year = y;
	    		month = m;
	    		day = d;
	    		whichMonth = b;
	    	}
	    	public _calendar(int y, int m, int d) { // 上个月默认
	    		this(y, m, d, PREVIOUS_MOUNT);
	    	}
	    };
	    _calendar tmpcells[][] = new _calendar[6][7];
	    
	    for(int i=0; i<tmpcells.length; i++) {
	    	int n[] = mHelper.getDigitsForRow(i);
	    	for(int d=0; d<n.length; d++) {
	    		if(mHelper.isWithinCurrentMonth(i,d))
	    			tmpcells[i][d] = new _calendar(mHelper.getYear(), mHelper.getMonth()+1, n[d], CURRENT_MOUNT);
	    		else if(i == 0) {
	    			tmpcells[i][d] = new _calendar(mHelper.getYear(), mHelper.getMonth(), n[d]);
	    		} else {
	    			tmpcells[i][d] = new _calendar(mHelper.getYear(), mHelper.getMonth()+2, n[d], NEXT_MOUNT);
	    		}
	    		
	    	}
	    }
	    
	    Calendar today = Calendar.getInstance();
	    int thisDay = 0;
	    mToday = null;
	    if(mHelper.getYear()==today.get(Calendar.YEAR) && mHelper.getMonth()==today.get(Calendar.MONTH)) {
	    	thisDay = today.get(Calendar.DAY_OF_MONTH);
	    }
	    // build cells
		Rect Bound = new Rect(getPaddingLeft(), mCellHeight+getPaddingTop(), mCellWidth+getPaddingLeft(), 2*mCellHeight+getPaddingTop());
		mCells = new CalendarCell[6][7];
		for(int week=0; week<mCells.length; week++) {
			for(int day=0; day<mCells[week].length; day++) {
				if(tmpcells[week][day].whichMonth == CURRENT_MOUNT) { // 如果临时cell等于当前月
					//-----------------------------------------判别六日，设置不同cell--------------------------------------------
//					if(day==0 || day==6 )
//						mCells[week][day] = new WeekendCell(tmpcells[week][day].year, tmpcells[week][day].month, tmpcells[week][day].day, new Rect(Bound), CELL_TEXT_SIZE);
//					else 
//						mCells[week][day] = new CalendarCell(tmpcells[week][day].year, tmpcells[week][day].month, tmpcells[week][day].day, new Rect(Bound), CELL_TEXT_SIZE);
					//---------------------------------------------------------------------------------------------------------------------
					if(tmpcells[week][day].day<thisDay)
						mCells[week][day] = new CalendarCell(tmpcells[week][day].year, tmpcells[week][day].month, tmpcells[week][day].day,day, new Rect(Bound), CELL_TEXT_SIZE);
					else
						mCells[week][day] = new CalendarFutureCell(tmpcells[week][day].year, tmpcells[week][day].month, tmpcells[week][day].day,day, new Rect(Bound), CELL_TEXT_SIZE);
				} else if(tmpcells[week][day].whichMonth == PREVIOUS_MOUNT) { 
					// 上月日期----为gray
//					mCells[week][day] = new GrayCell(tmpcells[week][day].year, tmpcells[week][day].month, tmpcells[week][day].day, new Rect(Bound), CELL_TEXT_SIZE);
				} else { 
					// 下月日期----为LTGray
//					mCells[week][day] = new LTGrayCell(tmpcells[week][day].year, tmpcells[week][day].month, tmpcells[week][day].day, new Rect(Bound), CELL_TEXT_SIZE);
				}
				
				Bound.offset(mCellWidth, 0); // move to next column 
				
				// get today
				if(tmpcells[week][day].day==thisDay && tmpcells[week][day].whichMonth == 0) {
					mToday = mCells[week][day];
				}
			}
			Bound.offset(0, mCellHeight); // move to next row and first column
			Bound.left = getPaddingLeft();
			Bound.right = getPaddingLeft()+mCellWidth;
		}
	}

	public int getYear() {
		return mHelper.getYear();
	}
	    
	public int getMonth() {
		return mHelper.getMonth()+1;
	}
	
	public void nextMonth() {
		mHelper.nextMonth();
		initCells();
		invalidate();
		if(monthChangeListener!=null)
			monthChangeListener.onMonthChanged();
	}
	    
	public void previousMonth() {
		mHelper.previousMonth();
		initCells();
		invalidate();
		if(monthChangeListener!=null)
			monthChangeListener.onMonthChanged();
	}
	
	//获取长宽，计算cell长宽，cell/星期字体大小
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int width = -1, height = -1;
        if (widthMode == MeasureSpec.EXACTLY) {
        	width = widthSize;
        } else {
        	width = DEFAULT_BOARD_SIZE;
        	if (widthMode == MeasureSpec.AT_MOST && width > widthSize ) {
        		width = widthSize;
        	}
        }
        if (heightMode == MeasureSpec.EXACTLY) {
        	height = heightSize;
        } else {
        	height = DEFAULT_BOARD_SIZE;
        	if (heightMode == MeasureSpec.AT_MOST && height > heightSize ) {
        		height = heightSize;
        	}
        }
        
        if (widthMode != MeasureSpec.EXACTLY) {
        	width = height;
        }
        
        if (heightMode != MeasureSpec.EXACTLY) {
        	height = width;
        }
        
    	if (widthMode == MeasureSpec.AT_MOST && width > widthSize ) {
    		width = widthSize;
    	}
    	if (heightMode == MeasureSpec.AT_MOST && height > heightSize ) {
    		height = heightSize;
    	}
    	
    	mCellWidth = (width - getPaddingLeft() - getPaddingRight()) / 7;
        mCellHeight = (height - getPaddingTop() - getPaddingBottom()) / 7;
        //先初始化cell，赋值mcells，测量view大小-----------------
        initCells();
        if(mCells[5][0]==null&&mCells[5][6]==null&&mCells[1][0]!=null){
        	setMeasuredDimension(width, height-mCellHeight);
        }else{
        	setMeasuredDimension(width, height);
        }
        //---------------------------------------------------------------------------
        CELL_TEXT_SIZE = mCellHeight * 0.35f;
        mWeekTitle.setTextSize(mCellHeight * 0.3f);
        
	}
//-------------------------------------------------------------------------------------------------
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		//draw backrgound
		if(mCells[5][0]==null&&mCells[5][6]==null){
			canvas.drawRect(getPaddingLeft(), getPaddingTop(), 7*mCellWidth+getPaddingLeft(), 6*mCellHeight+getPaddingTop(), mBackgroundColor);
		}else{
			canvas.drawRect(getPaddingLeft(), getPaddingTop(), 7*mCellWidth+getPaddingLeft(), 7*mCellHeight+getPaddingTop(), mBackgroundColor);
		}
		
		Rect tempBound = new Rect(getPaddingLeft(), getPaddingTop(), getPaddingLeft()+mCellWidth, getPaddingTop()+mCellHeight);
		for(String str:weekTitle){
			int dx,dy;
			dx = (int) (mWeekTitle.measureText(str)/2);
			dy = (int) ((-mWeekTitle.ascent() + mWeekTitle.descent()) / 2);
			if(str.equals("周六")||str.equals("周日")){
				mWeekTitle.setColor(Color.rgb(58, 128, 182));
			}else{
				mWeekTitle.setColor(Color.rgb(135, 135, 135));
			}
			canvas.drawText(str, tempBound.centerX()-dx, tempBound.centerY()+dy, mWeekTitle);
			tempBound.offset(mCellWidth, 0);
		}
		//check it if is normal calendar or price calendar
		if(isPriceCalendar){
//			UserInfoDBManager uidbm = new UserInfoDBManager();
//			minPrice = uidbm.getTicketMinPriceMap();
			String str_price;
			Calendar today = Calendar.getInstance();
			int m = today.get(Calendar.MONTH)+1;
			int d = today.get(Calendar.DAY_OF_MONTH);
			Paint pricePaint =  new Paint(Paint.SUBPIXEL_TEXT_FLAG |Paint.ANTI_ALIAS_FLAG);
			pricePaint.setTextSize(CELL_TEXT_SIZE-4);
			pricePaint.setColor(Color.rgb(255,144,0));
			// draw cells
			for(CalendarCell[] week : mCells) {
				for(CalendarCell day : week) {
					if(day!=null){
						if(day.getMonth() == m ){
							if(day.getDayOfMonth()<d ){
//								str_price = minPrice.get(String.valueOf(day.getYear())+String.valueOf(day.getMonth())+String.valueOf(day.getDayOfMonth()));
//								day.putPrice("");
								day.draw(canvas);		
							}else{
								str_price = minPrice.get(String.valueOf(day.getYear())+String.valueOf(day.getMonth())+String.valueOf(day.getDayOfMonth()));
								day.putPrice(str_price == null ? "查看" : "￥"+str_price,scale);
								day.draw(canvas);		
							}
						}else{
							str_price = minPrice.get(String.valueOf(day.getYear())+String.valueOf(day.getMonth())+String.valueOf(day.getDayOfMonth()));
							day.putPrice(str_price == null ? "查看" : "￥"+str_price,scale);
							day.draw(canvas);		
						}
					}
				}
			}
			// draw today
			if(mToday!=null){
				str_price = minPrice.get(String.valueOf(mToday.getYear())+String.valueOf(mToday.getMonth())+String.valueOf(mToday.getDayOfMonth()));
				str_price = str_price == null ? "查看" : "￥"+str_price;
				Rect bound = mToday.getBound();
				canvas.drawRect(bound.left+6, bound.top+6, bound.right-6, bound.bottom-6, today_CellBackground);
				today_CellTextColor.setTextSize(CELL_TEXT_SIZE);
				int dx, dy;
				dx = (int) today_CellTextColor.measureText(String.valueOf(mToday.getDayOfMonth())) / 2;
				dy = (int) (-today_CellTextColor.ascent() + today_CellTextColor.descent()) / 2;
				canvas.drawText(String.valueOf(mToday.getDayOfMonth()), bound.centerX()-dx, bound.centerY() - dy + 6*scale, today_CellTextColor);		
				dx = (int) pricePaint.measureText(str_price) / 2;
				dy = (int) (-pricePaint.ascent() + pricePaint.descent()) / 2;
				canvas.drawText(str_price, bound.centerX()-dx, bound.centerY() + dy + 5*scale, pricePaint);
			}
						
			//draw touched
						
			if (mTouchedCell != null) {
				str_price = minPrice.get(String.valueOf(mTouchedCell.getYear())+String.valueOf(mTouchedCell.getMonth())+String.valueOf(mTouchedCell.getDayOfMonth()));
				str_price = str_price == null ? "查看" : "￥"+str_price;
				if (mTouchedCell.getDayOfMonth() < Calendar.getInstance()
						.getTime().getDate()
						&& mTouchedCell.getMonth() == Calendar.getInstance()
								.get(Calendar.MONTH) + 1
						&& mTouchedCell.getYear() == Calendar.getInstance()
								.get(Calendar.YEAR)) {

				} else {
					Rect bound = mTouchedCell.getBound();
					canvas.drawRect(bound.left+6, bound.top+6, bound.right-6, bound.bottom-6, cellBackgroundColorTouched);
					Paint cellTextColorTouched = new Paint(
							Paint.SUBPIXEL_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
					cellTextColorTouched.setTextSize(CELL_TEXT_SIZE);
					cellTextColorTouched.setColor(getResources().getColor(
							R.color.white_script));
					
					int dx, dy;
					dx = (int) cellTextColorTouched.measureText(String.valueOf(mTouchedCell.getDayOfMonth())) / 2;
					dy = (int) (-cellTextColorTouched.ascent() + cellTextColorTouched.descent()) / 2;
					canvas.drawText(String.valueOf(mTouchedCell.getDayOfMonth()), bound.centerX()-dx, bound.centerY() - dy + 6*scale, cellTextColorTouched);		
					dx = (int) pricePaint.measureText(str_price) / 2;
					dy = (int) (-pricePaint.ascent() + pricePaint.descent()) / 2;
					canvas.drawText(str_price, bound.centerX()-dx, bound.centerY() + dy + 5*scale, pricePaint);
				}
			}
			
		}else{
			// draw cells
			for(CalendarCell[] week : mCells) {
				for(CalendarCell day : week) {
					if(day!=null)
						day.draw(canvas);			
				}
			}
			// draw today
			if(mToday!=null){
				Rect bound = mToday.getBound();
				canvas.drawRect(bound.left+6, bound.top+6, bound.right-6, bound.bottom-6, today_CellBackground);
				today_CellTextColor.setTextSize(CELL_TEXT_SIZE);
				int dx, dy;
				dx = (int) today_CellTextColor.measureText(String.valueOf(mToday.getDayOfMonth())) / 2;
				dy = (int) (-today_CellTextColor.ascent() + today_CellTextColor.descent()) / 2;
				canvas.drawText(String.valueOf(mToday.getDayOfMonth()), bound.centerX()-dx, bound.centerY() - dy + 6*scale, today_CellTextColor);
				today_CellTextColor.setTextSize(CELL_TEXT_SIZE-5);
//				today_CellTextColor.setFakeBoldText(true);			
				dx = (int) today_CellTextColor.measureText("今天") / 2;
				dy = (int) (-today_CellTextColor.ascent() + today_CellTextColor.descent()) / 2;
				canvas.drawText("今天", bound.centerX()-dx, bound.centerY()+dy + 5*scale, today_CellTextColor);
			}
			
			//draw touched
			
					if(mTouchedCell!=null){
						if (mTouchedCell.getDayOfMonth() < Calendar
								.getInstance().getTime().getDate()
								&& mTouchedCell.getMonth() == Calendar
										.getInstance().get(Calendar.MONTH)+1
								&& mTouchedCell.getYear() == Calendar
										.getInstance().get(Calendar.YEAR)) {
							
						}else{
						
						
						Rect bound = mTouchedCell.getBound();
						canvas.drawRect(bound.left+6, bound.top+6, bound.right-6, bound.bottom-6, cellBackgroundColorTouched);
						
						Paint cellTextColorTouched = new Paint(Paint.SUBPIXEL_TEXT_FLAG |Paint.ANTI_ALIAS_FLAG);
						cellTextColorTouched.setTextSize(CELL_TEXT_SIZE);
						cellTextColorTouched.setColor(getResources().getColor(R.color.white_script));
						int dx, dy;
						dx = (int) cellTextColorTouched.measureText(String.valueOf(mTouchedCell.getDayOfMonth())) / 2;
						dy = (int) (-cellTextColorTouched.ascent() + cellTextColorTouched.descent()) / 2;
						canvas.drawText(String.valueOf(mTouchedCell.getDayOfMonth()), bound.centerX()-dx, bound.centerY()+dy-5, cellTextColorTouched);
					}
			}
		}
	}
//-------------------------------------------------------------------------------------------------
	public void getCellAtPoint(int x, int y){
		int lx = x - getPaddingLeft();
		int ly = y - getPaddingTop();
		
		int row = (int) (ly / mCellHeight);
		int col = (int) (lx / mCellWidth);
		
		if(col>=0 && col<7 && row>=1 && row<7){
			if(mCells[row-1][col]!=null)
				mTouchedCell = mCells[row-1][col];
			else
				mTouchedCell = null;
		}else {
			mTouchedCell = null;
		}
	}
	
	private class GrayCell extends CalendarCell {
		public GrayCell(int year, int month, int dayOfMon,int week, Rect rect, float s) {
			super(year, month, dayOfMon,week, rect, s);
			mPaint.setColor(Color.GRAY);
		}
	}
	
	
	private class LTGrayCell extends CalendarCell {
		public LTGrayCell(int year, int month, int dayOfMon,int week, Rect rect, float s) {
			super(year, month, dayOfMon,week, rect, s);
			mPaint.setColor(Color.GRAY);
		}
	}
	
	private class WeekendCell extends CalendarCell {
		public WeekendCell(int year, int month, int dayOfMon,int week, Rect rect, float s) {
			super(year, month, dayOfMon,week, rect, s);
			mPaint.setColor(getResources().getColor(R.color.blue_script));
		}
	}
	
	private class CalendarFutureCell extends CalendarCell {
		public CalendarFutureCell(int year, int month, int dayOfMon,int week, Rect rect, float s) {
			super(year, month, dayOfMon,week, rect, s);
			mPaint.setColor(getResources().getColor(R.color.black_script));
			bgPaint.setColor(getResources().getColor(R.color.lightblue_cell));
		}
	}
	public CalendarCell getmTodayCell() {
		return mToday;
	}
	
	
	public CalendarCell getmTouchedCell() {
		return mTouchedCell;
	}
	
	public void setmTouchedCell(CalendarCell mTouchedCell) {
		this.mTouchedCell = mTouchedCell;
	}

	public void setMonthChangeListener(OnMonthChangeListener monthChangeListener) {
		this.monthChangeListener = monthChangeListener;
	}

	public interface OnMonthChangeListener{
		public void onMonthChanged();
	}
	
	public interface OnDayClickListener{
		public void onDayClick();
	}
//------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	//废弃
//	public void conversionPriceCalendar(String json) {
//		// TODO Auto-generated method stub
//		isPriceCalendar = true;
//		str_json = json;
//		
//	}
	//替代
	public void conversionPriceCalendar() {
		// TODO Auto-generated method stub
		isPriceCalendar = true;
	}
//------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
}
