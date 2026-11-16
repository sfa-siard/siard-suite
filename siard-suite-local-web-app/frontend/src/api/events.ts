import { useEffect } from "react";
import type { components } from "./schema";

export type AgentEvent = components["schemas"]["AgentEvent"];

/**
 * Keeps this window's event stream open while mounted (design D5). The open stream is the
 * window's presence signal: the agent exits once no window is connected. Events are handed to
 * `onEvent`; none needs handling yet besides `connected`.
 *
 * Call it only once per window (the app shell does). Every call opens its own stream.
 * TODO before the first job events: restructure into one stream per window that distributes
 * events to components, preferably by updating the React Query cache, so components never open
 * streams of their own (see design D5).
 */
export function useAgentEvents(onEvent?: (event: AgentEvent) => void): void {
  useEffect(() => {
    const source = new EventSource("/api/events");
    source.onmessage = (message: MessageEvent<string>) => {
      onEvent?.(JSON.parse(message.data) as AgentEvent);
    };
    return () => source.close();
  }, [onEvent]);
}
