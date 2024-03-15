package ch.admin.bar.siardsuite.ui.component.stepper;

import ch.admin.bar.siardsuite.framework.hooks.Destructible;
import ch.admin.bar.siardsuite.framework.steps.Step;
import ch.admin.bar.siardsuite.ui.component.stepper.skins.CustomStepperToggleSkin;
import ch.admin.bar.siardsuite.util.I18n;
import ch.admin.bar.siardsuite.framework.i18n.keys.I18nKey;
import ch.admin.bar.siardsuite.ui.component.stepper.skins.CustomStepperSkin;
import io.github.palexdev.materialfx.controls.MFXStepper;
import io.github.palexdev.materialfx.controls.MFXStepperToggle;
import io.github.palexdev.materialfx.enums.StepperToggleState;
import javafx.beans.value.ChangeListener;
import javafx.scene.Node;
import javafx.scene.control.Button;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
public class DrilledMFXStepper extends MFXStepper implements Destructible {

    private final List<Runnable> refreshables = new ArrayList<>();

    private final ChangeListener<Locale> onLanguageChanged = (observable, oldValue, newValue) -> refreshables.forEach(Runnable::run);

    public DrilledMFXStepper() {
        I18n.locale.addListener(onLanguageChanged);
    }

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

        getStepperToggles().addAll(toggles);

        val skin = new CustomStepperSkin(this);
        refreshables.add(skin::refresh);
        setSkin(skin);



        // bootstrap first step
        display(steps.get(0));
    }

    public void display(final Step step) {
        val viewSupplier = step.getViewSupplier().get();
        display(step.getStepIndex(), viewSupplier.getNode());
    }

    public void display(final int index, final Node node) {
        log.debug("Display step {}", index);

        val toggles = getStepperToggles();
        for (int counter = 0; counter < toggles.size(); counter++) {
            val toggle = toggles.get(counter);

            if (counter == index) {
                toggle.setState(StepperToggleState.SELECTED);
            }
            if (counter < index) {
                // before searched
                toggle.setState(StepperToggleState.NONE);
            }
            if (counter > index) {
                // after searched
                toggle.setState(StepperToggleState.NONE);
            }
        }

        setCurrentContent(node);
        setCurrentIndex(index);
    }

    private MFXStepperToggle createToggle(final Step step, final String stepNumber) {
        Button btn = new Button();
        btn.getStyleClass().setAll("stepper-btn", "number-btn");
        btn.setText(stepNumber);

        val toggle = new MFXStepperToggle(
                // passing the key is kind of a hack to bind it in the CustomStepperToggleSkin
                step.getTitle()
                        .map(I18nKey::getValue)
                        .orElse(""),
                btn);

        val skin = new CustomStepperToggleSkin(toggle, step.isVisible());
        toggle.setSkin(skin);
        refreshables.add(skin::refresh);

        return toggle;
    }

    @Override
    public void destruct() {
        I18n.locale.removeListener(onLanguageChanged);
    }
}
