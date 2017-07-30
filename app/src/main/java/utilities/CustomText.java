package utilities;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Administrator on 4/15/2016.
 */
public class CustomText extends TextView {

    public CustomText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public CustomText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomText(Context context) {
        super(context);
        init();
    }

    private void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(),"proxima-nova-regular.ttf");
        setTypeface(tf);
    }
}
