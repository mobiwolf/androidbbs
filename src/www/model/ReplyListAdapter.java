package www.model;

import java.io.IOException;
import java.util.List;

import www.data.Enum.forumType;
import www.data.Forum;
import www.image.ProfileImageCacheCallback;
import www.ui.HomeListItem;
import www.ui.LoginActivity;
import www.ui.ReplyActivity;
import www.ui.SendPostsActivity;
import www.utils.Base64;
import www.utils.ImageCache;
import www.utils.ImageUtil;
import www.utils.Utils;
import www.wealk.com.R;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.text.method.LinkMovementMethod;
import android.view.ContextThemeWrapper;
import android.view.Display;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class ReplyListAdapter extends BaseAdapter {

	public static forumType type;
	private Context mContext;
	private List<HomeItem> mHomeItemList;
	private ReplyActivity mActivity;
	private Bitmap head_img;
	private String imgurl;
	private String messageText;
	private String msgText;
	private String dateline;
	private String userName;
	private int numLou;
	private int closed;
	private int state;
	private int android_width;
	private int headImgState;
	private int userState;

	public ReplyListAdapter(Context context, List<HomeItem> ItemList,
			ReplyActivity activity, String closedText, int headState) {
		mContext = context;
		mHomeItemList = ItemList;
		mActivity = activity;
		closed = Integer.parseInt(closedText);
		headImgState = headState;
		WindowManager windowManager = mActivity.getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		android_width = display.getWidth();
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
			myView = new HomeListItem(mContext, forumType.REPLY);
			mHolder = new ViewHolder();
			mHolder.user_img = (ImageView) myView
					.findViewById(R.id.reply_user_img);
			mHolder.user_name = (TextView) myView
					.findViewById(R.id.reply_user_name);
			mHolder.date = (TextView) myView.findViewById(R.id.reply_date);
			mHolder.num = (TextView) myView.findViewById(R.id.reply_num);
			mHolder.msg = (TextView) myView.findViewById(R.id.reply_msg);
			mHolder.message = (TextView) myView.findViewById(R.id.reply_text);
			myView.setTag(mHolder);

		} else {

			myView = (HomeListItem) convertView;
			mHolder = (ViewHolder) myView.getTag();
		}

		try {
			imgurl = Base64.decodeString(mHomeItemList.get(position).imgurl);
			msgText = Base64.decodeString(mHomeItemList.get(position).msg);
			messageText = Base64
					.decodeString(mHomeItemList.get(position).message);
			dateline = Base64
					.decodeString(mHomeItemList.get(position).dateline);
			userName = Base64.decodeString(mHomeItemList.get(position).author);
			numLou = Integer.parseInt(Base64.decodeString(mHomeItemList
					.get(position).lou));
			userState = Integer.parseInt(Base64.decodeString(mHomeItemList
					.get(position).userState));
			state = Integer.parseInt(Base64.decodeString(mHomeItemList
					.get(position).status));
		} catch (RuntimeException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		if (headImgState == 1) {
			Bitmap bmp = null;
			Bitmap bmp1 = null;
			try {
				bmp1 = ImageCache.load(myView, imgurl, position,
						R.id.reply_user_img);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (bmp1 == null) {
				bmp1 = head_img;
			}
			bmp = ImageUtil.getRoundedCornerBitmap(bmp1, 10);
			myView.setImage(position, R.id.reply_user_img, bmp);
		} else {
			mHolder.user_img.setVisibility(View.GONE);
		}

		mHolder.date.setText(dateline);
		mHolder.user_name.setText(userName);
		if(userState == 1){
			if (state == 0) {
				try {
					String startImg = "<img";
					if(messageText.indexOf(startImg, 0) >= 0 && Utils.isEmpty(Forum.getUserId())){ 
						messageText = messageText+"<br/>" + "<font color=\"#ff6600\">图片: 你需要登录才可以查图片</font>";
					}
					mHolder.message.setText(Html.fromHtml(messageText, imgGetter,null));
					mHolder.message.setMovementMethod(LinkMovementMethod.getInstance());
					mHolder.msg.setText(Html.fromHtml(msgText, imgGetter, null));
	
					if (Utils.isEmpty(msgText)) {
						mHolder.msg.setVisibility(View.GONE);
					} else {
						mHolder.msg.setVisibility(View.VISIBLE);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				mHolder.msg.setText(R.string.no_msg);
				mHolder.message.setVisibility(View.GONE);
			}
		}else{
			mHolder.msg.setText(R.string.no_message);
			mHolder.message.setVisibility(View.GONE);
		}

		String lou = mActivity.getResources().getString(R.string.lou);

		if (numLou == 1) {
			mHolder.num.setText(R.string.num1);
		} else if (numLou == 2) {
			mHolder.num.setText(R.string.num2);
		} else if (numLou == 3) {
			mHolder.num.setText(R.string.num3);
		} else if (numLou == 4) {
			mHolder.num.setText(R.string.num4);
		} else {
			mHolder.num.setText(numLou + lou);
		}

		myView.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				final Context dialogContext = new ContextThemeWrapper(mContext,
						android.R.style.Theme_Light);
				String[] choices;
				choices = new String[2];
				choices[0] = mContext.getString(R.string.reply);
				choices[1] = mContext.getString(R.string.share);
				final ListAdapter adapter = new ArrayAdapter<String>(
						dialogContext, android.R.layout.simple_list_item_1,
						choices);

				final AlertDialog.Builder builder = new AlertDialog.Builder(
						dialogContext);
				builder.setTitle(R.string.choose);
				builder.setSingleChoiceItems(adapter, -1,
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								try {
									switch (which) {
									case 0: {
										Forum.setPId(Base64
												.decodeString(mHomeItemList
														.get(position).pid));
										if (!Utils.isEmpty(Forum.getUserId())) {
											if (Forum.getUserState() == 2) {
												Toast.makeText(mActivity,
														R.string.no_speak,
														Toast.LENGTH_SHORT)
														.show();
											} else if (Forum.getUserState() == 3) {
												Toast.makeText(mActivity,
														R.string.no_visit,
														Toast.LENGTH_SHORT)
														.show();
											} else if (Forum.getUserState() == 4) {
												Toast.makeText(mActivity,
														R.string.locking,
														Toast.LENGTH_SHORT)
														.show();
											} else {
												if (closed == 0 &&  Integer
														.parseInt(Base64
																.decodeString(mHomeItemList
																		.get(position).status)) == 0) {
													Intent intent = new Intent();
													Bundle bundle = new Bundle();
													bundle.putInt("sendState",
															1);
													intent.putExtras(bundle);
													intent.setClass(
															mActivity,
															SendPostsActivity.class);
													mActivity
															.startActivity(intent);
												} else {
													Toast.makeText(mContext,
															R.string.no_reply,
															Toast.LENGTH_SHORT)
															.show();
												}
											}
										} else {
											Intent intent = new Intent();
											intent.setClass(mActivity,
													LoginActivity.class);
											mActivity.startActivity(intent);
										}
										break;
									}
									case 1:
										share(Base64.decodeString(mHomeItemList
												.get(position).message));
										break;
									}
								} catch (RuntimeException e) {
									e.printStackTrace();
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						});
				builder.setNegativeButton(R.string.back,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}

						});
				builder.create().show();
				return false;
			}
		});

		mHolder.message.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				final Context dialogContext = new ContextThemeWrapper(mContext,
						android.R.style.Theme_Light);
				String[] choices;
				choices = new String[2];
				choices[0] = mContext.getString(R.string.reply);
				choices[1] = mContext.getString(R.string.share);
				final ListAdapter adapter = new ArrayAdapter<String>(
						dialogContext, android.R.layout.simple_list_item_1,
						choices);

				final AlertDialog.Builder builder = new AlertDialog.Builder(
						dialogContext);
				builder.setTitle(R.string.choose);
				builder.setSingleChoiceItems(adapter, -1,
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
								try {
									switch (which) {
									case 0: {
										Forum.setPId(Base64
												.decodeString(mHomeItemList
														.get(position).pid));
										if (!Utils.isEmpty(Forum.getUserId())) {
											if (Forum.getUserState() == 2) {
												Toast.makeText(mActivity,
														R.string.no_speak,
														Toast.LENGTH_SHORT)
														.show();
											} else if (Forum.getUserState() == 3) {
												Toast.makeText(mActivity,
														R.string.no_visit,
														Toast.LENGTH_SHORT)
														.show();
											} else if (Forum.getUserState() == 4) {
												Toast.makeText(mActivity,
														R.string.locking,
														Toast.LENGTH_SHORT)
														.show();
											} else {
												if (closed == 0 && Integer
														.parseInt(Base64
																.decodeString(mHomeItemList
																		.get(position).status)) == 0) {
													Intent intent = new Intent();
													Bundle bundle = new Bundle();
													bundle.putInt("sendState",
															1);
													intent.putExtras(bundle);
													intent.setClass(
															mActivity,
															SendPostsActivity.class);
													mActivity
															.startActivity(intent);
												} else {
													Toast.makeText(mContext,
															R.string.no_reply,
															Toast.LENGTH_SHORT)
															.show();
												}
											}
										} else {
											Intent intent = new Intent();
											intent.setClass(mActivity,
													LoginActivity.class);
											mActivity.startActivity(intent);
										}
										break;
									}
									case 1:
										share(Base64.decodeString(mHomeItemList
												.get(position).message));
										break;
									}
								} catch (RuntimeException e) {
									e.printStackTrace();
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						});
				builder.setNegativeButton(R.string.back,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}

						});
				builder.create().show();
				return false;
			}
		});

		return myView;
	}

	private void share(String shareText) {
		String forum_name = mActivity.getResources().getString(
				R.string.app_name);
		String share = mActivity.getResources().getString(R.string.share);
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_SUBJECT, share);
		intent.putExtra(Intent.EXTRA_TEXT, shareText + "\n" + "来自于：  "
				+ forum_name + "  android客户端");
		mActivity.startActivity(Intent.createChooser(intent,
				mActivity.getTitle()));
	}

	public static class ViewHolder {

		public TextView msg;
		public TextView message;
		public TextView num;
		public TextView date;
		public TextView user_name;
		public ImageView user_img;
	}

	ImageGetter imgGetter = new Html.ImageGetter() {
		private int width = 0;
		private int height = 0;

		public Drawable getDrawable(String source) {
			Drawable drawable = null;
			Bitmap mBitmap = null;
			try {
				if(!Utils.isEmpty(Forum.getUserId())){
					mBitmap = Forum.mProfileImageCacheManager.get(source, callback);
				}else{
					mBitmap = null;
				}
				// mBitmap = ImageCache.loadUrl(source, mActivity);
				drawable = Utils.bitmapToDrawable(mBitmap);
				if (android_width - 50 < drawable.getIntrinsicWidth()) {
					width = android_width - 50;
					height = drawable.getIntrinsicHeight() * (width)
							/ drawable.getIntrinsicWidth();
				} else {
					width = drawable.getIntrinsicWidth();
					height = drawable.getIntrinsicHeight();
				}
			} catch (Exception e) {
				drawable = null;
			}
			drawable.setBounds(0, 0, width, height);

			return drawable;
		}
	};

	private ProfileImageCacheCallback callback = new ProfileImageCacheCallback() {
		@Override
		public void refresh(String url, Bitmap bitmap) {
			notifyDataSetChanged();
		}
	};

}