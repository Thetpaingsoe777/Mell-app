package com.xavey.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class QuestionFragment extends Fragment {

	private LinearLayout parentLayout, linearLayout;
	private int field_id;

	public static QuestionFragment newInstance(LinearLayout layout, int field_id){
		QuestionFragment qFragment = new QuestionFragment();
		qFragment.setLinearLayout(layout);
		qFragment.setField_id(field_id);
//		Bundle args = new Bundle();
//		args.putString("title", title);
//		qFragment.setArguments(args);
		return qFragment; 
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//title = getArguments().getString("title");
	}

	View rootView=null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if(rootView==null){
			rootView = inflater.inflate(R.layout.question_fragment, container, false);
			parentLayout = (LinearLayout) rootView.findViewById(R.id.parentLinearLayout);
/*			ScrollView scrollView = (ScrollView) rootView.findViewById(R.id.svQuestionFragment);
			scrollView.addView(linearLayout);*/
			parentLayout.removeAllViews();
			parentLayout.addView(linearLayout);
		}else{
			((ViewGroup) rootView.getParent()).removeView(rootView);
		}
		return rootView;
	}
	public void setLinearLayout(LinearLayout linearLayout) {
		this.linearLayout = linearLayout;
	}

	public int getField_id() {
		return field_id;
	}

	public void setField_id(int field_id) {
		this.field_id = field_id;
	}

}
