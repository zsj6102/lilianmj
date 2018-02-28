package org.soshow.beautydu.photo.localphoto;

import java.util.ArrayList;
import java.util.List;

import org.soshow.beautydu.photo.localphoto.adapter.PhotoAdapter;
import org.soshow.beautydu.photo.localphoto.bean.PhotoInfo;
import org.soshow.beautydu.photo.localphoto.bean.PhotoSerializable;
import org.soshow.beautyedu.R;
import org.soshow.beautyedu.utils.UniversalImageLoadTool;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;

/**    
 */
public class PhotoFragment extends Fragment {

    public interface OnPhotoSelectClickListener {
        // public void onPhotoSelectClickListener(List<PhotoInfo> list);
        public void onPhotoSelectClickListener(PhotoInfo photoInfo);

    }

    private OnPhotoSelectClickListener onPhotoSelectClickListener;

    private GridView gridView;
    private PhotoAdapter photoAdapter;

    private List<PhotoInfo> list;

    private int hasSelect = 1;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (onPhotoSelectClickListener == null) {
            onPhotoSelectClickListener = (OnPhotoSelectClickListener) activity;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater
                .inflate(R.layout.fragment_photoselect, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        gridView = (GridView) getView().findViewById(R.id.gridview);

        Bundle args = getArguments();

        PhotoSerializable photoSerializable = (PhotoSerializable) args
                .getSerializable("list");
        list = new ArrayList<PhotoInfo>();
        list.addAll(photoSerializable.getList());
        for (int i = 0, size = list.size(); i < size; i++) {
            PhotoInfo photoInfo = list.get(i);
            if (isInSelectedDataList(photoInfo.getPath_absolute())) {
                photoInfo.setChoose(true);
            } else {
                photoInfo.setChoose(false);
            }
        }

        hasSelect = SelectPhotoActivity.hasList.size();

        photoAdapter = new PhotoAdapter(getActivity(), list, gridView);
        gridView.setAdapter(photoAdapter);

        gridView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                Log.i("position", list.get(position).isChoose() + "  "
                        + position);
                if (list.get(position).isChoose()) {
                    list.get(position).setChoose(false);
                    hasSelect--;
                } else if (hasSelect < 9) {
                    list.get(position).setChoose(true);
                    hasSelect++;
                } else {
                    Toast.makeText(getActivity(), "最多选择9张图片！",
                            Toast.LENGTH_SHORT).show();
                }
                Log.e("size", hasSelect + "");
                photoAdapter.refreshView(position);
                onPhotoSelectClickListener.onPhotoSelectClickListener(list
                        .get(position));
            }
        });

        gridView.setOnScrollListener(new OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == 0) {
                    UniversalImageLoadTool.resume();
                } else {
                    UniversalImageLoadTool.pause();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                    int visibleItemCount, int totalItemCount) {
            }
        });
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
}
