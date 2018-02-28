package org.soshow.beautyedu.activity;

import org.soshow.beautyedu.R;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
/**
 * 习题
 */
public class FragmentXiti extends BaseFragment implements OnClickListener {
	private View rootView;
	// private SharedPreferences sp;
	public static FragmentXiti fmt;
	// private TextView video_description;
	// private static int count = 0;
	public static boolean mHasLoadedOnce;
	private boolean isPrepared;
	private ImageView type_xiti;
	private RelativeLayout pull_to_refresh_header;
	private TextView title_xiti, choice_xiti, answer_xiti, xiti_no_content;
	private Button shangyiti, xiayiti, kandaan;
	public static int xiti_index = 0;
	private String[] choice = { "A.", "B.", "C.", "D.", "E.", "F.", "G.", "H。" };
	private View answer_left, answer_right;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		fmt = this;
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		if (rootView == null) {
			rootView = inflater.inflate(R.layout.fragment_xiti, null);
			xiti_no_content = (TextView) rootView.findViewById(R.id.xiti_no_content);
			type_xiti = (ImageView) rootView.findViewById(R.id.type_xiti);
			title_xiti = (TextView) rootView.findViewById(R.id.title_xiti);
			choice_xiti = (TextView) rootView.findViewById(R.id.choice_xiti);
			answer_xiti = (TextView) rootView.findViewById(R.id.answer_xiti);
			shangyiti = (Button) rootView.findViewById(R.id.shangyiti);
			pull_to_refresh_header = (RelativeLayout) rootView.findViewById(R.id.pull_to_refresh_header);
			answer_left = (View) rootView.findViewById(R.id.answer_left);
			answer_right = (View) rootView.findViewById(R.id.answer_right);
			shangyiti.setOnClickListener(this);
			xiayiti = (Button) rootView.findViewById(R.id.xiayiti);
			xiayiti.setOnClickListener(this);
			kandaan = (Button) rootView.findViewById(R.id.kandaan);
			kandaan.setOnClickListener(this);
			isPrepared = true;
			lazyLoad();

		}
		// 缓存的rootView需要判断是否已经被加过parent，
		// 如果有parent需要从parent删除，要不然会发生这个rootview已经有parent的错误。
		ViewGroup parent = (ViewGroup) rootView.getParent();
		if (parent != null) {
			parent.removeView(rootView);
		}

		return rootView;
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		Log.d("345abc", "xiti onstart");
		super.onStart();

	}

	@Override
	protected void lazyLoad() {
		// TODO Auto-generated method stub
		if (!isPrepared || !isVisible || mHasLoadedOnce) {
			return;
		}
		// Toast.makeText(getActivity(), "1", Toast.LENGTH_SHORT).show();
		setInfo();
		// Toast.makeText(getActivity(), "碎片加载", Toast.LENGTH_SHORT).show();
		mHasLoadedOnce = true;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.shangyiti:
			if (xiti_index == 0) {
				// Toast.makeText(getActivity(), "当前已是第一题",
				// Toast.LENGTH_SHORT).show();
				xiti_index = PlayVideoActivity.quests.size() - 1;
				setInfo();
			} else {
				// answer_xiti.setVisibility(View.GONE);
				xiti_index--;
				setInfo();
			}
			answer_xiti.setVisibility(View.GONE);
			break;
		case R.id.xiayiti:
			if (xiti_index == (PlayVideoActivity.quests.size() - 1)) {
				xiti_index = 0;
				setInfo();
			} else {
				xiti_index++;
				setInfo();
			}
			answer_xiti.setVisibility(View.GONE);
			break;
		case R.id.kandaan:
			String[] answer = PlayVideoActivity.quests.get(xiti_index).quest_answer.split(";");
			StringBuilder sb = new StringBuilder();
			sb.append("正确答案：");

			for (int i = 0; i < answer.length; i++) {
				if (i == answer.length - 1) {
					sb.append(answer[i]);
				} else {
					sb.append(answer[i]).append(" ");
				}

			}
			// Toast.makeText(getActivity(), sb.toString(),
			// Toast.LENGTH_SHORT).show();
			answer_xiti.setText(sb.toString());
			answer_xiti.setVisibility(View.VISIBLE);
			break;
		default:
			break;
		}
	}

	private void setInfo() {
		if (PlayVideoActivity.quests != null && !PlayVideoActivity.quests.isEmpty() && PlayVideoActivity.load_quest) {
			try {
				pull_to_refresh_header.setVisibility(View.GONE);
				xiti_no_content.setVisibility(View.GONE);
				type_xiti.setVisibility(View.VISIBLE);
				title_xiti.setVisibility(View.VISIBLE);
				choice_xiti.setVisibility(View.VISIBLE);
				answer_xiti.setVisibility(View.GONE);
				if (PlayVideoActivity.quests.size() != 1) {
					shangyiti.setVisibility(View.VISIBLE);
					xiayiti.setVisibility(View.VISIBLE);
					answer_left.setVisibility(View.GONE);
					answer_right.setVisibility(View.GONE);
				} else {
					shangyiti.setVisibility(View.GONE);
					xiayiti.setVisibility(View.GONE);
					answer_left.setVisibility(View.VISIBLE);
					answer_right.setVisibility(View.VISIBLE);
				}
				kandaan.setVisibility(View.VISIBLE);
				int type_quest = Integer.parseInt(PlayVideoActivity.quests.get(xiti_index).quest_type);
				switch (type_quest) {
				case 1:
					type_xiti.setImageResource(R.drawable.tiico_dx);
					break;
				case 2:
					type_xiti.setImageResource(R.drawable.tiico_duoxuan);
					break;
				case 3:
					type_xiti.setImageResource(R.drawable.tiico_panduan);
					break;
				default:
					break;
				}
				String xiti_number = (xiti_index + 1) + "/" + PlayVideoActivity.quests.size();
				Spannable span = new SpannableString(
						xiti_number + " " + PlayVideoActivity.quests.get(xiti_index).quest_title);
				span.setSpan(new ForegroundColorSpan(Color.BLUE), 0, xiti_number.length(),
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				title_xiti.setText(span);

				StringBuilder sb = new StringBuilder();
				switch (type_quest) {
				case 1:
					for (int i = 0; i < Integer
							.parseInt(PlayVideoActivity.quests.get(xiti_index).quest_select_num); i++) {
						if (i != Integer.parseInt(PlayVideoActivity.quests.get(xiti_index).quest_select_num) - 1) {
							sb.append(choice[i] + PlayVideoActivity.quests.get(xiti_index).choice[i] + "\n");
						} else {
							sb.append(choice[i] + PlayVideoActivity.quests.get(xiti_index).choice[i]);
						}
					}
					break;
				case 2:
					for (int i = 0; i < Integer
							.parseInt(PlayVideoActivity.quests.get(xiti_index).quest_select_num); i++) {
						if (i != Integer.parseInt(PlayVideoActivity.quests.get(xiti_index).quest_select_num) - 1) {
							sb.append(choice[i] + PlayVideoActivity.quests.get(xiti_index).choice[i] + "\n");
						} else {
							sb.append(choice[i] + PlayVideoActivity.quests.get(xiti_index).choice[i]);
						}
					}
					break;
				case 3:
					for (int i = 0; i < 2; i++) {
						if (i != 1) {
							sb.append(choice[i] + PlayVideoActivity.quests.get(xiti_index).choice[i] + "\n");
						} else {
							sb.append(choice[i] + PlayVideoActivity.quests.get(xiti_index).choice[i]);
						}
					}
					break;
				default:
					break;
				}

				choice_xiti.setText(sb.toString());
			} catch (Exception e) {
				// TODO: handle exception
			}

		} else if ((PlayVideoActivity.quests == null || PlayVideoActivity.quests.isEmpty())
				&& PlayVideoActivity.load_quest) {
			xiti_no_content.setVisibility(View.VISIBLE);
			type_xiti.setVisibility(View.GONE);
			title_xiti.setVisibility(View.GONE);
			choice_xiti.setVisibility(View.GONE);
			answer_xiti.setVisibility(View.GONE);
			shangyiti.setVisibility(View.GONE);
			xiayiti.setVisibility(View.GONE);
			kandaan.setVisibility(View.GONE);
			answer_left.setVisibility(View.GONE);
			answer_right.setVisibility(View.GONE);
			pull_to_refresh_header.setVisibility(View.GONE);
			if (PlayVideoActivity.lecture_questions_id_o == null
					|| PlayVideoActivity.lecture_questions_id_o.isEmpty()) {
				xiti_no_content.setText("该课程无相关习题");
			} else {
				// Toast.makeText(getActivity(), "1",
				// Toast.LENGTH_SHORT).show();
				xiti_no_content.setText("加载错误，请检查您的网络连接或稍后再试");
			}
		} else if (!PlayVideoActivity.load_quest) {
			xiti_no_content.setVisibility(View.GONE);
			type_xiti.setVisibility(View.GONE);
			title_xiti.setVisibility(View.GONE);
			choice_xiti.setVisibility(View.GONE);
			answer_xiti.setVisibility(View.GONE);
			shangyiti.setVisibility(View.GONE);
			xiayiti.setVisibility(View.GONE);
			kandaan.setVisibility(View.GONE);
			answer_left.setVisibility(View.GONE);
			answer_right.setVisibility(View.GONE);
			pull_to_refresh_header.setVisibility(View.VISIBLE);
		}
	}

	public void refresh() {
		// Toast.makeText(getActivity(), "1", Toast.LENGTH_SHORT).show();
		pull_to_refresh_header.setVisibility(View.GONE);
		lazyLoad();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		try {
			fmt = null;
		} catch (Exception e) {
			// TODO: handle exception
		}

		super.onDestroy();
	}

}