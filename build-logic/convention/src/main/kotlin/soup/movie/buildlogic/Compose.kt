package soup.movie.buildlogic

import com.android.build.gradle.BaseExtension
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.compose.compiler.gradle.ComposeCompilerGradlePluginExtension

fun Project.configureCompose() {
    pluginManager.apply("org.jetbrains.kotlin.plugin.compose")

    val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
    android {
        buildFeatures.compose = true

        dependencies {
            val bom = libs.findLibrary("compose-bom").get()
            implementation(platform(bom))
            androidTestImplementation(platform(bom))
        }
    }

    extensions.configure<ComposeCompilerGradlePluginExtension> {
        enableStrongSkippingMode = false
    }
}

private fun Project.android(action: BaseExtension.() -> Unit) {
    extensions.configure(action)
}
