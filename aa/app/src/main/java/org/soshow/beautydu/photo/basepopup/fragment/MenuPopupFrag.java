package org.soshow.beautydu.photo.basepopup.fragment;

import org.soshow.beautydu.photo.basepopup.base.BasePopupWindow;
import org.soshow.beautydu.photo.basepopup.popup.MenuPopup;
import org.soshow.beautyedu.R;

import android.view.View;
import android.widget.Button;

/**
 * Created by 大灯泡 on 2016/1/22. menu
 */
public class MenuPopupFrag extends SimpleBaseFrag {
    private Button mButton;
    private MenuPopup mMenuPopup;

    @Override
    public void bindEvent() {
        mButton = (Button) mFragment.findViewById(R.id.popup_show);
        mMenuPopup = new MenuPopup(mContext);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMenuPopup.showPopupWindow(v);
            }
        });
    }

    @Override
    public BasePopupWindow getPopup() {
        return null;
    }

    @Override
    public Button getButton() {
        return null;
    }

    @Override
    public View getFragment() {
        return mInflater.inflate(R.layout.frag_menu_popup, container, false);
    }
}
