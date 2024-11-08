import java.util.Properties

pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

// local.properties 파일 읽기
val localProperties = Properties().apply {
    load(File(rootDir, "local.properties").inputStream())
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://maven.pkg.github.com/hconnectdx/bluetooth-sdk-android-v2")
            credentials {
                username = localProperties.getProperty("githubUsername")
                password = localProperties.getProperty("githubAccessToken")
            }
        }
    }
}

rootProject.name = "Hemodialysis"
include(":app")
