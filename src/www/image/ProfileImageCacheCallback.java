package www.image;

import android.graphics.Bitmap;

public interface ProfileImageCacheCallback {
	void refresh(String url, Bitmap bitmap);
}
