plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    namespace = "com.project.demoapiprogress"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.project.demoapiprogress"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures{
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)


    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // sdp
    implementation("com.intuit.sdp:sdp-android:1.1.0")

    // ssp
    implementation("com.intuit.ssp:ssp-android:1.1.0")

     // Rounded Imageview
    implementation("com.makeramen:roundedimageview:2.3.0")

    // AvLoading
    implementation("com.wang.avi:library:2.1.3")

    // Custom Cookie Toast
    implementation("org.aviran.cookiebar2:cookiebar2:1.1.4")

    // Glide
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation("com.github.bumptech.glide:annotations:4.16.0")
    implementation("com.github.bumptech.glide:okhttp3-integration:4.12.0") { exclude("glide-parent") }
    annotationProcessor("com.github.bumptech.glide:compiler:4.13.2")
    implementation("jp.wasabeef:glide-transformations:4.3.0")
    implementation("jp.co.cyberagent.android.gpuimage:gpuimage-library:1.4.0")

    // media3 exoplayer
    implementation("androidx.media3:media3-exoplayer:1.3.0")
    implementation("androidx.media3:media3-exoplayer-dash:1.3.0")
    implementation("androidx.media3:media3-ui:1.3.0")

    // for video caching
    implementation("androidx.lifecycle:lifecycle-process:2.7.0")

    // Multi dex for image caching
    implementation("androidx.multidex:multidex:2.0.1")

    // FFMpeg for Video editing
    implementation("com.arthenica:ffmpeg-kit-min-gpl:5.1")

    // Image Cropper View
    implementation("com.vanniktech:android-image-cropper:4.5.0")

    // Kotlin Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    
    // For Croppy : RxJava & RxAndroid
    implementation("io.reactivex.rxjava2:rxjava:2.2.13")
    implementation("io.reactivex.rxjava2:rxandroid:2.1.1")
}