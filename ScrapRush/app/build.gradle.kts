plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("kotlin-parcelize")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("androidx.navigation.safeargs.kotlin")

}

android {
    namespace = "com.manisoft.scraprushapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.manisoft.scraprushapp"
        minSdk = 23
        targetSdk = 34
        versionCode = 14
        versionName = "1.0.9"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.8.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")

    // retrofit
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.3")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.google.code.gson:gson:2.9.0")

    // Koin
    implementation("io.insert-koin:koin-android:3.1.2")

    // For ViewModel
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")

    // Glide
    implementation("com.github.bumptech.glide:glide:4.13.2")

    //circle image view
    implementation("de.hdodenhof:circleimageview:3.1.0")

    //Dimen
    implementation("com.intuit.ssp:ssp-android:1.0.6")

    //opt pin view
    implementation("com.github.poovamraj:PinEditTextField:1.2.6")

    //lottie animation lib
    implementation("com.airbnb.android:lottie:5.2.0")

    //location picker and map lib
    implementation("com.google.android.gms:play-services-places:17.0.0")
    implementation("com.google.android.gms:play-services-maps:18.1.0")
    implementation("com.google.android.libraries.places:places:3.2.0")
    implementation("com.android.volley:volley:1.2.1")
    implementation("com.google.maps.android:android-maps-utils:3.4.0")

    //image slider
    implementation("com.github.denzcoskun:ImageSlideshow:0.1.2")

    //firebase crashlytics,analytics,dynamic-link
    implementation(enforcedPlatform("com.google.firebase:firebase-bom:32.2.2"))
    implementation("com.google.firebase:firebase-crashlytics")
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-dynamic-links-ktx")
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.android.gms:play-services-auth:21.0.0")
    implementation("com.google.firebase:firebase-appcheck-playintegrity")
    implementation("com.google.firebase:firebase-appcheck-ktx")
    implementation("com.google.firebase:firebase-appcheck-debug:17.1.2")
    implementation("com.google.firebase:firebase-messaging-ktx:23.4.1")

    //avatar progress view
    implementation("xyz.schwaab:avvylib:1.2.0")

    // Navigation Components
    val nav_version = "2.7.2"
    implementation("androidx.navigation:navigation-fragment-ktx:$nav_version")
    implementation("androidx.navigation:navigation-ui-ktx:$nav_version")

    //timeline view
    implementation("com.github.vipulasri:timelineview:1.1.5")

    //Force Update
    implementation("com.google.android.play:app-update:2.1.0")
    implementation("com.google.android.play:app-update-ktx:2.1.0")

    //amazon aws s3 lib
    implementation("com.amazonaws:aws-android-sdk-s3:2.18.0")

    //image compressor
    implementation("id.zelory:compressor:3.0.1")

    implementation("com.google.android.gms:play-services-auth-api-phone:18.0.2")
    implementation ("com.google.android.recaptcha:recaptcha:18.4.0")
}