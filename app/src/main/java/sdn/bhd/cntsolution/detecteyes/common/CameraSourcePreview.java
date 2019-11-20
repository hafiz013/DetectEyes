// Copyright 2018 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package sdn.bhd.cntsolution.detecteyes.common;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.common.images.Size;

import java.io.IOException;

/** Preview the camera image in the screen. */
public class CameraSourcePreview extends ViewGroup {
  private static final String TAG = "MIDemoApp:Preview";

  private Context context;
  private SurfaceView surfaceView;
  private boolean startRequested;
  private boolean surfaceAvailable;
  private CameraSource cameraSource;

  private GraphicOverlay overlay;
  private SurfaceHolder holder;

  public CameraSourcePreview(Context context, AttributeSet attrs) {
    super(context, attrs);
    this.context = context;
    startRequested = false;
    surfaceAvailable = false;

    surfaceView = new SurfaceView(context);

    holder = surfaceView.getHolder();
    holder.addCallback(new SurfaceCallback());
    holder.setSizeFromLayout();

    addView(surfaceView);
  }

  public void start(CameraSource cameraSource) throws IOException {
    if (cameraSource == null) {
      stop();
    }

    this.cameraSource = cameraSource;

    if (this.cameraSource != null) {
      startRequested = true;
      startIfReady();
    }
  }

  public void start(CameraSource cameraSource, GraphicOverlay overlay) throws IOException {
    this.overlay = overlay;
    start(cameraSource);
  }

  public void stop() {
    if (cameraSource != null) {
      cameraSource.stop();
    }
  }

  public void release() {
    if (cameraSource != null) {
      cameraSource.release();
      cameraSource = null;
    }
  }

  @SuppressLint("MissingPermission")
  private void startIfReady() throws IOException {
    if (startRequested && surfaceAvailable) {
      cameraSource.start();
      if (overlay != null) {
        Size size = cameraSource.getPreviewSize();
        int min = Math.min(size.getWidth(), size.getHeight());
        int max = Math.max(size.getWidth(), size.getHeight());
        if (isPortraitMode()) {
          // Swap width and height sizes when in portrait, since it will be rotated by
          // 90 degrees
          overlay.setCameraInfo(min, max, cameraSource.getCameraFacing());
        } else {
          overlay.setCameraInfo(max, min, cameraSource.getCameraFacing());
        }
        overlay.clear();
      }
      startRequested = false;
    }
  }

  private class SurfaceCallback implements SurfaceHolder.Callback {
    @Override
    public void surfaceCreated(SurfaceHolder surface) {
      surfaceAvailable = true;
      try {
        startIfReady();
      } catch (IOException e) {
        Log.e(TAG, "Could not start camera source.", e);
      }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surface) {
      surfaceAvailable = false;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}
  }

  @Override
  protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
    int width = 320;   //320
    int height = 187;  // ori 200
    if (cameraSource != null) {
      Size size = cameraSource.getPreviewSize();
      if (size != null) {
        width = size.getWidth();
        height = size.getHeight();
      }
    }

    // Swap width and height sizes when in portrait, since it will be rotated 90 degrees
    if (isPortraitMode()) {
      if(getDipPhone()>=320 && getDipPhone()<480){
          width = 300;
          height = 175;
      }else if (getDipPhone()>=480 && getDipPhone()<640){
          width = 298;
          height = 187;
      }else if (getDipPhone()>=640){
          width = 320;
          height = 187;
      }
      int tmp = width;
      width = height;
      height = tmp;
    }else{
      if(getDipPhone()>=320 && getDipPhone()<480){
        width = 380;
        height = 200;
      }else if (getDipPhone()>=480 && getDipPhone()<640){
        width = 320;
        height = 180;
      }else if (getDipPhone()>=640){
        width = 320;
        height = 167;
      }
    }

    final int layoutWidth = right - left;
    final int layoutHeight = bottom - top;

    // Computes height and width for potentially doing fit width.
    int childWidth = layoutWidth;
    int childHeight = (int) (((float) layoutWidth / (float) width) * height);

    // If height is too tall using fit width, does fit height instead.
    if (childHeight > layoutHeight) {
      childHeight = layoutHeight;
      childWidth = (int) (((float) layoutHeight / (float) height) * width);
    }

    for (int i = 0; i < getChildCount(); ++i) {
      getChildAt(i).layout(0, 0, childWidth, childHeight);
      Log.d(TAG, "Assigned view: " + i);
    }

    try {
      startIfReady();
    } catch (IOException e) {
      Log.e(TAG, "Could not start camera source.", e);
    }
  }

  public int getDipPhone(){
    int pixeldpi = context.getResources().getSystem().getDisplayMetrics().densityDpi;

    return pixeldpi;
  }
  private boolean isPortraitMode() {
    int orientation = context.getResources().getConfiguration().orientation;
    if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
      return false;
    }
    if (orientation == Configuration.ORIENTATION_PORTRAIT) {
      return true;
    }

    Log.d(TAG, "isPortraitMode returning false by default");
    return false;
  }
}
