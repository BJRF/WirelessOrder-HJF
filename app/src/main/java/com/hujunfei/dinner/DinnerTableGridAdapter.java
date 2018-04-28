package com.hujunfei.dinner;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.hujunfei.dinner.R;

import java.util.List;

public class DinnerTableGridAdapter extends BaseAdapter {

    private static final String TAG = "DINNERTABLEGRIDADAPTER";

    private List<DinnerTable> list_dinnertable = null;

    private Context context = null;

    private DinnerCarte dinnercarte = null;

    private int current_checkitem = -1;

    public DinnerTableGridAdapter(Context context, List<DinnerTable> listdinnertable,DinnerCarte dinnercarte) {
        this.context = context;
        this.list_dinnertable = listdinnertable;
        this.dinnercarte = dinnercarte;
    }

    public void NotifyUpdate(int position) {
        current_checkitem = position;
        notifyDataSetChanged();
    }

    public int GetDinnerTableId(int position) {
        return list_dinnertable.get(position).GetDinnerTableId();
    }

    /**
     * How many items are in the data set represented by this Adapter.
     *
     * @return Count of items.
     */
    @Override
    public int getCount() {
        return list_dinnertable.size();
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
        return position;
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_table_layout,null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        FillValue(position,viewHolder,convertView);
        return convertView;
    }

    private void FillValue(final int position, ViewHolder viewHolder, final View convertView) {
        viewHolder.textview_table_index.setText(String.valueOf(list_dinnertable.get(position).GetDinnerTableId()));
        viewHolder.button_dinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 预定一桌，指定index
                dinnercarte.DinnerStart(list_dinnertable.get(position).GetDinnerTableId());
                // 启动一个新的activity
                Intent intent = new Intent(context,DinnerFoodListActivity.class);
                // DinnerCarte id
                intent.putExtra(DinnerCarte.DINNERTABLE_ID_KEY,list_dinnertable.get(position).GetDinnerTableId());
                context.startActivity(intent);
                Log.d(TAG, list_dinnertable.get(position).GetDinnerTableId() + "号台点餐啦！");
            }
        });
        viewHolder.button_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 启动一个新的activity
                Intent intent = new Intent(context,DinnerAccountActivity.class);
                // DinnerCarte id
                intent.putExtra(DinnerCarte.DINNERTABLE_ID_KEY,list_dinnertable.get(position).GetDinnerTableId());
                context.startActivity(intent);
                Log.d(TAG,"老板，" + list_dinnertable.get(position).GetDinnerTableId()  + "号台结账了！");
            }
        });
        viewHolder.button_speech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 预定一桌，指定index
                dinnercarte.DinnerStart(list_dinnertable.get(position).GetDinnerTableId());
                // 启动一个新的activity
                Intent intent = new Intent(context,DinnerSpeechActivity.class);
                // DinnerCarte id
                intent.putExtra(DinnerCarte.DINNERTABLE_ID_KEY,list_dinnertable.get(position).GetDinnerTableId());
                context.startActivity(intent);
                Log.d(TAG,"语音点餐");
            }
        });
        if (position == current_checkitem) {
            viewHolder.textview_table_index.setTextColor(context.getResources().getColor(R.color.red));
            convertView.setBackgroundResource(R.drawable.check_bg);
        } else {
            viewHolder.textview_table_index.setTextColor(context.getResources().getColor(R.color.color_table_index));
            convertView.setBackgroundResource(R.drawable.uncheck_bg);
        }
    }

    static class ViewHolder {
        TextView textview_table_index = null;
        Button button_dinner = null;
        Button button_account = null;
        ImageButton button_speech = null;

        ViewHolder(View view) {
            textview_table_index = (TextView)view.findViewById(R.id.item_table_index);
            button_dinner = (Button)view.findViewById(R.id.dinner_start);
            button_account = (Button)view.findViewById(R.id.dinner_account);
            button_speech = (ImageButton)view.findViewById(R.id.item_dinner_speech);
        }
    }
}
