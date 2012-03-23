package www.ui;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import www.data.Enum.forumType;
import www.data.Forum;
import www.logic.ForumHome;
import www.model.HomeItem;
import www.model.MyAnimation;
import www.model.ReplyListAdapter;
import www.utils.Base64;
import www.utils.ExitApplication;
import www.wealk.com.R;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mobclick.android.MobclickAgent;

public class ReplyActivity extends Activity {
	private ListView replyListView;
	private ProgressBar replyProgressbar;
	private ReplyTask mReplyTask;
	private ArrayList<HomeItem> mHomeItemList = new ArrayList<HomeItem>();
	private ReplyListAdapter mReplyListAdapter;
	private LinearLayout more_layout;
	private Button nextPage;
	private TextView title_text;
	private Bundle bundle;
	private SharedPreferences sharedPreferences;
	private ImageButton backImgbtn;
	private ImageButton refreshImgbtn;
	private LinearLayout replyLinearLayout;
	private int counts;
	private int headImgState;
	private int model;
	private int page = 0;
	private int foot = 0;
	private int replyNum;
	private String closed;
	private LayoutParams FWLayoutParams = new LinearLayout.LayoutParams(
			LinearLayout.LayoutParams.FILL_PARENT,
			LinearLayout.LayoutParams.WRAP_CONTENT);
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				refreshImgbtn.setVisibility(View.INVISIBLE);
				backImgbtn.setVisibility(View.INVISIBLE);
				break;
			default:
				break;
			}
		};
	};

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.reply);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title);
		
		ExitApplication.getInstance().addActivity(this);
		
		sharedPreferences = getSharedPreferences("userApp",
				Context.MODE_PRIVATE);
		replyNum = sharedPreferences.getInt("replyNum", 10);
		replyListView = (ListView) findViewById(R.id.replyListview);
		replyProgressbar = (ProgressBar) findViewById(R.id.reply_progressbar);
		replyProgressbar.setIndeterminate(false);
		
		model = sharedPreferences.getInt("imgState", 0);
		headImgState = sharedPreferences.getInt("headImgState", 0);

		bundle = this.getIntent().getExtras();

		replyLinearLayout = (LinearLayout) findViewById(R.id.reply_ll);
		replyLinearLayout.setVisibility(View.INVISIBLE);

		backImgbtn = (ImageButton) findViewById(R.id.reply_back_imgbtn);
		refreshImgbtn = (ImageButton) findViewById(R.id.reply_refresh_imgbtn);
		backImgbtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
		refreshImgbtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				refreshTask();
			}
		});
		more_layout = new LinearLayout(this);
		
		title_text = new TextView(this);
		title_text.setPadding(10, 0, 0, 0);
		title_text.setText(bundle.getString("name"));
		title_text.setTextColor(0xff000000);
		title_text.setTextSize(16);
		title_text.setBackgroundResource(R.drawable.reply_subject_bg);
		title_text.setGravity(Gravity.CENTER_VERTICAL);
		replyListView.addHeaderView(title_text);
		
		nextPage = new Button(this);
		nextPage.setText(R.string.next);
		nextPage.setBackgroundResource(R.drawable.mforum_bg);
		nextPage.setGravity(Gravity.CENTER);
		more_layout.addView(nextPage, FWLayoutParams);

		nextPage.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				++page;
				replyTask();
			}
		});
		refreshImgbtn.setVisibility(View.INVISIBLE);
		backImgbtn.setVisibility(View.INVISIBLE);
		replyListView.setOnTouchListener(ListViewOnTouchListener);
	}

	public void refreshTask() {
		page = 0;
		model = sharedPreferences.getInt("imgState", 0);
		headImgState = sharedPreferences.getInt("headImgState", 0);
		replyNum = sharedPreferences.getInt("replyNum", 10);
		mHomeItemList = new ArrayList<HomeItem>();
		replyTask();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		
		outState.putString("fid", Forum.getForumId());
		outState.putString("postid", Forum.getPostsId());
		outState.putString("url", Forum.getUrl());
		outState.putString("username", Forum.getUserName());
		outState.putString("userid", Forum.getUserId());
		outState.putInt("userState", Forum.getUserState());
		super.onSaveInstanceState(outState);
	}

	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		
		Forum.setFroumId(savedInstanceState.getString("fid"));
		Forum.setPostsId(savedInstanceState.getString("postid"));
		Forum.setUrl(savedInstanceState.getString("url"));
		Forum.setUserId(savedInstanceState.getString("userid"));
		Forum.setUserName(savedInstanceState.getString("username"));
		Forum.setUserState(savedInstanceState.getInt("userState"));
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	protected void onPause() {

		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onResume() {
		try {
			closed = bundle.getString("closed");
			replyNum = sharedPreferences.getInt("replyNum", 10);
			refreshTask();
//			counts = Integer.parseInt(bundle.getString("counts"))/replyNum ;
		} catch (Exception e) {
			e.printStackTrace();
		}

		super.onResume();
		MobclickAgent.onResume(this);
	}

	private void replyTask() {

		if (mReplyTask != null
				&& mReplyTask.getStatus() == AsyncTask.Status.RUNNING) {
			mReplyTask.cancel(true);
		} else {
			mReplyTask = new ReplyTask();
			mReplyTask.execute(Forum.getPostsId(), String.valueOf(page),
					String.valueOf(model),String.valueOf(replyNum));
		}
	}

	public class ReplyTask extends AsyncTask<String, String, JSONObject> {

		protected void onPreExecute() {
			replyProgressbar.setVisibility(View.VISIBLE);
		}

		protected JSONObject doInBackground(String... params) {
			JSONObject json = null;
			try {
				json = ForumHome.getPostForum(params[0], params[1], params[2],params[3]);
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return json;
		}

		protected void onPostExecute(JSONObject result) {
			replyProgressbar.setVisibility(View.GONE);
			replyLinearLayout.setVisibility(View.VISIBLE);
			if (result != null) {
				try {

					JSONArray jsonArray = result.getJSONArray("list");

					for (int i = 0; i < jsonArray.length(); i++) {
						HomeItem mHomeItem = new HomeItem(
								jsonArray.getJSONObject(i), forumType.REPLY);
						mHomeItemList.add(mHomeItem);
					}
					int countNum = Integer.parseInt(Base64.decodeString(jsonArray.getJSONObject(0).getString("replies")));
					counts = (countNum-1)/replyNum;
					if (page < counts && foot == 0) {
						replyListView.addFooterView(more_layout);
						foot = 1;
					} else if (page == counts && foot == 1) {
						replyListView.removeFooterView(more_layout);
						foot = 0;
					}
					mReplyListAdapter = new ReplyListAdapter(
							ReplyActivity.this, mHomeItemList,
							ReplyActivity.this, closed, headImgState);

					replyListView.setAdapter(mReplyListAdapter);
					replyListView.setSelection(page * replyNum);
					replyListView.startAnimation(new MyAnimation());
					mReplyListAdapter.notifyDataSetChanged();
				} catch (JSONException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				Toast.makeText(getApplicationContext(), R.string.network_error,Toast.LENGTH_SHORT).show();
			}
		}
	}

	OnTouchListener ListViewOnTouchListener = new OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				refreshImgbtn.setVisibility(View.VISIBLE);
				backImgbtn.setVisibility(View.VISIBLE);
				break;
			case MotionEvent.ACTION_MOVE:
				refreshImgbtn.setVisibility(View.VISIBLE);
				backImgbtn.setVisibility(View.VISIBLE);
				break;
			case MotionEvent.ACTION_UP:
				 mHandler.sendEmptyMessageDelayed(0, 3000);
				break;
			}
			return false;
		}
	};
}