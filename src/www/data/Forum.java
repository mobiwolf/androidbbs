package www.data;

import www.image.ProfileImageCacheManager;
import android.app.Application;
import android.content.Context;
import android.widget.Toast;


public class Forum extends Application{
	
	private static String app_name;
	private static String app_url;
	private static String userName;
	private static String userid;
	private static String fid;
	private static String tid;
	private static String pid;
	private static int userState;
	
	
	public static String getName() {
		return app_name;
	}
	public static void setName(String name) {
		app_name = name;
	}

	public static String getUrl() {
		return app_url;
	}
	public static void setUrl(String url) {
		app_url = url;
	}
	
	public static void setUserName(String name){
		userName = name;
	}	
	public static String getUserName(){
		return userName;
	}
	
	public static void setUserId(String id){
		userid = id;
	}	
	public static String getUserId(){
		return userid;
	}
	
	public static void setFroumId(String id){
		fid = id;
	}	
	public static String getForumId(){
		return fid;
	}
	
	public static void setPostsId(String id){
		tid = id;
	}	
	public static String getPostsId(){
		return tid;
	}
	
	public static void setPId(String id){
		pid = id;
	}	
	public static String getPId(){
		return pid;
	}
	
	public static void setUserState(int state) {
		userState = state;	
	}
	public static int getUserState(){
		return userState;
	}
	
    
	public static ProfileImageCacheManager mProfileImageCacheManager;
	public static Context mContext;
	 
	@Override
	 public void onCreate() {
	     super.onCreate();

	     mContext = this.getApplicationContext();
	     //mImageManager = new ImageManager(this);
	     mProfileImageCacheManager = new ProfileImageCacheManager();
	}
	    
	 @Override
	 public void onTerminate() {
		 
		 	Toast.makeText(this, "exit app", Toast.LENGTH_LONG);
	        super.onTerminate();
	 }
}