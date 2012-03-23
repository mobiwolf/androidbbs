package www.ui;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import www.data.Forum;
import www.logic.ForumHome;
import www.utils.Base64;
import www.utils.ExitApplication;
import www.utils.JSONHelper;
import www.utils.Utils;
import www.wealk.com.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

public class SendPostsActivity extends Activity {
	private SendPostsTask mSendPostsTask;
	private ReplyPostsTask mReplyPostsTask;
	private TextView sendposts_error_text;
	private TextView send_posts_title;
	private FileInputStream stream;
	private Bundle bundle;
	private int sendState;
	private File photoFile = new File("/sdcard/forum");
	private static EditText sendMessage;
	private GridView titleGridView;
	private PopupWindow popup;
	private ViewFlipper mViewFlipper;
	private GridView mGridView;
	private TextView title1, title2, title3;
	private int titleIndex;
	private String[] menu_name_array = { "默认", "呆呆男", "酷猴" };
	private LinearLayout mLayout;
	protected Context mContext;
	private String aid;
	private String[] defaultName = { ":)", ":(", ":D", ":\'(", ":@", ":o",
			":P", ":$", ";P", ":L", ":Q", ":lol", ":loveliness:", ":funk:",
			":curse:", ":dizzy:", ":shutup:", ":sleepy:", ":hug:", ":victory:",
			":time:", ":kiss:", ":handshake", ":call:" };
	private String[] monkeyName = { "{:2_25:}", "{:2_26:}", "{:2_27:}",
			"{:2_28:}", "{:2_29:}", "{:2_30:}", "{:2_31:}", "{:2_32:}",
			"{:2_33:}", "{:2_34:}", "{:2_35:}", "{:2_36:}", "{:2_37:}",
			"{:2_38:}", "{:2_39:}", "{:2_40:}", "", "", "" };
	private String[] grapemanName = { "{:3_41:}", "{:3_42:}", "{:3_43:}",
			"{:3_44:}", "{:3_45:}", "{:3_46:}", "{:3_47:}", "{:3_48:}",
			"{:3_49:}", "{:3_50:}", "{:3_51:}", "{:3_52:}", "{:3_53:}",
			"{:3_54:}", "{:3_55:}", "{:3_56:}", "{:3_57:}", "{:3_58:}",
			"{:3_59:}", "{:3_60:}", "{:3_61:}", "{:3_62:}", "{:3_63:}",
			"{:3_64:}" };
	private int[] defaultId = { R.drawable.smile, R.drawable.sad,
			R.drawable.biggrin, R.drawable.cry, R.drawable.huffy,
			R.drawable.shocked, R.drawable.tongue, R.drawable.shy,
			R.drawable.titter, R.drawable.sweat, R.drawable.mad,
			R.drawable.lol, R.drawable.loveliness, R.drawable.funk,
			R.drawable.curse, R.drawable.dizzy, R.drawable.shutup,
			R.drawable.sleepy, R.drawable.hug, R.drawable.victory,
			R.drawable.time, R.drawable.kiss, R.drawable.handshake,
			R.drawable.call, };
	private int[] monkeyId = { R.drawable.monkey01, R.drawable.monkey02,
			R.drawable.monkey03, R.drawable.monkey04, R.drawable.monkey05,
			R.drawable.monkey06, R.drawable.monkey07, R.drawable.monkey08,
			R.drawable.monkey09, R.drawable.monkey10, R.drawable.monkey11,
			R.drawable.monkey12, R.drawable.monkey13, R.drawable.monkey14,
			R.drawable.monkey15, R.drawable.monkey16, 0, 0, 0 };
	private int[] grapemanId = { R.drawable.grapeman01, R.drawable.grapeman02,
			R.drawable.grapeman03, R.drawable.grapeman04,
			R.drawable.grapeman05, R.drawable.grapeman06,
			R.drawable.grapeman07, R.drawable.grapeman08,
			R.drawable.grapeman09, R.drawable.grapeman10,
			R.drawable.grapeman11, R.drawable.grapeman12,
			R.drawable.grapeman13, R.drawable.grapeman14,
			R.drawable.grapeman15, R.drawable.grapeman16,
			R.drawable.grapeman17, R.drawable.grapeman18,
			R.drawable.grapeman19, R.drawable.grapeman20,
			R.drawable.grapeman21, R.drawable.grapeman22,
			R.drawable.grapeman23, R.drawable.grapeman24, };
	protected int tt;
	private ImageView sendBitmap;
	private TextView sendBtn;
	protected Bitmap bmp;
	private String photoUrl;
	private String photoString;
	protected UploadTask mUploadTask;
	private ProgressDialog progressDialog;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.sendposts);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title);

		ExitApplication.getInstance().addActivity(this);

		Button backBtn = (Button) findViewById(R.id.back_imgbtn);
		backBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});

		initPopupMenu();
		sendposts_error_text = (TextView) findViewById(R.id.sendposts_error_text);
		send_posts_title = (TextView) findViewById(R.id.send_posts_title);

		final EditText sendTitle = (EditText) findViewById(R.id.send_title);
		sendMessage = (EditText) findViewById(R.id.send_message);
		sendBitmap = (ImageView) findViewById(R.id.send_bitmap);
		sendBtn = (TextView) findViewById(R.id.send_bitmap_btn);

		sendBitmap.setVisibility(View.GONE);
		sendBtn.setVisibility(View.GONE);

		sendBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				if (mUploadTask != null
						&& mUploadTask.getStatus() == AsyncTask.Status.RUNNING) {
					mUploadTask.cancel(true);
				} else {
					mUploadTask = new UploadTask();
					mUploadTask.execute(photoString, Forum.getUserId(),
							Forum.getForumId());
				}
				
			}
		});

		bundle = this.getIntent().getExtras();
		sendState = bundle.getInt("sendState");
		if (sendState == 1) {
			send_posts_title.setVisibility(View.GONE);
			sendTitle.setVisibility(View.GONE);
		}

		ImageButton camera_btn = (ImageButton) findViewById(R.id.camera_btn);
		camera_btn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				doPickPhotoAction();
			}
		});

		ImageButton face_btn = (ImageButton) findViewById(R.id.face_btn);
		face_btn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				if (popup != null) {
					if (popup.isShowing())
						popup.dismiss();
					else {
						popup.showAtLocation(findViewById(R.id.face_btn),
								Gravity.BOTTOM, 0, 0);
						mViewFlipper.startFlipping();
					}
				}
			}
		});

		Button send_btn = (Button) findViewById(R.id.send_btn);
		send_btn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

				String title = sendTitle.getText().toString().trim();
				String message = sendMessage.getText().toString().trim();

				switch (sendState) {
				case 0:
					int tilteLength = title.getBytes().length;
					int msgLength = message.getBytes().length;
					if (!Utils.isEmpty(title) && !Utils.isEmpty(message)) {
						if (tilteLength <= 100) {
							if (msgLength > 9) {
								if (mSendPostsTask != null
										&& mSendPostsTask.getStatus() == AsyncTask.Status.RUNNING) {
									mSendPostsTask.cancel(true);
								} else {
									mSendPostsTask = new SendPostsTask();
									mSendPostsTask.execute(Forum.getForumId(),
											Forum.getUserId(), title, message,
											aid);
								}
							} else {
								sendposts_error_text
										.setText(R.string.textshort);
							}
						} else {
							sendposts_error_text.setText(R.string.titleshort);
						}
					} else {
						sendposts_error_text.setText(R.string.empty);
					}
					break;
				case 1:
					int replyMsgLength = message.getBytes().length;
					if (!Utils.isEmpty(message)) {
						if (replyMsgLength > 9) {
							if (mReplyPostsTask != null
									&& mReplyPostsTask.getStatus() == AsyncTask.Status.RUNNING) {
								mReplyPostsTask.cancel(true);
							} else {
								mReplyPostsTask = new ReplyPostsTask();
								mReplyPostsTask.execute(Forum.getForumId(),
										Forum.getPostsId(), Forum.getPId(),
										Forum.getUserId(), message, aid);
							}
						} else {
							sendposts_error_text.setText(R.string.textshort);
						}
					} else {
						sendposts_error_text.setText(R.string.empty);
					}
					break;
				default:
					break;
				}
			}
		});
	}

	protected void onRestoreInstanceState(Bundle savedInstanceState) {

		aid = savedInstanceState.getString("aid");
		Forum.setFroumId(savedInstanceState.getString("fid"));
		Forum.setName(savedInstanceState.getString("name"));
		Forum.setPId(savedInstanceState.getString("pid"));
		Forum.setPostsId(savedInstanceState.getString("postid"));
		Forum.setUrl(savedInstanceState.getString("url"));
		Forum.setUserId(savedInstanceState.getString("userid"));
		Forum.setUserName(savedInstanceState.getString("username"));
		Forum.setUserState(savedInstanceState.getInt("userState"));
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {

		outState.putString("aid", aid);
		outState.putString("username", Forum.getUserName());
		outState.putString("userid", Forum.getUserId());
		outState.putString("url", Forum.getUrl());
		outState.putString("postid", Forum.getPostsId());
		outState.putString("pid", Forum.getPId());
		outState.putString("name", Forum.getName());
		outState.putString("fid", Forum.getForumId());
		outState.putInt("userState", Forum.getUserState());
		super.onSaveInstanceState(outState);
	}

	protected void onResume() {
		TextView user_message = (TextView) findViewById(R.id.main_user);
		if (Forum.getUserId() == null) {
			user_message.setText(R.string.no_login);
		} else {
			user_message.setText(Forum.getUserName());
		}
		super.onResume();
	}

	protected void doPickPhotoAction() {
		Context context = SendPostsActivity.this;

		final Context dialogContext = new ContextThemeWrapper(context,
				android.R.style.Theme_Light);
		String[] choices;
		choices = new String[2];
		choices[0] = getString(R.string.take_photo); // 拍照
		choices[1] = getString(R.string.pick_photo); // 从相册中选择
		final ListAdapter adapter = new ArrayAdapter<String>(dialogContext,
				android.R.layout.simple_list_item_1, choices);

		final AlertDialog.Builder builder = new AlertDialog.Builder(
				dialogContext);
		builder.setTitle(R.string.picture);
		builder.setSingleChoiceItems(adapter, -1,
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						switch (which) {
						case 0: {
							String status = Environment
									.getExternalStorageState();
							if (status.equals(Environment.MEDIA_MOUNTED)) {// 判断是否有SD卡
								if (!photoFile.exists()) {
									photoFile.mkdirs();// 创建照片的存储目录
								}
								Intent intent = new Intent(
										"android.media.action.IMAGE_CAPTURE");
								intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri
										.fromFile(new File(Environment
												.getExternalStorageDirectory()
												+ "/forum/", "forum.jpg")));
								intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY,
										0);
								startActivityForResult(intent, 0);
							} else {
								Toast.makeText(getApplicationContext(),
										"sd卡没插好", Toast.LENGTH_SHORT);
							}
							break;
						}
						case 1:
							doPickPhotoFromGallery();// 从相册中去获取
							break;
						}
					}
				});
		builder.setNegativeButton(R.string.back,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}

				});
		builder.create().show();
	}

	protected void doPickPhotoFromGallery() {
		try {
			Intent intent = new Intent();
			intent.setType("image/*");
			intent.setAction(Intent.ACTION_GET_CONTENT);
			startActivityForResult(
					Intent.createChooser(intent, "Select Picture"), 1);
		} catch (ActivityNotFoundException e) {
			Toast.makeText(this, R.string.no_msg, Toast.LENGTH_LONG).show();
		}
	}

	public static Intent getTakePickIntent(File f) {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE, null);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
		return intent;
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		ExitApplication.getInstance().addActivity(this);
		if (resultCode != Activity.RESULT_OK) {
			return;
		}

		switch (requestCode) {
		case 0:
			String cameraFilePath = Environment.getExternalStorageDirectory()
					+ "/forum/forum.jpg";
			decodeFile(cameraFilePath);
			break;
		case 1:
			Uri selectedImageUri = data.getData();
			String filePath = null;

			try {
				// OI FILE Manager
				String filemanagerstring = selectedImageUri.getPath();

				// MEDIA GALLERY
				String selectedImagePath = getPath(selectedImageUri);

				if (selectedImagePath != null) {
					filePath = selectedImagePath;
				} else if (filemanagerstring != null) {
					filePath = filemanagerstring;
				} else {
					Toast.makeText(getApplicationContext(), "Unknown path",
							Toast.LENGTH_SHORT).show();
				}
				decodeFile(filePath);
			} catch (Exception e) {
				Toast.makeText(getApplicationContext(), "Internal error",
						Toast.LENGTH_SHORT).show();
			}

			break;
		default:
			break;
		}
	}

	public String getPath(Uri uri) {
		String[] projection = { MediaStore.Images.Media.DATA };
		Cursor cursor = managedQuery(uri, projection, null, null, null);
		if (cursor != null) {
			int column_index = cursor
					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			String pathString = cursor.getString(column_index);
			cursor.close();
			return pathString;
		} else
			return null;
	}

	public void decodeFile(String filePath) {

		if (!photoFile.exists()) {
			photoFile.mkdirs();// 创建照片的存储目录
		}
		BitmapFactory.Options opts = new BitmapFactory.Options();

		opts.inJustDecodeBounds = true;
		// 获取这个图片的宽和高
		Bitmap bitmap = BitmapFactory.decodeFile(filePath, opts); // 此时返回bm为空
		opts.inJustDecodeBounds = false;
		// 计算缩放比
		int be;
		if (opts.outHeight > opts.outWidth) {
			be = (int) (opts.outHeight / 200);
		} else {
			be = (int) (opts.outWidth / 200);
		}
		if (be <= 0)
			be = 1;
		opts.inSampleSize = be;
		// opts.inSampleSize = ImageCache.computeSampleSize(opts, -1, 128*128);
		// 重新读入图片，注意这次要把options.inJustDecodeBounds 设为 false哦
		bitmap = BitmapFactory.decodeFile(filePath, opts);
		String fileName = "/sdcard/forum/forum.jpg";
		File file = new File(fileName);
		try {
			FileOutputStream out = new FileOutputStream(file);
			if (bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)) {
				out.flush();
				out.close();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		opts.inSampleSize = 1;
		try {
			stream = new FileInputStream(new File(fileName));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Bitmap bitmap2 = BitmapFactory.decodeStream(stream, null, opts);
		doFileUpload(bitmap2);
	}

	private void doFileUpload(Bitmap bitmapOrg) {
		sendBitmap.setVisibility(View.VISIBLE);
		sendBtn.setVisibility(View.VISIBLE);
		sendBitmap.setImageBitmap(bitmapOrg);
		sendBtn.setText(R.string.upload);
		bmp = bitmapOrg;
	
		ByteArrayOutputStream bao = new ByteArrayOutputStream();
		bitmapOrg.compress(Bitmap.CompressFormat.JPEG, 100, bao);
		byte[] ba = bao.toByteArray();
		photoString = Base64.encodeBytes(ba);
	}

	private void showEditText(Bitmap bitmap2, final String imgname) {

		// 根据Bitmap对象创建ImageSpan对象
		ImageSpan imageSpan = new ImageSpan(this, bitmap2);
		// 创建一个SpannableString对象，以便插入用ImageSpan对象封装的图像
		SpannableString spannableString = new SpannableString(imgname);
		// 用ImageSpan对象替换String
		spannableString.setSpan(imageSpan, 0, imgname.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		sendMessage.append(spannableString);

		sendMessage.requestFocus();
		sendMessage.setSelection(sendMessage.getText().toString().length());

	}

	public void showLoadingProgressDialog() {

		progressDialog = new ProgressDialog(this);
		progressDialog.setMessage(getResources().getString(R.string.loadding));
		progressDialog.setIndeterminate(true);
		progressDialog.setCancelable(true);
		// progressDialog.setIndeterminateDrawable(progress_drawable);
		Window window = progressDialog.getWindow();
		WindowManager.LayoutParams lp = window.getAttributes();
		lp.alpha = 0.8f;// 透明度
		lp.dimAmount = 0.0f;// 黑暗度
		window.setAttributes(lp);
		progressDialog.show();

	}

	public void dismissProgressDialog() {
		progressDialog.dismiss();
	}

	public class UploadTask extends AsyncTask<String, String, JSONObject> {

		protected void onPreExecute() {
			showLoadingProgressDialog();
		}

		protected JSONObject doInBackground(String... params) {
			JSONObject json = null;
			try {
				InputStream is;
				String url = Forum.getUrl()
						+ "?id=wealk_bbs_mobile_dzx20:uploadimg";
				ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("image", params[0]));
				nameValuePairs.add(new BasicNameValuePair("uid", Base64
						.encodeString(params[1])));
				nameValuePairs.add(new BasicNameValuePair("fid", Base64
						.encodeString(params[2])));

				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(url);

				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				HttpResponse response = httpclient.execute(httppost);
				HttpEntity entity = response.getEntity();
				is = entity.getContent();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(is, "UTF-8"));
				String sResponse = reader.readLine();
				json = JSONHelper.str2json(sResponse);
			} catch (Exception e) {
				e.printStackTrace();
			} catch (OutOfMemoryError e) {
				e.printStackTrace();
			}
			return json;
		}

		protected void onPostExecute(JSONObject result) {
			dismissProgressDialog();
			if (result != null) {
				try {
					String resultString = String.valueOf(result);
					if (resultString.substring(2, 10).equals("attachID")) {
						sendBtn.setText(R.string.delete);
		
						String photoid = Base64.decodeString(result
								.getString("attachID"));
						if(Utils.isEmpty(aid)){
							aid = photoid;
						}else{
							aid = aid+","+photoid;
						}
						photoUrl = "[attach]" + photoid + "[/attach]";
						Toast.makeText(getApplicationContext(),
								R.string.upload_sucess, Toast.LENGTH_SHORT)
								.show();
						showEditText(bmp,photoUrl);
						sendBitmap.setVisibility(View.GONE);
						sendBtn.setVisibility(View.GONE);
						sendBitmap.setImageBitmap(null);
					} else {
						Toast.makeText(getApplicationContext(),
								R.string.upload_fail, Toast.LENGTH_SHORT)
								.show();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				sendposts_error_text.setText(R.string.network_error);
			}
		}
	}

	public class SendPostsTask extends AsyncTask<String, String, JSONObject> {

		protected void onPreExecute() {
			showLoadingProgressDialog();
		}

		protected JSONObject doInBackground(String... params) {
			JSONObject json = null;
			try {
				json = ForumHome.SendPosts(params[0], params[1], params[2],
						params[3], params[4]);
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
					int Error = Integer.parseInt(Base64.decodeString(result
							.getString("error")));
					switch (Error) {
					case 0:
						sendposts_error_text.setText("");
						finish();
						break;
					case 1:
						sendposts_error_text.setText(R.string.format_error);
						break;
					case 2:
						sendposts_error_text.setText(R.string.fid_error);
						break;
					case 3:
						sendposts_error_text.setText(R.string.title_error);
						break;
					case 4:
						sendposts_error_text.setText(R.string.content_error);
						break;
					case 5:
						sendposts_error_text.setText(R.string.no_login);
						break;
					default:
						break;
					}
				} catch (JSONException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				sendposts_error_text.setText(R.string.network_error);
			}
		}
	}

	public class ReplyPostsTask extends AsyncTask<String, String, JSONObject> {
		protected void onPreExecute() {
			showLoadingProgressDialog();
		}

		protected JSONObject doInBackground(String... params) {
			JSONObject json = null;
			try {
				json = ForumHome.ReplyPosts(params[0], params[1], params[2],
						params[3], params[4], params[5]);
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return json;
		}

		protected void onPostExecute(JSONObject result) {
			dismissProgressDialog();
			try {
				if (result != null) {
					int Error = Integer.parseInt(Base64.decodeString(result
							.getString("error")));
					switch (Error) {
					case 0:
						finish();
						break;
					case 1:
						sendposts_error_text.setText(R.string.format_error);
						break;
					case 2:
						sendposts_error_text.setText(R.string.fid_error);
						break;
					case 3:
						sendposts_error_text.setText(R.string.tid_error);
						break;
					case 4:
						sendposts_error_text.setText(R.string.content_error);
						break;
					case 5:
						sendposts_error_text.setText(R.string.no_login);
						break;
					default:
						break;
					}
				} else {
					sendposts_error_text.setText(R.string.network_error);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void initPopupMenu() {

		mViewFlipper = new ViewFlipper(this);
		// mViewFlipper.setInAnimation(AnimationUtils.loadAnimation(this,
		// R.anim.menu_in));
		// mViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(this,
		// R.anim.menu_out));
		mLayout = new LinearLayout(SendPostsActivity.this);
		mLayout.setOrientation(LinearLayout.VERTICAL);

		titleGridView = new GridView(SendPostsActivity.this);
		titleGridView.setLayoutParams(new LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

		titleGridView.setNumColumns(3);
		titleGridView.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
		titleGridView.setVerticalSpacing(1);
		titleGridView.setHorizontalSpacing(1);
		titleGridView.setGravity(Gravity.CENTER);
		MenuTitleAdapter mta = new MenuTitleAdapter(this, menu_name_array, 0,
				0xffffffff);
		titleGridView.setAdapter(mta);
		titleGridView.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				onChangeItem(arg1, arg2);
			}

			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});
		titleGridView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				onChangeItem(arg1, arg2);
			}
		});

		mGridView = new GridView(SendPostsActivity.this);
		mGridView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT));
		mGridView.setNumColumns(6);
		mGridView.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
		mGridView.setGravity(Gravity.CENTER);
		mGridView.setAdapter(getMenuAdapter(defaultId));
		mGridView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Bitmap bmp = null;
				String imgName = null;
				switch (titleIndex) {
				case 0:
					bmp = BitmapFactory.decodeResource(getResources(),
							defaultId[arg2]);
					imgName = defaultName[arg2];
					break;
				case 1:
					bmp = BitmapFactory.decodeResource(getResources(),
							grapemanId[arg2]);
					imgName = grapemanName[arg2];
					break;
				case 2:
					bmp = BitmapFactory.decodeResource(getResources(),
							monkeyId[arg2]);
					imgName = monkeyName[arg2];
					break;
				}
				showEditText(bmp, imgName);
				popup.dismiss();
			}
		});

		mLayout.addView(titleGridView);
		mLayout.addView(mGridView);
		mViewFlipper.addView(mLayout);
		mViewFlipper.setFlipInterval(60000);

		popup = new PopupWindow(mViewFlipper, LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT);
		popup.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.tab_bg_unselected));
		popup.setFocusable(true);

		popup.update();

		title1 = (TextView) titleGridView.getItemAtPosition(0);
		title1.setBackgroundColor(0x00);
	}

	private SimpleAdapter getMenuAdapter(int[] imageResourceArray) {
		ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < imageResourceArray.length; i++) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("itemImage", imageResourceArray[i]);
			data.add(map);
		}
		SimpleAdapter simperAdapter = new SimpleAdapter(this, data,
				R.layout.grid_item, new String[] { "itemImage" },
				new int[] { R.id.itemIcon });
		return simperAdapter;
	}

	private void onChangeItem(View arg1, int arg2) {
		titleIndex = arg2;
		switch (titleIndex) {
		case 0:
			title1 = (TextView) arg1;
			title1.setBackgroundColor(0x00);
			if (title2 != null)
				title2.setBackgroundResource(R.drawable.tab_bg_unselected);
			if (title3 != null)
				title3.setBackgroundResource(R.drawable.tab_bg_unselected);
			mGridView.setAdapter(getMenuAdapter(defaultId));
			break;
		case 1:
			title2 = (TextView) arg1;
			title2.setBackgroundColor(0x00);
			if (title1 != null)
				title1.setBackgroundResource(R.drawable.tab_bg_unselected);
			if (title3 != null)
				title3.setBackgroundResource(R.drawable.tab_bg_unselected);
			mGridView.setAdapter(getMenuAdapter(grapemanId));
			break;
		case 2:
			title3 = (TextView) arg1;
			title3.setBackgroundColor(0x00);
			if (title2 != null)
				title2.setBackgroundResource(R.drawable.tab_bg_unselected);
			if (title1 != null)
				title1.setBackgroundResource(R.drawable.tab_bg_unselected);
			mGridView.setAdapter(getMenuAdapter(monkeyId));
			break;
		}
	}

	public class MenuTitleAdapter extends BaseAdapter {

		private Context mContext;
		private int fontColor;
		private TextView[] title;

		public MenuTitleAdapter(Context context, String[] titles, int fontSize,
				int color) {
			this.mContext = context;
			this.fontColor = color;
			this.title = new TextView[titles.length];
			for (int i = 0; i < titles.length; i++) {
				title[i] = new TextView(mContext);
				title[i].setText(titles[i]);
				title[i].setTextSize(fontSize);
				title[i].setTextColor(fontColor);
				title[i].setGravity(Gravity.CENTER);
				title[i].setPadding(10, 10, 10, 10);
				title[i].setBackgroundResource(R.drawable.tab_bg_unselected);
			}
		}

		public int getCount() {

			return title.length;
		}

		public Object getItem(int position) {

			return title[position];
		}

		public long getItemId(int position) {

			return title[position].getId();
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			View v;
			if (convertView == null) {
				v = title[position];
			} else {
				v = convertView;
			}
			return v;
		}

	}
}