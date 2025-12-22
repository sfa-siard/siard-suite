package ch.admin.bar.siard2.cmd.utils;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

public class ResourcesLoaderTest {

    private static final String EXISTING_RESOURCE = "dummy-resource.txt";
    private static final String NOT_EXISTING_RESOURCE = "mot-existing-dummy-resource.txt";

    @Test
    public void loadResource_withExistingResource_expectNoException() throws IOException {
        // given

        // when
        InputStream is = ResourcesLoader.loadResource(EXISTING_RESOURCE);

        // then
        Assertions.assertThat(is).isNotEmpty();
        is.close();
    }

    @Test
    public void loadResource_withNotExistingResource_expectException() {
        // given

        // when
        Throwable throwable = Assertions.catchThrowable(() -> ResourcesLoader.loadResource(NOT_EXISTING_RESOURCE).close());

        // then
        Assertions.assertThat(throwable).hasMessageContainingAll("", "not found");
    }

}