package ch.admin.bar.siardsuite.presenter;

import ch.admin.bar.siardsuite.Controller;
import ch.admin.bar.siardsuite.model.Step;
import ch.admin.bar.siardsuite.model.View;
import ch.admin.bar.siardsuite.util.CastHelper;
import ch.admin.bar.siardsuite.view.RootStage;
import ch.admin.bar.siardsuite.view.skins.CustomStepperSkin;
import ch.admin.bar.siardsuite.view.skins.CustomStepperToggleSkin;
import io.github.palexdev.materialfx.controls.MFXStepper;
import io.github.palexdev.materialfx.controls.MFXStepperToggle;
import io.github.palexdev.materialfx.enums.StepperToggleState;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import lombok.val;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public abstract class StepperPresenter extends Presenter implements StepperDependant {

    @FXML
    protected BorderPane borderPane;

    public abstract void init(Controller controller, RootStage stage, MFXStepper stepper);

    @Override
    public void injectStepper(MFXStepper stepper) {
        this.init(
                Objects.requireNonNull(controller),
                Objects.requireNonNull(stage),
                Objects.requireNonNull(stepper));
    }

    protected void createStepper(List<Step> steps, MFXStepper stepper) {
        boolean recentConnection = controller.getTempConnectionData().isPresent();
        List<MFXStepperToggle> stepperToggles = createSteps(steps, stepper);

        stepper.getStepperToggles().addAll(stepperToggles);
        stepper.setSkin(new CustomStepperSkin(stepper, stage));

        if (stepper.getCurrentIndex() == -1) {
            stepper.next();
        }

        if (recentConnection) {
            stepper.getStepperToggles().get(0).setState(StepperToggleState.COMPLETED);
            stepper.updateProgress();
            stepper.next();
        }
    }

    private List<MFXStepperToggle> createSteps(List<Step> steps, MFXStepper stepper) {

        return steps.stream()
                .map((step) -> createCustomStepperToggle(step.key(), step.position(), () -> loadView(step.contentView(), stepper), step.visible()))
                .collect(Collectors.toList());
    }

    private MFXStepperToggle createCustomStepperToggle(
            String key,
            int pos,
            Supplier<Node> contentSupplier,
            Boolean visible
    ) {
        Button btn = new Button();
        btn.getStyleClass().setAll("stepper-btn", "number-btn");
        btn.setText(String.valueOf(pos));
        // passing the key is kind of a hack to bind it in the CustomStepperToggleSkin
        MFXStepperToggle toggle = new MFXStepperToggle(key, btn);

        // do lazy load view, because view-data is probably not yet ready
        toggle.stateProperty().addListener((observable, oldValue, newValue) -> {
            if (StepperToggleState.SELECTED.equals(newValue) && toggle.getContent() == null) {
                toggle.setContent(contentSupplier.get());
            }
        });

        toggle.setSkin(new CustomStepperToggleSkin(toggle, visible, stage));
        toggle.setVisible(visible);

        return toggle;
    }

    private Node loadView(View viewName, MFXStepper stepper) {
        val loaded = viewName.getViewCreator()
                .apply(controller, stage);

        CastHelper.tryCast(loaded.getController(), StepperDependant.class)
                .orElseThrow(() -> new IllegalStateException(
                        String.format("%s is not a stepper presenter", viewName.name())))
                .injectStepper(stepper);

        return loaded.getNode();
    }
}
