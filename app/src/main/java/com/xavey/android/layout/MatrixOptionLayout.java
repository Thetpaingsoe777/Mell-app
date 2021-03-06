package com.xavey.android.layout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.xavey.android.R;
import com.xavey.android.model.MatrixCell;
import com.xavey.android.util.MYHorizontalScrollView;
import com.xavey.android.util.TypeFaceManager;

public class MatrixOptionLayout extends LinearLayout {
    private TypeFaceManager typeFaceManager;
	private ArrayList<HashMap<String, String>> _hValueList;
	private ArrayList<HashMap<String, String>> _vValueList;
	private JSONArray _cellValueList;

	private boolean willBeRandomizedV = true;
	private boolean willBeRandomizedH = false;

	public MatrixOptionLayout(Context context) {
		super(context);
        typeFaceManager = new TypeFaceManager((Activity) context);
	}

	public MatrixOptionLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MatrixOptionLayout(Context context, AttributeSet attrs, int defStyle) {
		this(context, attrs);
	}

	public void initLayout() throws Exception {

		removeAllViews();
		this.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
		this.setOrientation(LinearLayout.HORIZONTAL);
		this.setTag(R.id.layout_id, "theMatrixLayout");

		ArrayList<RadioButton> radioButtonList = new ArrayList<RadioButton>();

		int v_count = _vValueList.size();
		int h_count = _hValueList.size();

		// grouping all cells by each column
		LinkedList<ArrayList<MatrixCell>> columnSetCollection = new LinkedList<ArrayList<MatrixCell>>();

		for (int h = 0; h < h_count; h++) {
			ArrayList<MatrixCell> columnSet = new ArrayList<MatrixCell>();

			for (int count = 0; count < _cellValueList.length(); count++) {
				JSONObject cell_values = _cellValueList.getJSONObject(count);
				String fieldSkip = cell_values.getString("field_skip");
				String value = cell_values.getString("value");
				String index_ = cell_values.getString("index");
				String[] parts = index_.split(",");
				int h_ = Integer.parseInt(parts[0]);
				int v_ = Integer.parseInt(parts[1]);
				if (h_ == h) {
					MatrixCell cell = new MatrixCell();
					cell.setFieldSkip(fieldSkip);
					cell.setValue(value);
					cell.setH_index(h_);
					cell.setV_index(v_);
					columnSet.add(cell);
				}
			}
			columnSetCollection.add(columnSet);
		}
		// Collections.shuffle(columnSetCollection);

		// <producing row_label_set>
		LinearLayout row_label_column_set = new LinearLayout(this.getContext());
		row_label_column_set.setTag(R.id.layout_id, "rowLabelColumn");
		int cell_row_width = 200;
		int cell_column_width = 150;
		int cell_column_height = 70;

		LayoutParams rowLabelLayoutParams = new LayoutParams(cell_row_width,
				LayoutParams.WRAP_CONTENT);
		row_label_column_set.setLayoutParams(rowLabelLayoutParams);
		row_label_column_set.setOrientation(LinearLayout.VERTICAL);
		row_label_column_set.setGravity(Gravity.CENTER);

		LinearLayout blankLayout = new LinearLayout(this.getContext());
		LayoutParams blankLayoutParams = new LayoutParams(cell_row_width,
				cell_column_height);
		blankLayout.setLayoutParams(blankLayoutParams);
		blankLayout.setGravity(Gravity.CENTER);
		TextView blankText = new TextView(this.getContext());
		blankText.setLayoutParams(new LayoutParams(cell_row_width,
				cell_column_height));
		blankText.setText("   -   ");
		// row_label_column_set is the left vertical column
		blankLayout.addView(blankText);
		row_label_column_set.addView(blankLayout);

		for (int r = 0; r < _vValueList.size(); r++) {
			LinearLayout rowLabelCells = new LinearLayout(this.getContext());

			LayoutParams rowLabelCellsParams = new LayoutParams(cell_row_width,
					cell_column_height);
			rowLabelCells.setLayoutParams(rowLabelCellsParams);

			TextView rowLabel = new TextView(this.getContext());
			typeFaceManager.setTypeFace(rowLabel);
			LayoutParams rowLabelParams = new LayoutParams(cell_row_width,
					cell_column_height);
			rowLabel.setLayoutParams(rowLabelParams);
			rowLabel.setText(_vValueList.get(r).get("label").toString());
			rowLabel.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
			// rowLabel.setBackgroundColor(Color.parseColor("#49F2D6"));
			// value may be useless here

			row_label_column_set.addView(rowLabel);
		}

		this.addView(row_label_column_set);
		this.setPadding(10, 0, 0, 0);
		// </producing row_label_set>

		int maxHeight = 0;

		// <prepare AllColumns
		LinearLayout AllColumns = new LinearLayout(this.getContext());
		AllColumns.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		AllColumns.setOrientation(LinearLayout.HORIZONTAL);
		AllColumns.setTag(R.id.layout_id, "AllColumns");

		// <prepare columnSet>
		for (int column = 0; column < columnSetCollection.size(); column++) {
			ArrayList<MatrixCell> columnSet = columnSetCollection.get(column);
			if (columnSet.size() > 0) {
				MatrixCell labelCell = columnSet.get(0);

				LinearLayout columnLayout = new LinearLayout(this.getContext());
				LayoutParams columnLayoutParams = new LayoutParams(
						cell_column_width, LayoutParams.MATCH_PARENT);
				columnLayout.setLayoutParams(columnLayoutParams);
				columnLayout.setOrientation(LinearLayout.VERTICAL);
				columnLayout.setGravity(Gravity.CENTER);
				columnLayout.setTag(R.id.layout_id, "columnLayout");

				columnLayout.setTag("");
				// getting h_values_ here...
				String max_range = "";
				if (_hValueList.get(labelCell.getH_index()).get("max_range") != null)
					max_range = _hValueList.get(labelCell.getH_index()).get(
							"max_range");
				else
					max_range = "#no_value#";
				String field_skip = _hValueList.get(labelCell.getH_index())
						.get("field_skip");
				String value = _hValueList.get(labelCell.getH_index()).get(
						"value");
				String label = _hValueList.get(labelCell.getH_index()).get(
						"label");
				String extra = _hValueList.get(labelCell.getH_index()).get(
						"extra");
				String error_message = "";
				if (_hValueList.get(labelCell.getH_index())
						.get("error_message") != null)
					error_message = _hValueList.get(labelCell.getH_index())
							.get("error_message");
				else
					error_message = "#no_value#";
				// set them..
				// max_range
				columnLayout.setTag(R.id.dataset_max_range, max_range);
				// field_skip
				columnLayout.setTag(R.id.dataset_field_skip, field_skip);
				// value
				columnLayout.setTag(R.id.dataset_value, value);
				// label
				columnLayout.setTag(R.id.dataset_label, label);
				// extra
				columnLayout.setTag(R.id.dataset_extra, extra);
				// error_message
				columnLayout.setTag(R.id.dataset_error_message, error_message);

				TextView tvColumnTitle = new TextView(this.getContext());
				typeFaceManager.setTypeFace(tvColumnTitle);
				tvColumnTitle.setLayoutParams(new LayoutParams(
						cell_column_width, cell_column_height));
				tvColumnTitle.setText(label);
				// tvColumnTitle.setBackgroundColor(Color.parseColor("#aabbcc"));
				tvColumnTitle.setGravity(Gravity.CENTER);
				tvColumnTitle.setTag(R.id.layout_id, "columnTitle");
				// following code dosen't work
				// if(tvColumnTitle.getLayoutParams().height>maxHeight){
				// maxHeight = tvColumnTitle.getHeight();
				// }

				columnLayout.addView(tvColumnTitle);

				for (int cs = 0; cs < columnSet.size(); cs++) {
					MatrixCell cell = columnSet.get(cs);
					LinearLayout singleRadioLayout = new LinearLayout(
							this.getContext());
					singleRadioLayout.setLayoutParams(new LayoutParams(
							cell_column_width, cell_column_height));
					singleRadioLayout.setGravity(Gravity.CENTER);
					singleRadioLayout.setTag(R.id.layout_id, "cell");
					RadioButton rb = new RadioButton(this.getContext());
					LayoutParams RadioParams = new LayoutParams(
							LayoutParams.WRAP_CONTENT,
							LayoutParams.WRAP_CONTENT);
					rb.setLayoutParams(RadioParams);
					rb.setTag(R.id.matrix_cell, cell);
					rb.setTag(R.id.is_radiobutton_selected, false);
					// rb.setGravity(Gravity.CENTER); // <-- center
					// horizontal didn't work here
					singleRadioLayout.addView(rb);
					radioButtonList.add(rb);
					// singleRadioLayout.setBackgroundColor(Color.parseColor("#5abde4"));
					columnLayout.addView(singleRadioLayout);
				}
				// here
				AllColumns.addView(columnLayout);
				// theMatrixLayout.addView(columnLayout);
			}
		}
		// </prepare columnSet>

		// </matrix stuffs>

		MYHorizontalScrollView horizontalScrollView = new MYHorizontalScrollView(
				this.getContext());
		horizontalScrollView.setLayoutParams(new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		horizontalScrollView.addView(AllColumns);
		horizontalScrollView.setTag(R.id.layout_id, "horizontalScrollView");
		horizontalScrollView.requestDisallowInterceptTouchEvent(false);
		this.addView(horizontalScrollView);

		// <setting onClickListener to individual RadioButton>
		for (int v = 0; v < _vValueList.size(); v++) { // 4
			final ArrayList<RadioButton> singleRowAtC = new ArrayList<RadioButton>();
			// <collecting cell by v index>
			for (RadioButton rb : radioButtonList) {
				MatrixCell cell = (MatrixCell) rb.getTag(R.id.matrix_cell);
				if (cell.getV_index() == v) {
					singleRowAtC.add(rb);
				}
			}
			// </collecting cell by v index>

			// <setting Listener>
			for (int s = 0; s < singleRowAtC.size(); s++) { // 4
				final RadioButton rb = singleRowAtC.get(s);
				rb.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						rb.setTag(R.id.is_radiobutton_selected, true);
						MatrixCell cell = (MatrixCell) rb
								.getTag(R.id.matrix_cell);
						String uniqueValue = cell.getValue();
						for (int ss = 0; ss < singleRowAtC.size(); ss++) {
							RadioButton loopButton = singleRowAtC.get(ss);
							MatrixCell loopCell = (MatrixCell) loopButton
									.getTag(R.id.matrix_cell);
							if (!uniqueValue.equals(loopCell.getValue())) {
								RadioButton otherButton = singleRowAtC.get(ss);
								otherButton.setChecked(false);
								otherButton.setTag(
										R.id.is_radiobutton_selected, false);
							}
						}
					}
				});
			}
			// </setting Listener>
		}
		// </setting onClickListener to individual RadioButton>
		if (willBeRandomizedV) {
			randomized(this);
		}
	}

	public void randomized(MatrixOptionLayout matrixOptionLayout) {
		LinearLayout rowLabelColumn = null;
		LinearLayout AllColumns = null;
		// find above two layouts from matrixOptionLayout and assigning to
		// corresponding parameters
		for (int index = 0; index < matrixOptionLayout.getChildCount(); index++) {
			View v = matrixOptionLayout.getChildAt(index);
			if (v.getTag(R.id.layout_id).toString().equals("rowLabelColumn"))
				rowLabelColumn = (LinearLayout) v;
			else if (v.getTag(R.id.layout_id).toString()
					.equals("horizontalScrollView")) {
				MYHorizontalScrollView horizontalScrollView = (MYHorizontalScrollView) v;
				if (horizontalScrollView.getChildAt(0).getTag(R.id.layout_id)
						.toString().equals("AllColumns")) {
					AllColumns = (LinearLayout) horizontalScrollView
							.getChildAt(0);
				}
			}
		}
		// -------------------------------------------------------
		int rowLabelChildCount = rowLabelColumn.getChildCount();
		int AllColumnsChildCount = AllColumns.getChildCount();
		ArrayList<Integer> generatedRandomNumberList = generateRandomNumberList(rowLabelChildCount - 1);

		// <collecting the child views that are about to randomize binding with
		// numbers>
		HashMap<Integer, View> rowLabelChildMap = new HashMap<Integer, View>();
		for (int r = 1; r < rowLabelChildCount; r++) {
			// r starts from 1 in order to avoid the title
			rowLabelChildMap.put(r, rowLabelColumn.getChildAt(r));
		}
		ArrayList<HashMap<Integer, View>> eachColumnMapList = new ArrayList<HashMap<Integer, View>>();
		for (int c = 0; c < AllColumnsChildCount; c++) {
			HashMap<Integer, View> eachColumnMap = new HashMap<Integer, View>();
			LinearLayout columnLayout = (LinearLayout) AllColumns.getChildAt(c);
			for (int i = 1; i < columnLayout.getChildCount(); i++) {
				// i start from 1 in order to avoid the title like above
				// rowlabelColumn stuffs
				eachColumnMap.put(i, columnLayout.getChildAt(i));
			}
			eachColumnMapList.add(eachColumnMap);
		}
		// </collecting the child views that are about to randomize binding with
		// numbers>

		// remove views of rowLabelChildMap
		for (int i = rowLabelColumn.getChildCount(); i > 1; i--) {
			rowLabelColumn.removeViewAt(i - 1);
		}
		// add views from rowLabelChildMap according to the randomizedNumbers
		for (int i = 0; i < generatedRandomNumberList.size(); i++) {
			int currentRandomedIndex = generatedRandomNumberList.get(i);
			View rowLabelColumnChild = rowLabelChildMap
					.get(currentRandomedIndex);
			rowLabelColumn.addView(rowLabelColumnChild);
		}
		rowLabelColumn.getChildCount();

		for (int i = 0; i < AllColumns.getChildCount(); i++) {
			LinearLayout columnLayout = null;
			if (AllColumns.getChildAt(i).getTag(R.id.layout_id).toString()
					.equals("columnLayout")) {
				columnLayout = (LinearLayout) AllColumns.getChildAt(i);
				// remove views from columnLayout
				for (int j = columnLayout.getChildCount(); j > 1; j--) {
					columnLayout.removeViewAt(j - 1);
				}

				// add views from columnLayout according to the
				// randomizedNumbers

//				for (int j = 0; j < eachColumnMapList.size(); j++) {
					HashMap<Integer, View> eachColumnMap = eachColumnMapList
							.get(i);
					for (int k = 0; k < generatedRandomNumberList.size(); k++) {
						int currentRandomedIndex = generatedRandomNumberList
								.get(k);
						View columnLayoutChild = eachColumnMap
								.get(currentRandomedIndex);
						columnLayout.addView(columnLayoutChild);
					}
//				}
			}
		}

		matrixOptionLayout.refreshDrawableState();
	}

	private ArrayList<Integer> generateRandomNumberList(int range) {
		ArrayList<Integer> randomNumberList = new ArrayList<Integer>();
		while (randomNumberList.size() <= range - 1) {
			int randomedNumber = randInt(1, range);
			if (!randomNumberList.contains(randomedNumber))
				randomNumberList.add(randomedNumber);
		}
		return randomNumberList;
	}

	public static int randInt(int min, int max) {
		// copied from
		// http://stackoverflow.com/questions/363681/generating-random-integers-in-a-range-with-java
		// NOTE: Usually this should be a field rather than a method
		// variable so that it is not re-seeded every call.
		Random rand = new Random();
		// nextInt is normally exclusive of the top value,
		// so add 1 to make it inclusive
		int randomNum = rand.nextInt((max - min) + 1) + min;
		return randomNum;
	}

	public ArrayList<HashMap<String, String>> getHValueList() {
		return _hValueList;
	}

	public void setHValueList(ArrayList<HashMap<String, String>> _hValueList) {
		this._hValueList = _hValueList;
	}

	public ArrayList<HashMap<String, String>> getVValueList() {
		return _vValueList;
	}

	public void setVValueList(ArrayList<HashMap<String, String>> _vValueList) {
		this._vValueList = _vValueList;
	}

	public JSONArray getCellValueList() {
		return _cellValueList;
	}

	public void setCellValueList(JSONArray _cellValueList) {
		this._cellValueList = _cellValueList;
	}

	public boolean isWillBeRandomizedV() {
		return willBeRandomizedV;
	}

	public void setWillBeRandomizedV(boolean willBeRandomizedV) {
		this.willBeRandomizedV = willBeRandomizedV;
	}

	public boolean isWillBeRandomizedH() {
		return willBeRandomizedH;
	}

	public void setWillBeRandomizedH(boolean willBeRandomizedH) {
		this.willBeRandomizedH = willBeRandomizedH;
	}

}
