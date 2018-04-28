package com.hujunfei.library;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by lianfei1314 on 2016/8/30.
 */
public class DinnerCarteMenu extends LinearLayout {
    // 顶部菜单布局，是一个水平线性布局
    private LinearLayout tabmenu_layout;

    // Container区，framelayout布局
    private FrameLayout container_layout;

    // 当前菜单index
    private int current_tabindex = -1;

    // 分割线颜色
    private int divider_color = 0xFFCCCCCC;

    // 菜单中选中字体颜色
    private int text_selected_color = 0xff890c85;

    // 菜单中未选中字体颜色
    private int text_unselected_color = 0xff111111;

    // 菜单字体大小
    private int menu_textsize = 14;

    // 菜单选中背景
    private int menu_selected_icon;

    // 菜单未选中背景
    private int menu_unselected_icon;

    public DinnerCarteMenu(Context context) { super(context,null); }

    public DinnerCarteMenu(Context context, AttributeSet attrs) { this(context,attrs,0); }

    public DinnerCarteMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context,attrs,defStyleAttr);

        // 自定义默认menu background color
        int menu_background_color = 0xFFFFFFFF;

        // 自定义默认underline color
        int underline_color = 0xFFCCCCCC;

        // 设置垂直布局
        setOrientation(HORIZONTAL);

        // 获取参数
        TypedArray typedarray = context.obtainStyledAttributes(attrs,R.styleable.DinnerCarteMenu);

        // 获取underline颜色
        underline_color = typedarray.getColor(R.styleable.DinnerCarteMenu_underlineColor, underline_color);

        // 获取divider颜色
        divider_color = typedarray.getColor(R.styleable.DinnerCarteMenu_dividerColor, divider_color);

        // 获取text selected颜色
        text_selected_color = typedarray.getColor(R.styleable.DinnerCarteMenu_textSelectedColor, text_selected_color);

        // 获取text unselected颜色
        text_unselected_color = typedarray.getColor(R.styleable.DinnerCarteMenu_textUnselectedColor,text_unselected_color);

        // 获取menu背景颜色
        menu_background_color = typedarray.getColor(R.styleable.DinnerCarteMenu_menuBackgroundColor,menu_background_color);

        // 获取TextSize
        menu_textsize = typedarray.getDimensionPixelSize(R.styleable.DinnerCarteMenu_menuTextSize, menu_textsize);

        // 获取menu被选择图标
        menu_selected_icon = typedarray.getResourceId(R.styleable.DinnerCarteMenu_menuSelectedIcon, menu_selected_icon);

        // 获取menu未被选择图标
        menu_unselected_icon = typedarray.getResourceId(R.styleable.DinnerCarteMenu_menuUnselectedIcon, menu_unselected_icon);

        // 释放
        typedarray.recycle();

        // 初始化tabmenu layout,并添加到layout中
        ScrollView scroll = new ScrollView(context);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        scroll.setLayoutParams(params);

        tabmenu_layout = new LinearLayout(context);
        params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        tabmenu_layout.setOrientation(VERTICAL);
        tabmenu_layout.setBackgroundColor(menu_background_color);
        tabmenu_layout.setLayoutParams(params);
        scroll.addView(tabmenu_layout);
        addView(scroll,0);

        // 初始化underline,并添加到当前layout中
        View underline = new View(context);
        params = new LayoutParams(DpTpPx(1.0f),ViewGroup.LayoutParams.MATCH_PARENT);
        underline.setBackgroundColor(underline_color);
        underline.setLayoutParams(params);
        addView(underline,1);

        // 初始化container layout,并添加到当前layout中
        container_layout = new FrameLayout(context);
        params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        container_layout.setLayoutParams(params);
        addView(container_layout, 2);
    }

    /**
     * 设置DropDownMenu
     * */
    public void SetDinnerTableMenu(@NonNull List<String> tabtexts, @NonNull List<View> container_view) {
        // 参数判断
        if(tabtexts.size() != container_view.size()) {
            throw new IllegalArgumentException("params not match, tabTexts.size() should be equal popupViews.size()");
        }

        // 根据菜单添加tab
        for (int index = 0; index < tabtexts.size(); ++index) {
            AddTab(tabtexts,index);
        }

        // 将popupviews添加到popupmenu_layout中
        for (int index = 0; index < container_view.size(); ++index) {
            container_view.get(index).setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            container_view.get(index).setVisibility(index == 0 ? VISIBLE : GONE);
            ((TextView)tabmenu_layout.getChildAt(0)).setTextColor(text_selected_color);
            // 更新tab的背景图标
            ((TextView)tabmenu_layout.getChildAt(0)).setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(menu_selected_icon), null);
            container_layout.addView(container_view.get(index),index);
        }
    }

    /**
     * 关闭菜单
     * */
    public void CloseMenu() {
        if (current_tabindex != -1) {
            // 更新tab的字体颜色
            ((TextView)tabmenu_layout.getChildAt(current_tabindex)).setTextColor(text_unselected_color);

            // 更新tab的背景图标
            ((TextView)tabmenu_layout.getChildAt(current_tabindex)).setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(menu_unselected_icon), null);

            // container layout
            container_layout.setAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.dd_menu_out));
            container_layout.setVisibility(GONE);

            // 重置current tabindex
            current_tabindex = 0;
        }
    }

    /**
     * 添加Tab
     * */
    private void AddTab(@NonNull List<String> listtexts, int index) {
        final TextView tab = new TextView(getContext());
        tab.setSingleLine();
        tab.setEllipsize(TextUtils.TruncateAt.END);
        tab.setGravity(Gravity.CENTER);
        tab.setTextSize(TypedValue.COMPLEX_UNIT_PX,menu_textsize);
        tab.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 0,  1.0f));
        tab.setTextColor(text_unselected_color);
        tab.setCompoundDrawablesWithIntrinsicBounds(null,null,getResources().getDrawable(menu_unselected_icon), null);
        tab.setText(listtexts.get(index));
        tab.setPadding(DpTpPx(12),DpTpPx(12),DpTpPx(5),DpTpPx(12));

        // 添加点击事件
        tab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                SwitchMenu(tab);
            }
        });
        // 将tab添加到tabmenu_layout中
        tabmenu_layout.addView(tab);

        // 添加分割线
        if (index < listtexts.size() - 1) {
            View divider = new View(getContext());
            divider.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,DpTpPx(0.5f)));
            divider.setBackgroundColor(divider_color);
            tabmenu_layout.addView(divider);
        }
    }

    /**
     * 切换菜单
     * */
    public void SwitchMenu(View target) {
        System.out.println(current_tabindex);
        for (int index = 0; index < tabmenu_layout.getChildCount(); index += 2) {
            if (target == tabmenu_layout.getChildAt(index)) {
                // 设置index
                current_tabindex = index;
                // 设置被选择状态
                ((TextView)tabmenu_layout.getChildAt(index)).setTextColor(text_selected_color);
                ((TextView)tabmenu_layout.getChildAt(index)).setCompoundDrawablesWithIntrinsicBounds(null,null,getResources().getDrawable(menu_selected_icon),null);
                // 显示popupview对应index的view
                container_layout.getChildAt(index / 2).setVisibility(VISIBLE);

            } else {
                // 当前点击非此tab
                // 设置未被选择状态
                ((TextView)tabmenu_layout.getChildAt(index)).setTextColor(text_unselected_color);
                ((TextView)tabmenu_layout.getChildAt(index)).setCompoundDrawablesWithIntrinsicBounds(null,null,getResources().getDrawable(menu_unselected_icon),null);
                container_layout.getChildAt(index / 2).setVisibility(GONE);
            }
        }
    }

    /**
     * 改变菜单文字
     * */
    public void SetTabText(String text) {
        if (current_tabindex != -1) {
            ((TextView)tabmenu_layout.getChildAt(current_tabindex)).setText(text);
        }
    }

    /**
     * 设置菜单是否可点击
     * */
    public void SetTabClickable(boolean clickable) {
        for (int i = 0; i < tabmenu_layout.getChildCount(); i += 2) {
            ((TextView)tabmenu_layout.getChildAt(i)).setClickable(clickable);
        }
    }

    /**
     * 当前popupmenu是否可见
     * */
    public boolean IsShowing() {return current_tabindex != -1;}

    public int DpTpPx(float value) {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        return (int)(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,value,dm) + 0.5);
    }

}
