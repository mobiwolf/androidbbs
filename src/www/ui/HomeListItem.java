package www.ui;

import www.data.Enum.forumType;
import www.utils.ImageListView;
import www.wealk.com.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class HomeListItem extends LinearLayout implements ImageListView{
	
	public HomeListItem(Context context,forumType type) {
		super(context);
		
		switch(type){			
		case MASTER:			
			View.inflate(context, R.layout.member_childitem, this);
			break;
		case POSTS:			
			View.inflate(context, R.layout.posts_item, this);
			break;
		case REPLY:			
			View.inflate(context, R.layout.reply_item, this);
			break;
		default:
			break;
		}
	}
	
	public void setImage(int position, int viewId, Bitmap bitmap) {
		ImageView iv = (ImageView)this.findViewById(viewId);
		iv.setImageBitmap(bitmap);	
	}

}