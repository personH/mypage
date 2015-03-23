package com.txnetwork.mypage.dslv;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.txnetwork.mypage.R;
import com.txnetwork.mypage.activity.MainActivity;
import com.txnetwork.mypage.datacenter.OperateDBUtils;
import com.txnetwork.mypage.dsgv.DragGridAdapter;
import com.txnetwork.mypage.dsgv.DragGridView;
import com.txnetwork.mypage.entity.TypedUrlList;
import com.txnetwork.mypage.entity.UrlAddr;
import com.txnetwork.mypage.utils.StringUtil;
import com.txnetwork.mypage.utils.TxNetworkUtil;

import java.util.List;

/**
 * Created by hcz on 2015/2/2.
 */
public class DragSortAdapter extends ArrayAdapter<TypedUrlList> {

    private List<TypedUrlList> typedUrlLists;
    private LayoutInflater mInflater;
    private Context mContext;

    public DragSortAdapter(Context context, List<TypedUrlList> typedUrlListList) {
        super(context, R.layout.urlview_item, R.id.typeName, typedUrlListList);
        this.mContext = context;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.typedUrlLists = typedUrlListList;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (null == convertView) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.urlview_item, null);
            holder.dragGridView = (DragGridView) convertView.findViewById(R.id.dragGridView);
            holder.typeName = (EditText) convertView.findViewById(R.id.typeName);
            holder.expand = (ImageView) convertView.findViewById(R.id.expand);
            holder.delete = (ImageView) convertView.findViewById(R.id.delete);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        DragGridAdapter dragGridAdapter = this.typedUrlLists.get(position).getDragGridAdapter();
        holder.dragGridView.setAdapter(dragGridAdapter);
        holder.dragGridView.setParentPosition(position);
        holder.dragGridView.setOnItemClickListener(myItemClick);
        holder.typeName.setText(this.typedUrlLists.get(position).getName());

        if (!typedUrlLists.get(position).isExpanded()) {//收起当前的gridview
            holder.dragGridView.setVisibility(View.GONE);
            holder.expand.setImageResource(R.drawable.pull_down);
        }

        holder.expand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.dragGridView.getVisibility() == View.VISIBLE) {
                    typedUrlLists.get(position).setExpanded(false);
                    holder.dragGridView.setVisibility(View.GONE);
                    holder.expand.setImageResource(R.drawable.pull_down);
                } else {
                    typedUrlLists.get(position).setExpanded(true);
                    holder.dragGridView.setVisibility(View.VISIBLE);
                    holder.expand.setImageResource(R.drawable.pull_up);
                }
                MainActivity.isUrlEdit = true;//记录收起和展开状态
            }
        });
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "已删除" + typedUrlLists.get(position).getName() + "及", Toast.LENGTH_SHORT).show();
                typedUrlLists.remove(position);
                notifyDataSetChanged();
            }
        });

//        holder.typeName.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                holder.typeName.setEnabled(true);
////                holder.typeName.setBackgroundResource(R.drawable.bg_edittext);
////                holder.typeName.invalidate();
//            }
//        });

        holder.typeName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (StringUtil.isNotNull(s.toString())) {
                    typedUrlLists.get(position).setName(s.toString());
                } else {
                    //当类名为空时,默认设为"未分类"
                    typedUrlLists.get(position).setName("未分类");
                }

            }
        });

        return convertView;
    }

    public class ViewHolder {
        public EditText typeName;
        public DragGridView dragGridView;
        public ImageView expand;
        public ImageView delete;
    }

    /**
     * gridview item点击打开浏览器
     */
    private AdapterView.OnItemClickListener myItemClick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            DragGridAdapter.ViewHolder viewHolder = (DragGridAdapter.ViewHolder) view.getTag();
            String url = viewHolder.getUrl();
            if ("".equals(url) && "添加".equals(viewHolder.getName())) {
                //添加新的导航
                int type = viewHolder.getType();
                addNewUrl(type);
            } else {
                //网址前开头没有 http://时,会报错
                //No Activity found to handle Intent
                //android关于uri 都是有schema的.
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri content_url = null;
                if (url.startsWith("http://")) {
                    content_url = Uri.parse(url);
                } else {
                    content_url = Uri.parse("http://" + url);
                }
                intent.setData(content_url);
                mContext.startActivity(intent);
            }
        }
    };

    /**
     * 添加导航
     *
     * @param type
     */
    private void addNewUrl(int type) {
        final int urlType = type;
        final Dialog dialog = new Dialog(mContext, R.style.loading_dialog);
        dialog.setContentView(R.layout.dialog_main);

        TextView okBtn = (TextView) dialog.findViewById(R.id.ok);
        TextView cancelBtn = (TextView) dialog.findViewById(R.id.cancel);
        final EditText title = (EditText) dialog.findViewById(R.id.title);
        final EditText url = (EditText) dialog.findViewById(R.id.url);

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(title.getText()) || TextUtils.isEmpty(url.getText())) {
                    Toast.makeText(mContext, "请输入内容", Toast.LENGTH_SHORT).show();
                } else {
                    String nameStr = title.getText().toString();
                    String urlStr = url.getText().toString();

                    int newAddedOrder = 0;


                    for (int i = 0; i < typedUrlLists.size(); i++) {
                        if (urlType == typedUrlLists.get(i).getType()) {//根据type的类型,去找相关的分类列表.此处套二不能有重复
                            List<UrlAddr> tempList = typedUrlLists.get(i).getUrlAddrTypeList();
                            newAddedOrder = tempList.size() - 1;
                            tempList.remove(newAddedOrder);
                            tempList.add(new UrlAddr(nameStr, urlStr, TxNetworkUtil.getUrlIcon(urlStr), urlType, tempList.size()));
                            tempList.add(new UrlAddr("添加", "", "", urlType, tempList.size()));
                            typedUrlLists.get(i).getDragGridAdapter().notifyDataSetChanged();
                        }
                    }

                    MainActivity.isUrlEdit = true;
                    ContentResolver resolver = mContext.getContentResolver();
                    OperateDBUtils.insertNewUrl(resolver, urlStr, nameStr, urlType, newAddedOrder);//添加新导航
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
