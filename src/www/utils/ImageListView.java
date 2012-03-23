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

import android.content.Context;
import android.graphics.Bitmap;
/**
 * Interface used to asynchronously load images inside a ListView
 * 
 * @see ImageCache
 * @author Antoine Guigan - QIMNET
 * @version 1.0
 */
public interface ImageListView {
	/**
	 * Sets an image in the ListView
	 * 
	 * @param position The absolute position where the image is to be inserted in the list
	 * @param viewId The id of the ImageView where the image is to be inserted in the list child
	 * @param bitmap The image to insert
	 */
	public void setImage(int position, int viewId, Bitmap bitmap);
	/**
	 * Returns the context of the ListView
	 */
	public Context getContext();
}