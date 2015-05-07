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

	//ͼƬ���ģ
	private int mColumn = 3;
	
	//ͼƬ�ڱ߾�
	private int mPadding;
	
	//ÿ��ͼƬ��ľ��� dp
	private int mMargin = 3;
	
	//�洢ImageViewƴͼ
	private ImageView[] mPuzzleItems ;
	
	//С����
    private int mItemWidth;
	
    //��ϷͼƬ
    private Bitmap mBitmap;
	
    private List<ImagePiece> mItemBitmaps;
    
    private boolean once;

    //��Ϸ�����
    private int mWidth;
    
    //ѡ�еĵ�һ��ͼ
    private ImageView mFirst;
    //ѡ�еĵڶ���ͼ
    private ImageView mSecond;
    
    //������
  	private RelativeLayout mAnimLayout;
  	
  	//�ж϶����Ƿ�������
  	private boolean isAnining;
  	
  	private static final int TIME_CHANGED = 0x110;
  	private static final int NEXT_LEVEL = 0x111;
  	
  	private int mLevel = 1;
  	
  	//��ʱ����
  	private boolean isTimeOff = false;
  	
  	private boolean isGameSuccess;
  	private boolean isGameOver;
  	
  	//��ʱ��
  	private int mTime;
  	
  	//��Ϸ��ͣ
  	private boolean isPause;


	public PuzzleListener mListener;
  	
  	/**
  	 * ���ýӿڻص�
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
  				//ͨ�ػ�ʧ�ܼ�ʱ����
  				if(isGameSuccess || isGameOver || isPause){
  					return;
  				}
  				
  				//�ж�ʱ���Ƿ񱻼�����ͨ���������޸�ʱ��
  				if(mListener != null){
  					mListener.timeChanged(mTime);
  				}
  				if(mTime == 0){
	  					isGameOver = true;
	  					mListener.gameover();
	  					return;
	  				}
  				mTime--;
  				//ÿ��һ�뷢��һ��
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
	 * ��ʼ����Ϸ����
	 */
	private void init() {
		//ת������
		mMargin  = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, getResources().getDisplayMetrics());
		mPadding = min(getPaddingLeft(),getPaddingRight(),getPaddingTop(),getPaddingBottom());
	}
	
	/**
	 * ��Ϸ׼��
	 */
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		//ȡ��Ϸ����п�ߵ���Сֵ
		mWidth = Math.min(getMeasuredHeight(), getMeasuredWidth());
		
		if(!once){
			//������ͼ������
			initBitmap();
			//����ImageView(Item)�Ŀ�ߵ�����
			initItem();
			once = true;
			//�ж��Ƿ�����ʱ
			checkTimeEnable();
		}
		//������Ϸ���ߴ�
		setMeasuredDimension(mWidth, mWidth);
		
	}
	
	private void checkTimeEnable() {
		if(isTimeOff){
			//���ݹؿ����ü�ʱ��
			countTimeBaseLevel();
			//ͨ��Handler����ʱ��
			mHandler.sendEmptyMessage(TIME_CHANGED);
		}
		
	}

	private void countTimeBaseLevel() {
		//ָ������2��ָ��
		mTime = (int) Math.pow(2, mLevel)*60;
		
	}

	/**
	 * ������ͼ,����
	 */
	private void initBitmap() {
		if(mBitmap == null){
			//��ȡͼƬ
			mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.a);
		}
		//ͼƬ��Ƭ
		mItemBitmaps = ImageSplitterUtil.splitImage(mBitmap, mColumn);
		
		//����˳��ʹ��sort 
		Collections.sort(mItemBitmaps,new Comparator<ImagePiece>() {
			public int compare(ImagePiece a, ImagePiece b) {
				return Math.random() > 0.5 ? 1 : -1;
			}
		});
	}
	
	/**
	 * ����ImageView(Item)�Ŀ������
	 */
	private void initItem() {
		
		mItemWidth = (mWidth - mPadding * 2 - mMargin * (mColumn - 1))/mColumn; 
		
		mPuzzleItems = new ImageView[mColumn*mColumn];
		
		int length = mPuzzleItems.length;
		for(int i = 0;i < length;i++){
			ImageView item = new ImageView(getContext());
			item.setOnClickListener(this);
			//�����ͼƬ
			item.setImageBitmap(mItemBitmaps.get(i).getBitmap());
			
			mPuzzleItems[i] = item;
			item.setId(i+1);
			//��Item��tag�д洢index
			item.setTag(i +"_" + mItemBitmaps.get(i).getIndex());
			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(mItemWidth,mItemWidth);
			
			//����Item������϶,ͨ��rightMargin
			//�������һ��
			if((i+1)%mColumn != 0){
				lp.rightMargin = mMargin;
			}
			
			//���ǵ�һ��
			if(i%mColumn !=0 ){
				//���ò��ǵ�һ��ͼƬ�ĵ����ͼƬ
				lp.addRule(RelativeLayout.RIGHT_OF,mPuzzleItems[i-1].getId());
			}
			
			//���ǵ�һ�У�����topMargin��rule
			if((i+1) > mColumn){
				lp.topMargin = mMargin;
				//���ò��ǵ�һ�������ͼƬ
				lp.addRule(RelativeLayout.BELOW, mPuzzleItems[i-mColumn].getId());
			}
			
			addView(item,lp);
		}
	}


	//������н���ͼƬ
	public void onClick(View v) {
		//����ʹ�ö������ʧЧ
		if(isAnining){
			return;
		}
		//���ε��ͬһ��ͼƬ��ȡ��ѡ��״̬
		if(mFirst == v){
			mFirst.setColorFilter(null);
			mFirst = null;
			return;
		}
		//��һ�ε�����ı���ɫ
		if(mFirst == null){
			mFirst = (ImageView) v;
			mFirst.setColorFilter(Color.parseColor("#55FF0000"));
		}else{
			//�����һ��ͼƬ����������ͼƬ
			mSecond = (ImageView) v;
			exchangeView();
		}
		
	}

	/**
	 * ��������ѡ�е�Сͼ Item
	 */
	private void exchangeView() {
		//�ı��һ��ѡ�е�ͼƬ��״̬����Ϊ����
		mFirst.setColorFilter(null);
		
		
		//ͨ��Tag����ȡͼƬ
		final Bitmap firstBitmap = mItemBitmaps.get(getImageIdByTag((String) mFirst.getTag())).getBitmap();
		final Bitmap sencondBitmap = mItemBitmaps.get(getImageIdByTag((String) mSecond.getTag())).getBitmap();
		
		//���춯����
		setUpAnimaLayout();
		
		//������һ��ͼƬ�Ķ�����
		ImageView first = new ImageView(getContext());
		setAnimation(first,mFirst);
		
		//�����ڶ���ͼƬ�Ķ�����
		ImageView second = new ImageView(getContext());
		setAnimation(second,mSecond);
		
		
		//���õ�һ��ͼƬ����
		TranslateAnimation anim = new TranslateAnimation(0, mSecond.getLeft() - mFirst.getLeft(), 
				                                         0, mSecond.getTop()  - mFirst.getTop());
		anim.setDuration(300);
		anim.setFillAfter(true);
		first.startAnimation(anim);
		
		//���õڶ���ͼƬ����
		TranslateAnimation anim2 = new TranslateAnimation(0, -mSecond.getLeft() + mFirst.getLeft(), 
				                                         0,  -mSecond.getTop()  + mFirst.getTop());
		
		anim.setDuration(300);
		anim.setFillAfter(true);
		second.startAnimation(anim2);		
		
		//��������
		anim.setAnimationListener(new AnimationListener() {
			
			public void onAnimationStart(Animation animation) {
				//����ѡ�е�����ͼƬ
				mFirst.setVisibility(View.INVISIBLE);
				mSecond.setVisibility(View.INVISIBLE);
				isAnining = true;
				
			}
			
			public void onAnimationRepeat(Animation animation) {
	
			}
			
			public void onAnimationEnd(Animation animation) {
				//�õ�����ͼƬ��Tag
				String firstTag  = (String) mFirst.getTag();
				String secondTag = (String) mSecond.getTag();
				
				//����ѡ��ͼƬ��λ�� 
				mFirst.setImageBitmap(sencondBitmap);
				mSecond.setImageBitmap(firstBitmap);
				
				//�ı�ѡ��ͼƬ��Tag
				mFirst.setTag(secondTag);
				mSecond.setTag(firstTag);
				
				//�ѽ�����ͼƬ��ʾ
				mFirst.setVisibility(View.VISIBLE);
				mSecond.setVisibility(View.VISIBLE);
	
				//mFirst��mSecondΪ����
				mFirst = mSecond = null;
				//ȡ��������
				mAnimLayout.removeAllViews();
				//�ж��Ƿ�ͨ��
				checkSuccess();
				isAnining = false;
			}
		});
	}


	/**
	 * ����Tag��ȡid
	 * @param tag
	 * @return
	 */
	public int getImageIdByTag(String tag){
		String[] split = tag.split("_");
		return Integer.parseInt(split[0]);
 	}
	/**
	 * ����Tag��ȡindex
	 * @param tag
	 * @return 
	 */
	public int getImageIndexByTag(String tag){
		String[] split = tag.split("_");
		return Integer.parseInt(split[1]);
	}
	
	/**
	 * ���춯����
	 */
	private void setUpAnimaLayout() {
		if(mAnimLayout == null){
			mAnimLayout = new RelativeLayout(getContext()); 
			//�������
			addView(mAnimLayout); 
		}
		
	}
	
	/**
	 * ͼ�����ò����뵽������
	 * @param imageView   ͼ��
	 * @param mImageView  �����ͼƬ
	 */
	public void setAnimation(ImageView imageView,ImageView mImageView){
				//����ͼƬ�Ķ�����
				imageView.setImageBitmap(mItemBitmaps.get(getImageIdByTag((String) mImageView.getTag())).getBitmap());
				//����ͼƬͼ����
				LayoutParams lp = new LayoutParams(mItemWidth,mItemWidth);
				lp.leftMargin = mImageView.getLeft() - mPadding;
				lp.topMargin  = mImageView.getTop()  - mPadding;
				//����ͼ��Ĳ���
				imageView.setLayoutParams(lp);
				//ͼƬ����붯����
				mAnimLayout.addView(imageView);		
			}
	
	/**
	 * �ж��Ƿ�ͨ��
	 */
	private void checkSuccess() {
		boolean isSuccess = true;
		int length = mPuzzleItems.length;
		//ͨ��index���ж��Ƿ�ͨ��
		for(int i = 0;i < length;i++){
			ImageView imageView = mPuzzleItems[i];
			if(getImageIndexByTag((String)imageView.getTag()) != i){
				isSuccess = false;
			}
		}
			if(isSuccess){
				
			isGameSuccess = true ;
			//��ֹ�ظ����ͣ����ϸ�Handler�����
			mHandler.removeMessages(TIME_CHANGED);
			Log.e("TAG", "SUCCESS");
			Toast.makeText(getContext(), "�ɹ�ͨ��", Toast.LENGTH_SHORT).show();
			mHandler.sendEmptyMessage(NEXT_LEVEL);
		 }
		
	}
	
	public void pause(){
		isPause = true;
		mHandler.removeMessages(TIME_CHANGED);
	}
	
	public void resume(){
		if(isPause = true){
			isPause = false;
			mHandler.sendEmptyMessage(TIME_CHANGED);
		}
	}
	
	
	/**
	 * ���¿�ʼ
	 */
	public void restart(){
		isGameOver = false;
		mColumn --;
		nextLevel();
	}
	
	
	
	/**
	 * ������һ��
	 */
	public void nextLevel(){
		//���ǰһ�ص�ͼ
		this.removeAllViews();
		//�Ƴ�������
		mAnimLayout = null ;
		//�����¹ز���
		mColumn ++ ;
		isGameSuccess = false;
		//����ʱ�Ƿ���
		checkTimeEnable();
		//��ʼ����һ��
		initBitmap();
		initItem();
	}
	
	
	/**
	 * �����Ƿ�����ʱ����
	 * @param isTimeOff
	 */
	public void setTimeOff(boolean isTimeOff) {
		this.isTimeOff = isTimeOff;
	}
	
	/**
	 * ��ȡ�����������Сֵ
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
