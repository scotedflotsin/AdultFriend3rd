plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.adultfriendbella.adultfre"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.adultfriendbella.adultfre"
        minSdk = 24
        targetSdk = 36
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    viewBinding {
        enable = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.firebase.analytics.ktx)
    implementation(libs.play.services.cast.framework)
    implementation(libs.firebase.storage)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.analytics)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.sdp.android)
    implementation(libs.circleimageview)
    implementation(libs.material.v190)
    implementation(libs.github.glide)
    annotationProcessor(libs.github.compiler)
    implementation(libs.glide.transformations)
        implementation(platform(libs.firebase.bom))
        implementation(libs.firebase.auth)
    implementation(libs.imagepicker)
 //   implementation ("com.github.dhaval2404:imagepicker-support:1.7.1")
    implementation("com.github.dhaval2404:imagepicker:2.1")
    implementation ("com.squareup.picasso:picasso:2.8")


}