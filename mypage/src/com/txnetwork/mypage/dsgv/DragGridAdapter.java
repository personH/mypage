package com.txnetwork.mypage.dsgv;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.txnetwork.mypage.R;
import com.txnetwork.mypage.entity.UrlAddr;
import com.txnetwork.mypage.utils.TxNetworkUtil;
import com.txnetwork.mypage.utils.UILUtils;

import java.util.Collections;
import java.util.List;

/**
 * Created by Administrator on 2015/1/15.
 */
public class DragGridAdapter extends BaseAdapter implements DragGridBaseAdapter {

    private LayoutInflater mInflater;
    private int mHidePosition = -1;

    private int mDeletePosition = -1;
    private List<UrlAddr> urlAddrList;
    private int type;
    private String typeName;
    private Context mContext;
    private boolean isEditing = false;

    public DragGridAdapter(Context context, List<UrlAddr> urlAddrList, int type, String typeName) {
        this.mContext = context;
        this.urlAddrList = urlAddrList;
        mInflater = LayoutInflater.from(context);
        this.type = type;
        this.typeName = typeName;
    }

    @Override
    public int getCount() {
        return urlAddrList.size();
    }

    @Override
    public Object getItem(int position) {
        return urlAddrList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * 由于复用convertView导致某些item消失了，所以这里不复用item
     */
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        convertView = mInflater.inflate(R.layout.gridview_item, null);
        ImageView mImageView = (ImageView) convertView.findViewById(R.id.image);
        TextView mTextView = (TextView) convertView.findViewById(R.id.name);
        ImageView delete = (ImageView) convertView.findViewById(R.id.delete);
        ImageView edit = (ImageView) convertView.findViewById(R.id.edit);
        mTextView.setText(urlAddrList.get(position).getName());

        if (position == mHidePosition) {
            convertView.setVisibility(View.INVISIBLE);
        }

//        if (position == mDeletePosition) {
//            convertView.setVisibility(View.GONE);
//        }

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(mContext, "delete", Toast.LENGTH_SHORT).show();
                urlAddrList.remove(position);
                notifyDataSetChanged();
            }
        });
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(mContext, "edit", Toast.LENGTH_SHORT).show();
                editUrl(urlAddrList.get(position), position);
            }
        });


        if ("".equals(urlAddrList.get(position).getUrl()) && "添加".equals(urlAddrList.get(position).getName())) {
            mImageView.setImageResource(R.drawable.add);
            //mImageView.setPadding(10, 10, 10, 10);
            //mImageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        } else {
            ImageLoader.getInstance().displayImage(urlAddrList.get(position).getIcon(),
                    mImageView, UILUtils.DhOptions);
        }

        ViewHolder holder = new ViewHolder();
        holder.setName(urlAddrList.get(position).getName());
        holder.setUrl(urlAddrList.get(position).getUrl());
        holder.setType(urlAddrList.get(position).getType());
        convertView.setTag(holder);

        return convertView;
    }

    public class ViewHolder {
        private String name;
        private String url;
        private int type;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }
    }


    @Override
    public void reorderItems(int oldPosition, int newPosition) {

        UrlAddr temp = urlAddrList.get(oldPosition);
        if (oldPosition < newPosition) {
            for (int i = oldPosition; i < newPosition; i++) {
                Collections.swap(urlAddrList, i, i + 1);
            }
        } else if (oldPosition > newPosition) {
            for (int i = oldPosition; i > newPosition; i--) {
                Collections.swap(urlAddrList, i, i - 1);
            }
        }
        urlAddrList.set(newPosition, temp);
    }

    @Override
    public void setHideItem(int hidePosition) {
        this.mHidePosition = hidePosition;

        notifyDataSetChanged();
    }


    @Override
    public int getSize() {
        return urlAddrList.size();
    }

    @Override
    public UrlAddr getItemAtPostion(int postion) {
        return urlAddrList.get(postion);
    }

    @Override
    public void deleteItem(int position) {
        mDeletePosition = position;
        urlAddrList.remove(position);
        notifyDataSetChanged();
    }

    @Override
    public void addItem(UrlAddr urlAddr) {
        urlAddrList.remove(urlAddrList.size() - 1);
        urlAddrList.add(urlAddr);
        urlAddrList.add(new UrlAddr("添加", "", "", type, urlAddrList.size()));
        notifyDataSetChanged();
    }

    /**
     * 添加导航
     *
     * @param urlAddr
     */
    private void editUrl(UrlAddr urlAddr, final int position) {
        final int type = urlAddr.getType();

        final Dialog dialog = new Dialog(mContext, R.style.loading_dialog);
        dialog.setContentView(R.layout.dialog_main);

        TextView okBtn = (TextView) dialog.findViewById(R.id.ok);
        TextView cancelBtn = (TextView) dialog.findViewById(R.id.cancel);
        final EditText title = (EditText) dialog.findViewById(R.id.title);
        final EditText url = (EditText) dialog.findViewById(R.id.url);

        title.setText(urlAddr.getName());
        url.setText(urlAddr.getUrl());

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(title.getText()) || TextUtils.isEmpty(url.getText())) {
                    Toast.makeText(mContext, "请输入内容", Toast.LENGTH_SHORT).show();
                } else {
                    String nameStr = title.getText().toString();
                    String urlStr = url.getText().toString();
                    urlAddrList.remove(position);
                    urlAddrList.add(position, new UrlAddr(nameStr, urlStr, TxNetworkUtil.getUrlIcon(urlStr), type, position));
                    notifyDataSetChanged();
                    dialog.dismiss();
                }
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
