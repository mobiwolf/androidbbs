package www.image;

import www.data.Forum;
import www.utils.Utils;
import www.wealk.com.R;
import android.graphics.Bitmap;

public interface ImageCaches {
	public static Bitmap mDefaultBitmap = Utils.drawableToBitmap(Forum.mContext.getResources().getDrawable(R.drawable.default_img));
	public static Bitmap mFailBitmap = Utils.drawableToBitmap(Forum.mContext.getResources().getDrawable(R.drawable.fail_img));
	public Bitmap get(String url);

	public void put(String url, Bitmap bitmap);
}
