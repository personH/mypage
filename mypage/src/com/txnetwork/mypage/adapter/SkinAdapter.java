package com.txnetwork.mypage.adapter;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.txnetwork.mypage.R;
import com.txnetwork.mypage.datahandler.DataHandler;
import com.txnetwork.mypage.datahandler.DeleteSkinHandler;
import com.txnetwork.mypage.datahandler.OnDataRetrieveListener;
import com.txnetwork.mypage.datahandler.SecretkeyHandler;
import com.txnetwork.mypage.entity.Skin;
import com.txnetwork.mypage.fragment.SkinFragment;
import com.txnetwork.mypage.jsonparser.SecretKeyParserModel;
import com.txnetwork.mypage.utils.*;

import java.io.File;
import java.util.List;

/**
 * Created by Administrator on 2014/12/22.
 */
public class SkinAdapter extends BaseAdapter {

    private List<Skin> resList;
    private Context mContext;
    private LayoutInflater mInflater;
    private boolean isEditAble = false;
    private Handler mHandler;
    private String mPicid;
    private int mDeletePosition;

    public SkinAdapter(Context context, List<Skin> resList, boolean isEditAble, Handler handler) {
        mContext = context;
        this.mHandler = handler;
        this.isEditAble = isEditAble;
        this.resList = resList;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
        final ViewHolder holder;

        if (null == convertView) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.web_skin_item, null);
            holder.imageView = (ImageView) convertView.findViewById(R.id.image);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.size = (TextView) convertView.findViewById(R.id.size);
            holder.deleteView = (ImageView) convertView.findViewById(R.id.delete);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
//            holder.imageView.setImageResource(R.drawable.friends_sends_pictures_no);
        }

        if (isEditAble) {
            //1.删除本地皮肤
            //2.删除服务器皮肤
            //3.本地皮肤,有上传成功的,有上传未成功的
            //4.添加皮肤,上传成功的,不允许上传,未上传成功的继续上传
            holder.deleteView.setVisibility(View.VISIBLE);
            final int pos = position;
            holder.deleteView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    mPicid = resList.get(pos).getPicid();

                    if (StringUtil.isNotNull(mPicid)) {
                        String aeskey = TxNetworkUtil.getUserKey(mContext);
                        if (StringUtil.isNotNull(aeskey)) {
                            mDeletePosition = pos;
                            deleteDiyTheme(mPicid, aeskey);
                        } else {
                            reqAppkey();
                        }
                    } else {
                        String picdir = resList.get(pos).getUrl();
                        SkinUtils.deleteDiySkin(mContext, picdir);
                        resList.remove(pos);
                        notifyDataSetChanged();
                    }
                }
            });
        } else {
            holder.deleteView.setVisibility(View.GONE);
        }

        String fileName = resList.get(position).getName();
        if (fileName != null && !"".equals(fileName)) {
            if (fileName.length() > 5) {
                fileName = fileName.substring(0, 5);//截取图片名称
            }
        } else {
            fileName = "unknown";
        }

        if (Skin.OTHERS == resList.get(position).getType()) {
            //添加按钮
            holder.imageView.setBackgroundColor(mContext.getResources().getColor(R.color.alpha_background_color));
            holder.imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            holder.imageView.setImageResource(R.drawable.add_diy_skin);
            holder.name.setVisibility(View.INVISIBLE);
            holder.size.setVisibility(View.INVISIBLE);
        } else if (Skin.WEB_SKIN == resList.get(position).getType()) {

            //网络皮肤
            holder.name.setText(fileName);
            holder.size.setText(resList.get(position).getSize() + "KB");
            ImageLoader.getInstance().displayImage(resList.get(position).getS_url(),
                    holder.imageView, UILUtils.options, null, null);

        } else if (Skin.DIY_SKIN == resList.get(position).getType()) {

            //自定义皮肤
            holder.name.setText(fileName);
            holder.size.setText(resList.get(position).getSize() + "KB");
            String filePath = resList.get(position).getUrl();

            Uri uri = Uri.fromFile(new File(filePath));
            if (uri != null) {
                ImageLoader.getInstance().displayImage(uri.toString(), holder.imageView);
            }

        }


        return convertView;
    }

    class ViewHolder {
        private ImageView imageView;
        private TextView name;
        private TextView size;
        private ImageView deleteView;
    }


    private void deleteDiyTheme(String picid, String aesKey) {
        DeleteSkinHandler deleteSkinHandler = new DeleteSkinHandler(mContext);
        deleteSkinHandler.setOnDataRetrieveListener(onDataRetrieveListener);
        deleteSkinHandler.startNetWork(picid, aesKey);
    }

    /**
     * 获取手机密钥
     */
    private void reqAppkey() {
        SecretkeyHandler secretkeyHandler = new SecretkeyHandler(mContext);
        secretkeyHandler.setOnDataRetrieveListener(onDataRetrieveListener);
        secretkeyHandler.startNetWork();
    }

    OnDataRetrieveListener onDataRetrieveListener = new OnDataRetrieveListener() {
        @Override
        public void onDataRetrieve(DataHandler dataHandler, int resultCode, Object data) {
            switch (resultCode) {
                case ConstantPool.GET_SECRET_KEY_OK:
                    String resutStr = String.valueOf(data);
                    LogUtils.putLog(mContext, "请求密钥返回==" + resutStr);
                    SecretKeyParserModel.parseMainJson(mContext, mHandler, resutStr, SkinFragment.CHECK_KEY_SUC, SkinFragment.CHECK_KEY_FAIL);
                    break;
                case ConstantPool.GET_SECRET_KEY_ERROR:
                    //disDialog();
                    LogUtils.putLog(mContext, "请求密钥返回==" + data);
                    //mHandler.sendEmptyMessage(CHECK_KEY_FAIL);
                    break;
                case ConstantPool.DELETE_DIY_THEME_SUC:
                    resList.remove(mDeletePosition);
                    notifyDataSetChanged();
                    LogUtils.putLog(mContext, "删除成功");
                    break;
                case ConstantPool.DELETE_DIY_THEME_FAIL:
                    String errorMsg = String.valueOf(data);
                    Toast.makeText(mContext, errorMsg, Toast.LENGTH_SHORT).show();
                    LogUtils.putLog(mContext, "删除失败");
                    break;

            }
        }
    };
}
