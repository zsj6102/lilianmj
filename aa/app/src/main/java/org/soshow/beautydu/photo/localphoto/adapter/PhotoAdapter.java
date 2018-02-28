package org.soshow.beautydu.photo.localphoto.adapter;

import java.util.List;

import org.soshow.beautydu.photo.localphoto.SelectPhotoActivity;
import org.soshow.beautydu.photo.localphoto.bean.PhotoInfo;
import org.soshow.beautydu.photo.localphoto.imageaware.RotateImageViewAware;
import org.soshow.beautydu.photo.localphoto.util.DensityUtil;
import org.soshow.beautydu.photo.localphoto.util.ThumbnailsUtil;
import org.soshow.beautyedu.R;
import org.soshow.beautyedu.utils.UniversalImageLoadTool;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

/**
 * 相片适配器
 */
public class PhotoAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private List<PhotoInfo> list;
    private ViewHolder viewHolder;
    private GridView gridView;
    private int width;

    public PhotoAdapter(Context context, List<PhotoInfo> list, GridView gridView) {
        mInflater = LayoutInflater.from(context);
        this.list = list;
        this.gridView = gridView;
        width = DensityUtil.getScreenMetrics(context).x / 3;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * 刷新view
     * @param index
     */
    public void refreshView(int index) {
        int visiblePos = gridView.getFirstVisiblePosition();
        View view = gridView.getChildAt(index - visiblePos);
        ViewHolder holder = (ViewHolder) view.getTag();

        if (list.get(index).isChoose()) {
            holder.selectImage.setImageResource(R.drawable.gou_selected);
        } else {
            holder.selectImage.setImageResource(R.drawable.gou_normal);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_selectphoto, null);
            viewHolder.image = (ImageView) convertView
                    .findViewById(R.id.imageView);
            viewHolder.selectImage = (ImageView) convertView
                    .findViewById(R.id.selectImage);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (list.get(position).isChoose()) {
            viewHolder.selectImage.setImageResource(R.drawable.gou_selected);
        } else {
            viewHolder.selectImage.setImageResource(R.drawable.gou_normal);
        }
        PhotoInfo photoInfo = list.get(position);
        LayoutParams layoutParams = viewHolder.image.getLayoutParams();
        layoutParams.width = width;
        layoutParams.height = width;
        viewHolder.image.setLayoutParams(layoutParams);
        if (photoInfo != null) {
            UniversalImageLoadTool.disPlay(
                    ThumbnailsUtil.MapgetHashValue(photoInfo.getImage_id(),
                            photoInfo.getPath_file()),
                    new RotateImageViewAware(viewHolder.image, photoInfo
                            .getPath_absolute()), R.drawable.defaultpic);
            // UniversalImageLoadTool.disPlay(ThumbnailsUtil.MapgetHashValue(photoInfo.getImage_id(),photoInfo.getPath_file()),
            // viewHolder.image, R.drawable.common_defalt_bg);
        }
        return convertView;
    }

    private boolean isInSelectedDataList(String selectedString) {
        if (SelectPhotoActivity.hasList != null) {
            List<PhotoInfo> hasList = SelectPhotoActivity.hasList;
            Log.e("PhotoAdapter hasList", SelectPhotoActivity.hasList.size()
                    + "");
            for (int i = 0; i < hasList.size(); i++) {
                if (hasList.get(i).getPath_absolute().equals(selectedString)) {
                    return true;
                }
            }
        }
        return false;
    }

    public class ViewHolder {
        public ImageView image;
        public ImageView selectImage;
    }
}
