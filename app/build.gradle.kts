plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("maven-publish")
}

android {
    namespace = "com.adiuxm.genaisqa"
    compileSdk = 34

    defaultConfig {
//        applicationId = "com.adiuxm.genaisqa"
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        viewBinding = true
        dataBinding = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
//            proguardFiles(
//                getDefaultProguardFile("proguard-android-optimize.txt"),
//                "proguard-rules.pro"
//            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    project.afterEvaluate {
        publishing {
            publications {
                create<MavenPublication>("release") {
                    from(components["release"])

                    groupId = "com.github.vinvijdev"
                    artifactId = "SQA"
                    version = "1.0.31"
                }
            }
        }
    }

//    project.afterEvaluate {
//        publishing {
//            publications {
//                create<MavenPublication>("release") {
//                    artifact(tasks["bundleReleaseAar"])
//                    artifactId = "smartqueryassistant"
//                    groupId = "com.adiuxm.genaisqa"
//                    version = "1.0"
//                    task("PublishToMavenLocal")
//
//                    pom.withXml {
//                        asNode().appendNode("dependencies").apply {
//                            configurations["implementation"].allDependencies.forEach { dependency ->
//                                appendNode("dependency").apply {
//                                    appendNode("groupId", dependency.group)
//                                    appendNode("artifactId", dependency.name)
//                                    appendNode("version", dependency.version)
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.4")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.4")
    implementation("com.google.android.gms:play-services-location:21.3.0")

    //Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.4.0")
    implementation("com.squareup.retrofit2:converter-gson:2.4.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation("com.squareup.okhttp3:okhttp-urlconnection:4.12.0")
    implementation("com.squareup.retrofit2:adapter-rxjava2:2.9.0")

    //to add the dependency on Fragment
    implementation("androidx.fragment:fragment-ktx:1.8.2")

    //to add the dependency on the Activity
    implementation("androidx.activity:activity-ktx:1.9.1")

    // dots
    implementation("com.github.tajchert:WaitingDots:0.6.1")

    // image lib
    implementation ("com.github.bumptech.glide:glide:4.16.0")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
}