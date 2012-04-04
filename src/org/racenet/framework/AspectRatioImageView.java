package org.racenet.framework;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * ImageView which scales an image while maintaining
 * the original image aspect ratio
 * 
 * @author soh#zolex
 *
 */
public class AspectRatioImageView extends ImageView {

	/**
	 * Constructor
	 * 
	 * @param Context context
	 */
    public AspectRatioImageView(Context context) {
    	
        super(context);
    }

    /**
     * Constructor
     * 
     * @param Context context
     * @param AttributeSet attrs
     */
    public AspectRatioImageView(Context context, AttributeSet attrs) {
    	
        super(context, attrs);
    }

    /**
     * Constructor
     * 
     * @param Context context
     * @param AttributeSet attrs
     * @param int defStyle
     */
    public AspectRatioImageView(Context context, AttributeSet attrs, int defStyle) {
    	
        super(context, attrs, defStyle);
    }

    /**
     * Called from the view renderer.
     * Scales the image according to its aspect ratio.
     */
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    	
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = width * getDrawable().getIntrinsicHeight() / getDrawable().getIntrinsicWidth();
        setMeasuredDimension(width, height);
    }
}