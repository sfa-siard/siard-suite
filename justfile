[doc('List all available recipes')]
default:
    just --list

alias ar := app-run
[doc('Run the SIARD Suite Desktop App')]
[group('SIARD Suite Desktop Application')]
app-run:
    ./gradlew :siard-suite-app:run


alias wd := web-dev
[doc("Run the SIARD Suite local web app in dev mode (Quarkus :8080 + Vite :5173, opens the launch URL, Ctrl+C stops both)")]
[group('SIARD Suite Web Application')]
web-dev:
    #!/usr/bin/env bash
    set -uo pipefail

    if ! command -v npm >/dev/null 2>&1; then
        echo "error: npm not found — the dev frontend needs Node.js (see siard-web/.nvmrc)" >&2
        exit 1
    fi
    [ -d siard-web/node_modules ] || (cd siard-web && npm ci)

    # job control gives each dev server its own process group, so stop() can
    # take down whole trees (gradlew->java, npm->vite); without it kill $PID
    # would only reach the direct child
    set -m
    ./gradlew :siard-web:local:backend:quarkusDev &
    BACKEND=$!
    (cd siard-web/local/frontend && exec npm run dev) &
    FRONTEND=$!
    OPENER=

    stop() {
        kill -INT -- "-$BACKEND" "-$FRONTEND" 2>/dev/null || true
        [ -n "$OPENER" ] && kill "$OPENER" 2>/dev/null || true
        wait 2>/dev/null || true
    }
    trap stop EXIT
    trap 'exit 130' INT TERM

    # once the backend and Vite answer, open the documented dev launch URL
    # (siard-web/local/README.md); the token is the fixed dev token — reusable
    # and non-expiring in development mode (LaunchCredentials). Probe both
    # servers directly: hitting /launch through the Vite proxy while the backend
    # is still compiling would spam "http proxy error" into the dev output
    token=$(sed -n 's/^%dev\.siard\.agent\.dev-token=//p' \
        siard-web/local/backend/src/main/resources/application.properties | head -n1)
    if [ -n "$token" ] && command -v curl >/dev/null 2>&1; then
        url="http://127.0.0.1:5173/launch?token=$token"
        (
            for ((i = 0; i < 180; i++)); do
                # a missing token still yields a <400 response (303), so -f only
                # fails while a server is not up yet
                if curl -sf -o /dev/null "http://127.0.0.1:8080/launch" \
                    && curl -sf -o /dev/null "http://127.0.0.1:5173/"; then
                    echo "opening $url"
                    case "$(uname -s)" in
                        Linux*)               xdg-open "$url" >/dev/null 2>&1 ;;
                        Darwin*)              open "$url" >/dev/null 2>&1 ;;
                        MINGW*|MSYS*|CYGWIN*) cmd //c start "" "$url" >/dev/null 2>&1 ;;
                    esac
                    exit 0
                fi
                sleep 1
            done
        ) &
        OPENER=$!
    fi

    # poll instead of wait -n (bash 3.2 on macOS lacks it); sleep stays async
    # so this shell keeps the terminal's foreground and receives Ctrl+C
    while kill -0 "$BACKEND" 2>/dev/null && kill -0 "$FRONTEND" 2>/dev/null; do
        sleep 1 &
        wait $!
    done


alias cb := cli-build
[doc("Build the SIARD Suite CLI (dev distribution, requires Java 21)")]
[group('SIARD Suite CLI')]
cli-build:
    ./gradlew :siard-cmd:installDist


alias ci := cli-image
[doc("Build the runtime-bundled CLI image zip (no Java needed to run)")]
[group('SIARD Suite CLI')]
cli-image:
    ./gradlew :siard-cmd:runtimeZip


alias cp := cli-package
[doc("Build the native CLI installer for the current OS")]
[group('SIARD Suite CLI')]
cli-package:
    ./gradlew :siard-cmd:jpackage


alias ta := test-all
[doc("Run all tests (go get a coffee!)")]
[group('Testing')]
test-all:
    ./gradlew test


alias t := test
[doc("Run tests for a module, e.g. just test siard-api")]
[group('Testing')]
test module:
    ./gradlew :{{module}}:test


alias it := integration-test
[doc("Run integration tests for a database, e.g. just integration-test postgres (requires Docker)")]
[group('Testing')]
integration-test db:
    ./gradlew :siard-cmd:integrationTest{{capitalize(db)}}


alias cv := current-version
[doc("Check current version")]
[group('Releasing')]
current-version:
    ./gradlew currentVersion


[doc("Create a release tag and push to remote (see release guide)")]
[group('Releasing')]
release:
    ./gradlew release


alias um := user-manual-pdf
[doc("Create User Manual PDF")]
[group('Documentation')]
user-manual-pdf:
    ./gradlew :siard-suite-app:asciidoctorPdf
