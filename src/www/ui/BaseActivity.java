package www.ui;

import org.json.JSONException;
import org.json.JSONObject;

import www.data.Forum;
import www.image.ImageManager;
import www.logic.ForumHome;
import www.utils.ExitApplication;
import www.utils.Utils;
import www.wealk.com.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.LayoutInflater.Factory;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mobclick.android.MobclickAgent;

public class BaseActivity extends Activity {
	
	protected Intent intent = new Intent();
	private LogoutTask mLogoutTask;
	private ProgressDialog progressDialog;
	protected SharedPreferences sharedPreferences;
	private TextView user_message;
	private Dialog set_dialog;
	private String versionCode;
	public int post_num;
	public int reply_num;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);

		String app_url = getResources().getString(R.string.app_url);
		Forum.setUrl(app_url);
		sharedPreferences = getSharedPreferences("userApp",
				Context.MODE_PRIVATE);
		try {
			PackageInfo pinfo = getPackageManager().getPackageInfo(
					this.getPackageName(), PackageManager.GET_CONFIGURATIONS);
			versionCode = pinfo.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		MobclickAgent.onError(this);
	}

	// menu
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = new MenuInflater(getApplicationContext());
		inflater.inflate(R.menu.options_item, menu);
		setMenuBackground();
		return true;
	}

	protected void setMenuBackground() {
		getLayoutInflater().setFactory(new Factory() {
			public View onCreateView(String name, Context context,
					AttributeSet attrs) {
				if (name.equalsIgnoreCase("com.android.internal.view.menu.IconMenuItemView")) {
					try {
						LayoutInflater f = getLayoutInflater();
						final View view = f.createView(name, null, attrs);

						new Handler().post(new Runnable() {
							public void run() {
								view.setBackgroundResource(R.drawable.tab_bg_selector);
								((TextView) view).setTextColor(0xff74c1f4);
							}
						});
						return view;
					} catch (InflateException e) {
					} catch (ClassNotFoundException e) {
					}
				}
				return null;
			}
		});
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_help:
			aboutUs();
			break;
		case R.id.menu_set:
			showSetDialog();
			break;
		case R.id.menu_refresh:
			userRefresh();
			refreshTask();
			break;
		case R.id.menu_login:
			jumpToLogin();
			break;
		case R.id.menu_logout:
			logout();
			break;
		case R.id.menu_exit:
			showExitDialog();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	public void userRefresh() {
		user_message = (TextView) findViewById(R.id.main_user);
		if (Utils.isEmpty(Forum.getUserId())) {
			user_message.setText(R.string.no_login);
		} else {
			user_message.setText(Forum.getUserName());
		}
	}

	protected void initHeader() {
		userRefresh();
		Button backBtn = (Button) findViewById(R.id.back_imgbtn);
		backBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
	}

	// 关于我们
	public void aboutUs() {
		final Dialog help_dialog = new Dialog(this, R.style.dialogStyle);
		help_dialog.setContentView(R.layout.help);
		help_dialog.setCancelable(true);
		help_dialog.show();
		final Button help_back_btn = (Button) help_dialog
				.findViewById(R.id.help_back_btn);
		help_back_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				help_dialog.cancel();
			}
		});
	}

	// 设置
	private void showSetDialog() {

		set_dialog = new Dialog(this, R.style.dialogStyle);
		set_dialog.setContentView(R.layout.set);
		set_dialog.setCancelable(true);
		set_dialog.show();
		final CheckBox check_img = (CheckBox) set_dialog
				.findViewById(R.id.check_img);
		final CheckBox check_headImg = (CheckBox) set_dialog
				.findViewById(R.id.check_headImg);
		final Spinner post_num_spinner = (Spinner) set_dialog
				.findViewById(R.id.post_num_spinner);
		final Spinner reply_num_spinner = (Spinner) set_dialog
				.findViewById(R.id.reply_num_spinner);
		final Button datacacheBtn = (Button) set_dialog
				.findViewById(R.id.datacache_btn);
		final Button set_enter_btn = (Button) set_dialog
				.findViewById(R.id.set_enter_btn);
		final Button set_cancel_btn = (Button) set_dialog
				.findViewById(R.id.set_cancel_btn);
		final TextView version_text = (TextView) set_dialog
				.findViewById(R.id.version_text);
		version_text.setText(versionCode);

		if (sharedPreferences.getInt("imgState", 0) == 0) {
			check_img.setChecked(false);
		} else if (sharedPreferences.getInt("imgState", 0) == 1) {
			check_img.setChecked(true);
		}

		if (sharedPreferences.getInt("headImgState", 0) == 0) {
			check_headImg.setChecked(false);
		} else if (sharedPreferences.getInt("headImgState", 0) == 1) {
			check_headImg.setChecked(true);
		}

		// spiner
		ArrayAdapter<CharSequence> adapter_num = ArrayAdapter
				.createFromResource(this, R.array.num,
						android.R.layout.simple_spinner_item);

		adapter_num
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		post_num_spinner.setAdapter(adapter_num);
		reply_num_spinner.setAdapter(adapter_num);
		if (sharedPreferences.getInt("postNum", 0) == 10) {
			post_num_spinner.setSelection(0);
		} else if (sharedPreferences.getInt("postNum", 0) == 20) {
			post_num_spinner.setSelection(1);
		} else if (sharedPreferences.getInt("postNum", 0) == 50) {
			post_num_spinner.setSelection(2);
		}
		if (sharedPreferences.getInt("replyNum", 0) == 10) {
			reply_num_spinner.setSelection(0);
		} else if (sharedPreferences.getInt("replyNum", 0) == 20) {
			reply_num_spinner.setSelection(1);
		} else if (sharedPreferences.getInt("replyNum", 0) == 50) {
			reply_num_spinner.setSelection(2);
		}

		post_num_spinner
				.setOnItemSelectedListener(new SpinnerPostNumListener());
		reply_num_spinner
				.setOnItemSelectedListener(new SpinnerReplyNumListener());

		datacacheBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				ImageManager.clear();
				sharedPreferences.edit().clear().commit();
				Forum.setUserId("");
				Forum.setUserName("");
				userRefresh();
				Toast.makeText(BaseActivity.this, R.string.delete_sucess,
						Toast.LENGTH_SHORT).show();
			}
		});
		
		set_enter_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Editor editor = sharedPreferences.edit();
				if (check_img.isChecked()) {
					editor.putInt("imgState", 1);
				} else {
					editor.putInt("imgState", 0);
				}

				if (check_headImg.isChecked()) {
					editor.putInt("headImgState", 1);
				} else {
					editor.putInt("headImgState", 0);
				}
				editor.putInt("postNum", post_num);
				editor.putInt("replyNum", reply_num);
				editor.commit();
				set_dialog.cancel();
				refreshTask();
			}
		});
		set_cancel_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				set_dialog.cancel();
			}
		});
	}

	class SpinnerPostNumListener implements OnItemSelectedListener {

		public void onItemSelected(AdapterView<?> arg0, View view, int arg2,
				long arg3) {
			post_num = Integer
					.parseInt(arg0.getItemAtPosition(arg2).toString());
		}

		public void onNothingSelected(AdapterView<?> arg0) {

		}
	}

	class SpinnerReplyNumListener implements OnItemSelectedListener {

		public void onItemSelected(AdapterView<?> arg0, View view, int arg2,
				long arg3) {
			reply_num = Integer.parseInt(arg0.getItemAtPosition(arg2)
					.toString());
		}

		public void onNothingSelected(AdapterView<?> arg0) {

		}
	}

	public void jumpToLogin() {
		Intent intent = new Intent();
		intent.setClass(BaseActivity.this, LoginActivity.class);
		startActivity(intent);
	}

	protected void onRestoreInstanceState(Bundle savedInstanceState) {

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

	// 判断是否登陆
	public boolean checkIsLogedIn() {
		if (!Utils.isEmpty(Forum.getUserId())) {
			return true;
		} else {
			return false;
		}
	}

	public void refreshTask() {
		userRefresh();
	}

	@Override
	protected void onResume() {
		userRefresh();
		super.onResume();
		MobclickAgent.onResume(this);
	}

	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	// 注销
	public void logout() {
		if (!Utils.isEmpty(Forum.getUserId())) {
			mLogoutTask = new LogoutTask();
			mLogoutTask.execute(Forum.getUserId());
			Forum.setUserId(null);
		}
		Editor editor = sharedPreferences.edit();
		editor.putInt("loginState", -1);
		editor.putString("name", "");
		editor.putString("password", "");
		editor.putString("uid", "");
		editor.commit();
		userRefresh();
	}

	// 退出
	public void exit() {
		if (Forum.getUserId() != null) {
			mLogoutTask = new LogoutTask();
			mLogoutTask.execute(Forum.getUserId());
		}
		ExitApplication.getInstance().exit();
		// Intent startMain = new Intent(Intent.ACTION_MAIN);
		// startMain.addCategory(Intent.CATEGORY_HOME);
		// startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// startActivity(startMain);
		// System.exit(0);
	}

	public void showToast() {
		Toast toast = Toast.makeText(BaseActivity.this, R.string.network_error,
				Toast.LENGTH_SHORT);

		// GifView giftView = new GifView(BaseActivity.this);
		//
		// giftView.setGifImage(R.drawable.zerror);
		// // giftView.setShowDimension(100, 120);
		// giftView.setGifImageType(GifImageType.COVER);
		// //获得Toast的View
		// View toastView = toast.getView();
		//
		// toastView.setBackgroundColor(0x00000000);
		// //定义一个Layout，这里是Layout
		//
		// LinearLayout linearLayout = new LinearLayout(BaseActivity.this);
		// linearLayout.setOrientation(LinearLayout.VERTICAL);
		//
		// //将ImageView和ToastView合并到Layout中
		//
		// linearLayout.addView(toastView);
		// linearLayout.addView(giftView);
		//
		// toast.setView(linearLayout);
		toast.show();
	}

	public void showLoadingProgressDialog() {
		// Drawable progress_drawable =
		// getResources().getDrawable(R.drawable.progressbar_color);
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

	// 退出对话框
	public void showExitDialog() {
		final String appname = getResources().getString(R.string.app_name);
		final String exit_text = getResources().getString(R.string.exit_text);
		Dialog dialog = new AlertDialog.Builder(BaseActivity.this)
				.setMessage(exit_text + appname + " ?")
				// 设置内容
				.setPositiveButton(R.string.enter,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								exit();
							}
						})
				.setNegativeButton(R.string.cancel,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								dialog.cancel();
							}
						}).create();
		dialog.show();
	}

	public class LogoutTask extends AsyncTask<String, String, JSONObject> {

		protected void onPreExecute() {
		}

		@Override
		protected JSONObject doInBackground(String... arg) {
			JSONObject json = null;
			try {
				json = ForumHome.LogoutForum(arg[0]);
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return json;
		}

		protected void onPostExecute(JSONObject result) {

		}
	}
}