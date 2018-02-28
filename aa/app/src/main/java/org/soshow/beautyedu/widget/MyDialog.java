package org.soshow.beautyedu.widget;

import org.soshow.beautyedu.R;
import org.soshow.beautyedu.activity.MainTabActivity;
import org.soshow.beautyedu.utils.SoftInputUtil;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @Package: com.example.test
 * @Author: caixiaozhen
 * @Time: 2016年3月2日 上午10:31:06
 * @File: MyDialog.java
 * @Description: 自定义dialog
 */
public class MyDialog extends Dialog {

    public MyDialog(Context context) {
        super(context);
    }

    public MyDialog(Context context, int theme) {
        super(context, theme);
    }

    public static class MyBuilder {
        private Context context;
        private String message;
        private String title;
        private View contentsView;
        private String positiveButtonText;
        private String nagetiveButtonText;
        private String neutralButtonText;
        private DialogInterface.OnClickListener positiveListenner;
        private DialogInterface.OnClickListener nagetiveListenner;
        private DialogInterface.OnClickListener neutralListenner;
		private View layout;
		private int x;
		private int y;

        public MyBuilder(Context context) {
            this.context = context;
        }
        
        public View getView() {
            return layout;
        }

        public MyBuilder setMessage(String message, String money) {
            this.message = message;
            return this;
        }

        public MyBuilder setMessage(int messsage) {
            this.message = (String) context.getText(messsage);
            return this;
        }

        public MyBuilder setMessage(String messsage) {
            this.message = messsage;
            return this;
        }

        public MyBuilder setTitle(String title) {
            this.title = title;
            return this;
        }

        public MyBuilder setTitle(int title) {
            this.title = (String) context.getText(title);
            return this;
        }

        public MyBuilder setContentsView(View view) {
            this.contentsView = view;
            return this;
        }

        public MyBuilder setPositiveButton(String buttonText,
                DialogInterface.OnClickListener listenner) {
            this.positiveButtonText = buttonText;
            this.positiveListenner = listenner;
            return this;
        }

        public MyBuilder setPositiveButton(int positiveButtonText,
                DialogInterface.OnClickListener listenner) {
            this.positiveButtonText = (String) context
                    .getText(positiveButtonText);
            this.positiveListenner = listenner;
            return this;
        }

        public MyBuilder setNeutralButton(String nagetiveButtonText,
                DialogInterface.OnClickListener listenner) {
            this.neutralButtonText = nagetiveButtonText;
            this.neutralListenner = listenner;
            return this;
        }

        public MyBuilder setNeutralButton(int nagetiveButtonText,
                DialogInterface.OnClickListener listenner) {
            this.neutralButtonText = (String) context
                    .getText(nagetiveButtonText);
            this.neutralListenner = listenner;
            return this;
        }

        public MyBuilder setNagetiveButton(String nagetiveButtonText,
                DialogInterface.OnClickListener listenner) {
            this.nagetiveButtonText = nagetiveButtonText;
            this.nagetiveListenner = listenner;
            return this;
        }

        public MyBuilder setNagetiveButton(int nagetiveButtonText,
                DialogInterface.OnClickListener listenner) {
            this.nagetiveButtonText = (String) context
                    .getText(nagetiveButtonText);
            this.nagetiveListenner = listenner;
            return this;
        }

        @SuppressLint("InflateParams")
        @SuppressWarnings("deprecation")
        public MyDialog creatDialog() {
            final MyDialog myDialog = new MyDialog(context, R.style.Dialog);
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            layout = inflater.inflate(R.layout.popup_input, null);
            myDialog.addContentView(layout, new LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
//            ((TextView) layout.findViewById(R.id.pop_title)).setText(title);
            if (positiveButtonText != null) {
                ((TextView) layout.findViewById(R.id.btn_Compelete))
                        .setText(positiveButtonText);
                if (positiveListenner != null) {
                    ((TextView) layout.findViewById(R.id.btn_Compelete))
                            .setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    positiveListenner.onClick(myDialog,
                                            DialogInterface.BUTTON_POSITIVE);
                                }
                            });
                }
            } else {
                // if no confirm button just set the visibility to GONE
                layout.findViewById(R.id.btn_Compelete).setVisibility(
                        View.GONE);
            }
            // set the cancel button
            if (nagetiveButtonText != null) {
                ((TextView) layout.findViewById(R.id.btn_cancel))
                        .setText(nagetiveButtonText);
                if (nagetiveListenner != null) {
                    ((TextView) layout.findViewById(R.id.btn_cancel))
                            .setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    nagetiveListenner.onClick(myDialog,
                                            DialogInterface.BUTTON_NEGATIVE);
                                }
                            });
                }
            } else {
                // if no confirm button just set the visibility to GONE
                layout.findViewById(R.id.btn_cancel).setVisibility(
                        View.GONE);
            }

            // set the neutral button
//            if (neutralButtonText != null) {
//                ((EditText) layout.findViewById(R.id.ed_input))
//                        .getText().toString();
//                if (neutralListenner != null) {
//                    ((TextView) layout.findViewById(R.id.neutralButton))
//                            .setOnClickListener(new View.OnClickListener() {
//                                public void onClick(View v) {
//                                    neutralListenner.onClick(myDialog,
//                                            DialogInterface.BUTTON_NEGATIVE);
//                                }
//                            });
//                }
//            } else {
//                // if no confirm button just set the visibility to GONE
//                layout.findViewById(R.id.neutralButton)
//                        .setVisibility(View.GONE);
//            }
            // set the content message
            if (message != null) {
                ((TextView) layout.findViewById(R.id.message)).setText(message);
                /*
                 * Html .fromHtml(context.getResources().getString(
                 * R.string.content_one) + message +
                 * context.getResources().getString( R.string.content_two) +
                 * "<font color=\"#fdb302\">" + money + "</font>" +
                 * context.getResources().getString( R.string.content_three))
                 */
            } else if (contentsView != null) {
                // if no message set
                // add the contentView to the dialog body
                ((LinearLayout) layout.findViewById(R.id.content))
                        .removeAllViews();
                ((LinearLayout) layout.findViewById(R.id.content)).addView(
                        contentsView, new LayoutParams(
                                LayoutParams.FILL_PARENT,
                                LayoutParams.FILL_PARENT));
            }
            myDialog.setContentView(layout);
            return myDialog;
        }

        //设置dialog的位置
		public void setX(int x) {
			this.x = x;
		}
		
		public void setY(int y) {
			this.y = y;
		}
    }

}
