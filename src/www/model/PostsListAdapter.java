package www.model;

import java.io.IOException;
import java.util.List;

import www.data.Enum.forumType;
import www.data.Forum;
import www.ui.BaseActivity;
import www.ui.HomeListItem;
import www.ui.ReplyActivity;
import www.utils.Base64;
import www.utils.ImageCache;
import www.utils.ImageUtil;
import www.utils.Utils;
import www.wealk.com.R;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class PostsListAdapter extends BaseAdapter {

	public static forumType type;
	private Context mContext;
	private List<HomeItem> mHomeItemList;
	private Intent intent = new Intent();
	private int closed;
	private BaseActivity mActivity;
	private Bitmap head_img;
	private int headImgState;
	private String imgurl;

	public PostsListAdapter(Context context, List<HomeItem> ItemList,
			BaseActivity activity, int headState) {
		mContext = context;
		mHomeItemList = ItemList;
		headImgState = headState;
		mActivity = activity;
		head_img = BitmapFactory.decodeResource(mActivity.getResources(),
				R.drawable.head);
	}

	public int getCount() {
		// TODO Auto-generated method stub
		return mHomeItemList.size();
	}

	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {

		HomeListItem myView = null;
		ViewHolder mHolder = null;
		if (convertView == null) {

			myView = new HomeListItem(mContext, forumType.POSTS);
			mHolder = new ViewHolder();
			mHolder.user_img = (ImageView) myView
					.findViewById(R.id.posts_user_img);
			mHolder.lock_img = (ImageView) myView.findViewById(R.id.posts_lock);
			mHolder.posts_title = (TextView) myView
					.findViewById(R.id.posts_text);
			mHolder.user_name = (TextView) myView
					.findViewById(R.id.posts_user_name);
			mHolder.read_num = (TextView) myView
					.findViewById(R.id.posts_read_num);
			mHolder.reply_num = (TextView) myView
					.findViewById(R.id.posts_reply_num);
			myView.setTag(mHolder);
		} else {
			myView = (HomeListItem) convertView;
			mHolder = (ViewHolder) myView.getTag();
		}

		try {

			imgurl = Base64.decodeString(mHomeItemList.get(position).imgurl);
			mHolder.posts_title.setText(Base64.decodeString(mHomeItemList.get(position).subject));
			mHolder.user_name.setText(Base64.decodeString(mHomeItemList.get(position).author));
			mHolder.read_num.setText(Base64.decodeString(mHomeItemList.get(position).views));
			mHolder.reply_num.setText(Base64.decodeString(mHomeItemList.get(position).replies));
			closed = Integer.parseInt(Base64.decodeString(mHomeItemList.get(position).closed));
		} catch (RuntimeException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		if (headImgState == 1) {
			Bitmap bmp = null;
			Bitmap bmp1 = null;
			bmp1 = ImageCache
					.load(myView, imgurl, position, R.id.posts_user_img);

			if (bmp1 == null) {
				bmp1 = head_img;
			}
			bmp = ImageUtil.getRoundedCornerBitmap(bmp1, 10);
			myView.setImage(position, R.id.posts_user_img, bmp);
		} else {
			mHolder.user_img.setVisibility(View.GONE);
		}
		
		if (closed == 0) {
			mHolder.lock_img.setVisibility(View.GONE);
		} else if (closed == 1) {
			mHolder.lock_img.setVisibility(View.VISIBLE);
		} else if (closed == 2) {
			mHolder.lock_img.setImageResource(R.drawable.shield);
			mHolder.posts_title.setText(R.string.no_subject);
		}
		myView.setOnClickListener(new View.OnClickListener() {
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
							if (Integer.parseInt(Base64.decodeString(mHomeItemList.get(position).closed)) != 2) {
								Forum.setPostsId(Base64.decodeString(String.valueOf(mHomeItemList.get(position).tid)));
								intent.setClass(mContext, ReplyActivity.class);
								Bundle bundle = new Bundle();
								bundle.putString("name",Base64.decodeString(mHomeItemList.get(position).subject));
								bundle.putString("closed",Base64.decodeString(mHomeItemList.get(position).closed));
								intent.putExtras(bundle);
								mContext.startActivity(intent);
							} else {
								Toast.makeText(mContext, R.string.no_subject,
										Toast.LENGTH_SHORT).show();
							}
						}
					}else{
						if (Integer.parseInt(Base64.decodeString(mHomeItemList.get(position).closed)) != 2) {
							Forum.setPostsId(Base64.decodeString(String.valueOf(mHomeItemList.get(position).tid)));
							intent.setClass(mContext, ReplyActivity.class);
							Bundle bundle = new Bundle();
							bundle.putString("name",Base64.decodeString(mHomeItemList.get(position).subject));
							bundle.putString("closed",Base64.decodeString(mHomeItemList.get(position).closed));
							intent.putExtras(bundle);
							mContext.startActivity(intent);
						} else {
							Toast.makeText(mContext, R.string.no_subject,
									Toast.LENGTH_SHORT).show();
						}
					}			
				} catch (RuntimeException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		return myView;
	}

	public static class ViewHolder {
		public TextView reply_num;
		public TextView read_num;
		public ImageView user_img;
		public ImageView lock_img;
		public TextView user_name;
		public TextView posts_title;
	}
}