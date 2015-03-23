package com.txnetwork.mypage.adapter;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.txnetwork.mypage.R;
import com.txnetwork.mypage.entity.DownloadedSkin;
import com.txnetwork.mypage.utils.SharedUtils;
import com.txnetwork.mypage.utils.SkinUtils;
import com.txnetwork.mypage.utils.TxNetwork;

import java.io.File;
import java.util.List;

/**
 * Created by Administrator on 2014/12/22.
 * <p/>
 * 1.要有状态位,标识是本地的图片资源,还是下载下来的外部图片资源
 * 本地的保存,R.drawable.XX
 * 外部保存图片的路径,解析成Bitmap,设置背景图片
 * 2.本地的不允许删除,只允许删除外部的
 */
public class DownloadedSkinAdapter extends BaseAdapter {

    private List<DownloadedSkin> resList;
    private Context mContext;
    private LayoutInflater mInflater;
    private int drawableId;
    private String mCurrentSkinPicPath;
    private boolean isBackgroundOut;
    private boolean isEditAble = false;
    private boolean current_local = false;
    private int mSelectPosition = -1;

    public DownloadedSkinAdapter(Context context, List<DownloadedSkin> resList) {
        mContext = context;
        this.resList = resList;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        drawableId = SharedUtils.getBackGrounds(mContext);
        mCurrentSkinPicPath = SharedUtils.getBackGroundsOut(mContext);
        isBackgroundOut = SharedUtils.getIsBackGroundsOut(mContext);
    }

    public DownloadedSkinAdapter(Context context, List<DownloadedSkin> resList, boolean isEditAble) {
        this.isEditAble = isEditAble;
        mContext = context;
        this.resList = resList;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        drawableId = SharedUtils.getBackGrounds(mContext);
        mCurrentSkinPicPath = SharedUtils.getBackGroundsOut(mContext);
        isBackgroundOut = SharedUtils.getIsBackGroundsOut(mContext);
    }

    @Override
    public int getCount() {
        if (resList != null) {
            return resList.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (resList != null) {
            return resList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (null == convertView) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.downloaded_theme_item, null);
            holder.imageView = (ImageView) convertView.findViewById(R.id.image);
            holder.checkedView = (ImageView) convertView.findViewById(R.id.checked);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.deleteView = (ImageView) convertView.findViewById(R.id.delete);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
//            holder.imageView.setImageResource(R.drawable.friends_sends_pictures_no);
        }

        if (isEditAble) {
            holder.deleteView.setVisibility(View.VISIBLE);
            final int pos = position;
            holder.deleteView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext, "删除", Toast.LENGTH_SHORT).show();
                    final String filePath = resList.get(pos).getPicDirectory();
                    resList.remove(pos);
                    notifyDataSetChanged();
                    SkinUtils.deleteDownloadedSkin(mContext, filePath);
                }
            });
        } else {
            holder.deleteView.setVisibility(View.GONE);
        }

        String isLocalTheme = resList.get(position).getIsLocalTheme();

        String picPath = resList.get(position).getPicDirectory();

        if (DownloadedSkin.TRUE.equals(isLocalTheme)) {
            holder.imageView.setImageResource(resList.get(position).getDrawbleId());
            holder.deleteView.setVisibility(View.GONE);
        } else {
            Uri uri = Uri.fromFile(new File(picPath));
            if (uri != null) {
                ImageLoader.getInstance().displayImage(uri.toString(), holder.imageView);
            }
        }

        holder.name.setText(resList.get(position).getName());

        if (isBackgroundOut) {
            if (mCurrentSkinPicPath.equals(picPath)) {
                //holder.checkedView.setVisibility(View.VISIBLE);
                holder.deleteView.setVisibility(View.GONE);
                mSelectPosition = position;
            }
        } else {
            //本地默认皮肤
            if (!current_local) {
                if (drawableId == R.drawable.default_background) {
                    //holder.checkedView.setVisibility(View.VISIBLE);
                    holder.deleteView.setVisibility(View.GONE);
                    current_local = true;
                    mSelectPosition = position;
                }
            }
        }
        if (mSelectPosition == position) {
            holder.checkedView.setVisibility(View.VISIBLE);
        } else {
            holder.checkedView.setVisibility(View.INVISIBLE);
        }
        return convertView;
    }

    class ViewHolder {
        private ImageView imageView;
        private ImageView checkedView;
        private TextView name;
        private ImageView deleteView;
    }

    public int getmSelectPosition() {
        return mSelectPosition;
    }

    public void setmSelectPosition(int mSelectPosition) {
        this.mSelectPosition = mSelectPosition;
    }

    public boolean isBackgroundOut() {
        return isBackgroundOut;
    }

    public void setBackgroundOut(boolean isBackgroundOut) {
        this.isBackgroundOut = isBackgroundOut;
    }

    public String getmCurrentSkinPicPath() {
        return mCurrentSkinPicPath;
    }

    public void setmCurrentSkinPicPath(String mCurrentSkinPicPath) {
        this.mCurrentSkinPicPath = mCurrentSkinPicPath;
    }

    public int getDrawableId() {
        return drawableId;
    }

    public void setDrawableId(int drawableId) {
        this.drawableId = drawableId;
    }
}
