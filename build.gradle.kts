
import org.gradle.api.GradleException

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.google.services) apply false
}

// Keep Gradle on the JDK range supported by this Android/Gradle toolchain.
if (JavaVersion.current() < JavaVersion.VERSION_17 || JavaVersion.current() > JavaVersion.VERSION_21) {
    throw GradleException(
        "Unsupported JVM detected: ${System.getProperty("java.version")}. Please run Gradle with JDK 21."
    )
}
