package org.soshow.beautydu.photo.activity;

import org.soshow.beautydu.photo.basepopup.fragment.CommentPopupFrag;
import org.soshow.beautydu.photo.basepopup.fragment.CustomInterpolatorPopupFrag;
import org.soshow.beautydu.photo.basepopup.fragment.DialogPopupFrag;
import org.soshow.beautydu.photo.basepopup.fragment.InputPopupFrag;
import org.soshow.beautydu.photo.basepopup.fragment.ListPopupFrag;
import org.soshow.beautydu.photo.basepopup.fragment.MenuPopupFrag;
import org.soshow.beautydu.photo.basepopup.fragment.ScalePopupFrag;
import org.soshow.beautydu.photo.basepopup.fragment.SlideFromBottomPopupFrag;
import org.soshow.beautyedu.R;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuItem;

public class DemoActivity extends FragmentActivity {
    private FragmentManager mFragmentManager;
    private ScalePopupFrag mNormalPopupFrag;
    private SlideFromBottomPopupFrag mSlideFromBottomPopupFrag;
    private CommentPopupFrag mCommentPopupFrag;
    private InputPopupFrag mInputPopupFrag;
    private ListPopupFrag mListPopupFrag;
    private MenuPopupFrag mMenuPopupFrag;
    private DialogPopupFrag mDialogPopupFrag;
    private CustomInterpolatorPopupFrag mInterpolatorPopupFrag;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);
        mFragmentManager = getSupportFragmentManager();

        mNormalPopupFrag = new ScalePopupFrag();
        mSlideFromBottomPopupFrag = new SlideFromBottomPopupFrag();
        mCommentPopupFrag = new CommentPopupFrag();
        mInputPopupFrag = new InputPopupFrag();
        mListPopupFrag = new ListPopupFrag();
        mMenuPopupFrag = new MenuPopupFrag();
        mDialogPopupFrag = new DialogPopupFrag();
        mInterpolatorPopupFrag = new CustomInterpolatorPopupFrag();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_demo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.id_scale_popup:
            mFragmentManager.beginTransaction()
                    .replace(R.id.parent, mNormalPopupFrag).commit();
            break;
        case R.id.id_slide_from_bottom_popup:
            mFragmentManager.beginTransaction()
                    .replace(R.id.parent, mSlideFromBottomPopupFrag).commit();
            break;
        case R.id.id_comment_popup:
            mFragmentManager.beginTransaction()
                    .replace(R.id.parent, mCommentPopupFrag).commit();
            break;
        case R.id.id_input_popup:
            mFragmentManager.beginTransaction()
                    .replace(R.id.parent, mInputPopupFrag).commit();
            break;
        case R.id.id_list_popup:
            mFragmentManager.beginTransaction()
                    .replace(R.id.parent, mListPopupFrag).commit();
            break;
        case R.id.id_menu_popup:
            mFragmentManager.beginTransaction()
                    .replace(R.id.parent, mMenuPopupFrag).commit();
            break;
        case R.id.id_dialog_popup:
            mFragmentManager.beginTransaction()
                    .replace(R.id.parent, mDialogPopupFrag).commit();
            break;
        case R.id.id_custom_interpolator_popup:
            mFragmentManager.beginTransaction()
                    .replace(R.id.parent, mInterpolatorPopupFrag).commit();
            break;
        default:
            break;

        }

        return super.onOptionsItemSelected(item);
    }
}
