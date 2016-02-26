/**
 * BitmapUtil.java
 * 
 * @author zzc(zhangchao@xiaomi.com)
 * 
 */

package com.mmnn.bonn036.zoo.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Drawable;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

// so many same code in this file, just avoid to decode bitmap unnecessarily
public class BitmapUtil {
	private static final String TAG = BitmapUtil.class.getName();

	public static final int BYTES_EVERY_PIXEL_RGB565 = 2;
	public static final int BYTES_EVERY_PIXEL_ARGB8888 = 4;

	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
		if (bitmap == null) {
			return null;
		}
		final float radius = bitmap.getHeight() / 2f;
		return getRoundedCornerBitmap(bitmap, radius, radius);
	}

	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float rx,
			float ry) {
		if (bitmap == null) {
			return null;
		}
		final Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		final Canvas canvas = new Canvas(output);

		final BitmapShader shader = new BitmapShader(bitmap, TileMode.CLAMP,
				TileMode.CLAMP);

		final Paint paint = new Paint();
		paint.setShader(shader);
		paint.setAntiAlias(true);
		paint.setFilterBitmap(true);

		final RectF rectF = new RectF(0, 0, bitmap.getWidth(),
				bitmap.getHeight());

		// rect contains the bounds of the shape
		// radius is the radius in pixels of the rounded corners
		// paint contains the shader that will texture the shape
		canvas.drawRoundRect(rectF, rx, ry, paint);
		//Log.d(TAG, "rect: " + rectF + ", rx: " + rx + ", ry: " + ry);

		// final int color = 0xffff0000;
		// final Paint paint = new Paint();
		// final Rect rect = new Rect(0, 0, bitmap.getWidth(),
		// bitmap.getHeight());
		// Log.d(TAG, "rect: " + rect + ", rx: " + rx + ", ry: " + ry);
		//
		// paint.setAntiAlias(true);
		// canvas.drawARGB(0, 0, 0, 0);
		// paint.setColor(color);
		// canvas.drawRoundRect(new RectF(rect), rx, ry, paint);
		//
		// paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		// paint.setFilterBitmap(true);
		// paint.setDither(true);
		// canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}

	public static Bitmap centerCrop(Bitmap source, int width, int height) {
		if (source == null) {
			return null;
		}
		if (width <= 0 || height <= 0) {
			return null;
		}
		final int rawWidth = source.getWidth();
		final int rawHeight = source.getHeight();
		final int reqWidth = width > rawWidth ? rawWidth : width;
		final int reqHeight = height > rawHeight ? rawHeight : height;
		final int centerX = rawWidth / 2;
		final int centerY = rawHeight / 2;
		return Bitmap.createBitmap(source, centerX - reqWidth / 2, centerY
				- reqHeight / 2, reqWidth, reqHeight);
	}

	private static int calculateInSampleSize(int rawWidth, int rawHeight,
			int reqWidth, int reqHeight) {
		Log.d(TAG, "rawWidth: " + rawWidth + ", rawHeight: " + rawHeight
				+ ", reqWidth: " + reqWidth + ", reqHeight: " + reqHeight);
		if (reqWidth <= 0 || reqHeight <= 0
				|| (rawHeight <= reqHeight && rawWidth <= reqWidth)) {
			Log.w(TAG, "invalid parameters, sample size is 1");
			return 1;
		}

		// Calculate ratios of height and width to requested height and width
		float fHeightRatio = rawHeight / (float) reqHeight;
		float fWidthRatio = rawWidth / (float) reqWidth;
		final int heightRatio = Math.round(fHeightRatio);
		final int widthRatio = Math.round(fWidthRatio);
		Log.d(TAG, "fHeightRatio: " + fHeightRatio + ", fWidthRatio: "
				+ fWidthRatio + ", heightRatio: " + heightRatio
				+ ", widthRatio: " + widthRatio);

		// Choose the smallest ratio as inSampleSize value, this will
		// guarantee a final image with both dimensions larger than or equal to
		// the
		// requested height and width.
		return heightRatio < widthRatio ? heightRatio : widthRatio;
	}

	public static int detectBitmapWidth(InputStream is) throws IOException {
		byte[] data = IOUtil.inputStream2ByteArray(is);
		return detectBitmapWidth(data);
	}

	public static int detectBitmapHeight(InputStream is) throws IOException {
		byte[] data = IOUtil.inputStream2ByteArray(is);
		return detectBitmapHeight(data);
	}

	public static int detectBitmapWidth(byte[] data) {
		if (data == null) {
			return 0;
		}
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeByteArray(data, 0, data.length, options);
		return options.outWidth;
	}

	public static int detectBitmapHeight(byte[] data) {
		if (data == null) {
			return 0;
		}
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeByteArray(data, 0, data.length, options);
		return options.outHeight;
	}

	public static int detectBitmapWidth(Resources res, int resId) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(res, resId, options);
		return options.outWidth;
	}

	public static int detectBitmapHeight(Resources res, int resId) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(res, resId, options);
		return options.outHeight;
	}

	public static Bitmap decodeBitmapBySize(InputStream is, int reqWidth,
			int reqHeight) throws IOException {
		long start = System.currentTimeMillis();
		byte[] data = IOUtil.inputStream2ByteArray(is);
		Log.d(TAG, "convert time: " + (System.currentTimeMillis() - start));
		return decodeBitmapBySize(data, reqWidth, reqHeight);
	}

	public static Bitmap decodeBitmapBySize(byte[] data, int reqWidth,
			int reqHeight) {
		if (data == null || reqWidth <= 0 || reqHeight <= 0) {
			Log.w(TAG, "invalid parameters, null bitmap will be returned");
			return null;
		}
		long start = System.currentTimeMillis();
		final BitmapFactory.Options options = new BitmapFactory.Options();
		// Decode with inJustDecodeBounds=true to check dimensions
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeByteArray(data, 0, data.length, options);
		final int rawWidth = options.outWidth;
		final int rawHeight = options.outHeight;

		long start2 = System.currentTimeMillis();
		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(rawWidth, rawHeight,
				reqWidth, reqHeight);
		Log.d(TAG, "calculate time: " + (System.currentTimeMillis() - start2));

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length,
				options);
		Log.d(TAG, "decode time: " + (System.currentTimeMillis() - start));
		return bitmap;
	}

	public static Bitmap decodeBitmapBySize(Resources res, int resId,
			int reqWidth, int reqHeight) {
		if (reqWidth <= 0 || reqHeight <= 0) {
			Log.w(TAG, "invalid parameters, null bitmap will be returned");
			return null;
		}
		final BitmapFactory.Options options = new BitmapFactory.Options();
		// Decode with inJustDecodeBounds=true to check dimensions
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(res, resId, options);
		final int rawWidth = options.outWidth;
		final int rawHeight = options.outHeight;

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(rawWidth, rawHeight,
				reqWidth, reqHeight);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeResource(res, resId, options);
	}

	private static int getDivider(int width, int height, int pixelSize,
			int capacity) {
		final int size = width * height * pixelSize;
		float factor = size / (float) capacity;
		if (factor < 1.0f) {
			factor = 1.0f;
		}
		return (int) Math.ceil(factor);
	}

	public static Bitmap decodeBitmapByCapacity(InputStream is, int capacity)
			throws IOException {
		return decodeBitmapByCapacity(is, new BitmapFactory.Options(), capacity);
	}

	public static Bitmap decodeBitmapByCapacity(InputStream is,
			BitmapFactory.Options options, int capacity) throws IOException {
		byte[] data = IOUtil.inputStream2ByteArray(is);
		return decodeBitmapByCapacity(data, options, capacity);
	}

	public static Bitmap decodeBitmapByCapaciy(byte[] data, int capacity) {
		return decodeBitmapByCapacity(data, new BitmapFactory.Options(),
				capacity);
	}

	public static Bitmap decodeBitmapByCapacity(byte[] data,
			BitmapFactory.Options options, int capacity) {
		if (options == null) {
			Log.w(TAG, "options null");
			return null;
		}
		int pixelSize = BYTES_EVERY_PIXEL_ARGB8888;
		if (options.inPreferredConfig.equals(Config.RGB_565)) {
			pixelSize = BYTES_EVERY_PIXEL_RGB565;
		}
		if (data == null || capacity <= 0 || capacity < pixelSize) {
			Log.w(TAG, "data or capacity invalid");
			return null;
		}
		// Decode with inJustDecodeBounds=true to check dimensions
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeByteArray(data, 0, data.length, options);
		final int rawWidth = options.outWidth;
		final int rawHeight = options.outHeight;
		Log.d(TAG, "raw size: " + rawWidth + "x" + rawHeight);

		// Calculate request width and height
		final int divider = getDivider(rawWidth, rawHeight, pixelSize, capacity);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(rawWidth, rawHeight,
				rawWidth / divider, rawHeight / divider);
		Log.d(TAG, "req size: " + rawWidth / divider + "x" + rawHeight
				/ divider);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeByteArray(data, 0, data.length, options);
	}

	public static Bitmap decodeBitmapByCapacity(Resources res, int resId,
			int capacity) {
		return decodeBitmapByCapacity(res, resId, new BitmapFactory.Options(),
				capacity);
	}

	public static Bitmap decodeBitmapByCapacity(Resources res, int resId,
			BitmapFactory.Options options, int capacity) {
		if (options == null) {
			Log.w(TAG, "options null");
			return null;
		}
		int pixelSize = BYTES_EVERY_PIXEL_ARGB8888;
		if (options.inPreferredConfig.equals(Config.RGB_565)) {
			pixelSize = BYTES_EVERY_PIXEL_RGB565;
		}
		if (capacity <= 0 || capacity < pixelSize) {
			Log.w(TAG, "capacity invalid");
			return null;
		}
		// Decode with inJustDecodeBounds=true to check dimensions
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(res, resId, options);
		final int rawWidth = options.outWidth;
		final int rawHeight = options.outHeight;
		Log.d(TAG, "raw size: " + rawWidth + "x" + rawHeight);

		// Calculate request width and height
		final int divider = getDivider(rawWidth, rawHeight, pixelSize, capacity);

		// Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(rawWidth, rawHeight,
				rawWidth / divider, rawHeight / divider);
		Log.d(TAG, "req size: " + rawWidth / divider + "x" + rawHeight
				/ divider);

		// Decode bitmap with inSampleSize set
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeResource(res, resId, options);
	}

	public static Bitmap getCircularBitmap(Bitmap bitmap, int radius) {
		if (null == bitmap || radius <= 0) {
			return null;
		}

		Bitmap scaledBitmap;
		if (bitmap.getWidth() != radius || bitmap.getHeight() != radius) {
			scaledBitmap = Bitmap.createScaledBitmap(bitmap, radius, radius,
					false);
		} else {
			scaledBitmap = bitmap;
		}
		Bitmap output = Bitmap.createBitmap(scaledBitmap.getWidth(),
				scaledBitmap.getHeight(), Config.ARGB_8888);
		final Rect rect = new Rect(0, 0, scaledBitmap.getWidth(),
				scaledBitmap.getHeight());
		final Canvas canvas = new Canvas(output);
		final Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setFilterBitmap(true);
		paint.setDither(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(Color.parseColor("#BAB399"));
		canvas.drawCircle(scaledBitmap.getWidth() / 2 + 0.7f,
				scaledBitmap.getHeight() / 2 + 0.7f,
				scaledBitmap.getWidth() / 2 + 0.1f, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(scaledBitmap, rect, rect, paint);

		return output;
	}

	/**
	 * 转换图片成圆形
	 * 
	 * @param bitmap
	 *            传入Bitmap对象
	 * @return
	 */
	public static Bitmap toRoundBitmap(Bitmap bitmap) {

		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		float roundPx;
		float dst_left, dst_top, dst_right, dst_bottom;
		if (width <= height) {
			roundPx = width / 2;
			height = width;
		} else {
			roundPx = height / 2;
			width = height;
		}
		dst_left = 0;
		dst_top = 0;
		dst_right = dst_left + width;
		dst_bottom = dst_top + width;
		Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		final int color = Color.WHITE;
		final Paint paint = new Paint();
		final Rect dst = new Rect((int) dst_left, (int) dst_top,
				(int) dst_right, (int) dst_bottom);
		final RectF rectF = new RectF(dst);
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 255, 255, 255);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, null, dst, paint);
		return output;
	}

	/**
	 * 转换图片成圆形,并添加边框
	 * 
	 * @param bitmap
	 *            传入Bitmap对象
	 * @param frame
	 *            设置的边框
	 * @return
	 */
	public static Bitmap toRoundBitmap(Bitmap bitmap, Bitmap frame) {

		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		float roundPx;
		float dst_left, dst_top, dst_right, dst_bottom;
		if (width <= height) {
			roundPx = width / 2;
			height = width;
		} else {
			roundPx = height / 2;
			width = height;
		}

		int frameSize = frame.getWidth() <= frame.getHeight() ? frame
				.getWidth() : frame.getHeight();
		int borderSize = (frameSize - width) / 2;
		if (borderSize < 0) {
			borderSize = 0;
		}
		dst_left = 0 + borderSize;
		dst_top = 0 + borderSize;
		dst_right = dst_left + width;
		dst_bottom = dst_top + width;
		Bitmap output = Bitmap.createBitmap(width + borderSize * 2, height
				+ borderSize * 2, Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		final int color = Color.WHITE;
		final Paint paint = new Paint();
		final Rect dst = new Rect((int) dst_left, (int) dst_top,
				(int) dst_right, (int) dst_bottom);
		final RectF rectF = new RectF(dst);
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 255, 255, 255);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, null, dst, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.OVERLAY));
		canvas.drawBitmap(frame, 0, 0, null);
		return output;
	}

	public static Bitmap drawableToBitmap(Drawable drawable) {
		if (drawable == null) {
			return null;
		}
		// 取 drawable 的长宽
		int w = drawable.getIntrinsicWidth();
		int h = drawable.getIntrinsicHeight();

		// 取 drawable 的颜色格式
		Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Config.ARGB_8888
				: Config.RGB_565;
		// 建立对应 bitmap
		Bitmap bitmap = Bitmap.createBitmap(w, h, config);
		// 建立对应 bitmap 的画布
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, w, h);
		// 把 drawable 内容画到画布中
		drawable.draw(canvas);
		return bitmap;
	}

	public static byte[] bitmap2Bytes(Bitmap bm) {
		if (bm == null) {
			return null;
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
		return baos.toByteArray();
	}

	public static byte[] drawable2Bytes(Drawable drawable) {
		return bitmap2Bytes(drawableToBitmap(drawable));
	}

}
