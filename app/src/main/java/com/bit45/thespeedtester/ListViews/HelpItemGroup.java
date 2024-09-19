package com.bit45.thespeedtester.ListViews;

import java.util.ArrayList;
import java.util.List;

/**
 * The data structure that holds the info for the child items in the expandable list
 */
public class HelpItemGroup {
    public String icon;
    public String title;
    public List<HelpItemChild> items = new ArrayList<>();
}
