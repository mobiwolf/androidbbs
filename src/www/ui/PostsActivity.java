package www.ui;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import www.data.Enum.forumType;
import www.data.Forum;
import www.logic.ForumHome;
import www.model.ForumListAdapter;
import www.model.HomeItem;
import www.model.MyAnimation;
import www.model.PostsListAdapter;
import www.utils.Base64;
import www.utils.ExitApplication;
import www.utils.JSONHelper;
import www.utils.Utils;
import www.wealk.com.R;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class PostsActivity extends BaseActivity {

	private ArrayList<HomeItem> mHomeItemList;
	private ArrayList<HomeItem> mForumItemList;
	private PostsListAdapter mPostsListAdapter;
	private ListView postsListView;
	private TextView pageNum;
	private ImageButton sendPosts;
	private ImageButton nextImgbtn;
	private ImageButton refreshImgbtn;
	private ImageButton backImgbtn;
	private PostsTask mPostsTask;
	private Bundle bundle;
	private int counts = 0;
	private int page = 0;
	private String titleName;
	private TextView titleText;
	private int headImgState;
	private String fid;
	private String forumString;
	private JSONObject forumJsonObject;
	private ListView forumList;
	private ForumListAdapter mForumListAdapter;
	private LinearLayout postForumrl;
	private TextView postForumText;
	private ImageView postForumImg;
	private int postNum;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.posts);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title);
		
		ExitApplication.getInstance().addActivity(this);
		
		initHeader();
		postNum = sharedPreferences.getInt("postNum", 10);
		titleText = (TextView) findViewById(R.id.title_text);
		headImgState = sharedPreferences.getInt("headImgState", 0);

		postForumrl = (LinearLayout) findViewById(R.id.post_forumrl);
		postForumText = (TextView) findViewById(R.id.post_forum_text);
		postForumImg = (ImageView) findViewById(R.id.post_tubiao);
		forumList = (ListView) findViewById(R.id.post_forumlist);

		postsListView = (ListView) findViewById(R.id.post_listview);


		pageNum = (TextView) findViewById(R.id.posts_page);
		sendPosts = (ImageButton) findViewById(R.id.post_new);
		backImgbtn = (ImageButton) findViewById(R.id.post_back);
		nextImgbtn = (ImageButton) findViewById(R.id.post_next);
		refreshImgbtn = (ImageButton) findViewById(R.id.post_refresh);

		sendPosts.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (!Utils.isEmpty(Forum.getUserId())) {
					if (Forum.getUserState() == 2) {
						Toast.makeText(getApplicationContext(), R.string.no_speak,
								Toast.LENGTH_SHORT).show();
					}else if (Forum.getUserState() == 3) {
						Toast.makeText(getApplicationContext(), R.string.no_visit,
								Toast.LENGTH_SHORT).show();
					} else if (Forum.getUserState() == 4) {
						Toast.makeText(getApplicationContext(), R.string.locking,
								Toast.LENGTH_SHORT).show();
					} else {
						intent.setClass(PostsActivity.this, SendPostsActivity.class);
						Bundle bundle = new Bundle();   
						bundle.putInt("sendState", 0);
						intent.putExtras(bundle); 
						startActivity(intent);
					}
				} else {
					jumpToLogin();
				}
			}
		});

		backImgbtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				--page;
				PostsTask();
			}
		});

		nextImgbtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				++page;
				PostsTask();
			}
		});

		refreshImgbtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				refreshTask();
			}
		});
		bundle = this.getIntent().getExtras();
		fid = bundle.getString("fid");
		PostsTask();
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onPause() {

		if (mPostsTask != null
				&& mPostsTask.getStatus() == AsyncTask.Status.RUNNING) {

			mPostsTask.cancel(true);
		}
		super.onPause();
	}

	@Override
	protected void onResume() {
		try {
			postNum = sharedPreferences.getInt("postNum", 10);
			titleName = bundle.getString("fname");
			titleText.setText(titleName);
			fid = bundle.getString("fid");
			forumString = bundle.getString("childJsonArray");
			String child = "{\"child\":" + forumString + "}";
			forumJsonObject = JSONHelper.str2json(child);

			JSONArray jsonArray = null;
			jsonArray = forumJsonObject.getJSONArray("child");
			if (jsonArray.length() > 0) {
				postForumrl.setVisibility(View.VISIBLE);
				forumList.setVisibility(View.GONE);
				mForumItemList = new ArrayList<HomeItem>();
				for (int i = 0; i < jsonArray.length(); i++) {
					HomeItem mHomeItem = new HomeItem(
							jsonArray.getJSONObject(i), forumType.MASTER);
					mForumItemList.add(mHomeItem);
				}
				mForumListAdapter = new ForumListAdapter(PostsActivity.this,
						mForumItemList, PostsActivity.this);
				forumList.setAdapter(mForumListAdapter);
				mForumListAdapter.notifyDataSetChanged();
				postForumText.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						if(forumList.getVisibility() == View.VISIBLE){
							forumList.setVisibility(View.GONE);
							postForumImg.setImageResource(R.drawable.btn_browser);
						}else if(forumList.getVisibility() == View.GONE){
							forumList.setVisibility(View.VISIBLE);
							postForumImg.setImageResource(R.drawable.btn_browser2);
						}
					}
				});
			} else {
				postForumrl.setVisibility(View.GONE);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		super.onResume();
	}

	public void refreshTask() {
		headImgState = sharedPreferences.getInt("headImgState", 0);
		postNum = sharedPreferences.getInt("postNum", 10);
//		counts = Integer.parseInt(bundle.getString("counts"))/postNum +1;
//		pageNum.setText((page + 1) + "/" + (counts));
		PostsTask();
	}

	public void PostsTask() {

		if (mPostsTask != null
				&& mPostsTask.getStatus() == AsyncTask.Status.RUNNING) {
			return;
		} else {
			mPostsTask = new PostsTask();

			mPostsTask.execute(String.valueOf(0), fid, String.valueOf(page),String.valueOf(postNum));

		}
	}

	public class PostsTask extends AsyncTask<String, String, JSONObject> {
		protected void onPreExecute() {
			showLoadingProgressDialog();
		}

		protected JSONObject doInBackground(String... params) {
			JSONObject json = null;
			try {
				json = ForumHome
						.getThreadForum(params[0], params[1], params[2],params[3]);
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return json;
		}

		protected void onPostExecute(JSONObject result) {
			dismissProgressDialog();

			if (result != null) {
				try {
					JSONArray jsonArray = result.getJSONArray("list");
					mHomeItemList = new ArrayList<HomeItem>();
					if (jsonArray.length() == 0) {
						Toast.makeText(PostsActivity.this, R.string.no_posts,
								Toast.LENGTH_SHORT).show();
						pageNum.setText("1/1");
						backImgbtn.setEnabled(false);
						nextImgbtn.setEnabled(false);
						backImgbtn.setBackgroundResource(R.drawable.back1);
						nextImgbtn.setBackgroundResource(R.drawable.next1);
					}else{
						for (int i = 0; i < jsonArray.length(); i++) {
							HomeItem mHomeItem = new HomeItem(
									jsonArray.getJSONObject(i), forumType.POSTS);
							mHomeItemList.add(mHomeItem);
						}
						int countNum = Integer.parseInt(Base64.decodeString(jsonArray.getJSONObject(0).getString("threads")));
						counts = (countNum-1)/postNum;
						pageNum.setText((page+1) + "/" + (counts+1));
						if (counts == 0) {
							backImgbtn.setEnabled(false);
							nextImgbtn.setEnabled(false);
							backImgbtn.setBackgroundResource(R.drawable.back1);
							nextImgbtn.setBackgroundResource(R.drawable.next1);
						} else if (page == 0) {
							backImgbtn.setEnabled(false);
							nextImgbtn.setEnabled(true);
							backImgbtn.setBackgroundResource(R.drawable.back1);
							nextImgbtn.setBackgroundResource(R.drawable.next);
						} else if (page == counts) {
							backImgbtn.setEnabled(true);
							nextImgbtn.setEnabled(false);
							backImgbtn.setBackgroundResource(R.drawable.back);
							nextImgbtn.setBackgroundResource(R.drawable.next1);
						} else if (page < counts) {
							backImgbtn.setEnabled(true);
							nextImgbtn.setEnabled(true);
							backImgbtn.setBackgroundResource(R.drawable.back);
							nextImgbtn.setBackgroundResource(R.drawable.next);
						}
						mPostsListAdapter = new PostsListAdapter(
								PostsActivity.this, mHomeItemList,
								PostsActivity.this, headImgState);
				
						postsListView.setAdapter(mPostsListAdapter);
						
						postsListView.startAnimation(new MyAnimation());
						
						mPostsListAdapter.notifyDataSetChanged();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				showToast();
			}
		}
	}
}