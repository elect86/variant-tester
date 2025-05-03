plugins {
    java
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("../jogl-variant-publisher/plugin/repo")
}

open class GluegenRule : ComponentMetadataRule {
    data class NativeVariant(val os: String, val arch: String) {
        val classifier = "$os-$arch"
    }

    private val nativeVariants = listOf(NativeVariant("android", "aarch64"),
                                        NativeVariant("linux", "aarch64"),
                                        NativeVariant("linux", "amd64"),
                                        NativeVariant("linux", "armv6hf"),
                                        NativeVariant("macosx", "universal"),
                                        NativeVariant("windows", "amd64"))

    override fun execute(context: ComponentMetadataContext) {
        println(1)
        println("execute(${context.details})")
        val a = context.details.attributes.keySet().elementAt(0)
        println("${a.name}, ${a.type}")
        val pom = context.getDescriptor(PomModuleDescriptor::class)!!
        println(pom.packaging)
        context.details.withVariant("runtime") {
            println(2)
            attributes {
                println(3)
                attribute(Attribute.of("os", String::class.java), "none")
                attribute(Attribute.of("arch", String::class.java), "none")
            }
//            withFiles {
//                val file = "${context.details.id.name}-${context.details.id.version}-linux-amd64.jar"
//                println("addFile($file)")
//                addFile(file)
//            }
        }
        for (variant in nativeVariants)
            context.details.addVariant("${variant.classifier}-runtime", "runtime") {
                attributes {
                    attribute(Attribute.of("os", String::class.java), variant.os)
                    attribute(Attribute.of("arch", String::class.java), variant.arch)
                }
//                this as VariantMetadataAdapter
                val jar = "${context.details.id.name}-${context.details.id.version}-${variant.classifier}.jar"
                println("variant ${variant.classifier}-runtime, $jar")
                withFiles {
                    addFile(jar)
                }
            }
    }
}

configurations.runtimeClasspath.get().attributes {
    // select a platform, will fail to compose a runtime classpath if non is selected
    attribute(Attribute.of("os", String::class.java), "linux")
    attribute(Attribute.of("arch", String::class.java), "x86_64")
}

dependencies {
    components { withModule<GluegenRule>("org.jogamp.gluegen:gluegen-rt") }
    implementation("org.jogamp.gluegen:gluegen-rt:0.0.8")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

