/*
 * Copyright (C) 2009 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package www.image;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * 图像的检索和存储，使用PUT方法下载和存储图像，
 * 使用GET检索图像
 */
public class ImageManager implements ImageCaches {
	
    public static final int DEFAULT_COMPRESS_QUALITY = 90;
    public static final int MAX_WIDTH  = 496;
    public static final int MAX_HEIGHT = 992;
    private static Context mContext;
    // In memory cache.
    private static Map<String, SoftReference<Bitmap>> mCache;
    private HttpClient mClient;
    // MD5 hasher.
    private MessageDigest mDigest;
    
    private static final int CONNECTION_TIMEOUT_MS = 10 * 1000;
    private static final int SOCKET_TIMEOUT_MS = 10 * 1000;

    public ImageManager(Context context) {
        
    	mContext = context;
        mCache = new HashMap<String, SoftReference<Bitmap>>();
        mClient = new DefaultHttpClient();

        try {
        	
            mDigest = MessageDigest.getInstance("MD5");
            
        } catch (NoSuchAlgorithmException e) {
        	
            // This shouldn't happen.
            throw new RuntimeException("No MD5 algorithm.");
        }
    }
    private String getHashString(MessageDigest digest) {
        StringBuilder builder = new StringBuilder();

        for (byte b : digest.digest()) {
            builder.append(Integer.toHexString((b >> 4) & 0xf));
            builder.append(Integer.toHexString(b & 0xf));
        }

        return builder.toString();
    }
    
    // MD5 hases are used to generate filenames based off a URL.
    private String getMd5(String url) {
        
    	try{
    		mDigest.update(url.getBytes());
    	}catch (Exception e) {
			// TODO: handle exception
		}

        return getHashString(mDigest);
    }
    public void setContext(Context context) {
       
    	mContext = context;
    }
    
    // 查看图片是否存在文件中
    private Bitmap lookupFile(String url) {
        String hashedUrl = getMd5(url);
        
        //检查文件是否存在
        FileInputStream fis = null;
        try {
            fis = mContext.openFileInput(hashedUrl);
            
            return BitmapFactory.decodeStream(fis);
            
        } catch (FileNotFoundException e) {
            return null;
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    // Ignore.
                } catch (Exception e2) {
					// TODO: handle exception
                	e2.printStackTrace();
				}
            }
        }
    }
    
    /**
     * Downloads a file
     * @param url
     * @return
     * @throws IOException
     */
    public Bitmap fetchImage(String url) throws IOException {
    	Bitmap mbitmap = null;
        try {
            HttpGet get = new HttpGet(url);
            HttpConnectionParams.setConnectionTimeout(get.getParams(),
                    CONNECTION_TIMEOUT_MS);
            HttpConnectionParams.setSoTimeout(get.getParams(), SOCKET_TIMEOUT_MS);
            HttpResponse response = null;
            response = mClient.execute(get);
            HttpEntity entity = response.getEntity();
            BufferedInputStream bis = new BufferedInputStream(entity.getContent(),8 * 1024);
            mbitmap = scaleBitmap(bis, 120, 120);
            bis.close();

            if (response.getStatusLine().getStatusCode() != 200) {
            	mbitmap = mFailBitmap;
            }
        } catch (ClientProtocolException e) {
            throw new IOException("Invalid client protocol.");
        }catch (Exception e) {
        	e.printStackTrace();
        }
        return mbitmap;
    }
    /**
     * 下载远程图片 -> 转换为Bitmap -> 写入缓存器.
     * @param url
     * @param quality image quality 1～100
     * @throws IOException
     */
    public void put(String url, int quality, boolean forceOverride) throws IOException {
        if (!forceOverride && contains(url)) {
            // Image already exists.
            return;
        }
        // write to file if not present.
        Bitmap bitmap = fetchImage(url);

        if (bitmap == null) {
        	bitmap = mFailBitmap;
        } else {
            put(url, bitmap, quality);
        }
    }

    /**
     * 重载 put(String url, int quality)
     * @param url
     * @throws IOException
     */
    public void put(String url) throws IOException {
        put(url, DEFAULT_COMPRESS_QUALITY, false);
    }
    
    /**
     * 本地File -> 转换为Bitmap -> 写入缓存器.
     * @param file
     * @throws IOException
     */
    public void put(File file, int quality, boolean forceOverride) throws IOException {
        if (!file.exists()) {
            return;
        }
        if (!forceOverride && contains(file.getPath())) {
            // Image already exists.
            return;
            // TODO: write to file if not present.
        }

        Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
        bitmap = resizeBitmap(bitmap, MAX_WIDTH, MAX_HEIGHT);

        if (bitmap == null) {
        } else {
            put(file.getPath(), bitmap, quality);
        }
    }
    
    /**
     * 将Bitmap写入缓存器.
     * @param filePath file path
     * @param bitmap
     * @param quality 1~100
     */
    public void put(String file, Bitmap bitmap, int quality) {
        synchronized (this) {
            mCache.put(file, new SoftReference<Bitmap>(bitmap));
        }

        writeFile(file, bitmap, quality);
    }
    
    /**
     * 重载 put(String file, Bitmap bitmap, int quality)
     * @param filePath file path
     * @param bitmap
     * @param quality 1~100
     */
    @Override
    public void put(String file, Bitmap bitmap) {
        put(file, bitmap, DEFAULT_COMPRESS_QUALITY);
    }

    /**
     * 将Bitmap写入本地缓存文件.
     * @param file URL/PATH
     * @param bitmap
     * @param quality
     */
    private void writeFile(String file, Bitmap bitmap, int quality) {
    	if (bitmap == null) {
            return;
        }

        String hashedUrl = getMd5(file);

        FileOutputStream fos;

        try {
            fos = mContext.openFileOutput(hashedUrl, Context.MODE_PRIVATE);
        } catch (FileNotFoundException e) {
            return;
        }
        
        // image is too small
        if (bitmap.getWidth() < 100 && bitmap.getHeight() < 100) {
            quality = 100;
        }
        
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, fos);

        try {
            fos.close();
        } catch (IOException e) {
        	e.printStackTrace();
        }
    }
    
    public Bitmap get(File file) {
        return get(file.getPath());
    }
    
    /**
     * 从缓存器中读取文件
     * @param file file URL/file PATH
     * @param bitmap
     * @param quality
     */
    public Bitmap get(String file) {
        SoftReference<Bitmap> ref;
        Bitmap bitmap;
        //先查看缓存中是否存在图片
        synchronized (this){
            ref = mCache.get(file);
        }
        if (ref != null) {
            bitmap = ref.get();
            if (bitmap != null) {
                return bitmap;
            }
        }
        //如果缓存中不存在，就从文件中查找
        // Now try file.
        bitmap = lookupFile(file);
        
        if (bitmap != null) {
            synchronized (this) {
            	//写入缓存中方便下次读取
                mCache.put(file, new SoftReference<Bitmap>(bitmap));
            }

            return bitmap;
        }
        //如果没有则返回默认
        return mDefaultBitmap;
    }

    public boolean contains(String url) {
        return get(url) != mDefaultBitmap;
    }

    public static void clear() {
        String[] files = mContext.fileList();
        for (String file : files) {
            mContext.deleteFile(file);
        }
        synchronized (mContext) {
            mCache.clear();
        }
    }

    public void cleanup(HashSet<String> keepers) {
        String[] files = mContext.fileList();
        HashSet<String> hashedUrls = new HashSet<String>();

        for (String imageUrl : keepers) {
            hashedUrls.add(getMd5(imageUrl));
        }

        for (String file : files) {
            if (!hashedUrls.contains(file)) {
                mContext.deleteFile(file);
            }
        }
    }
    
    /**
     * Compress and resize the Image
     * @param targetFile
     * @param quality
     * @return
     * @throws IOException
     */
    public File compressImage(File targetFile, int quality) throws IOException {
        
        put(targetFile, quality, true); // compress, resize, store 
        
        String filePath = getMd5(targetFile.getPath());
        File compressedImage = mContext.getFileStreamPath(filePath);
        
        return compressedImage;
    }
    
    /**
     * 保持长宽比缩小Bitmap
     * @param bitmap
     * @param maxWidth
     * @param maxHeight
     * @param quality 1~100
     * @return
     */
    public Bitmap resizeBitmap(Bitmap bitmap, int maxWidth, int maxHeight) {
        
        int originWidth  = bitmap.getWidth();
        int originHeight = bitmap.getHeight();
        
        // no need to resize
        if (originWidth < maxWidth && originHeight < maxHeight) 
            return bitmap;
        
        int width  = originWidth;
        int height = originHeight;
        
        // 若图片过宽, 则保持长宽比缩放图片
        if (originWidth > maxWidth) {
            width = maxWidth;
            
            double i = originWidth * 1.0 / maxWidth;
            height = (int) Math.floor(originHeight / i);
        
            bitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
        }
        
        // 若图片过长, 则从上端截取
        if (height > maxHeight) {
            height = maxHeight;
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height);
        }
        return bitmap;
    }
    
    public static Bitmap scaleBitmap(BufferedInputStream imagePath, int width, int height) { 
    	Bitmap bmp = null;
    	BitmapFactory.Options opts = new BitmapFactory.Options();        
    	opts.inJustDecodeBounds = true;
    	BitmapFactory.decodeStream(imagePath,null,opts);
        try {
			imagePath.reset();
			int scaleWidth = (int) Math.floor((double) opts.outWidth / width);       
		    int scaleHeight = (int) Math.floor((double) opts.outHeight/ height);  
			opts.inJustDecodeBounds = false;
			opts.inSampleSize = Math.min(scaleWidth, scaleHeight);
			bmp = BitmapFactory.decodeStream(imagePath, null, opts);
        } catch (IOException e) {
			e.printStackTrace();
			bmp = mFailBitmap;
		}
    	return bmp;    	
    }
}
