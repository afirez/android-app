package com.gcml.common.utils;

import android.support.v7.widget.Toolbar;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

/**
 *
 * View 工具类
 * Created by afirez on 2017/2/14.
 */

public class ViewUtils {
    private static final int TAG_VIEW_MAP = 0x00;
    private static final int TAG_MENU_ITEM_MAP = 0x01;

    private View rootView;

    private Toolbar toolbar;

    private ViewUtils(View rootView) {
        this.rootView = rootView;
    }

    public static ViewUtils with(View rootView) {
        return new ViewUtils(rootView);
    }

    public ViewUtils rootView(View rootView) {
        this.rootView = rootView;
        return this;
    }

    public ViewUtils toolbar(Toolbar toolbar) {
        this.toolbar = toolbar;
        return this;
    }

    public ViewUtils toolbar(int id) {
        toolbar = getView(id);
        return this;
    }

    public <V extends View> V getView(int id) {
        SparseArray<View> viewMap = viewMap();
        if (viewMap == null) {
            return null;
        }
        View view = viewMap.get(id);
        if (view == null) {
            view = rootView.findViewById(id);
            if (view == null) {
                return null;
            }
            viewMap.put(id, view);
        }
        return (V) view;
    }

    private SparseArray<View> viewMap() {
        if (rootView == null) {
            return null;
        }
        SparseArray<View> viewMap = (SparseArray<View>) rootView.getTag(TAG_VIEW_MAP);
        if (viewMap == null) {
            viewMap = new SparseArray<>();
            rootView.setTag(TAG_VIEW_MAP, viewMap);
        }
        return viewMap;
    }

    public MenuItem getMenuItem(int id) {
        if (toolbar == null) {
            return null;
        }
        Menu menu = toolbar.getMenu();
        if (menu == null) {
            return null;
        }
        SparseArray<MenuItem> menuItemMap =
                (SparseArray<MenuItem>) toolbar.getTag(TAG_MENU_ITEM_MAP);
        if (menuItemMap == null) {
            menuItemMap = new SparseArray<>();
            toolbar.setTag(TAG_MENU_ITEM_MAP, menuItemMap);
        }
        //menuItemMap != null
        MenuItem menuItem = menuItemMap.get(id);
        if (menuItem == null) {
            menuItem = menu.findItem(id);
            if (menuItem == null) {
                return null;
            }
            menuItemMap.put(id, menuItem);
        }
        return menuItem;
    }

    public ViewUtils setOnClickListener(View.OnClickListener onClickListener, int... ids) {
        if (onClickListener == null
                || ids == null) {
            return this;
        }
        for (int id : ids) {
            getView(id).setOnClickListener(onClickListener);
        }
        return this;
    }
}
