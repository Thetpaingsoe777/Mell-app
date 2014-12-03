package com.xavey.app.util;

import android.content.Context;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

public class MYHorizontalScrollView extends HorizontalScrollView {

	Context context;

	public MYHorizontalScrollView(Context context) {
		super(context);
		this.context = context;
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		if(ev.getAction()==MotionEvent.ACTION_MOVE){
			getParent().getParent().getParent().getParent().getParent().getParent().requestDisallowInterceptTouchEvent(true);
		}
//		if(ev.getAction()==MotionEvent.ACTION_DOWN){
//			return false;
//		}
		
		return super.onTouchEvent(ev);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		//return true;
		return super.onInterceptTouchEvent(ev);
	}

}
