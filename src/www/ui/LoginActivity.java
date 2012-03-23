package www.ui;

import org.json.JSONException;
import org.json.JSONObject;

import www.data.Forum;
import www.logic.ForumHome;
import www.utils.Base64;
import www.utils.ExitApplication;
import www.utils.Utils;
import www.wealk.com.R;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mobclick.android.MobclickAgent;

public class LoginActivity extends Activity {

	private SharedPreferences sharedPreferences;
	private LoginTask mLoginTask;
	private RegistTask mRegistTask;
	private ForgetPwdTask mForgetPwdTask;
	private FindPwdTask mFindPwdTask;
	private ProgressDialog progressDialog;
	private TextView title_text;
	private TextView login_error_text;
	private TextView forget_error_text;
	private TextView regist_error_text;
	private TextView findpwd_error_text;
	private String username;
	private String password;
	private String regist_username;
	private int login_state = 0;
	protected String findPwd_userName;
	protected String findPwd_email;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.login);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title);

		ExitApplication.getInstance().addActivity(this);

		Button backBtn = (Button) findViewById(R.id.back_imgbtn);
		backBtn.setBackgroundResource(R.drawable.title_back_selected);
		backBtn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});

		RelativeLayout login_title = (RelativeLayout) findViewById(R.id.login_title);
		login_title.setBackgroundResource(R.drawable.tab_bg_selected);

		title_text = (TextView) findViewById(R.id.title_text);
		title_text.setText(R.string.login);

		LinearLayout title_right = (LinearLayout) findViewById(R.id.title_right);
		title_right.setVisibility(View.GONE);

		sharedPreferences = getSharedPreferences("userApp",
				Context.MODE_PRIVATE);

		login_error_text = (TextView) findViewById(R.id.login_error_text);
		final EditText username_et = (EditText) findViewById(R.id.login_username);
		final EditText password_et = (EditText) findViewById(R.id.login_password);
		final CheckBox check_remember = (CheckBox) findViewById(R.id.check_remember);
		final CheckBox check_auto = (CheckBox) findViewById(R.id.check_auto);

		if (sharedPreferences.getInt("loginState", 0) == 1) {
			check_remember.setChecked(true);
		} else if (sharedPreferences.getInt("loginState", 0) == 2) {
			check_remember.setChecked(true);
			check_auto.setChecked(true);
		}
		String user_name = sharedPreferences.getString("name", "");
		String user_password = sharedPreferences.getString("password", "");
		username_et.setText(user_name);
		password_et.setText(user_password);
		Button dialog_login_btn = (Button) findViewById(R.id.login_btn);
		Button dialog_regist_btn = (Button) findViewById(R.id.login_regist_btn);
		Button dialog_pwd_btn = (Button) findViewById(R.id.login_findpwd);

		dialog_regist_btn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				jumpToRegist();
			}
		});

		dialog_login_btn.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				if (check_remember.isChecked() && !check_auto.isChecked()) {
					login_state = 1;
				} else if (check_auto.isChecked()) {
					login_state = 2;
				} else {
					login_state = 0;
				}

				username = username_et.getText().toString().trim();
				password = password_et.getText().toString().trim();

				if (mLoginTask != null
						&& mLoginTask.getStatus() == AsyncTask.Status.RUNNING) {
					mLoginTask.cancel(true);
				}
				if (!Utils.isEmpty(username) && !Utils.isEmpty(password)) {
					mLoginTask = new LoginTask();
					mLoginTask.execute(username, password);
				} else {
					login_error_text.setText(R.string.empty);
				}
			}
		});

		dialog_pwd_btn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				jumpToForgetpwd();
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	protected void jumpToRegist() {
		setContentView(R.layout.regist);
		title_text.setText(R.string.regist);

		regist_error_text = (TextView) findViewById(R.id.regist_error_text);
		final EditText username_regist_et = (EditText) findViewById(R.id.regist_username);
		final EditText password_regist_et = (EditText) findViewById(R.id.regist_password);
		final EditText password_regist_again_et = (EditText) findViewById(R.id.regist_password_again);
		final EditText email_regist_et = (EditText) findViewById(R.id.regist_email);

		Button dialog_regist_btn = (Button) findViewById(R.id.regist_btn);
		dialog_regist_btn.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {

				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				regist_username = username_regist_et.getText().toString()
						.trim();
				String regist_password = password_regist_et.getText()
						.toString().trim();
				String regist_password_again = password_regist_again_et
						.getText().toString().trim();
				String regist_email = email_regist_et.getText().toString()
						.trim();
				if (!Utils.isEmpty(regist_username)
						&& !Utils.isEmpty(regist_password)
						&& !Utils.isEmpty(regist_password_again)
						&& !Utils.isEmpty(regist_email)) {
					if (regist_password.equals(regist_password_again)) {
						int usernameLength = regist_username.getBytes().length;
						int passwordLength = regist_password.getBytes().length;
						if (usernameLength < 16 && usernameLength > 3) {
							if (passwordLength > 5 && passwordLength < 17) {
								if (mRegistTask != null
										&& mRegistTask.getStatus() == AsyncTask.Status.RUNNING) {
									mRegistTask.cancel(true);
								} else {
									mRegistTask = new RegistTask();
									mRegistTask.execute(regist_username,
											regist_password, regist_email);
								}
							} else {
								regist_error_text
										.setText(R.string.passwordshort);
							}
						} else {
							regist_error_text.setText(R.string.usernameshort);
						}
					} else {
						regist_error_text.setText(R.string.compare_pwd);
					}
				} else {
					regist_error_text.setText(R.string.empty);
				}
			}
		});

	}

	protected void jumpToForgetpwd() {
		setContentView(R.layout.forget_pwd);
		title_text.setText(R.string.forget_password);

		forget_error_text = (TextView) findViewById(R.id.forget_error_text);
		final EditText username_et = (EditText) findViewById(R.id.forget_username);
		final EditText email_et = (EditText) findViewById(R.id.forget_email);

		Button sendBtn = (Button) findViewById(R.id.forget_send_btn);
		sendBtn.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

				findPwd_userName = username_et.getText().toString().trim();
				findPwd_email = email_et.getText().toString().trim();

				if (!Utils.isEmpty(findPwd_userName)
						&& !Utils.isEmpty(findPwd_email)) {
					if (mForgetPwdTask != null
							&& mForgetPwdTask.getStatus() == AsyncTask.Status.RUNNING) {
						mForgetPwdTask.cancel(true);
					} else {
						mForgetPwdTask = new ForgetPwdTask();
						mForgetPwdTask.execute(findPwd_userName, findPwd_email);
					}
				} else {
					forget_error_text.setText(R.string.empty);
				}
			}
		});
	}

	protected void jumpToFindPwd() {
		setContentView(R.layout.regist);
		title_text.setText(R.string.find_password);

		findpwd_error_text = (TextView) findViewById(R.id.regist_error_text);
		final EditText username_fpwd_et = (EditText) findViewById(R.id.regist_username);
		final EditText password_fpwd_et = (EditText) findViewById(R.id.regist_password);
		final EditText password_fpwd_again_et = (EditText) findViewById(R.id.regist_password_again);
		final EditText email_fpwd_et = (EditText) findViewById(R.id.regist_email);
		username_fpwd_et.setText(findPwd_userName);
		email_fpwd_et.setText(findPwd_email);

		Button dialog_pwd_btn = (Button) findViewById(R.id.regist_btn);
		dialog_pwd_btn.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				String fpwd_username = username_fpwd_et.getText().toString()
						.trim();
				String fpwd_password = password_fpwd_et.getText().toString()
						.trim();
				String fpwd_password_again = password_fpwd_again_et.getText()
						.toString().trim();
				String fpwd_email = email_fpwd_et.getText().toString().trim();

				if (mFindPwdTask != null
						&& mFindPwdTask.getStatus() == AsyncTask.Status.RUNNING) {
					mFindPwdTask.cancel(true);
				}

				if (!Utils.isEmpty(fpwd_username)
						&& !Utils.isEmpty(fpwd_password)
						&& !Utils.isEmpty(fpwd_password_again)
						&& !Utils.isEmpty(fpwd_email)) {
					if (fpwd_password.equals(fpwd_password_again)) {
						mFindPwdTask = new FindPwdTask();
						mFindPwdTask.execute(fpwd_username, fpwd_password,
								fpwd_password_again, fpwd_email);
					} else {
						findpwd_error_text.setText(R.string.compare_pwd);
					}
				} else {
					findpwd_error_text.setText(R.string.empty);
				}
			}
		});
	}

	public void showLoadingProgressDialog() {
		progressDialog = new ProgressDialog(this);
		progressDialog.setMessage(getResources().getString(R.string.loadding));
		progressDialog.setIndeterminate(true);
		progressDialog.setCancelable(true);

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

	public class LoginTask extends AsyncTask<String, String, JSONObject> {

		protected void onPreExecute() {
			showLoadingProgressDialog();
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
			dismissProgressDialog();
			if (result != null) {
				try {
					int Error = Integer.parseInt(Base64.decodeString(result
							.getString("error")));
					switch (Error) {
					case 0:
						login_error_text.setText("");
						int userState = Integer.valueOf(Base64.decodeString(result.getString("userstate")));
						Forum.setUserId(Base64.decodeString(result
								.getString("uid")));
						Forum.setUserName(username);
						Forum.setUserState(userState);
						switch(userState){
						case 2:
							Toast.makeText(getApplicationContext(), R.string.no_speak, Toast.LENGTH_SHORT).show();
							break;
						case 3:
							Toast.makeText(getApplicationContext(), R.string.no_visit, Toast.LENGTH_SHORT).show();
							break;
						case 4:
							Toast.makeText(getApplicationContext(), R.string.locking, Toast.LENGTH_SHORT).show();
							break;
						default:
							break;
						}
						if (login_state == 0) {
							Editor editor = sharedPreferences.edit();
							editor.putInt("loginState", 0);
							editor.putString("name", "");
							editor.putString("password", "");
							editor.putString("uid", "");
							editor.commit();
						}
						if (login_state == 1) {
							Editor editor = sharedPreferences.edit();
							editor.putInt("loginState", 1);
							editor.putString("name", username);
							editor.putString("password", password);
							editor.putString("uid", "");
							editor.commit();
						} else if (login_state == 2) {
							Editor editor = sharedPreferences.edit();
							editor.putInt("loginState", 2);
							editor.putString("name", username);
							editor.putString("password", password);
							editor.putString("userid", Base64.decodeString(result.getString("uid")));
							editor.commit();
						}
						finish();
						break;
					case 1:
						login_error_text.setText(R.string.format_error);
						break;
					case 2:
						login_error_text.setText(R.string.password_error);
						break;
					case 3:
						login_error_text.setText(R.string.username_error);
						break;
					case 4:
						login_error_text.setText(R.string.username_enter);
						break;
					case 5:
						login_error_text.setText(R.string.password_enter);
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
				login_error_text.setText(R.string.network_error);
			}
		}
	}

	public class RegistTask extends AsyncTask<String, String, JSONObject> {

		protected void onPreExecute() {
			showLoadingProgressDialog();
		}

		@Override
		protected JSONObject doInBackground(String... arg) {
			JSONObject json = null;
			try {
				json = ForumHome.RegistForum(arg[0], arg[1], arg[2]);
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
						Forum.setUserId(Base64.decodeString(result
								.getString("uid")));
						Forum.setUserName(regist_username);
						Forum.setUserState(1);
						regist_error_text.setText("");
						finish();
						break;
					case 1:
						regist_error_text.setText(R.string.format_error);
						break;
					case 2:
						regist_error_text.setText(R.string.username_enter);
						break;
					case 3:
						regist_error_text.setText(R.string.password_enter);
						break;
					case 4:
						regist_error_text.setText(R.string.email_enter);
						break;
					case 5:
						regist_error_text.setText(R.string.email_error);
						break;
					case 6:
						regist_error_text.setText(R.string.uname_error);
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
				regist_error_text.setText(R.string.network_error);
			}
		}
	}

	public class ForgetPwdTask extends AsyncTask<String, String, JSONObject> {

		protected void onPreExecute() {
			showLoadingProgressDialog();
		}

		@Override
		protected JSONObject doInBackground(String... arg) {
			JSONObject json = null;
			try {
				json = ForumHome.ForgetPwdForum(arg[0], arg[1]);
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
						forget_error_text.setText("");
						jumpToFindPwd();
						break;
					case 1:
						forget_error_text.setText(R.string.format_error);
						break;
					case 2:
						forget_error_text.setText(R.string.useremail_error);
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
				forget_error_text.setText(R.string.network_error);
			}
		}
	}

	public class FindPwdTask extends AsyncTask<String, String, JSONObject> {

		protected void onPreExecute() {
			showLoadingProgressDialog();
		}

		@Override
		protected JSONObject doInBackground(String... arg) {
			JSONObject json = null;
			try {
				json = ForumHome.FindPwdForum(arg[0], arg[1], arg[2], arg[3]);
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
						finish();
						findpwd_error_text.setText("");
						break;
					case 1:
						findpwd_error_text.setText(R.string.format_error);
						break;
					case 2:
						findpwd_error_text.setText(R.string.compare_pwd);
						break;
					case 3:
						findpwd_error_text.setText(R.string.useremail_error);
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
				findpwd_error_text.setText(R.string.network_error);
			}
		}
	}
}