package www.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import www.data.Enum.forumType;

public class HomeItem {

	public String forumid;
	public String tid;
	public String name;
	public String threads;
	public String posts;
	public String todayposts;
	public String author;
	public String authorId;
	public String subject;
	public String replies;
	public String views;
	public String dateline;
	public String lastpost;
	public String lastposter;
	public String imgurl;
	public String pid;
	public String first;
	public String msg;
	public String message;
	public String closed;
	public String status;
	public String lou;
	public String userState;
	public forumType type;
	public JSONArray child;

	public HomeItem(JSONObject json, forumType type) throws JSONException {
		switch (type) {
		case MASTER:
			try {
				this.forumid = json.getString("fid");
				this.name = json.getString("name");
				this.posts = json.getString("posts");
				this.todayposts = json.getString("todayposts");
				this.child = json.getJSONArray("child");
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case POSTS:
			try {
				this.forumid = json.getString("fid");
				this.tid = json.getString("tid");
				this.author = json.getString("author");
				this.authorId = json.getString("authorId");
				this.subject = json.getString("subject");
				this.replies = json.getString("replies");
				this.views = json.getString("views");
				this.dateline = json.getString("dateline");
				this.lastpost = json.getString("lastpost");
				this.lastposter = json.getString("lastposter");
				this.imgurl = json.getString("mimgurl");
				this.closed = json.getString("closed");
				this.threads = json.getString("threads");
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}

			break;
		case REPLY:
			try {
				this.pid = json.getString("pid");
				this.replies = json.getString("replies");
				this.author = json.getString("author");
				this.authorId = json.getString("authorid");
				this.msg = json.getString("msg");
				this.message = json.getString("message");
				this.imgurl = json.getString("mimgurl");
				this.dateline = json.getString("dateline");
				this.userState = json.getString("userstate");
				this.status = json.getString("status");
				this.lou = json.getString("lou");
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		default:
			break;
		}
	}
}