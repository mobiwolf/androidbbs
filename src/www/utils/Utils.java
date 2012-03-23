package www.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class Utils {
	
	public static boolean isEmpty(String s) {    
	  return s == null || s.length() == 0;
	}
	
	public static Bitmap drawableToBitmap(Drawable drawable) {  
		  Bitmap bitmap = Bitmap  
		                  .createBitmap(  
		                                  drawable.getIntrinsicWidth(),  
		                                  drawable.getIntrinsicHeight(),  
		                                  drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888  
		                                                  : Bitmap.Config.RGB_565);  
		  Canvas canvas = new Canvas(bitmap);  
		  drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());  
		  drawable.draw(canvas);  
		  return bitmap;  
	  }
	
	public  static Drawable bitmapToDrawable(Bitmap mBitmap){
		BitmapDrawable bitmapDrawable = new BitmapDrawable(mBitmap);    
		Drawable drawable = (Drawable)bitmapDrawable;
		return drawable;
	}

	public static boolean CreateFile(String filePath) {
		// TODO Auto-generated method stub
		return false;
	}
}