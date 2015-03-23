package com.txnetwork.mypage.dsgv;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.*;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.*;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.txnetwork.mypage.R;
import com.txnetwork.mypage.dslv.DragSortController;
import com.txnetwork.mypage.dslv.DragSortListView;
import com.txnetwork.mypage.entity.TypedUrlList;
import com.txnetwork.mypage.entity.UrlAddr;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by hcz on 2015/1/15.
 */
public class DragGridView extends GridView {
    /**
     * DragGridView的item长按响应的时间， 默认是600毫秒，也可以自行设置
     */
    private long dragResponseMS = 600;

    /**
     * 是否可以拖拽，默认不可以
     */
    private boolean isDrag = true;

    private int mDownX;
    private int mDownY;
    private int moveX;
    private int moveY;
    private int rawX;//相对于屏幕的位置
    private int rawY;//相对于屏幕的位置
    /**
     * 正在拖拽的position
     */
    private int mDragPosition;

    /**
     * 刚开始拖拽的item对应的View
     */
    private View mStartDragItemView = null;

    /**
     * 用于拖拽的镜像，这里直接用一个ImageView
     */
    private ImageView mDragImageView;

    /**
     * 震动器
     */
    private Vibrator mVibrator;

    private WindowManager mWindowManager;
    /**
     * item镜像的布局参数
     */
    private WindowManager.LayoutParams mWindowLayoutParams;

    /**
     * 我们拖拽的item对应的Bitmap
     */
    private Bitmap mDragBitmap;

    /**
     * 按下的点到所在item的上边缘的距离
     */
    private int mPoint2ItemTop;

    /**
     * 按下的点到所在item的左边缘的距离
     */
    private int mPoint2ItemLeft;

    /**
     * DragGridView距离屏幕顶部的偏移量
     */
    private int mOffset2Top;

    /**
     * DragGridView距离屏幕左边缘的偏移量
     */
    private int mOffset2Left;

    /**
     * 状态栏的高度
     */
    private int mStatusHeight;

    /**
     * DragGridView自动向下滚动的边界值
     */
    private int mDownScrollBorder;

    /**
     * DragGridView自动向上滚动的边界值
     */
    private int mUpScrollBorder;

    /**
     * DragGridView自动滚动的速度
     */
    private static final int speed = 20;

    private boolean mAnimationEnd = true;

    private DragGridBaseAdapter mDragAdapter;
    private int mNumColumns;
    private int mColumnWidth;
    private boolean mNumColumnsSet;
    private int mHorizontalSpacing;

    private int typeBarHeight;

    private boolean isEditing = false;//添加本地编辑状态
    private int parentPosition;//子gridview相对于父listview的位置

    public DragGridView(Context context) {
        this(context, null);
    }

    public DragGridView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mStatusHeight = getStatusHeight(context); //获取状态栏的高度

        if (!mNumColumnsSet) {
            mNumColumns = AUTO_FIT;
        }

    }

    private Handler mHandler = new Handler();

    //用来处理是否为长按的Runnable
    private Runnable mLongClickRunnable = new Runnable() {

        @Override
        public void run() {
            isDrag = true; //可以拖拽

            mStartDragItemView.setVisibility(View.INVISIBLE);//隐藏改item

            //
            if (!isEditing) {//当前不是编辑状态,设置父view为编辑状态
                mVibrator.vibrate(50); //非编辑状态下,震动一下
                dragResponseMS = 10;//减少长按时间
                DragSortListView dslv = (DragSortListView) getParent().getParent().getParent();
                DragSortController mController = buildController(dslv);
                dslv.setFloatViewManager(mController);
                dslv.setOnTouchListener(mController);
                dslv.setEditing(true);
                dslv.invalidate();

                //显示添加新分类的按钮
                LinearLayout linearLayout = (LinearLayout) dslv.getParent();
                linearLayout.findViewById(R.id.add_type).setVisibility(VISIBLE);
                linearLayout.invalidate();
                isEditing = true;
            } else {
                //根据我们按下的点显示item镜像
                createDragImage(mDragBitmap, mDownX, mDownY);
            }
        }
    };

    public DragSortController buildController(DragSortListView dslv) {
        DragSortController controller = new DragSortController(dslv);
        controller.setDragHandleId(R.id.drag_handle);
        controller.setClickRemoveId(R.id.click_remove);
        controller.setRemoveEnabled(false);//不允许删除
        controller.setSortEnabled(true);
        controller.setDragInitMode(DragSortController.ON_DRAG);
        controller.setRemoveMode(1);
        return controller;
    }


    @Override
    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(adapter);

        if (adapter instanceof DragGridBaseAdapter) {
            mDragAdapter = (DragGridBaseAdapter) adapter;
        } else {
            throw new IllegalStateException("the adapter must be implements DragGridAdapter");
        }
    }


    @Override
    public void setNumColumns(int numColumns) {
        super.setNumColumns(numColumns);
        mNumColumnsSet = true;
        this.mNumColumns = numColumns;
    }


    @Override
    public void setColumnWidth(int columnWidth) {
        super.setColumnWidth(columnWidth);
        mColumnWidth = columnWidth;
    }


    @Override
    public void setHorizontalSpacing(int horizontalSpacing) {
        super.setHorizontalSpacing(horizontalSpacing);
        this.mHorizontalSpacing = horizontalSpacing;
    }


    /**
     * 若设置为AUTO_FIT，计算有多少列
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mNumColumns == AUTO_FIT) {
            int numFittedColumns;
            if (mColumnWidth > 0) {
                int gridWidth = Math.max(MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft()
                        - getPaddingRight(), 0);
                numFittedColumns = gridWidth / mColumnWidth;
                if (numFittedColumns > 0) {
                    while (numFittedColumns != 1) {
                        if (numFittedColumns * mColumnWidth + (numFittedColumns - 1)
                                * mHorizontalSpacing > gridWidth) {
                            numFittedColumns--;
                        } else {
                            break;
                        }
                    }
                } else {
                    numFittedColumns = 1;
                }
            } else {
                numFittedColumns = 2;
            }
            mNumColumns = numFittedColumns;
        }

        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);

        super.onMeasure(widthMeasureSpec, expandSpec);
    }

    /**
     * 设置响应拖拽的毫秒数，默认是1000毫秒
     *
     * @param dragResponseMS
     */
    public void setDragResponseMS(long dragResponseMS) {
        this.dragResponseMS = dragResponseMS;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = (int) ev.getX();
                mDownY = (int) ev.getY();

                //根据按下的X,Y坐标获取所点击item的position
                mDragPosition = pointToPosition(mDownX, mDownY);

                if (mDragPosition == AdapterView.INVALID_POSITION) {
                    return super.dispatchTouchEvent(ev);
                }

                if (mDragPosition < mDragAdapter.getSize() - 1) {
                    //使用Handler延迟dragResponseMS执行mLongClickRunnable
                    mHandler.postDelayed(mLongClickRunnable, dragResponseMS);

                    //根据position获取该item所对应的View
                    mStartDragItemView = getChildAt(mDragPosition - getFirstVisiblePosition());

                    //下面这几个距离大家可以参考我的博客上面的图来理解下
                    mPoint2ItemTop = mDownY - mStartDragItemView.getTop();
                    mPoint2ItemLeft = mDownX - mStartDragItemView.getLeft();

                    mOffset2Top = (int) (ev.getRawY() - mDownY);
                    mOffset2Left = (int) (ev.getRawX() - mDownX);

                    //获取DragGridView自动向上滚动的偏移量，小于这个值，DragGridView向下滚动
                    mDownScrollBorder = getHeight() / 5;
                    //获取DragGridView自动向下滚动的偏移量，大于这个值，DragGridView向上滚动
                    mUpScrollBorder = getHeight() * 4 / 5;

                    //开启mDragItemView绘图缓存
                    mStartDragItemView.setDrawingCacheEnabled(true);
                    //获取mDragItemView在缓存中的Bitmap对象
                    mDragBitmap = Bitmap.createBitmap(mStartDragItemView.getDrawingCache());
                    //这一步很关键，释放绘图缓存，避免出现重复的镜像
                    mStartDragItemView.destroyDrawingCache();
                } else {
                    //自定义添加按钮,不能拖动
                }

                break;
            case MotionEvent.ACTION_MOVE:
                int moveX = (int) ev.getX();
                int moveY = (int) ev.getY();

                //如果我们在按下的item上面移动，只要不超过item的边界我们就不移除mRunnable
                if (!isTouchInItem(mStartDragItemView, moveX, moveY)) {
                    mHandler.removeCallbacks(mLongClickRunnable);
                }
                break;
            case MotionEvent.ACTION_UP:
                mHandler.removeCallbacks(mLongClickRunnable);
                mHandler.removeCallbacks(mScrollRunnable);
                break;
        }
        //getParent().getParent().getParent().requestDisallowInterceptTouchEvent(true);
        getParent().requestDisallowInterceptTouchEvent(true);//父组件不要拦截事件
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (isEditing) {
            dragResponseMS = 10;
            for (int i = 0; i < getChildCount(); i++) {
                View childView = getChildAt(i);
                DragGridAdapter.ViewHolder viewHolder = (DragGridAdapter.ViewHolder) childView.getTag();
                if ("".equals(viewHolder.getUrl()) && "添加".equals(viewHolder.getName())) {
                    //添加按钮不能删除,最好是删除添加按钮
                } else {
                    childView.findViewById(R.id.edit).setVisibility(View.VISIBLE);
                    childView.findViewById(R.id.delete).setVisibility(View.VISIBLE);
                }
                childView.setOnClickListener(null);//不响应点击事件
            }
        }
    }

    /**
     * 是否点击在GridView的item上面
     *
     * @param dragView
     * @param x
     * @param y
     * @return
     */
    private boolean isTouchInItem(View dragView, int x, int y) {
        if (dragView == null) {
            return false;
        }
        int leftOffset = dragView.getLeft();
        int topOffset = dragView.getTop();
        if (x < leftOffset || x > leftOffset + dragView.getWidth()) {
            return false;
        }

        if (y < topOffset || y > topOffset + dragView.getHeight()) {
            return false;
        }

        return true;
    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (isDrag && mDragImageView != null) {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_MOVE:
                    moveX = (int) ev.getX();
                    moveY = (int) ev.getY();
                    rawX = (int) ev.getRawX();
                    rawY = (int) ev.getRawY();
                    //拖动item
                    onDragItem(moveX, moveY);
                    break;
                case MotionEvent.ACTION_UP:
                    onStopDrag();
                    isDrag = false;
                    break;
            }
            return true;
        }
        return super.onTouchEvent(ev);
    }


    /**
     * 创建拖动的对象
     *
     * @param bitmap
     * @param downX  按下的点相对父控件的X坐标
     * @param downY  按下的点相对父控件的Y坐标
     */
    private void createDragImage(Bitmap bitmap, int downX, int downY) {
        mWindowLayoutParams = new WindowManager.LayoutParams();
        mWindowLayoutParams.format = PixelFormat.TRANSLUCENT; //图片之外的其他地方透明
        mWindowLayoutParams.gravity = Gravity.TOP | Gravity.LEFT;
        mWindowLayoutParams.x = downX - mPoint2ItemLeft + mOffset2Left;
        mWindowLayoutParams.y = downY - mPoint2ItemTop + mOffset2Top - mStatusHeight;
        mWindowLayoutParams.alpha = 0.55f; //透明度
        mWindowLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mWindowLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mWindowLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;

        mDragImageView = new ImageView(getContext());
        mDragImageView.setImageBitmap(bitmap);
        mWindowManager.addView(mDragImageView, mWindowLayoutParams);
    }

    /**
     * 从界面上面移动拖动镜像
     */
    private void removeDragImage() {
        if (mDragImageView != null) {
            mWindowManager.removeView(mDragImageView);
            mDragImageView = null;
        }
    }


    /**
     * 拖动item，在里面实现了item镜像的位置更新，item的相互交换以及GridView的自行滚动
     *
     * @param moveX
     * @param moveY
     */
    private void onDragItem(int moveX, int moveY) {
        //mPoint2ItemLeft
        mWindowLayoutParams.x = moveX - mPoint2ItemLeft + mOffset2Left;
//        mWindowLayoutParams.x = rawX - mPoint2ItemLeft;
//        mWindowLayoutParams.y = rawY - mPoint2ItemTop - mStatusHeight;
        mWindowLayoutParams.y = moveY - mPoint2ItemTop + mOffset2Top - mStatusHeight;

        Log.d("TAG", "mWindowLayoutParams.x=" + mWindowLayoutParams.x + " mWindowLayoutParams.y=" + mWindowLayoutParams.y);
        Log.d("TAG", "rawX=" + rawX + " rawY=" + rawY);
        Log.d("TAG", "moveX=" + moveX + " moveY=" + moveY);
        Log.d("TAG", "height=" + getHeight() + " width=" + getWidth());
        Log.d("TAG", "typeBarHeight=" + typeBarHeight);

        mWindowManager.updateViewLayout(mDragImageView, mWindowLayoutParams); //更新镜像位置

        LinearLayout parent = (LinearLayout) getParent();
        typeBarHeight = parent.findViewById(R.id.drag_handle).getHeight();

        if (moveY + typeBarHeight < 0 || moveY > getHeight() + typeBarHeight) {

            //越过当前gridview的范围,跳到另外的gridview
            DragSortListView dslv = (DragSortListView) getParent().getParent().getParent();
            ListAdapter dragSortAdapter = dslv.getAdapter();
            //此处的movY是相对于gridview 的位置
            //要找到当前gridview 是属于哪一个dslv的一项

            int height = moveY;
            if (parentPosition > 0) {
                for (int i = 0; i < parentPosition; i++) {
                    height = dslv.getChildAt(i).getHeight() + height;
                }
            }
            int dslvPosition = dslv.pointToPosition(moveX, height);

            Log.d("TAG", "dslvPosition=" + dslvPosition);

            if (dslvPosition != -1) {
                UrlAddr urlAddr = mDragAdapter.getItemAtPostion(mDragPosition);

                mDragAdapter.deleteItem(mDragPosition);//删除
                TypedUrlList typedUrlList = (TypedUrlList) dragSortAdapter.getItem(dslvPosition);
                DragGridAdapter dragGridAdapter = typedUrlList.getDragGridAdapter();
                dragGridAdapter.addItem(urlAddr);

                DragGridView dragGridView = (DragGridView) dslv.getChildAt(dslvPosition).findViewById(R.id.dragGridView);

//                dragGridView.mPoint2ItemLeft = this.mPoint2ItemLeft;
//                dragGridView.mOffset2Left = this.mOffset2Left;
//                dragGridView.mPoint2ItemTop = this.mPoint2ItemTop;
//                dragGridView.mOffset2Top = this.mOffset2Top;
//                dragGridView.mWindowLayoutParams = this.mWindowLayoutParams;
//                dragGridView.mDragImageView = this.mDragImageView;
//
//                dragGridView.onDragItem(moveX, dslv.getChildAt(dslvPosition).getHeight() + moveY);

                //dragGridView.isDrag = true;

                //dragGridView.createDragImage(mDragBitmap, moveX, dslv.getChildAt(dslvPosition).getHeight() + moveY);
                //dragGridView.onDragItem(moveX, moveY);

                onStopDrag();
                isDrag = false;
            }

        } else {
            onSwapItem(moveX, moveY);
        }
        //检测到边缘,GridView自动滚动
        mHandler.post(mScrollRunnable);
    }


    /**
     * 当moveY的值大于向上滚动的边界值，触发GridView自动向上滚动
     * 当moveY的值小于向下滚动的边界值，触发GridView自动向下滚动
     * 否则不进行滚动
     */
    private Runnable mScrollRunnable = new Runnable() {

        @Override
        public void run() {
            int scrollY;
            if (getFirstVisiblePosition() == 0 || getLastVisiblePosition() == getCount() - 1) {
                mHandler.removeCallbacks(mScrollRunnable);
            }

            if (moveY > mUpScrollBorder) {
                scrollY = speed;
                mHandler.postDelayed(mScrollRunnable, 25);
            } else if (moveY < mDownScrollBorder) {
                scrollY = -speed;
                mHandler.postDelayed(mScrollRunnable, 25);
            } else {
                scrollY = 0;
                mHandler.removeCallbacks(mScrollRunnable);
            }

            smoothScrollBy(scrollY, 10);
        }
    };

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev);
    }

    /**
     * 交换item,并且控制item之间的显示与隐藏效果
     *
     * @param moveX
     * @param moveY
     */
    private void onSwapItem(int moveX, int moveY) {
        ///获取我们手指移动到的那个item的position
        final int tempPosition = pointToPosition(moveX, moveY);

        //假如tempPosition 改变了并且tempPosition不等于-1,则进行交换
        if (tempPosition != mDragPosition && tempPosition != AdapterView.INVALID_POSITION && mAnimationEnd && tempPosition != mDragAdapter.getSize() - 1) {
            mDragAdapter.reorderItems(mDragPosition, tempPosition);
            mDragAdapter.setHideItem(tempPosition);

            final ViewTreeObserver observer = getViewTreeObserver();
            observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {

                @Override
                public boolean onPreDraw() {
                    observer.removeOnPreDrawListener(this);
                    animateReorder(mDragPosition, tempPosition);
                    mDragPosition = tempPosition;//当前位置为移动后的位置
                    return true;
                }
            });

        } else {
            //非有效位置
//            DragSortListView dslv = (DragSortListView) getParent().getParent().getParent();
//            ListAdapter dragSortAdapter = dslv.getAdapter();
//
//            int dslvPosition = dslv.pointToPosition(moveX, moveY);
//            TypedUrlList typedUrlList = (TypedUrlList) dragSortAdapter.getItem(dslvPosition);
//            Log.d("typedUrlList", "typedUrlList.name=" + typedUrlList.getName() +
//                    "typedUrlList.type=" + typedUrlList.getType() + "typedUrlList.list=" + typedUrlList.getUrlAddrTypeList());

        }
    }

    /**
     * 创建移动动画
     *
     * @param view
     * @param startX
     * @param endX
     * @param startY
     * @param endY
     * @return
     */
    private AnimatorSet createTranslationAnimations(View view, float startX,
                                                    float endX, float startY, float endY) {
        ObjectAnimator animX = ObjectAnimator.ofFloat(view, "translationX",
                startX, endX);
        ObjectAnimator animY = ObjectAnimator.ofFloat(view, "translationY",
                startY, endY);
        AnimatorSet animSetXY = new AnimatorSet();
        animSetXY.playTogether(animX, animY);
        return animSetXY;
    }


    /**
     * item的交换动画效果
     *
     * @param oldPosition
     * @param newPosition
     */
    private void animateReorder(final int oldPosition, final int newPosition) {
        boolean isForward = newPosition > oldPosition;
        List<Animator> resultList = new LinkedList<Animator>();
        if (isForward) {
            for (int pos = oldPosition; pos < newPosition; pos++) {
                View view = getChildAt(pos - getFirstVisiblePosition());
                System.out.println(pos);

                if ((pos + 1) % mNumColumns == 0) {
                    resultList.add(createTranslationAnimations(view, -view.getWidth() * (mNumColumns - 1), 0, view.getHeight(), 0));
                } else {
                    resultList.add(createTranslationAnimations(view, view.getWidth(), 0, 0, 0));
                }
            }
        } else {
            for (int pos = oldPosition; pos > newPosition; pos--) {
                View view = getChildAt(pos - getFirstVisiblePosition());
                if ((pos + mNumColumns) % mNumColumns == 0) {
                    resultList.add(createTranslationAnimations(view, view.getWidth() * (mNumColumns - 1), 0, -view.getHeight(), 0));
                } else {
                    resultList.add(createTranslationAnimations(view, -view.getWidth(), 0, 0, 0));
                }
            }
        }

        AnimatorSet resultSet = new AnimatorSet();
        resultSet.playTogether(resultList);
        resultSet.setDuration(300);
        resultSet.setInterpolator(new AccelerateDecelerateInterpolator());
        resultSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                mAnimationEnd = false;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mAnimationEnd = true;
            }
        });
        resultSet.start();
    }

    /**
     * 停止拖拽我们将之前隐藏的item显示出来，并将镜像移除
     */
    private void onStopDrag() {
        View view = getChildAt(mDragPosition - getFirstVisiblePosition());
        if (view != null) {
            view.setVisibility(View.VISIBLE);
        }
        mDragAdapter.setHideItem(-1);
        removeDragImage();
    }

    /**
     * 获取状态栏的高度
     *
     * @param context
     * @return
     */
    private static int getStatusHeight(Context context) {
        int statusHeight = 0;
        Rect localRect = new Rect();
        ((Activity) context).getWindow().getDecorView().getWindowVisibleDisplayFrame(localRect);
        statusHeight = localRect.top;
        if (0 == statusHeight) {
            Class<?> localClass;
            try {
                localClass = Class.forName("com.android.internal.R$dimen");
                Object localObject = localClass.newInstance();
                int i5 = Integer.parseInt(localClass.getField("status_bar_height").get(localObject).toString());
                statusHeight = context.getResources().getDimensionPixelSize(i5);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return statusHeight;
    }

    public boolean isEditing() {
        return isEditing;
    }

    public void setEditing(boolean isEditing) {
        this.isEditing = isEditing;
    }

    public void setParentPosition(int postion) {
        parentPosition = postion;
    }

    public int getParentPosition() {
        return parentPosition;
    }
}
