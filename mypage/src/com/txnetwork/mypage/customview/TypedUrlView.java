package com.txnetwork.mypage.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.*;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.txnetwork.mypage.R;
import com.txnetwork.mypage.dsgv.DragGridAdapter;
import com.txnetwork.mypage.dsgv.DragGridView;

/**
 * Created by Administrator on 2015/1/16.
 */
public class TypedUrlView extends FrameLayout {

    private boolean isGridViewVisible = true;
    private int type;
    private String typeName;
    private Context mContext;
    private String Tag = TypedUrlView.class.getName();
    public WindowManager.LayoutParams layoutParams;
    public WindowManager mWm;

    public TypedUrlView(Context context, AttributeSet attrs, DragGridAdapter adapter, int type, String typeName) {
        super(context, attrs);
        mContext = context;
        layoutParams = new WindowManager.LayoutParams();
        mWm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);

        this.type = type;
        this.typeName = typeName;

        LayoutInflater.from(context).inflate(R.layout.urlview_item, this);
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.drag_handle);
        final DragGridView dragGridView = (DragGridView) findViewById(R.id.dragGridView);
        dragGridView.setAdapter(adapter);

        TextView typeNmaeTv = (TextView) relativeLayout.findViewById(R.id.typeName);
        typeNmaeTv.setText(typeName);

        relativeLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isGridViewVisible) {
                    dragGridView.setVisibility(GONE);
                } else {
                    dragGridView.setVisibility(VISIBLE);
                }
                isGridViewVisible = !isGridViewVisible;
            }
        });

    }
}
