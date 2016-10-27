package com.mmnn.zoo.expendable;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.h6ah4i.android.widget.advrecyclerview.expandable.ExpandableItemConstants;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractExpandableItemAdapter;
import com.h6ah4i.android.widget.advrecyclerview.utils.AbstractExpandableItemViewHolder;
import com.mmnn.zoo.R;

import java.util.ArrayList;
import java.util.List;


public class ListAdapterV53 extends AbstractExpandableItemAdapter<ListAdapterV53.MyGroupViewHolder,
        ListAdapterV53.MyChildViewHolder> {
    private static final String TAG = "MyExpandableItemAdapter";

    private List<Item> mDeviceItems = new ArrayList<>();
    private Context mContext;
    private View.OnClickListener mOnClickListener;

    public ListAdapterV53(Context context, View.OnClickListener onClickListener) {
        // ExpandableItemAdapter requires stable ID, and also
        // have to implement the getGroupItemId()/getChildItemId() methods appropriately.
        setHasStableIds(true);
        mContext = context;
        mOnClickListener = onClickListener;
        for (int i = 0; i < 5; i++) {
            mDeviceItems.add(new Item("item " + i));
        }
    }

    @Override
    public int getGroupCount() {
        return mDeviceItems.size();
    }

    @Override
    public int getChildCount(int groupPosition) {
        return 1;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return groupPosition * 10 + childPosition;
    }

    @Override
    public int getGroupItemViewType(int groupPosition) {
        return 0;
    }

    @Override
    public int getChildItemViewType(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public MyGroupViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View v = inflater.inflate(R.layout.home_device_main_item, parent, false);
        return new MyGroupViewHolder(v, mOnClickListener);
    }

    @Override
    public MyChildViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View v = inflater.inflate(R.layout.home_device_pad_item, parent, false);
        return new MyChildViewHolder(v);
    }

    @Override
    public void onBindGroupViewHolder(MyGroupViewHolder holder, int groupPosition, int viewType) {

        if (mDeviceItems == null || groupPosition >= mDeviceItems.size()) {
            return;
        }
        Item item = mDeviceItems.get(groupPosition);
        holder.expendIcon.setVisibility(View.INVISIBLE);
        holder.itemView.setEnabled(false);
        holder.itemView.setClickable(false);
        if (item != null) {
            holder.button.setVisibility(View.GONE);
//            holder.deviceIcon.setImageResource(DKDeviceInfoFactory.getHomeListIconRes(deviceModel.getDeviceTypeId()));
            holder.deviceIcon.setVisibility(View.VISIBLE);
            holder.title.setText(item.text);
            holder.title.setVisibility(View.VISIBLE);
            holder.subTitle.setVisibility(View.GONE);
            holder.subTitleR.setVisibility(View.GONE);
            holder.deviceTypeIcon.setVisibility(View.GONE);
            holder.subTitleR.setVisibility(View.VISIBLE);
            holder.deviceStatus.setVisibility(View.GONE);
            holder.itemView.setEnabled(true);
            holder.itemView.setClickable(true);
            holder.expendIcon.setVisibility(View.VISIBLE);
        }
        // set background resource (target view ID: container)
        final int expandState = holder.getExpandStateFlags();
        if ((expandState & ExpandableItemConstants.STATE_FLAG_IS_UPDATED) != 0) {
            boolean isExpanded;
            boolean animateIndicator = ((expandState & Expandable.STATE_FLAG_HAS_EXPANDED_STATE_CHANGED) != 0);

            if ((expandState & Expandable.STATE_FLAG_IS_EXPANDED) != 0) {
                holder.expendIcon.setImageResource(R.drawable.ic_arrow_up);
            } else {
                holder.expendIcon.setImageResource(R.drawable.ic_arrow_down);
            }
        }
    }

    @Override
    public void onBindChildViewHolder(MyChildViewHolder holder, int groupPosition, int childPosition, int viewType) {
    }

    @Override
    public boolean onCheckCanExpandOrCollapseGroup(MyGroupViewHolder holder, int groupPosition, int x, int y, boolean expand) {
        // check the item is *not* pinned
//        if (mProvider.getGroupItem(groupPosition).isPinned()) {
//            // return false to raise View.OnClickListener#onClick() event
//            return false;
//        }

        // check is enabled
        return holder.itemView.isEnabled() && holder.itemView.isClickable();

    }

    // NOTE: Make accessible with short name
    private interface Expandable extends ExpandableItemConstants {
    }

    public static abstract class MyBaseViewHolder extends AbstractExpandableItemViewHolder {

        public MyBaseViewHolder(View v) {
            super(v);
        }
    }

    public static class MyGroupViewHolder extends MyBaseViewHolder {
        TextView groupName;
        //		View divider;
        ImageView deviceIcon;
        TextView title;
        TextView subTitle;
        TextView subTitleR;
        TextView deviceStatus;
        TextView button;
        ImageView deviceTypeIcon;
        ImageView expendIcon;
        View itemDivider;
        View content;

        public MyGroupViewHolder(View v, View.OnClickListener onClickListener) {
            super(v);
            groupName = (TextView) v.findViewById(R.id.group_name);
            deviceIcon = (ImageView) v.findViewById(R.id.device_icon);
            title = (TextView) v.findViewById(R.id.title);
            subTitle = (TextView) v.findViewById(R.id.subtitle);
            subTitleR = (TextView) v.findViewById(R.id.subtitle_r);
            button = (TextView) v.findViewById(R.id.btn_bottom);
            deviceTypeIcon = (ImageView) v.findViewById(R.id.device_type_icon);
            expendIcon = (ImageView) v.findViewById(R.id.expend_icon);
            deviceStatus = (TextView) v.findViewById(R.id.device_status);
            itemDivider = v.findViewById(R.id.item_divider);
            content = v.findViewById(R.id.content_group);

//            content.setOnClickListener(onClickListener);
//            expendIcon.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                }
//            });
        }
    }

    public static class MyChildViewHolder extends MyBaseViewHolder {
        public MyChildViewHolder(View v) {
            super(v);
        }
    }

    public class Item {
        public int distance = 0;
        public String text;

        public Item(String string) {
            text = string;
        }
    }
}
