/*******************************************************************************
 * Copyright (c) 2010 Antoine Guigan - QIMNET.
 *   
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl-3.0.txt
 *   
 * The current licence is only applicable for non-commercial uses. 
 * Commercial uses require a different license. 
 * More details are available on http://www.webodroid.com
 ******************************************************************************/
package www.utils;

import android.graphics.Bitmap;

/**
 * Implementation of {@link ImageTransformer} used to resize images
 * 
 * @see ArticleHelper 
 * @see ImageCache
 * @author Antoine Guigan -QIMNET
 * @version 1.0
 */
public class ImageResizer implements ImageTransformer {
	/**
	 * The maximum height of the image
	 */
	public int maxHeight=100;
	/**
	 * The maximum height of the image
	 */
	public int maxWidth=100;
	@Override
	public Bitmap transform(Bitmap bitmap) {
		boolean resize = false;
		int height = bitmap.getHeight();
		int width = bitmap.getWidth();
		if (width > maxWidth) {
			height = Math.round(((float)maxWidth / (float)width) * (float)height);
			width = maxWidth;
			resize = true;
		}
		if (height > maxHeight) {
			width = Math.round(((float) maxHeight / (float)height ) * (float)width);
			height = maxHeight;
			resize = true;
		}
		Bitmap ret = null;
		if (resize) {
			ret = Bitmap.createScaledBitmap(bitmap, width, height, false);
		} else {
			ret = bitmap;
		}
		return ret;
	}

}