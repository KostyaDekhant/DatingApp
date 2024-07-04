plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.datingappclient"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.datingappclient"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    packagingOptions {
        exclude("META-INF/spring.factories")
        exclude("META-INF/spring.schemas")
        exclude("META-INF/spring/aot.factories")
        exclude("META-INF/spring.tooling")
        exclude("META-INF/spring.handlers")
        exclude("META-INF/license.txt")
        exclude("META-INF/DEPENDENCIES")
        exclude("META-INF/INDEX.LIST")
        exclude("META-INF/notice.txt")
        exclude("META-INF/web-fragment.xml")
        exclude("META-INF/spring-configuration-metadata.json")
        exclude("META-INF/additional-spring-configuration-metadata.json")

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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17

    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation("com.squareup.retrofit2:retrofit:2.11.0")

    // https://mvnrepository.com/artifact/com.squareup.retrofit2/converter-gson
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")

    implementation("com.google.android.material:material:1.5.0")

    implementation("org.projectlombok:lombok:1.18.34")

    // https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-databind
    implementation("com.fasterxml.jackson.core:jackson-databind:2.0.1")

}