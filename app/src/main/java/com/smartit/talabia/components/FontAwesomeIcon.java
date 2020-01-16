package com.smartit.talabia.components;

import android.content.Context;
import android.support.annotation.DimenRes;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.util.TypedValue;

import com.smartit.talabia.R;
import com.smartit.talabia.application.GlobalApplication;

public class FontAwesomeIcon extends AppCompatTextView {

	public FontAwesomeIcon(Context context) {
		this(context, null);
	}

	public FontAwesomeIcon(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public FontAwesomeIcon(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.init();
	}

	private void init() {
		if (this.isInEditMode()) {
			return;
		}
		try {
			this.setTypeface(((GlobalApplication) getContext().getApplicationContext()).getTypeface());
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (getTextColors() == null) {
			this.setTextColor(getResources().getColor( R.color.text_color));
		}
	}

	@Override
	public void setTextSize(float size) {
		super.setTextSize( TypedValue.COMPLEX_UNIT_DIP, size);
	}

	public void setTextDimen(@DimenRes int dimenID) {
		super.setTextSize( TypedValue.COMPLEX_UNIT_PX, dimenID);
	}
}
