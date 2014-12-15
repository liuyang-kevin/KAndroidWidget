package com.kevin.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.View.MeasureSpec;

public class AQuarterCircleButton extends View {
	/**
	 * 
	 * 	 *********
	 *   * 0 * 1 *
	 *   *********
	 *   * 3 * 2 *
	 *   *********
	 */
	private static int qc_ButtonStyle = 0;
	private static final int SHADOW_OF_SIDE = 5;    // the shadow of side effect,i think it should be a confirm value;
	private static final int DEFAULT_W_BOARD_SIZE = 50;
	private static final int DEFAULT_H_BOARD_SIZE = 50;    //默认宽高
	
	
	private static float val_x_side = DEFAULT_W_BOARD_SIZE - SHADOW_OF_SIDE;
	private static float val_y_side = DEFAULT_H_BOARD_SIZE - SHADOW_OF_SIDE;    // its value should be "backgroundHeight-shadow_of_side"
	private float backgroundWidth;
	private float backgroundHeight;
	
	//pen
	private Paint backgroundColor;
	private Paint sideLinePaint;
	
	
	//---------------------------------------
	//1920×1080
	float screen_density = 3.0f; //得到密度
	float screen_width = 1080f;//得到宽度
//	float screen_height = displayMetrics.heightPixels;//得到高度
	
	float screen_rate = 1f;
	/////////////////////////////////////////

    public AQuarterCircleButton(Context context) {
        this(context, null);
    }

    public AQuarterCircleButton(Context context, AttributeSet attrs) {
//        this(context, attrs, com.android.internal.R.attr.buttonStyle);		//???
        this(context, attrs, qc_ButtonStyle);
    }
    
	public AQuarterCircleButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		if (!this.isInEditMode()){
			Display display = ((Activity)context).getWindowManager().getDefaultDisplay();
			DisplayMetrics displayMetrics = new DisplayMetrics();
			display.getMetrics(displayMetrics);
			float density = displayMetrics.density; //得到密度
			float width = displayMetrics.widthPixels;//得到宽度
//			float height = displayMetrics.heightPixels;//得到高度
			
			screen_rate = density/screen_density;
//			screen_rate = (width/density)/(screen_width/screen_density);
		}
		
		initValueAQuarterCircleButton();
//		textToChartDistance = 3*screen_rate;
//		rangeSideHeight = 15*screen_rate;
//		sectionSideWidth = 55*screen_rate;
	}
	
	
	private void initValueAQuarterCircleButton() {
		// TODO Auto-generated method stub
		backgroundColor = new Paint();
		backgroundColor.setColor(Color.rgb(255, 255, 255));
		
		sideLinePaint = new Paint();
		sideLinePaint.isAntiAlias();
		sideLinePaint.setStyle(Paint.Style.STROKE);
		sideLinePaint.setStrokeWidth(3);
		sideLinePaint.setColor(Color.argb(255 ,175, 175, 175));
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);
		int width = -1, height = -1;
		if (widthMode == MeasureSpec.EXACTLY) {
			width = widthSize;
		} else {
			width = DEFAULT_W_BOARD_SIZE;
			if (widthMode == MeasureSpec.AT_MOST && width > widthSize) {
				width = widthSize;
			}
		}
		if (heightMode == MeasureSpec.EXACTLY) {
			height = heightSize;
		} else {
			height = DEFAULT_H_BOARD_SIZE;
			if (heightMode == MeasureSpec.AT_MOST && height > heightSize) {
				height = heightSize;
			}
		}

		backgroundWidth = width;
		backgroundHeight = height;
		
		val_x_side = backgroundWidth-SHADOW_OF_SIDE;
		val_y_side = backgroundHeight-SHADOW_OF_SIDE;
		setMeasuredDimension(width, height);
	}

	
    @Override
    protected void onDraw(Canvas canvas) {
//        restartMarqueeIfNeeded();

        // Draw the background for this view
        super.onDraw(canvas);

        final int compoundPaddingLeft = getPaddingLeft();
        final int compoundPaddingTop = getPaddingTop();
        final int compoundPaddingRight = getPaddingRight();
        final int compoundPaddingBottom = getPaddingBottom();


        
        Path quarterShape = new Path();  
        quarterShape.moveTo(0, 0);  
        quarterShape.lineTo(0, val_y_side);
        quarterShape.arcTo(new RectF(-val_x_side, -val_y_side, val_x_side, val_y_side), 0, 90);
        quarterShape.lineTo(val_x_side, 0);
        quarterShape.close();
        

        canvas.drawArc(new RectF(-val_x_side, -val_y_side, val_x_side, val_y_side), 0, 90, true, sideLinePaint);

//        canvas.drawPath(quarterShape, sideLinePaint);  

    }	
}
