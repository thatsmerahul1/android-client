package com.ecarezone.android.patient.app.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ecarezone.android.patient.R;


/**
 * Created by CHAO WEI on 5/24/2015.
 */
public class NavigationItem extends RelativeLayout implements View.OnClickListener {

    private CheckBox mCheckBox = null;
    private ImageView mImageView = null;
    private TextView mLabel = null;

    private int mCheckBoxResId;
    private int mLabelResId;
    private int mLabelTextResId;
    private int mIconResId;
    private int mIconImageResId;

    private OnNavigationItemClickListener mListener = null;

    public NavigationItem(Context context) {
        super(context);
    }

    public NavigationItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public NavigationItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public NavigationItem(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(final Context context, final AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.NavigationItem, 0, 0);

        int cbResId = a.getResourceId(R.styleable.NavigationItem_niIndicator, 0);

        if (cbResId == 0) {
            throw new IllegalArgumentException("CheckBox id can not be 0");
        }

        int iconResId = a.getResourceId(R.styleable.NavigationItem_niIcon, 0);

        if (iconResId == 0) {
            //throw new IllegalArgumentException("Icon id can not be 0");
        }

        int labelResId = a.getResourceId(R.styleable.NavigationItem_niLabel, 0);

        if (labelResId == 0) {
            throw new IllegalArgumentException("Label id can not be 0");
        }

        int labelTextResId = a.getResourceId(R.styleable.NavigationItem_niTitle, 0);
        int iconImageResId = a.getResourceId(R.styleable.NavigationItem_niIconImage, 0);

        mIconResId = iconResId;
        mLabelResId = labelResId;
        mLabelTextResId = labelTextResId;
        mCheckBoxResId = cbResId;
        mIconImageResId = iconImageResId;

        a.recycle();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mCheckBox = (CheckBox) findViewById(mCheckBoxResId);
        mLabel = (TextView) findViewById(mLabelResId);
        mImageView = (ImageView) findViewById(mIconResId);
        mImageView.setImageResource(mIconImageResId);
        mImageView.setVisibility(INVISIBLE);
        mLabel.setText(mLabelTextResId);
        // use the label to identify the item
        setTag(mLabel.getText());
        setClickable(true);
        setOnClickListener(this);
    }

    /**
     * Highlight the item
     *
     * @param highlight
     */
    public void highlightItem(boolean highlight) {
        mCheckBox.setChecked(highlight);
        if(isEnabled()) {
            mLabel.setTextColor(getContext().getResources().getColor((highlight ? android.R.color.white : R.color.ecarezone_green_dark)));
        }
    }


    public void setOnNavigationItemClickListener(OnNavigationItemClickListener listener) {
        mListener = listener;
    }

    @Override
    public void setEnabled(boolean enabled) {
        if(!enabled) {
            mLabel.setTextColor(getContext().getResources().getColor(android.R.color.darker_gray));
        }
        super.setEnabled(enabled);
    }

    /**
     * Pass the click event to OnNavigationItemClickListener instance
     */
    @Override
    public void onClick(View v) {
        if(mListener != null){
            mListener.onItemClick(this);
        }
    }

    /**
     *
     */
    public static interface OnNavigationItemClickListener {
        void onItemClick(View v);
    }
}
