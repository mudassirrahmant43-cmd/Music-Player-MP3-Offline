plugins {
    alias(libs.plugins.android).apply(false)
    alias(libs.plugins.kotlin.android).apply(false)
    alias(libs.plugins.ksp).apply(false)
    alias(libs.plugins.detekt).apply(false)
}

tasks.register<Delete>("clean") {
    delete {
        rootProject.layout.buildDirectory
    }
}
