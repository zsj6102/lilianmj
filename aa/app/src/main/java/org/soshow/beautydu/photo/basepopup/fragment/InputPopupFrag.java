package org.soshow.beautydu.photo.basepopup.fragment;

import org.soshow.beautydu.photo.basepopup.base.BasePopupWindow;
import org.soshow.beautydu.photo.basepopup.popup.InputPopup;
import org.soshow.beautyedu.R;

import android.view.View;
import android.widget.Button;

/**
 * Created by 大灯泡 on 2016/1/18. 自动弹出输入框的popup
 */
public class InputPopupFrag extends SimpleBaseFrag {

    @Override
    public void bindEvent() {

    }

    @Override
    public BasePopupWindow getPopup() {
        return new InputPopup(mContext);
    }

    @Override
    public Button getButton() {
        return (Button) mFragment.findViewById(R.id.popup_show);
    }

    @Override
    public View getFragment() {
        return mInflater.inflate(R.layout.frag_input_popup, container, false);
    }
}
