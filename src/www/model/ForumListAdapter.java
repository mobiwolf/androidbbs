package www.model;

import java.io.IOException;
import java.util.List;

import www.data.Enum.forumType;
import www.data.Forum;
import www.ui.HomeListItem;
import www.ui.PostsActivity;
import www.utils.Base64;
import www.utils.Utils;
import www.wealk.com.R;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ForumListAdapter extends BaseAdapter {

	private Context mContext;
	private List<HomeItem> mHomeItemList;
	private Intent intent = new Intent();
	private PostsActivity mActivity;

	public ForumListAdapter(Context context, List<HomeItem> ItemList,
			PostsActivity activity) {
		mContext = context;
		mHomeItemList = ItemList;
		mActivity = activity;
	}

	public int getCount() {
		return mHomeItemList.size();
	}

	public Object getItem(int position) {
		return null;
	}

	public long getItemId(int position) {
		return 0;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {

		HomeListItem myView = null;
		ViewHolder mHolder = null;
		if (convertView == null) {
			myView = new HomeListItem(mContext, forumType.MASTER);
			mHolder = new ViewHolder();
			mHolder.itemText = (TextView) myView.findViewById(R.id.child_text);
			myView.setTag(mHolder);
		} else {

			myView = (HomeListItem) convertView;
			mHolder = (ViewHolder) myView.getTag();
		}

		try {
			mHolder.itemText.setText(Base64.decodeString(String
					.valueOf(mHomeItemList.get(position).name)));
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		mHolder.itemText.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				try {
					if (!Utils.isEmpty(Forum.getUserId())) {
						if (Forum.getUserState() == 3) {
							Toast.makeText(mActivity, R.string.no_visit,
									Toast.LENGTH_SHORT).show();
						} else if (Forum.getUserState() == 4) {
							Toast.makeText(mActivity, R.string.locking,
									Toast.LENGTH_SHORT).show();
						} else {
							Forum.setFroumId(Base64.decodeString(String
									.valueOf(mHomeItemList.get(position).forumid)));
							intent.setClass(mActivity, PostsActivity.class);

							Bundle bundle = new Bundle();
							bundle.putString("fid",
									Base64.decodeString(String
											.valueOf(mHomeItemList
													.get(position).forumid)));
							bundle.putString("fname", Base64
									.decodeString(String.valueOf(mHomeItemList
											.get(position).name)));
							bundle.putString("childJsonArray", String
									.valueOf(mHomeItemList.get(position).child));
							intent.putExtras(bundle);

							mActivity.startActivity(intent);
						}
					} else {
						Forum.setFroumId(Base64.decodeString(String
								.valueOf(mHomeItemList.get(position).forumid)));
						intent.setClass(mActivity, PostsActivity.class);

						Bundle bundle = new Bundle();
						bundle.putString("fid", Base64.decodeString(String
								.valueOf(mHomeItemList.get(position).forumid)));
						bundle.putString("fname", Base64.decodeString(String
								.valueOf(mHomeItemList.get(position).name)));
						bundle.putString("childJsonArray", String
								.valueOf(mHomeItemList.get(position).child));
						intent.putExtras(bundle);

						mActivity.startActivity(intent);
					}
				} catch (RuntimeException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		return myView;
	}

	public static class ViewHolder {

		public ListView listView;
		public TextView itemText;
	}
}