package com.john.game.puzzle.view;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.john.game.puzzle.R;
import com.john.game.utils.ImagePiece;
import com.john.game.utils.ImageSplitterUtil;

public class PuzzleLayout extends RelativeLayout implements OnClickListener {

	//图片块规模
	private int mColumn = 3;
	
	//图片内边距
	private int mPadding;
	
	//每张图片间的距离 dp
	private int mMargin = 3;
	
	//存储ImageView拼图
	private ImageView[] mPuzzleItems ;
	
	//小块宽高
    private int mItemWidth;
	
    //游戏图片
    private Bitmap mBitmap;
	
    private List<ImagePiece> mItemBitmaps;
    
    private boolean once;

    //游戏面板宽度
    private int mWidth;
    
    //选中的第一张图
    private ImageView mFirst;
    //选中的第二张图
    private ImageView mSecond;
    
    //动画层
  	private RelativeLayout mAnimLayout;
  	
  	//判断动画是否在运行
  	private boolean isAnining;
  	
  	private static final int TIME_CHANGED = 0x110;
  	private static final int NEXT_LEVEL = 0x111;
  	
  	private int mLevel = 1;
  	
  	//计时开关
  	private boolean isTimeOff = false;
  	
  	private boolean isGameSuccess;
  	private boolean isGameOver;
  	
  	//计时器
  	private int mTime;


	public PuzzleListener mListener;
  	
  	/**
  	 * 设置接口回调
  	 * @param mListener
  	 */
	
	public interface PuzzleListener{
  		void nextLevel(int nextLevel);
  		void timeChanged(int currentTime);
  		void gameover();
  	}
  	
  	
  	public void setOnPuzzleListener(PuzzleListener mListener) {
		this.mListener = mListener;
	}
  	
  	
  	private Handler mHandler = new Handler(){
  		public void handleMessage(android.os.Message msg) {
  			switch(msg.what){
  			case TIME_CHANGED:
  				//通关或失败计时结束
  				if(isGameSuccess || isGameOver){
  					return;
  				}
  				
  				//判断时间是否被监听，通过主界面修改时间
  				if(mListener != null){
  					mListener.timeChanged(mTime);
  					if(mTime == 0){
  	  					isGameOver = true;
  	  					mListener.gameover();
  	  				}
  				}
  				mTime--;
  				//每过一秒发送一次
  				mHandler.sendEmptyMessageDelayed(TIME_CHANGED, 1000);
  				break;
  			
  			case NEXT_LEVEL:
  				mLevel = mLevel + 1;
  				if(mListener != null){
  					mListener.nextLevel(mLevel);
  				}else{
  					nextLevel();
  				}
  				break;
  			
  			default:
  				break;
  			
  			}
  			
  		};
  	};
    
    public PuzzleLayout(Context context) {
		this(context,null);
		
	}
	
	public PuzzleLayout(Context context, AttributeSet attrs) {
		this(context, attrs,0);
		
	}

	public PuzzleLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);		
		init();
		
	}
	
	/**
	 * 初始化游戏数据
	 */
	private void init() {
		//转变像素
		mMargin  = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, getResources().getDisplayMetrics());
		mPadding = min(getPaddingLeft(),getPaddingRight(),getPaddingTop(),getPaddingBottom());
	}
	
	/**
	 * 游戏准备
	 */
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		//取游戏面板中宽高的最小值
		mWidth = Math.min(getMeasuredHeight(), getMeasuredWidth());
		
		if(!once){
			//进行切图，排序
			initBitmap();
			//设置ImageView(Item)的宽高等属性
			initItem();
			once = true;
			//判断是否开启计时
			checkTimeEnable();
		}
		//设置游戏面板尺寸
		setMeasuredDimension(mWidth, mWidth);
		
	}
	
	private void checkTimeEnable() {
		if(isTimeOff){
			//根据关卡设置计时器
			countTimeBaseLevel();
			//通过Handler减少时间
			mHandler.sendEmptyMessage(TIME_CHANGED);
		}
		
	}

	private void countTimeBaseLevel() {
		//指数增长2的指数
		mTime = (int) Math.pow(2, mLevel)*60;
		
	}

	/**
	 * 进行切图,排序
	 */
	private void initBitmap() {
		if(mBitmap == null){
			//获取图片
			mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.a);
		}
		//图片切片
		mItemBitmaps = ImageSplitterUtil.splitImage(mBitmap, mColumn);
		
		//打乱顺序，使用sort 
		Collections.sort(mItemBitmaps,new Comparator<ImagePiece>() {
			public int compare(ImagePiece a, ImagePiece b) {
				return Math.random() > 0.5 ? 1 : -1;
			}
		});
	}
	
	/**
	 * 设置ImageView(Item)的宽高属性
	 */
	private void initItem() {
		
		mItemWidth = (mWidth - mPadding * 2 - mMargin * (mColumn - 1))/mColumn; 
		
		mPuzzleItems = new ImageView[mColumn*mColumn];
		
		int length = mPuzzleItems.length;
		for(int i = 0;i < length;i++){
			ImageView item = new ImageView(getContext());
			item.setOnClickListener(this);
			//乱序的图片
			item.setImageBitmap(mItemBitmaps.get(i).getBitmap());
			
			mPuzzleItems[i] = item;
			item.setId(i+1);
			//在Item的tag中存储index
			item.setTag(i +"_" + mItemBitmaps.get(i).getIndex());
			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(mItemWidth,mItemWidth);
			
			//设置Item间横向间隙,通过rightMargin
			//不是最后一列
			if((i+1)%mColumn != 0){
				lp.rightMargin = mMargin;
			}
			
			//不是第一列
			if(i%mColumn !=0 ){
				//设置不是第一列图片的的左边图片
				lp.addRule(RelativeLayout.RIGHT_OF,mPuzzleItems[i-1].getId());
			}
			
			//不是第一行，设置topMargin和rule
			if((i+1) > mColumn){
				lp.topMargin = mMargin;
				//设置不是第一行上面的图片
				lp.addRule(RelativeLayout.BELOW, mPuzzleItems[i-mColumn].getId());
			}
			
			addView(item,lp);
		}
	}


	//点击进行交换图片
	public void onClick(View v) {
		//正在使用动画点击失效
		if(isAnining){
			return;
		}
		//两次点击同一张图片，取消选中状态
		if(mFirst == v){
			mFirst.setColorFilter(null);
			mFirst = null;
			return;
		}
		//第一次点击，改变颜色
		if(mFirst == null){
			mFirst = (ImageView) v;
			mFirst.setColorFilter(Color.parseColor("#55FF0000"));
		}else{
			//点击另一张图片，交换两张图片
			mSecond = (ImageView) v;
			exchangeView();
		}
		
	}

	/**
	 * 交换两个选中的小图 Item
	 */
	private void exchangeView() {
		//改变第一个选中的图片的状态，设为正常
		mFirst.setColorFilter(null);
		
		
		//通过Tag来获取图片
		final Bitmap firstBitmap = mItemBitmaps.get(getImageIdByTag((String) mFirst.getTag())).getBitmap();
		final Bitmap sencondBitmap = mItemBitmaps.get(getImageIdByTag((String) mSecond.getTag())).getBitmap();
		
		//构造动画层
		setUpAnimaLayout();
		
		//创建第一张图片的动画层
		ImageView first = new ImageView(getContext());
		setAnimation(first,mFirst);
		
		//创建第二张图片的动画层
		ImageView second = new ImageView(getContext());
		setAnimation(second,mSecond);
		
		
		//设置第一张图片动画
		TranslateAnimation anim = new TranslateAnimation(0, mSecond.getLeft() - mFirst.getLeft(), 
				                                         0, mSecond.getTop()  - mFirst.getTop());
		anim.setDuration(300);
		anim.setFillAfter(true);
		first.startAnimation(anim);
		
		//设置第二张图片动画
		TranslateAnimation anim2 = new TranslateAnimation(0, -mSecond.getLeft() + mFirst.getLeft(), 
				                                         0,  -mSecond.getTop()  + mFirst.getTop());
		
		anim.setDuration(300);
		anim.setFillAfter(true);
		second.startAnimation(anim2);		
		
		//动画监听
		anim.setAnimationListener(new AnimationListener() {
			
			public void onAnimationStart(Animation animation) {
				//隐藏选中的两张图片
				mFirst.setVisibility(View.INVISIBLE);
				mSecond.setVisibility(View.INVISIBLE);
				isAnining = true;
				
			}
			
			public void onAnimationRepeat(Animation animation) {
	
			}
			
			public void onAnimationEnd(Animation animation) {
				//得到两张图片的Tag
				String firstTag  = (String) mFirst.getTag();
				String secondTag = (String) mSecond.getTag();
				
				//交换选中图片的位置 
				mFirst.setImageBitmap(sencondBitmap);
				mSecond.setImageBitmap(firstBitmap);
				
				//改变选中图片的Tag
				mFirst.setTag(secondTag);
				mSecond.setTag(firstTag);
				
				//把交换的图片显示
				mFirst.setVisibility(View.VISIBLE);
				mSecond.setVisibility(View.VISIBLE);
	
				//mFirst和mSecond为引用
				mFirst = mSecond = null;
				//取消动画层
				mAnimLayout.removeAllViews();
				//判断是否通关
				checkSuccess();
				isAnining = false;
			}
		});
	}


	/**
	 * 根据Tag获取id
	 * @param tag
	 * @return
	 */
	public int getImageIdByTag(String tag){
		String[] split = tag.split("_");
		return Integer.parseInt(split[0]);
 	}
	/**
	 * 根据Tag获取index
	 * @param tag
	 * @return 
	 */
	public int getImageIndexByTag(String tag){
		String[] split = tag.split("_");
		return Integer.parseInt(split[1]);
	}
	
	/**
	 * 构造动画层
	 */
	private void setUpAnimaLayout() {
		if(mAnimLayout == null){
			mAnimLayout = new RelativeLayout(getContext()); 
			//加入面板
			addView(mAnimLayout); 
		}
		
	}
	
	/**
	 * 图层设置并加入到动画层
	 * @param imageView   图层
	 * @param mImageView  点击的图片
	 */
	public void setAnimation(ImageView imageView,ImageView mImageView){
				//设置图片的动画层
				imageView.setImageBitmap(mItemBitmaps.get(getImageIdByTag((String) mImageView.getTag())).getBitmap());
				//设置图片图层宽高
				LayoutParams lp = new LayoutParams(mItemWidth,mItemWidth);
				lp.leftMargin = mImageView.getLeft() - mPadding;
				lp.topMargin  = mImageView.getTop()  - mPadding;
				//设置图层的参数
				imageView.setLayoutParams(lp);
				//图片层加入动画层
				mAnimLayout.addView(imageView);		
			}
	
	/**
	 * 判断是否通关
	 */
	private void checkSuccess() {
		boolean isSuccess = true;
		int length = mPuzzleItems.length;
		//通过index来判断是否通关
		for(int i = 0;i < length;i++){
			ImageView imageView = mPuzzleItems[i];
			if(getImageIndexByTag((String)imageView.getTag()) != i){
				isSuccess = false;
			}
		}
			if(isSuccess){
				
			isGameSuccess = true ;
			//防止重复发送，把上个Handler给清除
			mHandler.removeMessages(TIME_CHANGED);
			Log.e("TAG", "SUCCESS");
			Toast.makeText(getContext(), "成功通过", Toast.LENGTH_SHORT).show();
			mHandler.sendEmptyMessage(NEXT_LEVEL);
		 }
		
	}
	
	/**
	 * 进入下一关
	 */
	public void nextLevel(){
		//清空前一关的图
		this.removeAllViews();
		//移除动画层
		mAnimLayout = null ;
		//设置下关参数
		mColumn ++ ;
		isGameSuccess = false;
		//检查计时是否开启
		checkTimeEnable();
		//初始化下一关
		initBitmap();
		initItem();
	}
	
	
	/**
	 * 设置是否开启计时功能
	 * @param isTimeOff
	 */
	public void setTimeOff(boolean isTimeOff) {
		this.isTimeOff = isTimeOff;
	}
	
	/**
	 * 获取多个参数的最小值
	 * @return
	 */
	private int min(int ...params) {
		int min = params[0];
		for(int param:params){
			if(param < min){
				min =param;
			}
		}
		return min;
	}
	
}
