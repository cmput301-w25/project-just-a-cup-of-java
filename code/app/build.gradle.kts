plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.androidx.navigation.safeargs)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.justacupofjavapersonal"
    compileSdk = 35

    buildFeatures {
        viewBinding = true
    }

    defaultConfig {
        applicationId = "com.example.justacupofjavapersonal"
        minSdk = 24
        targetSdk = 35
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(files("C:\\Users\\bat16\\AppData\\Local\\Android\\Sdk\\platforms\\android-35\\android.jar"))
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.inappmessaging)
    implementation(libs.core)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core) // Use this version from libs.versions.toml
    implementation("com.google.android.gms:play-services-maps:18.1.0")
    implementation(platform("com.google.firebase:firebase-bom:33.10.0"))
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.prolificinteractive:material-calendarview:1.4.3")
    implementation("de.hdodenhof:circleimageview:3.1.0")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.fragment:fragment-testing:1.5.7")
    androidTestImplementation("androidx.navigation:navigation-testing:2.8.5")
    androidTestImplementation("androidx.test:core:1.5.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.test.espresso:espresso-contrib:3.5.1") // Required for RecyclerViewActions
}