plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.navigation.safeargs)
    alias(libs.plugins.apollo3)
}

kotlin {
    jvmToolchain(17)
}

android {
    signingConfigs {
        getByName("debug") {
            keyAlias = "debug"
            keyPassword = "123456"
            storeFile = file("debug-keystore.jks")
            storePassword = "123456"
        }
    }
    namespace = "com.github.andreyasadchy.xtra"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.github.andreyasadchy.xtra"
        minSdk = 21
        targetSdk = 34
        versionCode = 121
        versionName = "2.26.3"
        resourceConfigurations += listOf("ar", "de", "en", "es", "fr", "in", "ja", "pt-rBR", "ru", "tr", "zh-rTW")
    }

    buildTypes {
        getByName("debug") {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-DEBUG"
            signingConfig = signingConfigs.getByName("debug")
        }
        getByName("release") {
            isShrinkResources = true
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    buildFeatures {
        buildConfig = true
        viewBinding = true
    }
    lint {
        disable += "ContentDescription"
    }
}

dependencies {
    //UI
    implementation(libs.material)
    implementation(libs.appcompat)
    implementation(libs.constraintlayout)
    implementation(libs.preference.ktx)
    implementation(libs.swiperefreshlayout)
    implementation(libs.flexbox)
    implementation(libs.draglistview)

    //Architecture components
    implementation(libs.paging.runtime.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.common.java8)
    implementation(libs.lifecycle.process)
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    implementation(libs.room.paging)
    ksp(libs.room.compiler)
    implementation(libs.work.runtime)
    implementation(libs.core.ktx)
    implementation(libs.fragment.ktx)
    implementation(libs.navigation.fragment.ktx)
    implementation(libs.navigation.ui.ktx)
    implementation(libs.webkit)

    //Misc
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp.logging.interceptor)

    implementation(libs.apollo.runtime)

    implementation(libs.fetch)
    implementation(libs.fetch.okhttp)
    implementation(libs.open.m3u8)

    implementation(libs.media3.exoplayer)
    implementation(libs.media3.exoplayer.hls)
    implementation(libs.media3.datasource.okhttp)
    implementation(libs.media3.ui)
    implementation(libs.media3.session)
    implementation(libs.checker.qual)

    constraints {
        implementation("androidx.media:media:1.7.0") // https://github.com/androidx/media/blob/release/constants.gradle#L47
    }

    implementation(libs.coil)
    implementation(libs.coil.gif)

    implementation(libs.glide)
    ksp(libs.glide.ksp)
    implementation(libs.glide.okhttp)
    implementation(libs.glide.webpdecoder)

    implementation(libs.hilt)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.work)
    ksp(libs.hilt.extension.compiler)

    implementation(libs.coroutines)
}

ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}

apollo {
    service("service") {
        packageName.set("com.github.andreyasadchy.xtra")
        outputDirConnection {
            connectToAndroidSourceSet("main")
        }
    }
}

// Delete large build log files from ~/.gradle/daemon/X.X/daemon-XXX.out.log
// Source: https://discuss.gradle.org/t/gradle-daemon-produces-a-lot-of-logs/9905
File("${project.gradle.gradleUserHomeDir.absolutePath}/daemon/${project.gradle.gradleVersion}").listFiles()?.forEach {
    if (it.name.endsWith(".out.log")) {
        // println("Deleting gradle log file: $it") // Optional debug output
        it.delete()
    }
}