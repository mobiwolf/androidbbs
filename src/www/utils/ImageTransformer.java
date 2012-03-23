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
 * Interface used to specify transformations which should be applied to an image
 * 
 * @author Antoine Guigan - QIMNET
 * @version 1.0
 */
public interface ImageTransformer {
	/**
	 * Transforms an image and returns the result.
	 * @param bitmap The original image.
	 */
	public Bitmap transform(Bitmap bitmap);
}