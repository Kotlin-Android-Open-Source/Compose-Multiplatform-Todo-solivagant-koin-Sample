import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
  alias(libs.plugins.kotlinMultiplatform)
  alias(libs.plugins.androidApplication)
  alias(libs.plugins.kotlinParcelize)
  alias(libs.plugins.jetbrainsCompose)
}

kotlin {
  //  @OptIn(ExperimentalWasmDsl::class)
  //  wasmJs {
  //    moduleName = "composeApp"
  //    browser {
  //      commonWebpackConfig {
  //        outputFileName = "composeApp.js"
  //      }
  //    }
  //    binaries.executable()
  //  }

  jvmToolchain {
    languageVersion = JavaLanguageVersion.of(17)
    vendor = JvmVendorSpec.AZUL
  }

  androidTarget {
    compilations.all {
      kotlinOptions {
        jvmTarget = "11"
      }
    }
  }

  jvm("desktop")

  listOf(
    iosX64(),
    iosArm64(),
    iosSimulatorArm64(),
  ).forEach { iosTarget ->
    iosTarget.binaries.framework {
      baseName = "ComposeApp"
      isStatic = true
    }
  }

  sourceSets {
    val desktopMain by getting

    androidMain.dependencies {
      implementation(libs.compose.ui.tooling.preview)
      implementation(libs.androidx.activity.compose)

      // Koin
      implementation(libs.koin.android)
      implementation(libs.koin.androidx.compose)

      // Coroutines
      implementation(libs.kotlinx.coroutines.android)
    }
    commonMain.dependencies {
      implementation(compose.runtime)
      implementation(compose.foundation)
      implementation(compose.material3)
      implementation(compose.ui)
      implementation(compose.components.resources)

      // KMP View Model & Solivagant navigation
      implementation(libs.kmp.viewmodel)
      implementation(libs.kmp.viewmodel.savedstate)
      implementation(libs.kmp.viewmodel.compose)
      implementation(libs.kmp.viewmodel.koin.compose)
      implementation(libs.solivagant.navigation)

      // Koin
      implementation(libs.koin.core)
      implementation(libs.koin.compose)

      // Coroutines & FlowExt
      implementation(libs.kotlinx.coroutines.core)
      implementation(libs.flow.ext)

      // Immutable collections
      implementation(libs.kotlinx.collections.immutable)
    }
    desktopMain.dependencies {
      implementation(compose.desktop.currentOs)

      // Coroutines
      implementation(libs.kotlinx.coroutines.swing)
    }
  }
}

android {
  namespace = "com.hoc081098.solivagant.sample.todo"
  compileSdk = libs.versions.android.compileSdk.get().toInt()

  sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
  sourceSets["main"].res.srcDirs("src/androidMain/res")
  sourceSets["main"].resources.srcDirs("src/commonMain/resources")

  defaultConfig {
    applicationId = "com.hoc081098.solivagant.sample.todo"
    minSdk = libs.versions.android.minSdk.get().toInt()
    targetSdk = libs.versions.android.targetSdk.get().toInt()
    versionCode = 1
    versionName = "1.0"
  }
  packaging {
    resources {
      excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }
  }
  buildTypes {
    getByName("release") {
      isMinifyEnabled = false
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }
  buildFeatures {
    buildConfig = true
  }
  dependencies {
    debugImplementation(libs.compose.ui.tooling)
  }
}

compose.desktop {
  application {
    mainClass = "MainKt"

    nativeDistributions {
      targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
      packageName = "com.hoc081098.solivagant.sample.todo"
      packageVersion = "1.0.0"
    }
  }
}

compose.experimental {
  web.application {}
}
