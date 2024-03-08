package ch.admin.bar.siardsuite.component.stepper;

import ch.admin.bar.siardsuite.framework.steps.Step;
import ch.admin.bar.siardsuite.util.i18n.keys.I18nKey;
import ch.admin.bar.siardsuite.view.RootStage;
import ch.admin.bar.siardsuite.view.skins.CustomStepperSkin;
import ch.admin.bar.siardsuite.view.skins.CustomStepperToggleSkin;
import io.github.palexdev.materialfx.controls.MFXStepper;
import io.github.palexdev.materialfx.controls.MFXStepperToggle;
import io.github.palexdev.materialfx.enums.StepperToggleState;
import javafx.scene.control.Button;
import lombok.RequiredArgsConstructor;
import lombok.val;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class StepperInitializer {

    private final RootStage stage;
    private final MFXStepper stepper;

    public void init(List<Step> steps) {
        val stepNumber = new AtomicInteger(1);
        val toggles = steps.stream()
                .map(step -> {
                    if (step.isVisible()) {
                        return createToggle(step, stepNumber.getAndIncrement() + "");
                    }
                    return createToggle(step, "");
                })
                .collect(Collectors.toList());

        stepper.getStepperToggles()
                .addAll(toggles);
        stepper.setSkin(new CustomStepperSkin(stepper, stage));

        if (stepper.getCurrentIndex() == -1) {
            stepper.next();
        }
    }

    private MFXStepperToggle createToggle(final Step step, final String stepNumber) {
        Button btn = new Button();
        btn.getStyleClass().setAll("stepper-btn", "number-btn");
        btn.setText(stepNumber);

        MFXStepperToggle toggle = new MFXStepperToggle(
                // passing the key is kind of a hack to bind it in the CustomStepperToggleSkin
                step.getTitle()
                        .map(I18nKey::getValue)
                        .orElse(""),
                btn);

        // do lazy load view
        toggle.stateProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (StepperToggleState.SELECTED.equals(newValue) && toggle.getContent() == null) {
                        toggle.setContent(step.getViewLoader().get().getNode());
                    }
                });

        toggle.setSkin(new CustomStepperToggleSkin(toggle, step.isVisible(), stage));

        return toggle;
    }
}
