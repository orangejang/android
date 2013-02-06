/*
 * Copyright 2000-2012 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.intellij.android.designer.designSurface;

import com.intellij.android.designer.ImageUtils;
import com.intellij.android.designer.ShadowPainter;
import com.intellij.designer.designSurface.ScalableComponent;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import static com.intellij.android.designer.ShadowPainter.SHADOW_SIZE;

/**
 * @author Alexander Lobas
 */
public class RootView extends com.intellij.designer.designSurface.RootView implements ScalableComponent {
  private List<EmptyRegion> myEmptyRegions;
  private final AndroidDesignerEditorPanel myPanel;
  private BufferedImage myScaledImage;
  private boolean myShowDropShadow;

  public RootView(AndroidDesignerEditorPanel panel, int x, int y, BufferedImage image, boolean isAlphaChannelImage) {
    super(x, y, image, isAlphaChannelImage);
    myPanel = panel;
  }

  /**
   * Sets the image to be drawn
   * <p>
   * The image <b>can</b> be null, which is the case when we are dealing with
   * an empty document.
   *
   * @param image The image to be rendered
   * @param isAlphaChannelImage whether the alpha channel of the image is relevant
   */
  @Override
  public void setImage(@Nullable BufferedImage image, boolean isAlphaChannelImage) {
    myShowDropShadow = !isAlphaChannelImage;
    myEmptyRegions = new ArrayList<EmptyRegion>();
    super.setImage(image, isAlphaChannelImage);
  }

  /**
   * Returns whether this image overlay should be painted with a drop shadow.
   * This is usually the case, but not for transparent themes like the dialog
   * theme (Theme.*Dialog), which already provides its own shadow.
   *
   * @return true if the image overlay should be shown with a drop shadow.
   */
  public boolean getShowDropShadow() {
    return myShowDropShadow;
  }

  @Override
  protected void updateSize() {
    if (myImage != null) {
      double zoom = getScale();
      int newWidth = (int)(zoom * myImage.getWidth());
      int newHeight = (int)(zoom * myImage.getHeight());
      if (myShowDropShadow) {
        newWidth += SHADOW_SIZE;
        newHeight += SHADOW_SIZE;
      }
      setBounds(myX, myY, newWidth, newHeight);
    }
    myScaledImage = null;
  }

  public void addEmptyRegion(int x, int y, int width, int height) {
    if (new Rectangle(0, 0, myImage.getWidth(), myImage.getHeight()).contains(x, y)) {
      EmptyRegion r = new EmptyRegion();
      r.myX = x;
      r.myY = y;
      r.myWidth = width;
      r.myHeight = height;
      r.myColor = new Color(~myImage.getRGB(x, y));
      myEmptyRegions.add(r);
    }
  }

  @Override
  protected void paintImage(Graphics g) {
    double scale = myPanel.getZoom();
    if (myScaledImage == null) {
      // Special cases scale=1 to be fast

      if (scale == 1) {
        // Scaling to 100% is easy!
        myScaledImage = myImage;

        if (myShowDropShadow) {
          // Just need to draw drop shadows
          myScaledImage = ShadowPainter.createRectangularDropShadow(myImage);
        }
      } else {
        if (myShowDropShadow) {
          myScaledImage = ImageUtils.scale(myImage, scale, scale,
                                            SHADOW_SIZE, SHADOW_SIZE);
          ShadowPainter.drawRectangleShadow(myScaledImage, 0, 0,
                                         myScaledImage.getWidth() - SHADOW_SIZE,
                                         myScaledImage.getHeight() - SHADOW_SIZE);
        } else {
          myScaledImage = ImageUtils.scale(myImage, scale, scale);
        }
      }
    }

    g.drawImage(myScaledImage, 0, 0, null);

    if (!myEmptyRegions.isEmpty()) {
      if (scale == 1) {
        for (EmptyRegion r : myEmptyRegions) {
          g.setColor(r.myColor);
          g.fillRect(r.myX, r.myY, r.myWidth, r.myHeight);
        }
      } else {
        for (EmptyRegion r : myEmptyRegions) {
          g.setColor(r.myColor);
          g.fillRect((int)(scale * r.myX),
                     (int)(scale * r.myY),
                     (int)(scale * r.myWidth),
                     (int)(scale * r.myHeight));
        }
      }
    }
  }

  // Implements ScalableComponent
  @Override
  public double getScale() {
    if (myPanel != null) {
      return myPanel.getZoom();
    }
    return 1;
  }

  private static class EmptyRegion {
    public Color myColor;
    public int myX;
    public int myY;
    public int myWidth;
    public int myHeight;
  }
}
