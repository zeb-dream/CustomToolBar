package com.custom.toolbar;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Map;

/**
 * ToolBarActivity.java
 *
 * created at 15/9/13 13:36
 */
public class ToolBarActivity extends AppCompatActivity {

    public static final String LOG_TAG = ToolBarActivity.class.getSimpleName();

    public static final int TITLE_MODE_CENTER = -1;
    public static final int TITLE_MODE_NONE = -2;

    private int titleMode = TITLE_MODE_NONE;

    protected TextView tvTitle;
    protected Toolbar toolbar;
    protected FrameLayout toolbarLayout;
    private LinearLayout contentView;
    private BackBtnCallBack backBtnCallBack;
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initToolBar();
        initContentView();
    }

    private void initToolBar() {
        toolbarLayout = new FrameLayout(this);
        LayoutInflater.from(this).inflate(R.layout.activity_tool_bar, toolbarLayout, true);
        tvTitle = (TextView) toolbarLayout.findViewById(R.id.tv_title);
        toolbar = (Toolbar) toolbarLayout.findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setHomeAsUpIndicator(R.mipmap.ico_back);


    }

    public void addMenuItem(Map<Integer,String> resIdMap, final View.OnClickListener onClickListener){
        int index = 0;
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                onClickListener.onClick(item.getActionView());
                return false;
            }
        });
        for (Integer resId: resIdMap.keySet()) {
            MenuItem item = toolbar.getMenu().add(0,resId,index,resIdMap.get(resId));
            item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
            item.setActionView(R.layout.item_menu);
            ImageView iv = (ImageView) item.getActionView().findViewById(R.id.menu_icon);
            iv.setImageResource(resId);
            item.getActionView().setOnClickListener(onClickListener);
            item.getActionView().setId(resId);
            index++;
        }

    }

    public void removeAllMenuItem(){
        if (toolbar != null) {
            //remove All MenuItem
            toolbar.getMenu().clear();
        }
    }

    public void removeMenuItemById(int id){
        toolbar.getMenu().removeItem(id);
    }

    public void showSearchBar(boolean isShow){
        RelativeLayout searchBar = (RelativeLayout) findViewById(R.id.searchBar);
        assert searchBar != null;
        int isVisible;
        if (isShow) {
            isVisible = View.VISIBLE;
        } else {
            InputMethodManager imm =  (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
            if(imm != null) {
                imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
            }
            searchBar.removeAllViews();
            isVisible = View.GONE;
        }
        searchBar.setVisibility(isVisible);
    }

    protected void initContentView() {
        contentView = new LinearLayout(this);
        contentView.setOrientation(LinearLayout.VERTICAL);
    }

    protected void setToolbarCustomView(int layoutResId) {
        try {
            LayoutInflater.from(this).inflate(layoutResId, toolbar, true);
        } catch (Exception e) {
            Log.w(LOG_TAG, "set toolbar customview exception", e);
        }
        tvTitle.setVisibility(View.GONE);
    }

    protected void setToolbarCustomView(View view) {
        if (toolbarLayout != null && view != null) {
            toolbar.addView(view, view.getLayoutParams());
        }
        tvTitle.setVisibility(View.GONE);
    }

    @Override
    public void setContentView(int layoutResID) {
        // add toolbar layout
        contentView.addView(toolbarLayout, new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT));

        LayoutInflater.from(this).inflate(layoutResID, contentView, true);

        super.setContentView(contentView, new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));
    }

    @Override
    public void setContentView(View view) {
        setNewContentView(view, view.getLayoutParams());
    }

    @Override
    public void setContentView(View view, LayoutParams params) {
        setNewContentView(view, params);
    }

    private void setNewContentView(View view, LayoutParams params) {
        // add toolbar layout
        contentView.addView(toolbarLayout, new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT));

        // content FrameLayout
        FrameLayout contentLayout = new FrameLayout(this);
        if (params == null) {
            params = new LayoutParams(
                    LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT);
        }
        contentLayout.addView(view, params);

        // add content layout
        contentView.addView(contentLayout, new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));

        super.setContentView(contentView, new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));
    }

    public void setTitleText(int titleId) {

        this.titleMode = TITLE_MODE_CENTER;
        super.setTitle(titleId);
    }

    public void removeToolbarCustomView(int layoutId){
        View hiddenView = toolbar.findViewById(layoutId);
        if ( null != hiddenView ) {
            ViewGroup parent = (ViewGroup)hiddenView.getParent();
            parent.removeView(hiddenView);
        }
    }

    @Override
    protected void onTitleChanged(CharSequence title, int color) {
        super.onTitleChanged(title, color);
        tvTitle.setVisibility(View.VISIBLE);
        if (titleMode == TITLE_MODE_NONE) {
            if (tvTitle != null) {
                tvTitle.setText("");
            }
        }else if (titleMode == TITLE_MODE_CENTER) {

            if (tvTitle != null) {
                tvTitle.setText(title);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (backBtnCallBack != null) {
                backBtnCallBack.onClick();
            } else {
                back();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * set backBtnOnClick
     *
     * @param backBtnCallBack backBtnCallBack
     */
    public void setBackBtnOnClick(BackBtnCallBack backBtnCallBack){
        this.backBtnCallBack = backBtnCallBack;
    }

    public interface BackBtnCallBack{
        void onClick();
    }

    /**
     * set Image Resource to left
     *
     * @param resId Image Resource
     */
    protected void setNavigationIcon(int resId) {
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(resId);
        }
    }

    /**
     * set Image Resource to left
     *
     * @param icon Image Resource
     */
    protected void setNavigationIcon(Drawable icon) {
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(icon);
        }
    }

    public void showOrHideBackBtn(boolean isShow){
        actionBar.setDisplayHomeAsUpEnabled(isShow);
    }

    public void back() {
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
