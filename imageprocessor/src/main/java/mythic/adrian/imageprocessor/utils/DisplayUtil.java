package mythic.adrian.imageprocessor.utils;

import android.content.Context;
import android.util.DisplayMetrics;

public class DisplayUtil {
	private static DisplayMetrics sDm;
	public static void init(Context c) {
		sDm = c.getResources().getDisplayMetrics();
	}

	public static float getDensity() {
		return sDm.density;
	}

	public static int getDensityDpi() {
		return sDm.densityDpi;
	}

	public static int getScreenWidth() {
		return sDm.widthPixels;
	}

	public static int getScreenHeight() {
		return sDm.heightPixels;
	}

	public static int getStatusbarHeight(Context context) {
		int statusHeight = 0;
		Class<?> localClass = null;
		try {
			localClass = Class.forName("com.android.internal.R$dimen");
			Object localObject = localClass.newInstance();
			int h = Integer.parseInt(localClass.getField("status_bar_height")
					.get(localObject).toString());
			statusHeight = context.getResources()
					.getDimensionPixelSize(h);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (statusHeight == 0) {
			return Math.round(getDensity() * 25);
		}

		return statusHeight;
	}

	public static int dip2px(int i) {
		return (int) (getDensity() * i);
	}
}
