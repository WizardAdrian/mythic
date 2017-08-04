package mythic.adrian.imageprocessor.camera.view;

import android.content.Context;
import android.graphics.Point;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;

import mythic.adrian.imageprocessor.R;


/**
 * 聚焦时的动画
 * @author Zack.Zhang
 *
 */
public class CameraFocusImageView extends ImageView {
	public final static String TAG="FocusImageView";
	private static final int NO_ID=-1;
	private int mFocusImg=NO_ID;
	private int mFocusSucceedImg=NO_ID;
	private int mFocusFailedImg=NO_ID;
	private Animation mAnimation;
	private Handler mHandler;
	
	public CameraFocusImageView(Context context) {
		super(context);
		mAnimation = AnimationUtils.loadAnimation(getContext(),
				R.anim.camera_focusview_show);
		setVisibility(View.INVISIBLE);
		mHandler = new Handler();
		initFocusImage();
	}

	public CameraFocusImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mAnimation = AnimationUtils.loadAnimation(getContext(),
				R.anim.camera_focusview_show);
		setVisibility(View.INVISIBLE);
		mHandler=new Handler();
		initFocusImage();
	}
	
	private void initFocusImage() {
		mFocusImg = R.drawable.camera_focus_focusing;
		mFocusSucceedImg = R.drawable.camera_focus_focused;
		mFocusFailedImg = R.drawable.camera_focus_focus_failed;

		// 聚焦图片不能为空
		if (mFocusImg == NO_ID || mFocusSucceedImg == NO_ID
				|| mFocusFailedImg == NO_ID) {
			throw new RuntimeException("Animation is null");
		}
		setImageResource(mFocusImg);
	}
	
	private Runnable mDismissRunnable = new Runnable() {
		
		@Override
		public void run() {
			setVisibility(View.GONE);
		}
	};

	/**  
	 *  显示聚焦图案
	 *  @param x 触屏的x坐标
	 *  @param y 触屏的y坐标
	 */
	public void startFocus(Point point) {
		if (mFocusImg == NO_ID || mFocusSucceedImg == NO_ID
				|| mFocusFailedImg == NO_ID)
			throw new RuntimeException("focus image is null");
		// 根据触摸的坐标设置聚焦图案的位置
		FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) getLayoutParams();
		params.topMargin = point.y - getHeight() / 2;
		params.leftMargin = point.x - getWidth() / 2;
		setLayoutParams(params);
		// 设置控件可见，并开始动画
		setVisibility(View.VISIBLE);
		setImageResource(mFocusImg);
		startAnimation(mAnimation);
		// 3秒后隐藏View。在此处设置是由于可能聚焦事件可能不触发。
		mHandler.removeCallbacks(mDismissRunnable, null);
		mHandler.postDelayed(mDismissRunnable, 3500);
	}
	
	/**
	 * 聚焦成功回调
	 */
	public void onFocusSuccess() {
		setImageResource(mFocusSucceedImg);
		// 移除在startFocus中设置的callback，1秒后隐藏该控件
		mHandler.removeCallbacks(mDismissRunnable, null);
		mHandler.postDelayed(mDismissRunnable, 500);
	}
	
	/**
	 * 聚焦失败回调
	 */
	public void onFocusFailed() {
		setImageResource(mFocusFailedImg);
		// 移除在startFocus中设置的callback，1秒后隐藏该控件
		mHandler.removeCallbacks(mDismissRunnable, null);
		mHandler.postDelayed(mDismissRunnable, 500);
	}

	/**  
	 * 设置开始聚焦时的图片
	 *  @param focus   
	 */
	public void setFocusImg(int focus) {
		this.mFocusImg = focus;
	}

	/**  
	 *  设置聚焦成功显示的图片
	 *  @param focusSucceed   
	 */
	public void setFocusSucceedImg(int focusSucceed) {
		this.mFocusSucceedImg = focusSucceed;
	}
}
