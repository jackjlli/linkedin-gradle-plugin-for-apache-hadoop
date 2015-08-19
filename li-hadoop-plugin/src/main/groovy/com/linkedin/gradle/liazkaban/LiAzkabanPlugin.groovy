/*
 * Copyright 2015 LinkedIn Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.linkedin.gradle.liazkaban;

import com.linkedin.gradle.azkaban.AzkabanPlugin;
import com.linkedin.gradle.azkaban.AzkabanProject

import org.gradle.api.Project;

/**
 * LinkedIn-specific customizations to the Azkaban Plugin.
 */
class LiAzkabanPlugin extends AzkabanPlugin {
  /**
   * Factory method to build a default AzkabanProject for use with the writePluginJson method. Can
   * be overridden by subclasses.
   * <p>
   * The LinkedIn override of this method sets a couple of properties to common LinkedIn values.
   *
   * @param project The Gradle project
   * @return The AzkabanProject object
   */
  @Override
  AzkabanProject makeDefaultAzkabanProject(Project project) {
    AzkabanProject azkabanProject = makeAzkabanProject();
    azkabanProject.azkabanProjName = "";
    azkabanProject.azkabanUrl = "https://eat1-nertzaz01.grid.linkedin.com:8443/";
    azkabanProject.azkabanUserName = System.getProperty("user.name");
    azkabanProject.azkabanValidatorAutoFix = "true";
    azkabanProject.azkabanZipTask = "";
    return azkabanProject;
  }
}