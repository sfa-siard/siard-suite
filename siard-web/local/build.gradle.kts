// Aggregates the local web app's two modules (backend, frontend), so CI and the README can use
// :siard-web:local:check and :siard-web:local:e2eTest (design D14).

tasks.register("check") {
    description = "Runs all checks of the local web app's backend and frontend."
    group = "verification"
    dependsOn(
        ":siard-web:local:backend:check",
        ":siard-web:local:frontend:check"
    )
}

tasks.register("e2eTest") {
    description = "Runs the Playwright smoke test of the local web app against the packaged agent."
    group = "verification"
    dependsOn(":siard-web:local:frontend:e2eTest")
}
