package ch.admin.bar.siard2.cmd.utils.siard.update;

import lombok.Builder;
import lombok.Singular;
import lombok.Value;
import lombok.val;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.util.Set;
import java.util.stream.Collectors;

public class UpdaterTest {

    private static final DummyA DUMMY_DATA = DummyA.builder()
            .name("name_dummy_A1")
            .singleChild(DummyB.builder()
                    .name("name_dummy_B1")
                    .singleChild(DummyC.builder()
                            .name("name_dummy_C1")
                            .build())
                    .build())
            .multipleChild(DummyC.builder()
                    .name("name_dummy_C2")
                    .build())
            .multipleChild(DummyC.builder()
                    .name("name_dummy_C3")
                    .build())
            .build();

    @Test
    public void applyUpdates_onRootParent_expectAppliedUpdate() {
        // given
        val updater = Updater.builder()
                .instruction(UpdateInstruction.<DummyA>builder()
                        .clazz(DummyA.class)
                        .updater(dummyA -> dummyA.toBuilder()
                                .name("UPDATED_" + dummyA.getName())
                                .build())
                        .build())
                .build();

        // when
        val updated = DUMMY_DATA.applyUpdates(updater);

        // then
        val expected = DUMMY_DATA.toBuilder()
                .name("UPDATED_" + DUMMY_DATA.getName())
                .build();

        Assertions.assertThat(updated).isEqualTo(expected);
    }

    @Test
    public void applyUpdates_onChild_expectAppliedUpdate() {
        // given
        val updater = Updater.builder()
                .instruction(UpdateInstruction.<DummyB>builder()
                        .clazz(DummyB.class)
                        .updater(dummyB -> dummyB.toBuilder()
                                .name("UPDATED_" + dummyB.getName())
                                .build())
                        .build())
                .build();

        // when
        val updated = DUMMY_DATA.applyUpdates(updater);

        // then
        val expected = DUMMY_DATA.toBuilder()
                .singleChild(DUMMY_DATA.getSingleChild().toBuilder()
                        .name("UPDATED_" + DUMMY_DATA.getSingleChild().getName())
                        .build())
                .build();

        Assertions.assertThat(updated).isEqualTo(expected);
    }

    @Test
    public void applyUpdates_onChildFromChild_expectAppliedUpdate() {
        // given
        val updater = Updater.builder()
                .instruction(UpdateInstruction.<DummyC>builder()
                        .clazz(DummyC.class)
                        .updater(dummyC -> dummyC.toBuilder()
                                .name("UPDATED_" + dummyC.getName())
                                .build())
                        .build())
                .build();

        // when
        val updated = DUMMY_DATA.applyUpdates(updater);

        // then
        val expected = DUMMY_DATA.toBuilder()
                .clearMultipleChildren()
                .singleChild(DUMMY_DATA.getSingleChild().toBuilder()
                        .singleChild(DUMMY_DATA.getSingleChild().getSingleChild().toBuilder()
                                .name("UPDATED_" + DUMMY_DATA.getSingleChild().getSingleChild().getName())
                                .build())
                        .build())
                .multipleChildren(DUMMY_DATA.getMultipleChildren().stream()
                        .map(dummyC -> dummyC.toBuilder()
                                .name("UPDATED_" + dummyC.getName())
                                .build())
                        .collect(Collectors.toSet()))
                .build();

        Assertions.assertThat(updated).isEqualTo(expected);
    }

    @Test
    public void applyUpdates_generalStrings_expectAppliedUpdate() {
        // given
        val updater = Updater.builder()
                .instruction(UpdateInstruction.<String>builder()
                        .clazz(String.class)
                        .updater(String::toUpperCase)
                        .build())
                .build();

        // when
        val updated = DUMMY_DATA.applyUpdates(updater);

        // then
        val expected = DUMMY_DATA.toBuilder()
                .name(DUMMY_DATA.getName().toUpperCase())
                .singleChild(DUMMY_DATA.getSingleChild().toBuilder()
                        .name(DUMMY_DATA.getSingleChild().getName().toUpperCase())
                        .singleChild(DUMMY_DATA.getSingleChild().getSingleChild().toBuilder()
                                .name(DUMMY_DATA.getSingleChild().getSingleChild().getName().toUpperCase())
                                .build())
                        .build())
                .clearMultipleChildren()
                .multipleChildren(DUMMY_DATA.getMultipleChildren().stream()
                        .map(dummyC -> dummyC.toBuilder()
                                .name(dummyC.getName().toUpperCase())
                                .build())
                        .collect(Collectors.toSet()))
                .build();

        Assertions.assertThat(updated).isEqualTo(expected);
    }


    @Value
    @Builder(toBuilder = true)
    private static class DummyA implements Updatable<DummyA> {
        String name;
        DummyB singleChild;

        @Singular
        Set<DummyC> multipleChildren;

        @Override
        public DummyA applyUpdates(Updater updater) {
            val updatedThis = updater.applyUpdate(this);

            return new DummyA(
                    updater.applyUpdate(updatedThis.name),
                    updatedThis.singleChild.applyUpdates(updater),
                    multipleChildren.stream()
                            .map(dummyC -> dummyC.applyUpdates(updater))
                            .collect(Collectors.toSet()));
        }
    }

    @Value
    @Builder(toBuilder = true)
    private static class DummyB implements Updatable<DummyB> {
        String name;
        DummyC singleChild;

        @Override
        public DummyB applyUpdates(Updater updater) {
            val updatedThis = updater.applyUpdate(this);

            return new DummyB(
                    updater.applyUpdate(updatedThis.name),
                    updatedThis.singleChild.applyUpdates(updater));
        }
    }

    @Value
    @Builder(toBuilder = true)
    private static class DummyC implements Updatable<DummyC> {
        String name;

        @Override
        public DummyC applyUpdates(Updater updater) {
            val updatedThis = updater.applyUpdate(this);

            return new DummyC(updater.applyUpdate(updatedThis.name));
        }
    }
}