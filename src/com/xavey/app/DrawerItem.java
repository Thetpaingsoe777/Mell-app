package com.xavey.app;

public class DrawerItem {

	int ImageId;
	String ItemName;

	public DrawerItem(String itemName, int imageId) {
		super();
		ImageId = imageId;
		ItemName = itemName;
	}

	public int getImageId() {
		return ImageId;
	}

	public void setImageId(int imageId) {
		ImageId = imageId;
	}

	public String getItemName() {
		return ItemName;
	}

	public void setItemName(String itemName) {
		ItemName = itemName;
	}
}
