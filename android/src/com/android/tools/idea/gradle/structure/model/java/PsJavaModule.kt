/*
 * Copyright (C) 2016 The Android Open Source Project
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
package com.android.tools.idea.gradle.structure.model.java

import com.android.tools.idea.gradle.dsl.api.GradleBuildModel
import com.android.tools.idea.gradle.project.model.JavaModuleModel
import com.android.tools.idea.gradle.structure.model.*
import com.android.tools.idea.gradle.structure.model.meta.ModelDescriptor
import com.android.tools.idea.gradle.structure.model.meta.getValue
import com.intellij.icons.AllIcons
import java.io.File
import javax.swing.Icon

class PsJavaModule(
  parent: PsProject,
  override val gradlePath: String
  ) : PsModule(parent) {
  override val descriptor by JavaModuleDescriptors
  var resolvedModel: JavaModuleModel? = null ; private set
  override var rootDir: File? = null ; private set
  override val projectType: PsModuleType = PsModuleType.JAVA
  override val icon: Icon? = AllIcons.Nodes.PpJdk
  private var myDependencyCollection: PsDeclaredJavaDependencyCollection? = null
  private var myResolvedDependencyCollection: PsResolvedJavaDependencyCollection? = null

  fun init(name: String, parentModule: PsModule?, resolvedModel: JavaModuleModel?, parsedModel: GradleBuildModel?) {
    super.init(name, parentModule, parsedModel)
    this.resolvedModel = resolvedModel
    rootDir = resolvedModel?.contentRoots?.firstOrNull()?.rootDirPath
    myDependencyCollection = null
  }

  override val dependencies: PsDeclaredJavaDependencyCollection
    get() = myDependencyCollection ?: PsDeclaredJavaDependencyCollection(this).also { myDependencyCollection = it }

  val resolvedDependencies: PsResolvedJavaDependencyCollection
    get() = myResolvedDependencyCollection ?: PsResolvedJavaDependencyCollection(this).also { myResolvedDependencyCollection = it }

  override fun getConfigurations(onlyImportant: Boolean): List<String> = resolvedModel?.configurations.orEmpty()

  // Java libraries can depend on any type of modules, including Android apps (when a Java library is actually a 'test'
  // module for the Android app.)
  override fun canDependOn(module: PsModule): Boolean = true

  override fun findLibraryDependencies(group: String?, name: String): List<PsDeclaredLibraryDependency> =
    dependencies.findLibraryDependencies(group, name)

  override fun resetDependencies() {
    myDependencyCollection = null
    myResolvedDependencyCollection = null
  }

  object JavaModuleDescriptors: ModelDescriptor<PsJavaModule, Nothing, Nothing> {
    override fun getResolved(model: PsJavaModule): Nothing? = null
    override fun getParsed(model: PsJavaModule): Nothing? = null
    override fun setModified(model: PsJavaModule) { model.isModified = true }
    override fun enumerateModels(model: PsJavaModule): Collection<PsModel> = model.dependencies.items
  }
}
