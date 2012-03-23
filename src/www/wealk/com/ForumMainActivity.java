package www.wealk.com;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import www.data.Enum.forumType;
import www.data.Forum;
import www.logic.ForumHome;
import www.model.HomeItem;
import www.model.PostsListAdapter;
import www.ui.BaseActivity;
import www.ui.PostsActivity;
import www.utils.Base64;
import www.utils.ExitApplication;
import www.utils.JSONHelper;
import www.utils.Utils;
import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mobclick.android.MobclickAgent;

public class ForumMainActivity extends BaseActivity {

	private static HomeTask mHomeTask;
	private NewTask mNewTask;
	private HotTask mHotTask;
	private PostsListAdapter mPostsListAdapter;
	private ArrayList<HomeItem> mHomeItemList;
	private ExpandableListView homeListView;
	private ListView hotListView;
	private ListView newListView;
	private RelativeLayout forumBtn;
	private RelativeLayout hotBtn;
	private RelativeLayout newBtn;
	private JSONObject result;
	private int hotState;
	private int newState;
	private int MAIN_REFRESH_TASK = 0;
	private int headImgState;
	private TreeViewAdapter adapter;
	private int loginState;
	private LoginTask mLoginTask;
	private int postNum;
	private static LayoutInflater inflater;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title);
		inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		ExitApplication.getInstance().addActivity(this);

		initHeader();
		Button backBtn = (Button) findViewById(R.id.back_imgbtn);
		backBtn.setVisibility(View.GONE);

		loginState = sharedPreferences.getInt("loginState", 0);
		if (loginState == 2) {
			Forum.setUserId(sharedPreferences.getString("userid", ""));
			Forum.setUserName(sharedPreferences.getString("name", ""));
			if (mLoginTask != null
					&& mLoginTask.getStatus() == AsyncTask.Status.RUNNING) {
				return;
			} else {
				mLoginTask = new LoginTask();
				mLoginTask.execute(sharedPreferences.getString("name", ""),
						sharedPreferences.getString("password", ""));
			}
		}
		postNum = sharedPreferences.getInt("postNum", 10);

		headImgState = sharedPreferences.getInt("headImgState", 0);

		homeListView = (ExpandableListView) findViewById(R.id.mainList);
		hotListView = (ListView) findViewById(R.id.hostList);
		newListView = (ListView) findViewById(R.id.newList);
		homeListView.setVisibility(View.VISIBLE);
		hotListView.setVisibility(View.INVISIBLE);
		newListView.setVisibility(View.INVISIBLE);

		adapter = new TreeViewAdapter(this);

		forumBtn = (RelativeLayout) findViewById(R.id.forum_rl);
		hotBtn = (RelativeLayout) findViewById(R.id.hot_rl);
		newBtn = (RelativeLayout) findViewById(R.id.new_rl);
		forumBtn.setBackgroundResource(R.drawable.tab_bg_unselected);

		forumBtn.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				if (mHotTask != null
						&& mHotTask.getStatus() == AsyncTask.Status.RUNNING) {
					mHotTask.cancel(true);
				}
				if (mNewTask != null
						&& mNewTask.getStatus() == AsyncTask.Status.RUNNING) {
					mNewTask.cancel(true);
				}
				forumBtn.setBackgroundResource(R.drawable.tab_bg_unselected);
				hotBtn.setBackgroundResource(0x00);
				newBtn.setBackgroundResource(0x00);
				homeListView.setVisibility(View.VISIBLE);
				hotListView.setVisibility(View.INVISIBLE);
				newListView.setVisibility(View.INVISIBLE);
				MAIN_REFRESH_TASK = 0;
			}
		});

		hotBtn.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				if (hotState == 0) {
					if (mHomeTask != null
							&& mHomeTask.getStatus() == AsyncTask.Status.RUNNING) {
						mHomeTask.cancel(true);
					}
					if (mNewTask != null
							&& mNewTask.getStatus() == AsyncTask.Status.RUNNING) {
						mNewTask.cancel(true);
					}
					HotTask();
				}
				forumBtn.setBackgroundResource(0x00);
				hotBtn.setBackgroundResource(R.drawable.tab_bg_unselected);
				newBtn.setBackgroundResource(0x00);
				homeListView.setVisibility(View.INVISIBLE);
				hotListView.setVisibility(View.VISIBLE);
				newListView.setVisibility(View.INVISIBLE);
				MAIN_REFRESH_TASK = 1;
			}
		});

		newBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (newState == 0) {
					if (mHotTask != null
							&& mHotTask.getStatus() == AsyncTask.Status.RUNNING) {
						mHotTask.cancel(true);
					}
					if (mHomeTask != null
							&& mHomeTask.getStatus() == AsyncTask.Status.RUNNING) {
						mHomeTask.cancel(true);
					}
					NewTask();
				}
				forumBtn.setBackgroundResource(0x00);
				hotBtn.setBackgroundResource(0x00);
				newBtn.setBackgroundResource(R.drawable.tab_bg_unselected);
				homeListView.setVisibility(View.INVISIBLE);
				hotListView.setVisibility(View.INVISIBLE);
				newListView.setVisibility(View.VISIBLE);
				MAIN_REFRESH_TASK = 2;
			}
		});

		int forumState = sharedPreferences.getInt("forumState", 0);
		if (forumState == 0) {
			HomeListTask();
		} else if (forumState == 1) {
			String list = sharedPreferences.getString("forumList", null);
			result = JSONHelper.str2json(list);
			showResult(result);
		}

		MobclickAgent.update(this);
		MobclickAgent.setUpdateOnlyWifi(false);

	}

	// key down
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			showExitDialog();
		}
		return false;
	}

	public void refreshTask() {
		switch (MAIN_REFRESH_TASK) {
		case 0:
			HomeListTask();
			break;
		case 1:
			headImgState = sharedPreferences.getInt("headImgState", 0);
			HotTask();
			break;
		case 2:
			headImgState = sharedPreferences.getInt("headImgState", 0);
			NewTask();
			break;
		default:
			break;
		}
	}

	public void HomeListTask() {
		if (mHomeTask != null
				&& mHomeTask.getStatus() == AsyncTask.Status.RUNNING) {
			return;
		} else {
			mHomeTask = new HomeTask();
			mHomeTask.execute();
		}
	}

	private void HotTask() {
		if (mHotTask != null
				&& mHotTask.getStatus() == AsyncTask.Status.RUNNING) {
			return;
		} else {
			mHotTask = new HotTask();
			mHotTask.execute(String.valueOf(postNum));
		}
	}

	private void NewTask() {
		if (mNewTask != null
				&& mNewTask.getStatus() == AsyncTask.Status.RUNNING) {
			return;
		} else {
			mNewTask = new NewTask();
			mNewTask.execute(String.valueOf(postNum));
		}
	}

	protected class HomeTask extends AsyncTask<String, String, JSONObject> {
		protected void onPreExecute() {
			homeListView.setVisibility(View.INVISIBLE);
			showLoadingProgressDialog();
		}

		protected JSONObject doInBackground(String... params) {
			JSONObject json = null;
			try {
				json = ForumHome.getHomeForum();
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return json;
		}

		protected void onPostExecute(JSONObject result) {
			dismissProgressDialog();
			homeListView.setVisibility(View.VISIBLE);
			showResult(result);
			if (result != null) {
				Editor editor = sharedPreferences.edit();
				editor.putInt("forumState", 1);
				editor.putString("forumList", String.valueOf(result));
				editor.commit();
			} else {
				showToast();
			}
		}
	}

	public void showResult(final JSONObject result) {
		try {
			adapter.RemoveAll();
			JSONArray jsonArray = null;
			jsonArray = result.getJSONArray("list");

			List<TreeViewAdapter.TreeNode> treeNode = adapter.GetTreeNode();
			for (int i = 0; i < jsonArray.length(); i++) {
				TreeViewAdapter.TreeNode node = new TreeViewAdapter.TreeNode();
				node.parent = Base64.decodeString(jsonArray.getJSONObject(i)
						.getString("name"));
				JSONArray childJsonArray = null;
				childJsonArray = jsonArray.getJSONObject(i).getJSONArray(
						"child");
				for (int j = 0; j < childJsonArray.length(); j++) {
					node.childs.add(Base64.decodeString(childJsonArray
							.getJSONObject(j).getString("name")));
					node.fid.add(Base64.decodeString(childJsonArray
							.getJSONObject(j).getString("fid")));
					// node.counts.add(Base64.decodeString(childJsonArray
					// .getJSONObject(j).getString("threads")));
					JSONArray thirdJsonArray = null;
					thirdJsonArray = childJsonArray.getJSONObject(j)
							.getJSONArray("child");
					node.childJsonArray.add(thirdJsonArray);

				}
				treeNode.add(node);
			}

			adapter.UpdateTreeNode(treeNode);
			homeListView.setAdapter(adapter);
			homeListView.setGroupIndicator(null);
			homeListView.setDivider(null);
			homeListView.setOnChildClickListener(new OnChildClickListener() {
				public boolean onChildClick(ExpandableListView arg0, View arg1,
						int parent, int children, long arg4) {
					if (!Utils.isEmpty(Forum.getUserId())) {
						if (Forum.getUserState() == 3) {
							Toast.makeText(getApplicationContext(),
									R.string.no_visit, Toast.LENGTH_SHORT)
									.show();
						} else if (Forum.getUserState() == 4) {
							Toast.makeText(getApplicationContext(),
									R.string.locking, Toast.LENGTH_SHORT)
									.show();
						} else {
							List<TreeViewAdapter.TreeNode> treeNode = adapter
									.GetTreeNode();
							Forum.setFroumId(String.valueOf(treeNode
									.get(parent).fid.get(children)));
							intent.setClass(ForumMainActivity.this,
									PostsActivity.class);
							Bundle bundle = new Bundle();
							bundle.putString("fid", String.valueOf(treeNode
									.get(parent).fid.get(children)));
							bundle.putString("fname", String.valueOf(treeNode
									.get(parent).childs.get(children)));
							bundle.putString(
									"childJsonArray",
									String.valueOf(treeNode.get(parent).childJsonArray
											.get(children)));
							intent.putExtras(bundle);
							startActivity(intent);
						}
					} else {
						List<TreeViewAdapter.TreeNode> treeNode = adapter
								.GetTreeNode();
						Forum.setFroumId(String.valueOf(treeNode.get(parent).fid
								.get(children)));
						intent.setClass(ForumMainActivity.this,
								PostsActivity.class);
						Bundle bundle = new Bundle();
						bundle.putString("fid", String.valueOf(treeNode
								.get(parent).fid.get(children)));
						bundle.putString("fname", String.valueOf(treeNode
								.get(parent).childs.get(children)));
						bundle.putString("childJsonArray", String
								.valueOf(treeNode.get(parent).childJsonArray
										.get(children)));
						intent.putExtras(bundle);
						startActivity(intent);
					}
					return false;
				}
			});
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public class HotTask extends AsyncTask<String, String, JSONObject> {
		protected void onPreExecute() {
			hotListView.setVisibility(View.INVISIBLE);
			showLoadingProgressDialog();
		}

		protected JSONObject doInBackground(String... params) {
			JSONObject json = null;
			try {
				json = ForumHome.getHotForum(params[0]);
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return json;
		}

		protected void onPostExecute(JSONObject result) {
			dismissProgressDialog();
			hotListView.setVisibility(View.VISIBLE);
			if (result != null) {
				hotState = 1;
				try {
					mHomeItemList = new ArrayList<HomeItem>();
					JSONArray jsonArray = null;
					jsonArray = result.getJSONArray("list");

					if (jsonArray.length() == 0) {
						Toast.makeText(ForumMainActivity.this,
								R.string.no_posts, Toast.LENGTH_LONG).show();
					} else {
						for (int i = 0; i < jsonArray.length(); i++) {
							HomeItem mHomeItem = new HomeItem(
									jsonArray.getJSONObject(i), forumType.POSTS);
							mHomeItemList.add(mHomeItem);
						}
						mPostsListAdapter = new PostsListAdapter(
								ForumMainActivity.this, mHomeItemList,
								ForumMainActivity.this, headImgState);
						hotListView.setAdapter(mPostsListAdapter);

						mPostsListAdapter.notifyDataSetChanged();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				Toast.makeText(ForumMainActivity.this,
						R.string.hot_error, Toast.LENGTH_LONG).show();
			}
		}
	}

	public class NewTask extends AsyncTask<String, String, JSONObject> {
		protected void onPreExecute() {
			newListView.setVisibility(View.INVISIBLE);
			showLoadingProgressDialog();
		}

		protected JSONObject doInBackground(String... params) {
			JSONObject json = null;
			try {
				json = ForumHome.getNewForum(params[0]);
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return json;
		}

		protected void onPostExecute(JSONObject result) {
			dismissProgressDialog();
			newListView.setVisibility(View.VISIBLE);
			if (result != null) {
				newState = 1;
				try {
					mHomeItemList = new ArrayList<HomeItem>();
					JSONArray jsonArray = null;
					jsonArray = result.getJSONArray("list");

					if (jsonArray.length() == 0) {
						Toast.makeText(ForumMainActivity.this,
								R.string.no_posts, Toast.LENGTH_SHORT).show();
					} else {
						for (int i = 0; i < jsonArray.length(); i++) {
							HomeItem mHomeItem = new HomeItem(
									jsonArray.getJSONObject(i), forumType.POSTS);
							mHomeItemList.add(mHomeItem);
						}
						mPostsListAdapter = new PostsListAdapter(
								ForumMainActivity.this, mHomeItemList,
								ForumMainActivity.this, headImgState);
						newListView.setAdapter(mPostsListAdapter);

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

	public class LoginTask extends AsyncTask<String, String, JSONObject> {

		protected void onPreExecute() {
		}

		@Override
		protected JSONObject doInBackground(String... arg) {
			JSONObject json = null;
			try {
				json = ForumHome.LoginForum(arg[0], arg[1]);
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return json;
		}

		protected void onPostExecute(JSONObject result) {
			if (result != null) {
				try {
					int Error = Integer.parseInt(Base64.decodeString(result
							.getString("error")));
					switch (Error) {
					case 0:
						int userState = Integer.valueOf(Base64
								.decodeString(result.getString("userstate")));
						Forum.setUserId(Base64.decodeString(result
								.getString("uid")));
						Forum.setUserState(userState);
						switch (Integer.valueOf(userState)) {
						case 2:
							Toast.makeText(getApplicationContext(),
									R.string.no_speak, Toast.LENGTH_SHORT)
									.show();
							break;
						case 3:
							Toast.makeText(getApplicationContext(),
									R.string.no_visit, Toast.LENGTH_SHORT)
									.show();
							break;
						case 4:
							Toast.makeText(getApplicationContext(),
									R.string.locking, Toast.LENGTH_SHORT)
									.show();
							break;
						default:
							break;
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static class TreeViewAdapter extends BaseExpandableListAdapter {
		public static final int ItemHeight = 48;// 每项的高度
		public static final int PaddingLeft = 36;
		TreeViewAdapter mTreeViewAdapter;

		public static class TreeNode {
			Object parent;
			List<Object> childs = new ArrayList<Object>();
			List<Object> fid = new ArrayList<Object>();
			// List<Object> counts = new ArrayList<Object>();
			List<Object> childJsonArray = new ArrayList<Object>();
		}

		List<TreeNode> treeNodes = new ArrayList<TreeNode>();
		Context parentContext;

		public TreeViewAdapter(Context view) {
			parentContext = view;
		}

		public List<TreeNode> GetTreeNode() {
			return treeNodes;
		}

		public void UpdateTreeNode(List<TreeNode> nodes) {
			treeNodes = nodes;
		}

		public void RemoveAll() {
			treeNodes.clear();
		}

		public Object getChild(int groupPosition, int childPosition) {
			return treeNodes.get(groupPosition).childs.get(childPosition);
		}

		public int getChildrenCount(int groupPosition) {
			return treeNodes.get(groupPosition).childs.size();
		}

		public static TextView getTextView(Context context) {
			AbsListView.LayoutParams lp = new AbsListView.LayoutParams(
					ViewGroup.LayoutParams.FILL_PARENT, ItemHeight);

			TextView textView = new TextView(context);
			textView.setLayoutParams(lp);
			textView.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
			textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
			return textView;
		}

		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			View view = convertView;
			if (view == null) {
				view = inflater.inflate(R.layout.member_listview, null);
			}

			TextView title = (TextView) view.findViewById(R.id.content_001);
			title.setText(getGroup(groupPosition).toString());

			ImageView image = (ImageView) view.findViewById(R.id.tubiao);
			if (isExpanded) {
				image.setBackgroundResource(R.drawable.btn_browser2);
			} else {
				image.setBackgroundResource(R.drawable.btn_browser);

			}

			return view;
		}

		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			View view = convertView;
			if (view == null) {
				view = inflater.inflate(R.layout.member_childitem, null);
			}
			final TextView title = (TextView) view
					.findViewById(R.id.child_text);
			title.setText(getChild(groupPosition, childPosition).toString());

			title.setBackgroundResource(R.drawable.child_bg_selector);
		
			return view;
		}

		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		public Object getGroup(int groupPosition) {
			return treeNodes.get(groupPosition).parent;
		}

		public int getGroupCount() {
			return treeNodes.size();
		}

		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}

		public boolean hasStableIds() {
			return true;
		}
	}
}