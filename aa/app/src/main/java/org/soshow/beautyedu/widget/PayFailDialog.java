package org.soshow.beautyedu.widget;

import org.soshow.beautyedu.R;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @Package: com.example.test
 * @Author: caixiaozhen
 * @Time: 2016年3月2日 上午10:31:06
 * @File: MyDialog.java
 * @Description: 自定义dialog
 */
public class PayFailDialog extends Dialog {

    public PayFailDialog(Context context) {
        super(context);
    }

    public PayFailDialog(Context context, int theme) {
        super(context, theme);
    }

    public static class FailBuilder {
        private Context context;
        private View contentsView;
        private String positiveButtonText;
        private DialogInterface.OnClickListener positiveListenner;
        private DialogInterface.OnClickListener nagetiveListenner;
		private MyDialog myDialog;

        public FailBuilder(Context context) {
            this.context = context;
        }

        public FailBuilder setContentsView(View view) {
            this.contentsView = view;
            return this;
        }

        public FailBuilder setPositiveButton(String buttonText,
                DialogInterface.OnClickListener listenner) {
            this.positiveButtonText = buttonText;
            this.positiveListenner = listenner;
            return this;
        }

        public FailBuilder setPositiveButton(int positiveButtonText,
                DialogInterface.OnClickListener listenner) {
            this.positiveButtonText = (String) context
                    .getText(positiveButtonText);
            this.positiveListenner = listenner;
            return this;
        }


        public FailBuilder setNagetiveButton(String nagetiveButtonText,
                DialogInterface.OnClickListener listenner) {
            this.nagetiveListenner = listenner;
            return this;
        }

        public FailBuilder setNagetiveButton(int nagetiveButtonText,
                DialogInterface.OnClickListener listenner) {
            this.nagetiveListenner = listenner;
            return this;
        }

        @SuppressLint("InflateParams")
        @SuppressWarnings("deprecation")
        public MyDialog creatDialog() {
            myDialog = new MyDialog(context, R.style.Dialog);
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.dialog_fail_to_pay, null);
            myDialog.addContentView(layout, new LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            if (positiveButtonText != null) {
                ((TextView) layout.findViewById(R.id.positiveButton))
                        .setText(positiveButtonText);
                if (positiveListenner != null) {
                    ((TextView) layout.findViewById(R.id.positiveButton))
                            .setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    positiveListenner.onClick(myDialog,
                                            DialogInterface.BUTTON_POSITIVE);
                                }
                            });
                }
            } else {
                layout.findViewById(R.id.positiveButton).setVisibility(
                        View.GONE);
            }
                if (nagetiveListenner != null) {
                    ((ImageView) layout.findViewById(R.id.negativeButton))
                            .setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    nagetiveListenner.onClick(myDialog,
                                            DialogInterface.BUTTON_NEGATIVE);
                                }
                            });
                }

            myDialog.setContentView(layout);
            return myDialog;
        }

		public void dismiss() {
			myDialog.dismiss();
		}
    }

}
