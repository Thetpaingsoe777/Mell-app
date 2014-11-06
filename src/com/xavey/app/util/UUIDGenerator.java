package com.xavey.app.util;

import java.util.UUID;

public class UUIDGenerator {
	public static String getUUIDForDocument(){
		UUID uuid = UUID.randomUUID();
		String documentID = "doc-"+uuid.toString();
		return documentID;
	}
}
