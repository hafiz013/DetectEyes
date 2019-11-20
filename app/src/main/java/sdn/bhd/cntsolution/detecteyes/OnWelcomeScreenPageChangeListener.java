package sdn.bhd.cntsolution.detecteyes;

import androidx.viewpager.widget.ViewPager;

import com.stephentuso.welcome.WelcomeConfiguration;

interface OnWelcomeScreenPageChangeListener extends ViewPager.OnPageChangeListener {
    void setup(WelcomeConfiguration config);
}
