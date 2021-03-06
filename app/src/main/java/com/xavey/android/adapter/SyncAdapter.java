package com.xavey.android.adapter;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;

public class SyncAdapter extends AbstractThreadedSyncAdapter {

	ContentResolver mContentResolver;
	
	public SyncAdapter(Context context, boolean autoInitialize) {
		super(context, autoInitialize);
		
		mContentResolver = context.getContentResolver();
		
	}
	
	public SyncAdapter(
            Context context,
            boolean autoInitialize,
            boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        
        mContentResolver = context.getContentResolver();
	}
	
	

	@Override
	public void onPerformSync(Account account, Bundle extras, String authority,
			ContentProviderClient provider, SyncResult syncResult) {
		// TODO Auto-generated method stub
		
		/*
		 * Put the data transfer code here
		 * 
		 *(1) Connecting to a server
		 *
		 *(2) Downloading and uploading data
		 *
		 *(3) Handling data conflicts or determining how current the data is 	
		 * 
		 */
		 

	}

}
