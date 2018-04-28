package com.hujunfei.dinner;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.hujunfei.dinner.R;

import java.util.List;


public class DinnerFoodListAdapter extends BaseAdapter {
    private static final String TAG = "DINNERFOODLISTADAPTER";
    private Context context = null;
    private DinnerCarte dinnercarte = null;
    private int tableid = -1;
    private String kindname = "";

    public DinnerFoodListAdapter(Context context, DinnerCarte dinnercarte, int dinnertable, String type) {
        this.context = context;
        this.dinnercarte = dinnercarte;
        this.tableid = dinnertable;
        this.kindname = type;
    }
    /**
     * How many items are in the data set represented by this Adapter.
     *
     * @return Count of items.
     */
    @Override
    public int getCount() {
        return dinnercarte.GetDinnerFoodsByType(kindname).size();
    }

    /**
     * Get the data item associated with the specified position in the data set.
     *
     * @param position Position of the item whose data we want within the adapter's
     *                 data set.
     * @return The data at the specified position.
     */
    @Override
    public Object getItem(int position) {
        return null;
    }

    /**
     * Get the row id associated with the specified position in the list.
     *
     * @param position The position of the item within the adapter's data set whose row id we want.
     * @return The id of the item at the specified position.
     */
    @Override
    public long getItemId(int position) {
        return 0;
    }

    /**
     * Get a View that displays the data at the specified position in the data set. You can either
     * create a View manually or inflate it from an XML layout file. When the View is inflated, the
     * parent View (GridView, ListView...) will apply default layout parameters unless you use
     * {@link LayoutInflater#inflate(int, ViewGroup, boolean)}
     * to specify a root view and to prevent attachment to the root.
     *
     * @param position    The position of the item within the adapter's data set of the item whose view
     *                    we want.
     * @param convertView The old view to reuse, if possible. Note: You should check that this view
     *                    is non-null and of an appropriate type before using. If it is not possible to convert
     *                    this view to display the correct data, this method can create a new view.
     *                    Heterogeneous lists can specify their number of view types, so that this View is
     *                    always of the right type (see {@link #getViewTypeCount()} and
     *                    {@link #getItemViewType(int)}).
     * @param parent      The parent that this view will eventually be attached to
     * @return A View corresponding to the data at the specified position.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView != null) {
            viewHolder = (ViewHolder)convertView.getTag();
        } else {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_custom_goods_layout,null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        FillValue(position,viewHolder);
        return convertView;
    }

    private void FillValue(final int position, ViewHolder viewHolder) {
        List<DinnerFood> dinnerfoods = dinnercarte.GetDinnerFoodsByType(kindname);
        DinnerFood food = dinnerfoods.get(position);
        int number = dinnercarte.GetDinnerFoodNumber(tableid,kindname,position);
        if (number < 0)
            number = 0;
        if (number >= 1000)
            number = 999;
        viewHolder.textview_name.setText(food.GetFoodName().toString());
        viewHolder.textview_price.setText("单价: " + String.valueOf(food.GetFoodPrice()) + "元");
        viewHolder.textview_number.setText(String.valueOf(number));
        if (number == 0) {
            viewHolder.imageview_minus.setVisibility(View.INVISIBLE);
            viewHolder.textview_number.setVisibility(View.INVISIBLE);
        } else {
            viewHolder.imageview_minus.setVisibility(View.VISIBLE);
            viewHolder.textview_number.setVisibility(View.VISIBLE);
        }
        SetEventListener(position,viewHolder);
    }

    private void SetEventListener(final int position, final ViewHolder viewHolder) {
        // 设置减事件
        viewHolder.imageview_minus.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        viewHolder.imageview_minus.setImageResource(R.mipmap.minus_2);
                        break;
                    case MotionEvent.ACTION_UP:
                        viewHolder.imageview_minus.setImageResource(R.mipmap.minus_1);
                        break;
                }
                return false;
            }
        });
        viewHolder.imageview_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int nitems = Integer.valueOf(viewHolder.textview_number.getText().toString()).intValue();
                dinnercarte.DinnerOrder(tableid,kindname,position,nitems - 1);
                notifyDataSetChanged();
            }
        });

        // 设置加事件
        viewHolder.imageview_plus.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        viewHolder.imageview_plus.setImageResource(R.mipmap.plus_2);
                        break;
                    case MotionEvent.ACTION_UP:
                        viewHolder.imageview_plus.setImageResource(R.mipmap.plus_1);
                        break;
                }
                return false;
            }
        });

        viewHolder.imageview_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int nitems = Integer.valueOf(viewHolder.textview_number.getText().toString()).intValue();
                dinnercarte.DinnerOrder(tableid,kindname,position,nitems + 1);
                notifyDataSetChanged();
            }
        });

        viewHolder.textview_number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 构建一个新的Dialog获取item number
                AlertDialog.Builder dialog_builder = new AlertDialog.Builder(context);
                // 获取自定义布局
                View item_number_input_layout = LayoutInflater.from(context).inflate(R.layout.item_number_input_layout,null);
                final EditText input_edittext = (EditText)item_number_input_layout.findViewById(R.id.item_number_input_edittext);
                input_edittext.setText(viewHolder.textview_number.getText().toString());
                input_edittext.setSelection(viewHolder.textview_number.getText().length());
                dialog_builder.setIcon(R.mipmap.w0)
                        .setTitle("请输入数量:")
                        .setView(item_number_input_layout)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String str = input_edittext.getText().toString();
                                int nitems = 0;
                                if (!str.equals("")) {
                                    nitems = Integer.valueOf(str).intValue();
                                }
                                dinnercarte.DinnerOrder(tableid,kindname,position,nitems);
                                notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("取消",null)
                        .setCancelable(true)
                        .create()
                        .show();
            }
        });
    }

    static class ViewHolder {
        TextView textview_name = null;
        TextView textview_price = null;
        ImageView imageview_minus = null;
        TextView textview_number = null;
        ImageView imageview_plus = null;

        ViewHolder(View view) {
            textview_name = (TextView)view.findViewById(R.id.name_item);
            textview_price = (TextView)view.findViewById(R.id.price_item);
            imageview_minus = (ImageView)view.findViewById(R.id.minus_item_number);
            textview_number = (TextView)view.findViewById(R.id.item_number);
            imageview_plus = (ImageView)view.findViewById(R.id.plus_item_number);
        }
    }
}
