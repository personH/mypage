package com.txnetwork.mypage.cropimage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.CompoundButton.OnCheckedChangeListener;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.txnetwork.mypage.R;
import com.txnetwork.mypage.customview.MyImageView;
import com.txnetwork.mypage.utils.NativeImageLoader;

import java.io.File;
import java.util.List;

public class ChildAdapter extends BaseAdapter {
    private Point mPoint = new Point(0, 0);//用来封装ImageView的宽和高的对象
    private GridView mGridView;
    private List<String> list;
    protected LayoutInflater mInflater;

    private int mSelectedPos = -1;

    public ChildAdapter(Context context, List<String> list, GridView mGridView) {
        this.list = list;
        this.mGridView = mGridView;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        String path = list.get(position);

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.grid_child_item, null);
            viewHolder = new ViewHolder();
            viewHolder.mImageView = (MyImageView) convertView.findViewById(R.id.child_image);
            viewHolder.mCheckBox = (CheckBox) convertView.findViewById(R.id.child_checkbox);

            //用来监听ImageView的宽和高
            viewHolder.mImageView.setOnMeasureListener(new MyImageView.OnMeasureListener() {
                @Override
                public void onMeasureSize(int width, int height) {
                    mPoint.set(width, height);
                }
            });

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            viewHolder.mImageView.setImageResource(R.drawable.friends_sends_pictures_no);
        }
        viewHolder.mImageView.setTag(path);
        viewHolder.mCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //如果是未选中的CheckBox,则添加动画
                if (mSelectedPos != position && isChecked == true) {
                    //addAnimation(viewHolder.mCheckBox);
                    mSelectedPos = position;
                    notifyDataSetChanged();
                }
            }
        });

        if (mSelectedPos != position) {
            viewHolder.mCheckBox.setChecked(false);
        }

        //利用NativeImageLoader类加载本地图片
        Bitmap bitmap = NativeImageLoader.getInstance().loadNativeImage(path, mPoint, new NativeImageLoader.NativeImageCallBack() {

            @Override
            public void onImageLoader(Bitmap bitmap, String path) {
                ImageView mImageView = (ImageView) mGridView.findViewWithTag(path);
                if (bitmap != null && mImageView != null) {
                    mImageView.setImageBitmap(bitmap);
                }
            }
        });

        if (bitmap != null) {
            viewHolder.mImageView.setImageBitmap(bitmap);
        } else {
            viewHolder.mImageView.setImageResource(R.drawable.friends_sends_pictures_no);
        }
//        Uri uri = Uri.fromFile(new File(path));
//        if (uri != null) {
//            ImageLoader.getInstance().displayImage(uri.toString(), viewHolder.mImageView);
//        }

        return convertView;
    }

    /**
     * 给CheckBox加点击动画，利用开源库nineoldandroids设置动画
     *
     * @param view
     */
    private void addAnimation(View view) {
        float[] vaules = new float[]{0.5f, 0.6f, 0.7f, 0.8f, 0.9f, 1.0f, 1.1f, 1.2f, 1.3f, 1.25f, 1.2f, 1.15f, 1.1f, 1.0f};
        AnimatorSet set = new AnimatorSet();
        set.playTogether(ObjectAnimator.ofFloat(view, "scaleX", vaules),
                ObjectAnimator.ofFloat(view, "scaleY", vaules));
        set.setDuration(150);
        set.start();
    }


    /**
     * 获取选中的Item的position
     *
     * @return
     */
    public int getSelectItem() {
        return mSelectedPos;
    }


    public static class ViewHolder {
        public MyImageView mImageView;
        public CheckBox mCheckBox;
    }


}
