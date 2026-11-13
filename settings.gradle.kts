pluginManagement { repositories { google(); mavenCentral(); gradlePluginPortal() } }
dependencyResolutionManagement { repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS); repositories { google(); mavenCentral() } }
rootProject.name = "Plugin Studio"
include(":app", ":core:common", ":core:storage", ":core:templates", ":core:editor", ":domain", ":data")
