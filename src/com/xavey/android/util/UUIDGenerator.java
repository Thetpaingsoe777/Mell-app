package com.xavey.android.util;

import java.util.UUID;

public class UUIDGenerator {
	public static String getUUIDForDocument(){
		UUID uuid = UUID.randomUUID();
		String documentID = "doc-"+uuid.toString();
		return documentID;
	}
	public static String getUUIDForSyncedID(){
		UUID uuid = UUID.randomUUID();
		String syncedID = "sync-"+uuid;
		return syncedID;
	}
}
