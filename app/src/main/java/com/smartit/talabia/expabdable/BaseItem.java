package com.smartit.talabia.expabdable;

/**
 *  Created by android on 12/04/19.
 */
public class BaseItem {

    private Object menuName;

    public BaseItem(Object name) {
        menuName = name;
    }

    public Object getName() {
        return menuName;
    }

}
