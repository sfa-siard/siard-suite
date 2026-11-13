import { spawn, type ChildProcess } from "node:child_process";

/** The packaged agent as a separate process, started like an installed app. */
export interface Agent {
  launchUrl: string;
  port: number;
  /** Resolves with the exit code once the process has ended. */
  exited: Promise<number | null>;
  stop(): void;
}

const LAUNCH_URL =
  /launch URL: (http:\/\/127\.0\.0\.1:(\d+)\/launch\?token=\S+)/;

// the launcher's JVM flags for siard-api (design D2), as in the backend's build.gradle.kts
const SIARD_JVM_ARGS = [
  "-Djavax.xml.parsers.SAXParserFactory=com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl",
  "--add-opens=java.xml/com.sun.org.apache.xerces.internal.jaxp=ALL-UNNAMED",
  "--add-opens=java.xml/com.sun.org.apache.xalan.internal.xsltc.trax=ALL-UNNAMED",
  "--add-opens=java.base/java.lang=ALL-UNNAMED",
];

export function required(name: string): string {
  const value = process.env[name];
  if (!value) {
    throw new Error(
      `${name} is not set; run the test via ./gradlew :siard-suite-local-web-app:e2eTest`,
    );
  }
  return value;
}

/**
 * Starts the agent built by quarkusBuild with no browser of its own and the given grace period,
 * and waits for the launch URL in its log.
 */
export async function startAgent(idleExitGraceSeconds: number): Promise<Agent> {
  const child: ChildProcess = spawn(
    required("E2E_JAVA"),
    [
      ...SIARD_JVM_ARGS,
      "-Dsiard.agent.open-browser=false",
      `-Dsiard.agent.idle-exit-grace=${idleExitGraceSeconds}s`,
      "-jar",
      required("E2E_AGENT_JAR"),
    ],
    { stdio: ["ignore", "pipe", "pipe"] },
  );
  const exited = new Promise<number | null>((resolve) =>
    child.once("exit", (code) => resolve(code)),
  );
  const stop = () => {
    if (child.exitCode === null && child.signalCode === null) {
      child.kill();
    }
  };

  let output = "";
  const launch = new Promise<RegExpMatchArray>((resolve, reject) => {
    const onData = (chunk: Buffer) => {
      output += chunk.toString();
      const match = LAUNCH_URL.exec(output);
      if (match) {
        resolve(match);
      }
    };
    child.stdout?.on("data", onData);
    child.stderr?.on("data", onData);
    child.once("exit", () =>
      reject(
        new Error(`agent exited before logging its launch URL:\n${output}`),
      ),
    );
    setTimeout(
      () => reject(new Error(`no launch URL within 30 s:\n${output}`)),
      30_000,
    );
  });

  try {
    const match = await launch;
    return { launchUrl: match[1], port: Number(match[2]), exited, stop };
  } catch (error) {
    stop();
    throw error;
  }
}
