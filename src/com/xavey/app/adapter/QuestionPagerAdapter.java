package com.xavey.app.adapter;

import java.util.ArrayList;

import com.xavey.app.QuestionFragment;
import com.xavey.app.R;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class QuestionPagerAdapter extends SmartFragmentStatePagerAdapter {



	int numberOfPages;
	ArrayList<LinearLayout> linearLayoutList;
	//ArrayList<Fragment> fragmentList;
	ArrayList<QuestionFragment> fragmentList;
	
	public QuestionPagerAdapter(FragmentManager fragmentManager, ArrayList<LinearLayout> linearLayoutList) {
		super(fragmentManager);
		this.linearLayoutList = linearLayoutList;
		numberOfPages = linearLayoutList.size();
		//fragmentList = new ArrayList<Fragment>();
		fragmentList = new ArrayList<QuestionFragment>();
		for(int i=0; i<numberOfPages; i++){
			int field_id = 0;
			LinearLayout parentLayout = linearLayoutList.get(i);
			LinearLayout targetLayout = null;
			for(int j=0; j<parentLayout.getChildCount(); j++){
				// this loops occur two for relative and linear
				View view = parentLayout.getChildAt(j);
				String className = parentLayout.getChildAt(j).getClass().getName();
				if(view.getTag(R.id.layout_id)!=null && className.equals("android.widget.LinearLayout")){
					targetLayout = (LinearLayout) view;
				}
				if(className.equals("android.widget.RelativeLayout")){
					RelativeLayout upperLayout = (RelativeLayout) view;
					for(int z=0; z<upperLayout.getChildCount(); z++){
						if(upperLayout.getChildAt(z).getTag().equals("index")){
							TextView index = (TextView) upperLayout.getChildAt(z);
							index.setText(i+1+"/"+numberOfPages);
						}
					}
				}
			}
			if(targetLayout!=null)
				if(targetLayout.getTag(R.id.field_id)!=null)
					field_id = Integer.parseInt(targetLayout.getTag(R.id.field_id).toString());
			int pageNo = i+1;
			QuestionFragment qFragment = QuestionFragment.newInstance(linearLayoutList.get(i), field_id);
			fragmentList.add(qFragment);
		}
	}

	@Override
	public Fragment getItem(int position) {
		return fragmentList.get(position);
	}

	@Override
	public int getCount() {
		return numberOfPages;
	}

}
