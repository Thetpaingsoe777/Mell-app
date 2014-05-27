package com.xavey.app;


import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.xavey.app.R;
import com.xavey.app.adapter.CustomDrawerAdapter;

public class MainActivity extends Activity {
	
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	private CharSequence mDrawerTitle;
	CustomDrawerAdapter adapter;
	List<DrawerItem> itemList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initializeUI();
		if(savedInstanceState == null){
			selectItem(0);
		}
	}
	
	private void initializeUI(){
		itemList = new ArrayList<DrawerItem>();
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);
		addDrawerItem();
		adapter = new CustomDrawerAdapter(this, R.layout.custom_drawer_item, itemList);
		mDrawerList.setAdapter(adapter);
		mDrawerList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long arg3) {
				selectItem(position);
			}
		});
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
		
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,R.drawable.ic_drawer,R.string.drawer_open,R.string.drawer_close);
		mDrawerLayout.setDrawerListener(mDrawerToggle);
		
		
	}
	
	private void addDrawerItem(){
		itemList.add(new DrawerItem(getString(R.string.action_home)));
		itemList.add(new DrawerItem(getString(R.string.action_history)));
		itemList.add(new DrawerItem(getString(R.string.action_settings)));
	}
	
	public void selectItem(int position){
		Fragment fragment = null;
		Bundle args = new Bundle();
		switch (position) {
		case 0:
			fragment = new HomeFragment();
			args.putString(HomeFragment.ITEM_NAME, itemList.get(position).getItemName());
			break;
		case 1:
			fragment = new HistoryFragment();
			args.putString(HistoryFragment.ITEM_NAME, itemList.get(position).getItemName());
			break;
		case 2:
			fragment = new SettingFragment();
			args.putString(SettingFragment.ITEM_NAME, itemList.get(position).getItemName());
		default:
			break;
		}
		fragment.setArguments(args);
		FragmentManager frgManager = getFragmentManager();
		frgManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
		mDrawerList.setItemChecked(position, true);
		//setTitle(itemList.get(position).getItemName());
		mDrawerLayout.closeDrawer(mDrawerList);
	}
	
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState();
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
		// The action bar home/up action should open or close the drawer.
		// ActionBarDrawerToggle will take care of this.
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}

		return false;
	}
	
}
