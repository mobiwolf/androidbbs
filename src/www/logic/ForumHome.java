package www.logic;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import www.data.Forum;
import www.utils.Base64;
import www.utils.JSONHelper;
import www.utils.RestClient;
import www.utils.RestClient.RequestMethod;
import www.utils.Utils;

public class ForumHome {
	
	private static RestClient client;
	private static String BaseUrl = Forum.getUrl();
	private static String masterId = "wealk_bbs_mobile_dzx20:getpletlist";
	private static String postsId = "wealk_bbs_mobile_dzx20:forumGetThread";
	private static String latestId = "wealk_bbs_mobile_dzx20:forumGetLatestThread";
	private static String hostId = "wealk_bbs_mobile_dzx20:forumGetHot";
	private static String replyId = "wealk_bbs_mobile_dzx20:forumGetPost";
	private static String sendPostsid = "wealk_bbs_mobile_dzx20:forumNewThread";
	private static String replyPostsid = "wealk_bbs_mobile_dzx20:forumNewPost";
	private static String loginid = "wealk_bbs_mobile_dzx20:MemberLogin";
	private static String registid = "wealk_bbs_mobile_dzx20:MemberRegister";
	private static String forgetpwdid = "wealk_bbs_mobile_dzx20:MemberRetrievePassword";
	private static String findpwdid = "wealk_bbs_mobile_dzx20:MemberResetthePassword";
	private static String logoutid = "wealk_bbs_mobile_dzx20:memberLogout";

	//获取所有主板块信息
	public static JSONObject getHomeForum() throws JSONException{
		client = new RestClient(BaseUrl);
		client.AddParam("id", masterId);

		try {
		    client.Execute(RequestMethod.GET);
		    
		} catch (Exception e) {
			
		    e.printStackTrace();
		}
		 
		String response = client.getResponse();
		
		if(response != null){
		    return JSONHelper.str2json(response);
		}
		else{
		    return null;
		}
	}
	
	//获取板块下的热帖信息
	public static JSONObject getHotForum(String pageSize) throws JSONException{
		client = new RestClient(BaseUrl);

		client.AddParam("id", hostId);
		client.AddHeader("pagesize", Base64.encodeString(pageSize));
		try {
		    client.Execute(RequestMethod.GET);
		    
		} catch (Exception e) {
			
		    e.printStackTrace();
		}
		 
		String response = client.getResponse();
		
		if(response != null){
		    return JSONHelper.str2json(response);
		}
		else{
		    return null;
		}
	}
	
	//获取板块下的新贴信息
	public static JSONObject getNewForum(String pageSize) throws JSONException{
		client = new RestClient(BaseUrl);

		client.AddParam("id", latestId);
		client.AddHeader("pagesize", Base64.encodeString(pageSize));
		try {
		    client.Execute(RequestMethod.GET);
		    
		} catch (Exception e) {
			
		    e.printStackTrace();
		}
		 
		String response = client.getResponse();
		
		if(response != null){
		    return JSONHelper.str2json(response);
		}
		else{
		    return null;
		}
	}
	
	//获取板块下的帖子信息
	public static JSONObject getThreadForum(String status,String forumid,String page,String pageSize) throws JSONException, RuntimeException, IOException{	
		client = new RestClient(BaseUrl);

		client.AddParam("id", postsId);
		client.AddHeader("fid", Base64.encodeString(forumid));	 
		client.AddHeader("page", Base64.encodeString(page));	
		client.AddHeader("pagesize", Base64.encodeString(pageSize));
		
		try {
		    client.Execute(RequestMethod.GET);
		    
		} catch (Exception e) {
			
		    e.printStackTrace();
		}
		 
		String response = client.getResponse();

		if(response != null){
			
		    return JSONHelper.str2json(response);
		}
		else{
		    return null;
		}
	}
	
	//获取帖子信息
	public static JSONObject getPostForum(String tid,String page,String model,String pageSize) throws JSONException{
		
		client = new RestClient(BaseUrl);

		client.AddParam("id", replyId);;
		client.AddHeader("tid", Base64.encodeString(tid));	 
		client.AddHeader("page", Base64.encodeString(page));	
		client.AddHeader("pagesize", Base64.encodeString(pageSize));
		client.AddHeader("model", Base64.encodeString(model));	

		try {
		    client.Execute(RequestMethod.GET);
		    
		} catch (Exception e) {
			
		    e.printStackTrace();
		}
		 
		String response = client.getResponse();

		if(response != null){
			
		    return JSONHelper.str2json(response);
		}
		else{
		    return null;
		}
	}
	
	//发帖
	public static JSONObject SendPosts(String fid,String userId,String title,String message,String aid) throws JSONException{
		
		client = new RestClient(BaseUrl);

		client.AddParam("id", sendPostsid);
		client.AddHeader("fid", Base64.encodeString(fid));	 
		client.AddHeader("uid", Base64.encodeString(userId));
		client.AddHeader("subject", Base64.encodeString(title));	
		client.AddHeader("message", Base64.encodeString(message));	
		if(!Utils.isEmpty(aid)){
			client.AddHeader("aid", Base64.encodeString(aid));
		}
		
		try {
		    client.Execute(RequestMethod.POST);
		    
		} catch (Exception e) {
			
		    e.printStackTrace();
		}
		 
		String response = client.getResponse();
		
		if(response != null){
			
		    return JSONHelper.str2json(response);
		}
		else{
		    return null;
		}
	}
	
	
	//回帖
	public static JSONObject ReplyPosts(String fid,String tid,String pid,String userId,String message,String aid) throws JSONException{
		
		client = new RestClient(BaseUrl);
		
		client.AddParam("id", replyPostsid);
		client.AddHeader("fid", Base64.encodeString(fid));	 
		client.AddHeader("tid", Base64.encodeString(tid));
		client.AddHeader("pid", Base64.encodeString(pid));	
		client.AddHeader("uid", Base64.encodeString(userId));
		client.AddHeader("message", Base64.encodeString(message));	
		if(!Utils.isEmpty(aid)){
			client.AddHeader("aid", Base64.encodeString(aid));
		
		}
		
		try {
		    client.Execute(RequestMethod.POST);
		    
		} catch (Exception e) {
			
		    e.printStackTrace();
		}
		 
		String response = client.getResponse();

		if(response != null){
			
		    return JSONHelper.str2json(response);
		}
		else{
		    return null;
		}
	}
	
	//登陆
	public static JSONObject LoginForum(String username,String password) throws JSONException{
		
		client = new RestClient(BaseUrl);

		client.AddParam("id", loginid);
		client.AddHeader("username", Base64.encodeString(username));	 
		client.AddHeader("password", Base64.encodeString(password));	

		try {
		    client.Execute(RequestMethod.POST);
		    
		} catch (Exception e) {
			
		    e.printStackTrace();
		}
		 
		String response = client.getResponse();
		
		if(response != null){
			
		    return JSONHelper.str2json(response);
		}
		else{
		    return null;
		}
	}
	
	//注册
	public static JSONObject RegistForum(String username,String password,String email) throws JSONException{
		
		client = new RestClient(BaseUrl);

		client.AddParam("id", registid);
		client.AddHeader("username", Base64.encodeString(username));	 
		client.AddHeader("password", Base64.encodeString(password));	
		client.AddHeader("email", Base64.encodeString(email));	
		try {
		    client.Execute(RequestMethod.POST);
		    
		} catch (Exception e) {
			
		    e.printStackTrace();
		}
		 
		String response = client.getResponse();
		
		if(response != null){
			
		    return JSONHelper.str2json(response);
		}
		else{
		    return null;
		}
	}
	
	//忘记密码
	public static JSONObject ForgetPwdForum(String username,String email) throws JSONException{
		
		client = new RestClient(BaseUrl);

		client.AddParam("id", forgetpwdid);
		client.AddHeader("username", Base64.encodeString(username));	 
		client.AddHeader("email", Base64.encodeString(email));	
		try {
		    client.Execute(RequestMethod.POST);
		    
		} catch (Exception e) {
			
		    e.printStackTrace();
		}
		 
		String response = client.getResponse();
		
		if(response != null){
			
		    return JSONHelper.str2json(response);
		}
		else{
		    return null;
		}
	}
	
	//重置密码
	public static JSONObject FindPwdForum(String username,String password,String password2,String email) throws JSONException{
		
		client = new RestClient(BaseUrl);

		client.AddParam("id", findpwdid);
		client.AddHeader("username", Base64.encodeString(username));	 
		client.AddHeader("email", Base64.encodeString(email));	
		client.AddHeader("password", Base64.encodeString(password));
		client.AddHeader("password2", Base64.encodeString(password2));
		try {
		    client.Execute(RequestMethod.POST);
		    
		} catch (Exception e) {
			
		    e.printStackTrace();
		}
		 
		String response = client.getResponse();
		
		if(response != null){
			
		    return JSONHelper.str2json(response);
		}
		else{
		    return null;
		}
	}

	public static JSONObject LogoutForum(String uid)throws JSONException{
		client = new RestClient(BaseUrl);

		client.AddParam("id", logoutid );
		client.AddHeader("uid", Base64.encodeString(uid));	 

		try {
		    client.Execute(RequestMethod.POST);
		    
		} catch (Exception e) {
			
		    e.printStackTrace();
		}
		 
		String response = client.getResponse();
		
		if(response != null){
			
		    return JSONHelper.str2json(response);
		}
		else{
		    return null;
		}
	}
}