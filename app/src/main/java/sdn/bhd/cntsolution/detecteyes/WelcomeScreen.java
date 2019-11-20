package sdn.bhd.cntsolution.detecteyes;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.stephentuso.welcome.BasicPage;
import com.stephentuso.welcome.WelcomeBackgroundView;
import com.stephentuso.welcome.WelcomeConfiguration;
import com.stephentuso.welcome.WelcomePageList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WelcomeScreen extends AppCompatActivity {
    private static final int PERMISSION_REQUESTS = 123;
    private WelcomeFragmentPagerAdapter adapter;
    protected ViewPager viewPager;
    private WelcomeConfiguration configuration;
    private WelcomeItemList responsiveItems;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        configuration = new WelcomeConfiguration.Builder(this)
                .defaultTitleTypefacePath("Montserrat-Bold.ttf")
                .defaultHeaderTypefacePath("Montserrat-Bold.ttf")
                .page(new BasicPage(R.drawable.ic_driver,
                        "Welcome", //Welcome
                                //"App Mengesan Mata semasa memandu anda.Sila izinkan semua kebenaran!")
                        "App Detect Eyes while your driving.Please allow all permission!")
                        .background(R.color.colorPrimary)
                )
                .swipeToDismiss(false)
                .exitAnimation(android.R.anim.fade_out)
                .build();

        context = WelcomeScreen.this;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.wel_activity_welcome);

        adapter = new WelcomeFragmentPagerAdapter(getSupportFragmentManager());

        viewPager = (ViewPager) findViewById(R.id.wel_view_pager);
        viewPager.setAdapter(adapter);

        responsiveItems = new WelcomeItemList();

        // -- Inflate the bottom layout -- //

        FrameLayout bottomFrame = (FrameLayout) findViewById(R.id.wel_bottom_frame);
        View.inflate(this, configuration.getBottomLayoutResId(), bottomFrame);

        SkipButton skip = new SkipButton(findViewById(R.id.wel_button_skip));
        addViewWrapper(skip, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        PreviousButton prev = new PreviousButton(findViewById(R.id.wel_button_prev));
        addViewWrapper(prev, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //scrollToPreviousPage();
            }
        });

        NextButton next = new NextButton(findViewById(R.id.wel_button_next));
        addViewWrapper(next, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //scrollToNextPage();
            }
        });

        DoneButton done = new DoneButton(findViewById(R.id.wel_button_done));
        addViewWrapper(done, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!allPermissionsGranted()) {
                    getRuntimePermissions();
                }else{
                    Intent mainActivity = new Intent(context, MainActivity.class);
                    startActivity(mainActivity);
                    finish();
                }
            }
        });

        responsiveItems.setup(configuration);

        viewPager.addOnPageChangeListener(responsiveItems);
        viewPager.setCurrentItem(configuration.firstPageIndex());

        responsiveItems.onPageSelected(viewPager.getCurrentItem());

        if (allPermissionsGranted()){
            Intent mainActivity = new Intent(context, MainActivity.class);
            startActivity(mainActivity);
            finish();
        }
    }

    private void addViewWrapper(WelcomeViewWrapper wrapper, View.OnClickListener onClickListener) {
        if (wrapper.getView() != null) {
            wrapper.setOnClickListener(onClickListener);
            responsiveItems.add(wrapper);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case PERMISSION_REQUESTS :{
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Intent mainActivity = new Intent(this, MainActivity.class);
                    startActivity(mainActivity);
                    finish();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }

    private boolean allPermissionsGranted() {
        for (String permission : getRequiredPermissions()) {
            if (!isPermissionGranted(this, permission)) {
                return false;
            }
        }
        return true;
    }

    private String[] getRequiredPermissions() {
        try {
            PackageInfo info =
                    this.getPackageManager()
                            .getPackageInfo(this.getPackageName(), PackageManager.GET_PERMISSIONS);
            String[] ps = info.requestedPermissions;
            if (ps != null && ps.length > 0) {
                return ps;
            } else {
                return new String[0];
            }
        } catch (Exception e) {
            return new String[0];
        }
    }

    private void getRuntimePermissions() {
        List<String> allNeededPermissions = new ArrayList<>();
        for (String permission : getRequiredPermissions()) {
            if (!isPermissionGranted(this, permission)) {
                allNeededPermissions.add(permission);
            }
        }

        if (!allNeededPermissions.isEmpty()) {
            ActivityCompat.requestPermissions(
                    this, allNeededPermissions.toArray(new String[0]), PERMISSION_REQUESTS);
        }
    }

    private static boolean isPermissionGranted(Context context, String permission) {
        if (ContextCompat.checkSelfPermission(context, permission)
                == PackageManager.PERMISSION_GRANTED) {
            //Log.i(TAG, "Permission granted: " + permission);
            return true;
        }
        //Log.i(TAG, "Permission NOT granted: " + permission);
        return false;
    }

    private class WelcomeFragmentPagerAdapter extends FragmentPagerAdapter {

        public WelcomeFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return configuration.createFragment(position);
        }

        @Override
        public int getCount() {
            return configuration.pageCount();
        }
    }

    class WelcomeItemList extends ArrayList<OnWelcomeScreenPageChangeListener> implements OnWelcomeScreenPageChangeListener {

        /* package */ WelcomeItemList(OnWelcomeScreenPageChangeListener... items) {
            super(Arrays.asList(items));
        }

        /* package */ void addAll(OnWelcomeScreenPageChangeListener... items) {
            super.addAll(Arrays.asList(items));
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            for (OnWelcomeScreenPageChangeListener changeListener : this) {
                changeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }
        }

        @Override
        public void onPageSelected(int position) {
            for (OnWelcomeScreenPageChangeListener changeListener : this) {
                changeListener.onPageSelected(position);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            for (OnWelcomeScreenPageChangeListener changeListener : this) {
                changeListener.onPageScrollStateChanged(state);
            }
        }

        @Override
        public void setup(WelcomeConfiguration config) {
            for (OnWelcomeScreenPageChangeListener changeListener : this) {
                changeListener.setup(config);
            }
        }

        public void addAll(WelcomeBackgroundView background, WelcomeViewHider hider, WelcomePageList pages) {

        }

        /*public void addAll(WelcomeBackgroundView background, WelcomeViewHider hider, WelcomePageList pages) {

        }*/
    }

    class DoneButton extends WelcomeViewWrapper {

        private boolean shouldShow = true;

        public DoneButton(View button) {
            super(button);
            if (button != null) hideImmediately();
        }

        @Override
        public void setup(WelcomeConfiguration config) {
            super.setup(config);
            shouldShow = !config.getUseCustomDoneButton();
            if (this.getView() instanceof TextView) {
                WelcomeUtils.setTypeface((TextView) this.getView(), config.getDoneButtonTypefacePath(), config.getContext());
            }
        }

        @Override
        public void onPageSelected(int pageIndex, int firstPageIndex, int lastPageIndex) {
            setVisibility(shouldShow && !WelcomeUtils.isIndexBeforeLastPage(pageIndex, lastPageIndex, isRtl));
        }


    }

    class SkipButton extends WelcomeViewWrapper {

        private boolean enabled = true;
        private boolean onlyShowOnFirstPage = false;

        public SkipButton(View button) {
            super(button);
        }

        @Override
        public void setup(WelcomeConfiguration config) {
            super.setup(config);
            onlyShowOnFirstPage = config.getShowPrevButton();
            this.enabled = config.getCanSkip();
            setVisibility(enabled, false);
            if (getView() instanceof TextView) {
                WelcomeUtils.setTypeface((TextView) this.getView(), config.getSkipButtonTypefacePath(), config.getContext());
            }
        }

        @Override
        public void onPageSelected(int pageIndex, int firstPageIndex, int lastPageIndex) {
            if (onlyShowOnFirstPage)
                setVisibility(enabled && pageIndex == firstPageIndex);
            else
                setVisibility(enabled && WelcomeUtils.isIndexBeforeLastPage(pageIndex, lastPageIndex, isRtl));
        }

    }

    class PreviousButton extends WelcomeViewWrapper {

        private boolean shouldShow = false;

        public PreviousButton(View button) {
            super(button);
        }

        @Override
        public void setup(WelcomeConfiguration config) {
            super.setup(config);
            this.shouldShow = config.getShowPrevButton();
        }

        @Override
        public void onPageSelected(int pageIndex, int firstPageIndex, int lastPageIndex) {
            setVisibility(shouldShow && pageIndex != firstPageIndex);
        }


    }

    class NextButton extends WelcomeViewWrapper {

        private boolean shouldShow = true;

        public NextButton(View button) {
            super(button);
        }

        @Override
        public void setup(WelcomeConfiguration config) {
            super.setup(config);
            this.shouldShow = config.getShowNextButton();
        }

        @Override
        public void onPageSelected(int pageIndex, int firstPageIndex, int lastPageIndex) {
            setVisibility(shouldShow && WelcomeUtils.isIndexBeforeLastPage(pageIndex, lastPageIndex, isRtl));
        }
    }

}
