plugins {
    id("java")
    `java-library`
}

group = "services.spice.kazsite"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    // https://mvnrepository.com/artifact/org.jetbrains/annotations
    implementation("org.jetbrains:annotations:24.0.1")

    // Web server
    implementation("io.javalin:javalin:5.4.2")

    // Avaje
    implementation("io.avaje:avaje-config:3.2")
    implementation("io.avaje:avaje-http-api:1.39")
    annotationProcessor("io.avaje:avaje-http-javalin-generator:1.31")
    // inject
    implementation("io.avaje:avaje-inject:9.1-RC2")
    annotationProcessor("io.avaje:avaje-inject-generator:9.1-RC2")

    // Pac (Security)
    implementation("org.pac4j:javalin-pac4j:6.0.0")
    // https://mvnrepository.com/artifact/org.pac4j/pac4j-oauth
    implementation("org.pac4j:pac4j-oauth:5.7.1")

    // https://mvnrepository.com/artifact/org.postgresql/postgresql/
    implementation("org.postgresql:postgresql:42.6.0")
    implementation("com.zaxxer:HikariCP:5.0.1")

    // Web requests
    implementation("com.squareup.okhttp3:okhttp:4.11.0")

    // Vue (front-end)
    implementation("org.webjars.npm:vue:3.2.47")

    // https://mvnrepository.com/artifact/com.google.code.gson/gson
    implementation("com.google.code.gson:gson:2.10.1")

    // Logging
    // https://mvnrepository.com/artifact/org.slf4j/slf4j-simple
    api("org.slf4j:slf4j-simple:2.0.6")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.2")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}