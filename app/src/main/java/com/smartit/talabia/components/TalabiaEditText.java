package com.smartit.talabia.components;

import android.content.Context;
import android.support.annotation.DimenRes;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;
import android.util.TypedValue;

import com.smartit.talabia.application.ApplicationHelper;
import com.smartit.talabia.util.FontTypeface;
import com.smartit.talabia.util.ObjectUtil;
import com.smartit.talabia.util.UIUtil;

import static com.smartit.talabia.util.Constants.ENGLISH_LANGUAGE_ID;
import static com.smartit.talabia.util.Constants.LANGUAGE_PREFERENCE;
import static com.smartit.talabia.util.Constants.SELECTED_LANG_ID;

/**
 * Created by android on 2/5/19.
 */

public class TalabiaEditText extends AppCompatEditText {

    public TalabiaEditText(Context context) {
        super ( context );
        init ( context );
    }

    public TalabiaEditText(Context context, AttributeSet attrs) {
        super ( context, attrs );
        init ( context );
    }

    public TalabiaEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super ( context, attrs, defStyleAttr );
        init ( context );
    }

    private void init(Context context) {
        if(this.isInEditMode ( )) {
            return;
        }

        try {
            if(getTypeface ( ) != null) {
                SessionSecuredPreferences securedPreferences = ApplicationHelper.application ( ).languagePreferences ( LANGUAGE_PREFERENCE );
                String selectedLangId = securedPreferences.getString ( SELECTED_LANG_ID, "" );
                FontTypeface fontTypeface = FontTypeface.getInstance ( context );
                if(!ObjectUtil.isEmpty ( selectedLangId )) {
                    this.setTypeface ( selectedLangId.equals ( ENGLISH_LANGUAGE_ID ) ? fontTypeface.getEnglishTypeface ( ) : fontTypeface.getArabicTypeface ( ) );
                }
            }
        } catch (Exception e) {
            e.printStackTrace ( );
        }
    }

    public void setTextDimen(@DimenRes int dimenID) {
        super.setTextSize ( TypedValue.COMPLEX_UNIT_PX, UIUtil.getDimension ( dimenID ) );
    }

    @Override
    public void setTextSize(float size) {
        super.setTextSize ( TypedValue.COMPLEX_UNIT_DIP, size );
    }
}
