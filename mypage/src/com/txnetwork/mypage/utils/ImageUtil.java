package com.txnetwork.mypage.utils;

import android.content.Context;
import android.graphics.*;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.BitmapDrawable;
import com.txnetwork.mypage.entity.ReqFileBean;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ImageUtil {

	private static final String TAG = ImageUtil.class.getSimpleName();

	public static Bitmap getBitmapFromnFile(Context ctx,int resId)
    {
    	InputStream in = ctx.getResources().openRawResource(resId);
    	return  getBitmapFromnFile(in);
    }
    
    public static Bitmap getBitmapFromnFile(InputStream is)
    {
    	if(is == null)
            return null;
         Bitmap bitmap = null;
         try {
        	BitmapFactory.Options options = new BitmapFactory.Options();
             // Modified by Li Yong
             // Compare image quality under RGB_565 with ARGB_8888
//         	options.inPreferredConfig = Config.RGB_565;
         	options.inPreferredConfig = Config.ARGB_8888;
             // End
         	options.inPurgeable = true;
         	options.inInputShareable = true;
      	    bitmap =  BitmapFactory.decodeStream(is,null,options);
  		 } catch (Exception e) {
  			bitmap = null;
  			e.printStackTrace();
  		}finally
  		{
  			if(is != null)
  			{
  				try {
  					is.close();
  				} catch (IOException e) {
  					e.printStackTrace();
  				}
  			}
  		}
      	return bitmap;
    }
    
    public static Bitmap getBitmapFromFileInputStream(FileInputStream is)
    {
    	if(is == null)
            return null;
         Bitmap bitmap = null;
         try {
        	BitmapFactory.Options options = new BitmapFactory.Options();
             // Modified by Li Yong
             // Compare image quality under RGB_565 with ARGB_8888
//         	options.inPreferredConfig = Config.RGB_565;
             options.inPreferredConfig = Config.ARGB_8888;
             // End
         	options.inPurgeable = true;
         	options.inInputShareable = true;
      	    bitmap =  BitmapFactory.decodeFileDescriptor(is.getFD(),null,options);
  		 } catch (Exception e) {
  			bitmap = null;
  			e.printStackTrace();
  		}finally
  		{
  			if(is != null)
  			{
  				try {
  					is.close();
  				} catch (IOException e) {
  					e.printStackTrace();
  				}
  			}
  		}
      	return bitmap;
    }
    /**
     * 使用decodeStream获取file
     * @param file
     * @return
     */
    public static Bitmap getBitmapFromFile(File file)
    {
    	try
    	{
    		if(null != file){
    			FileInputStream fis = new FileInputStream(file);
    	    	return getBitmapFromFileInputStream(fis);
    		}else{
    			return null;
    		}
    	}
    	catch(Exception ex)
    	{
    		ex.printStackTrace();
    		return null;
    	}
    }
    
    
    public static Bitmap getBitmapFromnFile(String filePath)
    {
    	return getBitmapFromFile(new File(filePath));
    }
    
    
    public static Bitmap getBitmapFromFile(Context ctx,int resId)
    {
    	InputStream in = ctx.getResources().openRawResource(resId);
    	return  getBitmapFromnFile(in);
    }
    
    public static BitmapDrawable getBitmapDrawableFromnFile(Context ctx,int resId)
    {
    	return new BitmapDrawable(ctx.getResources(),getBitmapFromFile(ctx,resId));
    }

 	public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) {

 		Bitmap output = null;
 		try {

 			output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(),
 					Config.ARGB_8888);
 			Canvas canvas = new Canvas(output);

 			final Paint paint = new Paint();
 			final Rect rect = new Rect(0, 0, bitmap.getWidth(),
 					bitmap.getHeight());
 			final RectF rectF = new RectF(rect);
 			final float roundPx = pixels;

 			paint.setAntiAlias(true);
 			canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

 			paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
 			canvas.drawBitmap(bitmap, rect, rect, paint);

 		} catch (Exception e) {

 			LogUtils.LOGW(TAG,
 					"Utility Exception toRoundCorner ----->" + e.getMessage());
 			output = null;
 		}

 		return output;
 	}
    
    public static void freeReqFileBean(ReqFileBean bean) {
		if(bean != null)
		{
			bean.setContext(null);
			bean.setHanlder(null);
			bean.setListener(null);
			bean = null;
		}
		
	}
}
