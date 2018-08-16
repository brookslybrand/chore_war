package brookslybrand.chorewar;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

public class DropDownListAdapterWarriors extends BaseAdapter {

    private String TAG = "DropDownListAdapterWarriors";

    private ArrayList<String> mListItems;
    private LayoutInflater mInflater;
    private TextView mSelectedItems;
    private static int selectedCount = 0;
    private static String firstSelected = "";
    private ViewHolder holder;
    private static String selected = "";	//shortened selected values representation

    public static String getSelected() {
        return selected;
    }

    public void setSelected(String selected) {
        DropDownListAdapterWarriors.selected = selected;
    }

    public static void resetSelectedCount() {
        DropDownListAdapterWarriors.selectedCount = 0;
    }


    public DropDownListAdapterWarriors(Context context, ArrayList<String> items,
                                       TextView tv) {
        mListItems = new ArrayList<String>();
        mListItems.addAll(items);
        mInflater = LayoutInflater.from(context);
        mSelectedItems = tv;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mListItems.size();
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.drop_down_warriors_list, null);
            holder = new ViewHolder();
            holder.tv = (TextView) convertView.findViewById(R.id.selectOptionWarriors);
            holder.chkbox = (CheckBox) convertView.findViewById(R.id.checkboxWarriors);
            convertView.setTag(holder);


        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tv.setText(mListItems.get(position));

        final int position1 = position;

        //whenever the checkbox is clicked the selected values textview is updated with new selected values
        holder.chkbox.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                setText(position1);
            }
        });

        if(NewWarActivity.checkSelectedWarriors[position]) {
            holder.chkbox.setChecked(true);
        }
        else
            holder.chkbox.setChecked(false);
        return convertView;
    }

    /*
     * Function which updates the selected values display and information(checkSelected[])
     * */
    private void setText(int position1){
        if (!NewWarActivity.checkSelectedWarriors[position1]) {
            NewWarActivity.checkSelectedWarriors[position1] = true;
            selectedCount++;
        } else {
            NewWarActivity.checkSelectedWarriors[position1] = false;
            selectedCount--;
        }

        if (selectedCount == 0) {
            mSelectedItems.setText(R.string.select_string);
        } else if (selectedCount == 1) {
            for (int i = 0; i < NewWarActivity.checkSelectedWarriors.length; i++) {
                if (NewWarActivity.checkSelectedWarriors[i] == true) {
                    firstSelected = mListItems.get(i);
                    break;
                }
            }
            mSelectedItems.setText(firstSelected);
            setSelected(firstSelected);
        } else if (selectedCount > 1) {
            for (int i = 0; i < NewWarActivity.checkSelectedWarriors.length; i++) {
                if (NewWarActivity.checkSelectedWarriors[i] == true) {
                    firstSelected = mListItems.get(i);
                    break;
                }
            }
            mSelectedItems.setText(firstSelected + " & "+ (selectedCount - 1) + " more");
            setSelected(firstSelected + " & "+ (selectedCount - 1) + " more");
        }
    }

    private class ViewHolder {
        TextView tv;
        CheckBox chkbox;
    }
}
