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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Stack;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.View;
/**
 * This class is used to add images to a ListView through HTTP.
 * 
 * The images are downloaded and saved to the filesystem, and are afterwards inserted 
 * inside the list by a callback. The corresponding list should  implement the 
 * {@link ImageListView} interface. 
 * 
 * @author Antoine Guigan - QIMNET
 * @version 1.0
 * @param <T> The class of the linked view. 
 */
public class ImageCache<T extends View&ImageListView> {
	private static final int DEFAULT_MAX_THREADS=3;
	private static final int DEFAULT_MAX_QUEUE_SIZE=20;
	private static final int DEFAULT_MAX_CACHED=5;
	/**
	 * The maximum number of simultaneaous downloads
	 */
	public static int maxThreads = DEFAULT_MAX_THREADS;
	/**
     * The maximum queue size for downloads
     */
	public static int maxQueueSize = DEFAULT_MAX_QUEUE_SIZE;
	/**
	 * The maximum number of images cached in memory
	 */
	public static int maxCachedImages = DEFAULT_MAX_CACHED;
	private static Hashtable<String, CachedFile> files = new Hashtable<String, CachedFile>();
	private static List<String> cachedFiles = new ArrayList<String>();
	private static List<String> currentDownloads = new ArrayList<String>();
	private static List<AsyncDownload> runningDownloads = new ArrayList<AsyncDownload>();
	private static Stack<AsyncDownload> queuedDownloads = new Stack<AsyncDownload>();


    private static String getCacheFilename(String url) {
    	MessageDigest digest=null;
		try {
			digest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
    	digest.update((url).getBytes());
    	byte[] sum = digest.digest();
    	StringBuilder ret = new StringBuilder();
    	for(int i=0; i < sum.length; i++) {
    		ret.append(Integer.toHexString(0xFF & sum[i]));
    	}
    	
    	return ret.toString();
    }
    
    private static void runQueue() {
    	while ((queuedDownloads.size() > 0) && (runningDownloads.size() < maxThreads)) {
    		queuedDownloads.pop().execute();
    	}
    }
    /**
     * Loads a remote image inside a listview.
     * @param <T> The class of the linked view
     * 
     * @param list A ListView implementing the {@link ImageListView} interface
     * @param url The url of the file to be downloaded
     * @param position The absolute position of the element inside the ListView
     * @param viewId The id of the ImageView which should receive the image
     * @param transformer An optional image transformer implementing {@link ImageTransformer}
     * @return If the image is already cached in the filesystem, the corresponding bitmap. Otherwise,
     * returns null
     */
    
  
    public static <T extends View&ImageListView> Bitmap load(T list, String url, int position, int viewId, ImageTransformer transformer) {
        String cacheFilename = getCacheFilename(url);
		CachedFile f = null;
		if (!currentDownloads.contains(cacheFilename)) {
			if (!files.containsKey(cacheFilename)) {
				f = new CachedFile(cacheFilename);
				files.put(cacheFilename, f);
			} else {
				f = files.get(cacheFilename);
			}
			if (f.bitmap == null) {
				InputStream input = f.getInputStream((Activity) list.getContext()); 
				if (input == null) {
					while(queuedDownloads.size() > (maxQueueSize - 1)) {
						AsyncDownload download = queuedDownloads.remove(0);
						currentDownloads.remove(download.cacheFilename);
					}
					queuedDownloads.push(new AsyncDownload(
							cacheFilename,url, 
							new ImageDisplayer(position, viewId, list),
							transformer));
					runQueue();
				} else {
					//Cache the image in memory
					while (cachedFiles.size() > (maxCachedImages - 1)) {
						unloadBitmap();
					}
					while (f.bitmap == null) {
						try {
							f.bitmap = BitmapFactory.decodeStream(input);
						} catch (OutOfMemoryError e) {
							if (cachedFiles.size() > 0) {
								unloadBitmap();
							} else {
								break;
							}
						}
					}
				}
			}
		}
    	return (f != null) ? f.bitmap : null;
    }
    
    public static  Bitmap loadUrl( String url,Activity mActivity) throws MalformedURLException,IOException {
        String cacheFilename = getCacheFilename(url);
		CachedFile f = null;
		if (!currentDownloads.contains(cacheFilename)) {
			if (!files.containsKey(cacheFilename)) {
				f = new CachedFile(cacheFilename);
				files.put(cacheFilename, f);
			}else {
				f = files.get(cacheFilename);
			}
			if (f.bitmap == null) {
				InputStream input = f.getInputStream(mActivity); 
				if (input == null) {
					while(queuedDownloads.size() > (maxQueueSize - 1)) {
						AsyncDownload download = queuedDownloads.remove(0);
						currentDownloads.remove(download.cacheFilename);
					}

					HttpURLConnection conn = (HttpURLConnection)(new URL(url)).openConnection();
					conn.setDoInput(true); 
		            conn.connect();
		          	InputStream is = conn.getInputStream(); 
		          		
		          	BitmapFactory.Options opts = new BitmapFactory.Options();
		          	opts.inJustDecodeBounds = true;
		          	opts.inSampleSize = computeSampleSize(opts, -1, 128*128);
		          	opts.inJustDecodeBounds = false;
		          	try {
		          		f.bitmap =  BitmapFactory.decodeStream(is, null , opts);   
		          	} catch (OutOfMemoryError err) {
						f.bitmap.recycle();
		          	} 
					runQueue();
					is.close();
				} else {
					while (cachedFiles.size() > (maxCachedImages - 1)) {
						unloadBitmap();
					}
					while (f.bitmap == null) {
						try {
							f.bitmap = BitmapFactory.decodeStream(input);
						} catch (OutOfMemoryError e) {
							if (cachedFiles.size() > 0) {
								unloadBitmap();
							} else {
								break;
							}
						}
					}
				}
			}
		}
    	return (f != null) ? f.bitmap : null;
    }
    
    public static int computeSampleSize(BitmapFactory.Options options,
            int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength,maxNumOfPixels);

        int roundedSize;
        if (initialSize <= 8 ) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }

        return roundedSize;
    }

    private static int computeInitialSampleSize(BitmapFactory.Options options,int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;

        int lowerBound = (maxNumOfPixels == -1) ? 1 :
                (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 :
                (int) Math.min(Math.floor(w / minSideLength),
                Math.floor(h / minSideLength));

        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }

        if ((maxNumOfPixels == -1) &&
                (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }
    private static void unloadBitmap() {
    	CachedFile file = files.get(cachedFiles.remove(0));
    	file.bitmap.recycle();
    	file.bitmap = null;
    }
    /**
     * Loads a remote image inside a listview.
     * @param <T> The class of the linked view.
     * 
     * @param list A ListView implementing the {@link ImageListView} interface
     * @param url The url of the file to be downloaded
     * @param position The absolute position of the element inside the ListView
     * @param viewId The id of the ImageView which should receive the image
     * @return If the image is already cached in the filesystem, the corresponding bitmap. Otherwise,
     * returns null
     */
    public static <T extends View&ImageListView> Bitmap load(T list, String url, int position, int viewId) {
    	return load(list,url,position,viewId,null);
    }
	private static class CachedFile {
		private String cacheFilename;
		Bitmap bitmap;
		public CachedFile(String cacheFilename) {
			this.cacheFilename = cacheFilename;
		}
		private synchronized void renameCachedFile(Activity activity, File tmpfile) {
			tmpfile.renameTo(activity.getFileStreamPath(cacheFilename));
		}
		private synchronized InputStream getInputStream(Activity activity) {
			InputStream ret = null;
			try {
				ret = activity.openFileInput(cacheFilename);
			} catch (FileNotFoundException e) {
			}
			return ret;
		}
	}
	private static class ImageDisplayer implements Runnable {
		Bitmap bitmap;
		private int position;
		private int viewId;
		private WeakReference<ImageListView> list;
		ImageListView getList() {
			return list.get();
		}
		public ImageDisplayer(int position, int viewId, ImageListView list) {
			this.position = position;
			this.viewId = viewId;
			this.list = new WeakReference<ImageListView>(list);
		}
	    public void run() {
	    	ImageListView view = list.get();
	    	if (view != null) {
	    		view.setImage(position, viewId, bitmap);
	    	}
	    }
	}
	private static class AsyncDownload  extends AsyncTask<Void, Void, Void> {
		private ImageDisplayer displayer;
		private String cacheFilename;
		private String url;
		private ImageTransformer imageTransformer;
		public AsyncDownload(String cacheFilename, String url,
				ImageDisplayer displayer,
				ImageTransformer imageTransformer) {
			this.displayer = displayer;
			this.cacheFilename = cacheFilename;
			this.url = url;
			this.imageTransformer = imageTransformer;
			currentDownloads.add(cacheFilename);
		}
		@Override
		protected void onPreExecute() {
			runningDownloads.add(this);
		}
		@Override
		protected void onPostExecute(Void result) {
			currentDownloads.remove(cacheFilename);
			runningDownloads.remove(this);
			runQueue();
		}
		@Override
		protected void onCancelled() {
			currentDownloads.remove(cacheFilename);
			runningDownloads.remove(this);
			runQueue();
		}
	    @Override
	    protected Void doInBackground(Void... params) {
	    	OutputStream output = null;
	        try {
	            HttpURLConnection conn = (HttpURLConnection)(new URL(url)).openConnection();
	            conn.connect();
	            Bitmap bmp = BitmapFactory.decodeStream(conn.getInputStream());
	            if (bmp == null) {
	            	throw new IOException();
	            }
	            if (imageTransformer != null) {
	            	bmp = imageTransformer.transform(bmp);
	            }
	            ImageListView list = displayer.getList();
	            if (list != null) {
	            	Activity mactivity = (Activity) list.getContext();
	            	displayer.bitmap = bmp;
	            	File tmpfile = File.createTempFile("tmp", "png",mactivity.getFilesDir());
	            	output = mactivity.openFileOutput(tmpfile.getName(),0);
		            bmp.compress(CompressFormat.PNG, 100	, output);
		            output.close();
		            output = null;
		            files.get(cacheFilename).renameCachedFile(mactivity,tmpfile);
		            mactivity.runOnUiThread(displayer);
	            }
	        } catch (IOException e) {
	        	if (output != null) {
	        		try {
						output.close();
					} catch (IOException e2) {
					}
	        	}
	        } catch (OutOfMemoryError e) {
	        	
	        } 
			return null;

	    }
	}
}