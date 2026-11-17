// :siard-web groups the web modules: the shared backend core and the web apps.

subprojects {
    // JVM flags siard-api needs at runtime, for the web modules' tests, launchers and packaged
    // runtimes (restructure-into-siard-web design R2). Set here and not in backend-core, because an
    // extra property is only visible to the project that sets it and its subprojects, and the apps
    // are siblings of the core. xercesSaxParserFactory comes from the root build.
    extra["siardApiJvmArgs"] = listOf(
        extra["xercesSaxParserFactory"] as String,
        "--add-opens=java.xml/com.sun.org.apache.xerces.internal.jaxp=ALL-UNNAMED",
        "--add-opens=java.xml/com.sun.org.apache.xalan.internal.xsltc.trax=ALL-UNNAMED",
        "--add-opens=java.base/java.lang=ALL-UNNAMED"
    )
}
