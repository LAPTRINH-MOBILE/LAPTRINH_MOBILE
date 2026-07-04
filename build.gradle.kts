// Top-level build file where you can add configuration options common to all sub-projects/modules.
import org.gradle.api.GradleException

// Fail fast with a clear message when Gradle is run with an unsupported (too-old) JVM.
// Unsupported class file major version 69 indicates the runtime JDK is older than the
// compiled toolings (requires JDK 17+ or newer). Please run Gradle with JDK 17 or later.
if (JavaVersion.current() < JavaVersion.VERSION_17) {
    throw GradleException("Unsupported JVM detected: ${'$'}{System.getProperty("java.version")}. Please run Gradle with JDK 17 or newer.")
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.ksp) apply false
}
