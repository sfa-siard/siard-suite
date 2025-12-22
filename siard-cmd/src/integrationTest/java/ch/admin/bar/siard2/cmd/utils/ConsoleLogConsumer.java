package ch.admin.bar.siard2.cmd.utils;

import org.testcontainers.containers.output.OutputFrame;

import java.util.function.Consumer;

/**
 * Can be used to write outputs from a test container to the system out.
 */
public class ConsoleLogConsumer implements Consumer<OutputFrame> {
    @Override
    public void accept(OutputFrame outputFrame) {
        if (outputFrame.getType() == OutputFrame.OutputType.STDERR) {
            System.err.println("Container-Output: " + outputFrame.getUtf8StringWithoutLineEnding());
        } else {
            System.out.println("Container-Output: " + outputFrame.getUtf8StringWithoutLineEnding());
        }
    }
}
