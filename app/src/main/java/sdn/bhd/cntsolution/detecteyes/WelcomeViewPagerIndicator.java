package sdn.bhd.cntsolution.detecteyes;

import android.content.Context;
import android.util.AttributeSet;

import com.stephentuso.welcome.WelcomeConfiguration;

class WelcomeViewPagerIndicator extends SimpleViewPagerIndicator implements OnWelcomeScreenPageChangeListener {

    public WelcomeViewPagerIndicator(Context context) {
        super(context);
    }

    public WelcomeViewPagerIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WelcomeViewPagerIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setup(WelcomeConfiguration config) {
        setTotalPages(config.viewablePageCount());
        if (config.isRtl()) {
            setRtl(true);
            if (config.getSwipeToDismiss()) {
                setPageIndexOffset(-1);
            }
        }
    }
}