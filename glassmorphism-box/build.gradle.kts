import com.vanniktech.maven.publish.SonatypeHost

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.vanniktech.maven.publish)
}

android {
    namespace = "io.github.taufik_kurahman.glassmorphism_box"
    compileSdk = 34

    defaultConfig {
        minSdk = 21

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
    buildFeatures {
        compose = true
    }
}

mavenPublishing {
    coordinates(
        groupId = "io.github.taufik-kurahman",
        artifactId = "glassmorphism-box",
        version = "1.0.1"
    )
    pom {
        name.set("glassmorphism-box")
        description.set("A library for creating glassmorphism effect on Jetpack Compose.")
        inceptionYear.set("2024")
        url.set("https://github.com/taufik-kurahman/glassmorphism-box/")
        licenses {
            license {
                name.set("The Apache License, Version 2.0")
                url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                distribution.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }
        developers {
            developer {
                id.set("taufik-kurahman")
                name.set("Taufik Kurahman")
                url.set("https://github.com/taufik-kurahman/")
            }
        }
        scm {
            url.set("https://github.com/taufik-kurahman/glassmorphism-box/")
            connection.set("scm:git:git://github.com/taufik-kurahman/glassmorphism-box.git")
            developerConnection.set("scm:git:ssh://git@github.com/taufik-kurahman/glassmorphism-box.git")
        }
    }
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
    signAllPublications()
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.runtime.android)
    implementation(libs.androidx.ui.graphics.android)
    implementation(libs.androidx.ui.android)
    implementation(libs.androidx.foundation.layout.android)
    implementation(libs.renderscript)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}