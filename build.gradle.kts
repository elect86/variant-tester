import org.gradle.internal.component.model.DefaultVariantMetadata

plugins {
    `java-library`
    `maven-publish`
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
        maven("../jogl-variant-publisher/test/build/repo")
//    maven("../jogl-variant-publisher/plugin/repo")
    maven("https://jogamp.org/deployment/maven")
}
fun NamedDomainObjectProvider<Configuration>.linuxAmd64() = configure {
    attributes {
        attributes.attribute(OperatingSystemFamily.OPERATING_SYSTEM_ATTRIBUTE, objects.named("linux"))
        attributes.attribute(MachineArchitecture.ARCHITECTURE_ATTRIBUTE, objects.named("amd64"))
    }
}
configurations.runtimeClasspath.linuxAmd64()
configurations.testRuntimeClasspath.linuxAmd64()

dependencies {
    implementation("org.scijava:gluegen-rt:2.5.0")
    implementation("org.scijava:jogl-all:2.5.0")
    //    runtimeOnly("org.scijava:gluegen-rt:2.5.0:linux-amd64")
    //    runtimeOnly("org.scijava:jogl-all:2.5.0:linux-amd64")
    //    implementation("org.jogamp.gluegen:gluegen-rt:2.5.0")
    //    implementation("org.jogamp.jogl:jogl-all:2.5.0")
    //    runtimeOnly("org.jogamp.gluegen:gluegen-rt:2.5.0:natives-linux-amd64")
    //    runtimeOnly("org.jogamp.jogl:jogl-all:2.5.0:natives-linux-amd64")

//    testImplementation(platform("org.junit:junit-bom:5.10.0"))
//    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks {
    withType<Test> { logging.captureStandardOutput(LogLevel.INFO) }
    test { useJUnitPlatform() }

    register("checkClasspath") {
        doLast {
            println("=== COMPILE ===")
            configurations.compileClasspath.get().forEach { println(it.name) }
            println("=== RUNTIME ===")
            configurations.runtimeClasspath.get().forEach { println(it.name) }
        }
    }
}

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
        groupId = "group"
        artifactId = "artifact"
        version = "1.0"
    }
    repositories.maven {
        name = "repo"
        url = uri("$projectDir/$name")
    }
}