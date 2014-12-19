package com.xavey.android;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.xavey.android.db.XaveyDBHelper;
import com.xavey.android.model.Document;

public class HistoryFragment extends Fragment {

	public static String ITEM_NAME = "HISTORY";

	XaveyDBHelper db;
	ListView historyListView;
	ArrayList<Document> documentList = new ArrayList<Document>();
	DocumentPagerAdapter documentAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.history_fragment, container,
				false);
		//refreshItem = (MenuItem) view.findViewById(R.id.app_menuRefresh);
		MainActivity.optionMenu.getItem(0).setVisible(false);
		loadUI(view);
		getActivity().getActionBar().setIcon(R.drawable.history);
		getActivity().getActionBar().setTitle("History");
		refreshData();
		return view;
	}

	public void refreshData(){
		documentList.clear();
		db = new XaveyDBHelper(getActivity());
		ArrayList<Document> logginUserDocuments = new ArrayList<Document>();
		logginUserDocuments = db.getAllDocumentsByCreaterID(ApplicationValues.loginUser.getUser_id());
		
		documentList = logginUserDocuments;
		documentAdapter = new DocumentPagerAdapter(getActivity(), R.layout.history_listview_row_item, documentList);
		historyListView.setAdapter(documentAdapter);
		db.close();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		MainActivity.optionMenu.getItem(0).setVisible(true);
	}

	private void loadUI(View v){
		db = new XaveyDBHelper(getActivity());
		historyListView = (ListView) v.findViewById(R.id.history_form_listview);
		historyListView.setItemsCanFocus(false);
	}

	public class DocumentPagerAdapter extends ArrayAdapter<Document>{

		Activity activity;
		int layoutResourceId;
		Document document;
		ArrayList<Document> document_list;
		String selectedFormName="";
		String selectedFormTitle="";

		public DocumentPagerAdapter(Activity act, int resource, ArrayList<Document> document_list) {
			super(act, resource, document_list);
			this.activity = act;
			this.layoutResourceId = resource;
			this.document_list = document_list;
			notifyDataSetChanged();
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			View row = convertView;
			DocumentHolder holder = null;
			if(row==null){
				LayoutInflater inflater = LayoutInflater.from(activity);
				row = inflater.inflate(layoutResourceId, parent, false);
				holder = new DocumentHolder();
				holder.document_img_small = (ImageView) row.findViewById(R.id.imgViewDrawerItemIcon);
				holder.document_name = (TextView) row.findViewById(R.id.tv_history_form_name);
				holder.created_date = (TextView) row.findViewById(R.id.tv_history_created_date);
				holder.imgVCheck = (ImageView) row.findViewById(R.id.imgCheck);
				row.setTag(holder);
			}
			else{
				holder = (DocumentHolder) row.getTag();
			}
			document = document_list.get(position);
			holder.document_name.setText(document.getDocument_name());
			holder.created_date.setText(document.getCreated_at());
			if(document.getSubmitted().equals("1")){
				holder.imgVCheck.setImageResource(R.drawable.checked);
			}
			else{
				holder.imgVCheck.setImageResource(R.drawable.unchecked);
			}
			row.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					String documentID = document_list.get(position).getDocument_id();
					Intent i = new Intent(getActivity(),ShowDocumentDetailActivity.class);
					i.putExtra("documentID", documentID);
					startActivity(i);
				}
			});
			return row;
		}

		class DocumentHolder{
			ImageView document_img_small;
			TextView document_name;
			TextView created_date;
			ImageView imgVCheck;
		}
	}

}
