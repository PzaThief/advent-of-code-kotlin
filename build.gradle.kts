plugins {
    kotlin("jvm") version "1.9.21"
}

sourceSets {
    main {
        kotlin.srcDir("src")
    }
}

dependencies {
    implementation(rootProject.files("z3/com.microsoft.z3.jar"))
}

tasks {
    wrapper {
        gradleVersion = "8.5"
    }
}
