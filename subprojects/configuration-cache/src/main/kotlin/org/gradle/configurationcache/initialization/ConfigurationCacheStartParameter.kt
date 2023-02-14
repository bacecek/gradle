/*
 * Copyright 2020 the original author or authors.
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

package org.gradle.configurationcache.initialization

import org.gradle.StartParameter
import org.gradle.api.internal.StartParameterInternal
import org.gradle.configurationcache.extensions.unsafeLazy
import org.gradle.initialization.StartParameterBuildOptions.ConfigurationCacheProblemsOption
import org.gradle.initialization.layout.BuildLayout
import org.gradle.internal.Factory
import org.gradle.internal.buildoption.IntegerInternalOption
import org.gradle.internal.buildoption.StringInternalOption
import org.gradle.internal.buildoption.InternalFlag
import org.gradle.internal.buildoption.InternalOptions
import org.gradle.internal.buildtree.BuildModelParameters
import org.gradle.internal.deprecation.DeprecationLogger
import org.gradle.internal.service.scopes.Scopes
import org.gradle.internal.service.scopes.ServiceScope
import java.io.File


@ServiceScope(Scopes.BuildTree::class)
class ConfigurationCacheStartParameter(
    private val buildLayout: BuildLayout,
    private val startParameter: StartParameterInternal,
    options: InternalOptions,
    modelParameters: BuildModelParameters
) {
    val loadAfterStore: Boolean = !modelParameters.isRequiresBuildModel && options.getOption(InternalFlag("org.gradle.configuration-cache.internal.load-after-store", true)).get()

    val taskExecutionAccessPreStable: Boolean = options.getOption(InternalFlag("org.gradle.configuration-cache.internal.task-execution-access-pre-stable")).get()

    val encryptedCache: Boolean = options.getOption(InternalFlag("org.gradle.configuration-cache.internal.encrypted", false)).get()

    val keystorePath: String? = options.getOption(StringInternalOption("org.gradle.configuration-cache.internal.key-store-path", null)).get()

    val keystorePassword: String? = options.getOption(StringInternalOption("org.gradle.configuration-cache.internal.key-store-password", null)).get()

    val keyAlias: String = options.getOption(StringInternalOption("org.gradle.configuration-cache.internal.key-alias", "gradle-secret")).get()

    val keyPassword: String = options.getOption(StringInternalOption("org.gradle.configuration-cache.internal.key-password", "")).get()

    val keyAlgorithm: String = options.getOption(StringInternalOption("org.gradle.configuration-cache.internal.key-alg", "AES")).get()

    val encryptionAlgorithm: String = options.getOption(StringInternalOption("org.gradle.configuration-cache.internal.encryption-alg", "AES/CBC/PKCS5PADDING")).get()

    val initVectorLength: Int = options.getOption(IntegerInternalOption("org.gradle.configuration-cache.internal.init-vector-len", 16)).get()

    val gradleProperties: Map<String, Any?>
        get() = startParameter.projectProperties

    val isQuiet: Boolean
        get() = startParameter.isConfigurationCacheQuiet

    val maxProblems: Int
        get() = startParameter.configurationCacheMaxProblems

    val isDebug: Boolean
        get() = startParameter.isConfigurationCacheDebug

    val failOnProblems: Boolean
        get() = startParameter.configurationCacheProblems == ConfigurationCacheProblemsOption.Value.FAIL

    val recreateCache: Boolean
        get() = startParameter.isConfigurationCacheRecreateCache

    /**
     * See [StartParameter.getProjectDir].
     */
    val projectDirectory: File?
        get() = startParameter.projectDir

    val currentDirectory: File
        get() = startParameter.currentDir

    val settingsDirectory: File
        get() = buildLayout.settingsDir

    @Suppress("DEPRECATION")
    val settingsFile: File?
        get() = DeprecationLogger.whileDisabled(Factory { startParameter.settingsFile })

    val rootDirectory: File
        get() = buildLayout.rootDirectory

    val isOffline
        get() = startParameter.isOffline

    val isRefreshDependencies
        get() = startParameter.isRefreshDependencies

    val isWriteDependencyLocks
        get() = startParameter.isWriteDependencyLocks && !isUpdateDependencyLocks

    val isUpdateDependencyLocks
        get() = startParameter.lockedDependenciesToUpdate.isNotEmpty()

    val isWriteDependencyVerifications
        get() = startParameter.writeDependencyVerifications.isNotEmpty()

    val requestedTaskNames: List<String> by unsafeLazy {
        startParameter.taskNames
    }

    val excludedTaskNames: Set<String>
        get() = startParameter.excludedTaskNames

    val allInitScripts: List<File>
        get() = startParameter.allInitScripts

    val gradleUserHomeDir: File
        get() = startParameter.gradleUserHomeDir

    val includedBuilds: List<File>
        get() = startParameter.includedBuilds

    val isBuildScan: Boolean
        get() = startParameter.isBuildScan

    val isNoBuildScan: Boolean
        get() = startParameter.isNoBuildScan
}
