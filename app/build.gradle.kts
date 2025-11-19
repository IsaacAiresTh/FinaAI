import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.example.finai"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.finai"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

//        // Carregar API Key do local.properties
//        val properties = Properties()
//        val localPropertiesFile = project.rootProject.file("local.properties")
//        if (localPropertiesFile.exists()) {
//            properties.load(localPropertiesFile.inputStream())
//        }
//        val geminiKey = properties.getProperty("GEMINI_API_KEY") ?: ""
//
//        // Injeta a chave no BuildConfig gerado
//        buildConfigField("String", "GEMINI_API_KEY", "\"$geminiKey\"")
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
//        buildConfig = true // Habilita a geração da classe BuildConfig
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // Navegação
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // Ícones estendidos
    implementation("androidx.compose.material:material-icons-extended")

    // Coil para carregar imagens
    implementation("io.coil-kt:coil-compose:2.5.0")

    // Google AI (Gemini)
    implementation(libs.google.ai.client)

    // Tesseract OCR
    implementation("cz.adaptech.tesseract4android:tesseract4android:4.9.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.5.1")

    // Room Database
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
}
