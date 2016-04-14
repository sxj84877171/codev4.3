package com.pvi.ap.reader.view;

import java.util.Map;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Paint.Align;
import android.view.View;

/**
 * 带图与文字的视图组件
 * @author FlyBird
 * @since 2011-6-17
 */
public class PviTextView {
	
	private Bitmap image;
	private Bitmap foucsImage;
	public String text;
	private int imageX;
	private int imageY;
	private int textX;
	private int textY;
	private int textSize;
	private Rect clickRect;
	
	private int textLength;
	private int imageLength;
	private int textAngle;
	
	private boolean isBold;
	private boolean isClickAble;
	private boolean isFoucsAble;
	private boolean isFoucs;
	
	private View parentView;
	private Canvas canvas;
	private Paint paint;
	private Paint boldPaint;
	
	private PviTextViewClickListenser listenser;
	private Map data;
	
	
	public PviTextView(){
	}
	
	public PviTextView(View parentView,Bitmap image,Bitmap foucsImage,String text,
			int imageX,int imageY,int textX,int textY,int textSize,Rect clickRect,
			 boolean isBold,boolean isClickAble,boolean isFoucsAble,boolean isFoucs,
			 int textLength,int imageLength,int textAngle){
		
		this.image = image;
		this.foucsImage = foucsImage;
		this.text = text;
		this.imageX = imageX;
		this.imageY = imageY;
		this.textX = textX;
		this.textY = textY;
		this.textSize = textSize;
		this.clickRect = clickRect;
		this.isBold = isBold;
		this.isClickAble = isClickAble;
		this.isFoucsAble = isFoucsAble;
		this.isFoucs = isFoucs;
		this.textLength = textLength;
		this.imageLength = imageLength;
		this.textAngle = textAngle;
		
		paint = new Paint();
		paint.setTextSize(textSize);
		paint.setAntiAlias(true);
		boldPaint = new Paint();
		boldPaint.setFakeBoldText(true);
		boldPaint.setTextSize(textSize);
		boldPaint.setAntiAlias(true);
		
		if(textAngle == 1){
			paint.setTextAlign(Align.LEFT);
			boldPaint.setTextAlign(Align.LEFT);
		}else if(textAngle == 2){
			paint.setTextAlign(Align.CENTER);
			boldPaint.setTextAlign(Align.CENTER);
		}else if(textAngle == 3){
			paint.setTextAlign(Align.RIGHT);
			boldPaint.setTextAlign(Align.RIGHT);
		}
	}
	
	public void setCanvas(Canvas canvas){
		this.canvas = canvas;
	}
	
	public void setPviTextViewClickListenser(PviTextViewClickListenser listenser,Map data){
		this.listenser = listenser;
		this.data = data;
		
	}
	
	public void perfomClick(){
		if(isClickAble == true && listenser != null){
			listenser.onClick(this, data);
		}
	}
	
	public void onDraw(){
		
		/*
		Paint p = new Paint();
		p.setColor(Color.RED);
		p.setStyle(Paint.Style.STROKE);
		p.setStrokeWidth(1);
		canvas.drawRect(clickRect, p);*/
		
		if(isFoucs){
			if(foucsImage!=null){
				if(imageLength==-1){
					canvas.drawBitmap(foucsImage, imageX, imageY, paint);
				}else{
					Bitmap drawBitmap = Bitmap.createScaledBitmap(foucsImage,imageLength, foucsImage.getHeight(), true);
					//Bitmap drawBitmap = Bitmap.createBitmap(foucsImage,0,0,90,foucsImage.getHeight());
					canvas.drawBitmap(drawBitmap, imageX, imageY, paint);
				}
				
			}
		}else{
			if(image != null){
				canvas.drawBitmap(image, imageX, imageY, paint);
			}
		}
		
		
		if(isBold){
			String t = getLine(boldPaint,text,textLength);
			if(!t.equals(text) && !"".equals(text) && !"".equals(t)){
				t = t.substring(0, t.length()-2)+"...";
			}
			if(textAngle == 0){
				canvas.drawText(t, textX, textY, boldPaint);
			}else{
				canvas.drawText(t, textX+textLength/2, textY, boldPaint);
			}
			
		}else{
			String t = getLine(paint,text,textLength);
			if(!t.equals(text)){
				t = t.substring(0, t.length()-2)+"...";
			}
			if(textAngle == 0){
				canvas.drawText(t, textX, textY, paint);
			}else{
				canvas.drawText(t, textX+textLength/2, textY, paint);
			}
		}
	}
	
	public void requestFoucs(){
		if(isFoucsAble){
			isFoucs = true;
		}
	}
	
	public void lostFoucs(){
		isFoucs = false;
	}
	
	
	public boolean checkClick(int x,int y){
		if(x>=clickRect.left && x<=clickRect.right
		   && y>=clickRect.top && y<=clickRect.bottom && isClickAble == true){
			perfomClick();
			return true;
		}else{
			return false;
		}
	}
	
	public boolean isFoucsAble(){
		return isFoucsAble;
	}
	
	
	public void setText(String text){
		this.text = text;
		isFoucsAble = true;
		isClickAble = true;
	}
	
	private String getLine(Paint mpaint,String source, int width)
	 {
	  Rect rect = new Rect();
	  String dest = null;
	  for(int i = 1; i <= source.length(); i ++)
	  {
	   mpaint.getTextBounds(source, 0, i, rect);
	   if(rect.right - rect.left > width)
	   {
	    dest = source.substring(0, i-1);
	    return dest;
	   }
	  }
	  dest = source.substring(0);
	  return dest;
	 }
	
}
