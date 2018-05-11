/*
 * Copyright (C) 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.tools.idea.gradle.project.model;

import com.android.ide.common.repository.GradleVersion;
import org.jetbrains.annotations.Nullable;

public class NdkModelFeatures {
  private final boolean myWorkingDirectorySupported;
  private final boolean myGroupNameSupported;
  private final boolean myExportedHeadersSupported;
  private final boolean myBuildSystemNameSupported;

  public NdkModelFeatures(@Nullable GradleVersion modelVersion) {
    boolean isAtLeastTwoDotTwo = modelVersion != null && modelVersion.compareIgnoringQualifiers("2.0.0") >= 0;
    myWorkingDirectorySupported = isAtLeastTwoDotTwo;
    myGroupNameSupported = isAtLeastTwoDotTwo;
    myExportedHeadersSupported = isAtLeastTwoDotTwo;
    myBuildSystemNameSupported = modelVersion != null && modelVersion.compareIgnoringQualifiers("2.2.0") >= 0;
  }

  public boolean isGroupNameSupported() {
    return myGroupNameSupported;
  }

  public boolean isWorkingDirectorySupported() {
    return myWorkingDirectorySupported;
  }

  public boolean isExportedHeadersSupported() {
    return myExportedHeadersSupported;
  }

  public boolean isBuildSystemNameSupported() {
    return myBuildSystemNameSupported;
  }
}
