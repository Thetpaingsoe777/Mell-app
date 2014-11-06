package com.xavey.app;

<<<<<<< HEAD
=======
import java.lang.reflect.Array;
>>>>>>> ce4c53483e36d66116a944fa419f4f5c31caf09c
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

<<<<<<< HEAD
=======
import com.xavey.app.db.XaveyDBHelper;
import com.xavey.app.model.Document;
import com.xavey.app.model.Form;
import com.xavey.app.util.CSVExportManager;
import com.xavey.app.util.JSONReader;

>>>>>>> ce4c53483e36d66116a944fa419f4f5c31caf09c
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

<<<<<<< HEAD
import com.xavey.app.db.XaveyDBHelper;
import com.xavey.app.model.Document;
import com.xavey.app.model.Form;
import com.xavey.app.util.CSVExportManager;
import com.xavey.app.util.JSONReader;

=======
>>>>>>> ce4c53483e36d66116a944fa419f4f5c31caf09c

// referenced from http://theopentutorials.com/tutorials/android/listview/android-multiple-selection-listview/
public class CSVExportFragment extends Fragment {
	
	Button btnExport;
	ListView listView;
	ArrayAdapter<String> adapter;
	XaveyDBHelper dbHelper;
	ArrayList<Form> formList;
	ArrayList<String> formNames;
	String[] formNames_;
	
	JSONReader jsonReader;
	CSVExportManager csv;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.csv_export_fragment,
				container, false);
		getActivity().getActionBar().setIcon(R.drawable.csv_large_icon);
		getActivity().getActionBar().setTitle("CSV Export");
		loadUI(rootView);

		dbHelper = new XaveyDBHelper(getActivity().getApplicationContext());
		formList = dbHelper.getFormsByUserID(ApplicationValues.loginUser.getUser_id());
		formNames = getFormNamesFromFormList(formList);
		formNames_ = convertArrayListToStringArray(formNames);
		adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_multiple_choice, formNames_);
		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		listView.setAdapter(adapter);
		btnExport.setOnClickListener(new OnClickListener() {
<<<<<<< HEAD

=======
			
>>>>>>> ce4c53483e36d66116a944fa419f4f5c31caf09c
			@Override
			public void onClick(View v) {
				 SparseBooleanArray checked = listView.getCheckedItemPositions();
			        ArrayList<Form> selectedFormsList = new ArrayList<Form>();
			        for (int i = 0; i < checked.size(); i++) {
			            // Item position in adapter
			            int position = checked.keyAt(i);
			           /* // Add sport if it is checked i.e.) == TRUE!
			            if (checked.valueAt(i))
			                selectedItems.add(adapter.getItem(position));*/
			            selectedFormsList.add(formList.get(position));
			        }
<<<<<<< HEAD

=======
			        
>>>>>>> ce4c53483e36d66116a944fa419f4f5c31caf09c
			        for(int i=0; i<selectedFormsList.size(); i++){
			        	Form selectedForm = selectedFormsList.get(i);
			        	ArrayList<String> headerList = jsonReader.getHeaderList(selectedForm.getForm_json());
			        	ArrayList<Document> documentList = dbHelper.getAllDocumentsByFormID(selectedForm.getForm_id());
			        	ArrayList<HashMap<String, String>> dataList = jsonReader.getDataList(documentList);
			        	String outFileName = selectedForm.getForm_title()+"-"+getCurrentDate();
			        	boolean isSuccess = csv.exportDocumentsToCSV(outFileName , headerList, dataList);
			        	if(isSuccess){
			        		Log.i("csv exported : " , outFileName);
				        	Toast.makeText(getActivity(), outFileName + " is exported", 1000).show();	
			        	}else{
			        		Log.i("csv not exported : " , outFileName);
				        	Toast.makeText(getActivity(), outFileName + " is not exported.", 1000).show();
			        	}
			        }
			}
<<<<<<< HEAD
=======
			
>>>>>>> ce4c53483e36d66116a944fa419f4f5c31caf09c
		});
		
		refreshData();
		return rootView;
	}
	
	private void loadUI(View rootView){
		listView = (ListView) rootView.findViewById(R.id.lvForm_csv_export_frag);
		btnExport = (Button) rootView.findViewById(R.id.btnExport_csv_export_fragment);
		jsonReader = new JSONReader(getActivity());
		csv = new CSVExportManager(getActivity().getApplicationContext());
	}
	
	private void refreshData(){
		
	}
	
	public String[] convertArrayListToStringArray(ArrayList<String> arrayList){
		String[] stringArray = new String[arrayList.size()];
		stringArray = arrayList.toArray(stringArray);
		return stringArray;
	}
	
	private ArrayList<String> getFormNamesFromFormList(ArrayList<Form> formList){
		ArrayList<String> formNames = new ArrayList<String>();
		for(Form form: formList){
			formNames.add(form.getForm_title());
		}
		return formNames;
	}
	
	private String getCurrentDate() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
		Date date = new Date();
		return dateFormat.format(date);
	}
	
}
