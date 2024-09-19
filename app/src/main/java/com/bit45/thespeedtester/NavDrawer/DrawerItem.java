package com.bit45.thespeedtester.NavDrawer;

public class DrawerItem {
	/* Commented tags are expected in future updates.
	 */
	public static final int DRAWER_ITEM_TAG_TOUR = 1;
	public static final int DRAWER_ITEM_TAG_RATE = 2;
	public static final int DRAWER_ITEM_TAG_HELP = 3;
	public static final int DRAWER_ITEM_TAG_ABOUT = 4;

	public DrawerItem(int icon, int title, int tag) {
		this.icon = icon;
		this.title = title;
		this.tag = tag;
	}

	private int icon;
	private int title;
	private int tag;

	public int getIcon() {
		return icon;
	}

	public void setIcon(int icon) {
		this.icon = icon;
	}

	public int getTitle() {
		return title;
	}

	public void setTitle(int title) {
		this.title = title;
	}

	public int getTag() {
		return tag;
	}

	public void setTag(int tag) {
		this.tag = tag;
	}
}
