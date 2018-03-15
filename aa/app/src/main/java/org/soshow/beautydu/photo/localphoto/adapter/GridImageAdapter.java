package org.soshow.beautydu.photo.localphoto.adapter;

import java.io.File;
import java.util.List;

import org.soshow.beautydu.photo.localphoto.util.DensityUtil;
import org.soshow.beautyedu.R;


import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;

import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;


public class GridImageAdapter extends BaseAdapter {
    private Context mContext;
    private List<ImageItem> dataList;
    private int width;

    public GridImageAdapter(Context c, List<ImageItem> dataList) {
        this.mContext = c;
        this.dataList = dataList;
        this.width = (DensityUtil.getScreenMetrics(c).x - DensityUtil.dip2px(
                mContext, 20)) / 3;
    }

    @Override
    public int getCount() {
        return dataList.size() >= 9 ? 9 : (dataList.size() + 1);
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(mContext, R.layout.item_grid_img, null);
            holder.imageview = (ImageView) convertView
                    .findViewById(R.id.row_gridview_imageview);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        LayoutParams layoutParams = new LayoutParams(width, width);
        holder.imageview.setLayoutParams(layoutParams);

        if (position == dataList.size()) {
            holder.imageview.setImageResource(R.drawable.icon_camera);
            if (position == 9) {
                holder.imageview.setVisibility(View.GONE);
            }
        } else {
            ImagePicker.getInstance().getImageLoader().displayImage((Activity) mContext, dataList.get(position).path, holder.imageview, 0, 0);

        }


        return convertView;
    }

    private class ViewHolder {
        public ImageView imageview;
    }
}
