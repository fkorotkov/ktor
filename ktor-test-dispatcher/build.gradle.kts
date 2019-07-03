val ideaActive: Boolean by project.extra
val isMacosHost: Boolean by project.extra
kotlin {

    sourceSets {
        if (ideaActive) {
            val srcDir = when {
                isMacosHost -> "macosX64/src"
                else -> "linuxX64/src"
            }

            val posixIde by creating {
                kotlin.srcDir(srcDir)
            }

            get("posixMain").dependsOn(posixIde)
        } else {
            configure(listOf(watchosArm32().compilations.get("main").defaultSourceSet)) {
                kotlin.srcDir("iosArm32/src")
            }

            configure(
                listOf(
                    watchosArm64().compilations.get("main").defaultSourceSet,
                    tvosArm64().compilations.get("main").defaultSourceSet
                )
            ) {
                kotlin.srcDir("iosArm64/src")
            }

            configure(
                listOf(
                    watchosX86().compilations.get("main").defaultSourceSet,
                    tvosX64().compilations.get("main").defaultSourceSet
                )
            ) {
                kotlin.srcDir("iosX64")
            }
        }
    }
}
