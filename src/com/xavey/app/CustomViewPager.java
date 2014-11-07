package com.xavey.app;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;

// http://stackoverflow.com/questions/18660011/viewpager-disable-swiping-to-a-certain-direction

public class CustomViewPager extends ViewPager {

	private boolean enabled;

	public CustomViewPager(Context context) {
		super(context);
		this.enabled = true;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (this.enabled && isSwipeLeftToRight(event)) {
			return super.onTouchEvent(event);
		}
		return false;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		if (this.enabled && isSwipeLeftToRight(event)) {
			return super.onInterceptTouchEvent(event);
		}
		return false;
	}

	// To enable/disable swipe
	public void setPagingEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isSwipeLeftToRight(MotionEvent event) {
		int initialXValue = 0; // as we have to detect swipe to right
		final int SWIPE_THRESHOLD = 100; // detect swipe
		boolean result = false;
		try {
			float diffX = event.getX() - initialXValue;
			if (Math.abs(diffX) > SWIPE_THRESHOLD) {
				if (diffX > 0) {
					// swipe from left to right detected ie.SwipeRight
					result = true;
				} else {
					// swipe from right to left detected ie.SwipeLeft
					result = false;
				}
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return result;
	}

}
