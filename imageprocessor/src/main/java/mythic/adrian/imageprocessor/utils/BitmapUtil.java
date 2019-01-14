package mythic.adrian.imageprocessor.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * @author Adrian
 * @date 2019/1/14 11:02
 * @description
 */
public class BitmapUtil {

    public static Bitmap getPicFromBytes(byte[] bytes, BitmapFactory.Options opts) {
        if (bytes != null)
            if (opts != null)
                return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opts);
            else
                return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        return null;
    }

    public static boolean checkBitmap(Bitmap bitmap) {
        return bitmap != null && !bitmap.isRecycled();
    }
}
