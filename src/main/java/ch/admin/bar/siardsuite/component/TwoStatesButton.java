package ch.admin.bar.siardsuite.component;

import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import lombok.Setter;

public class TwoStatesButton extends MFXButton {
    @Setter
    private EventHandler<ActionEvent> normalStateAction = event -> {
    };
    @Setter
    private EventHandler<ActionEvent> boldStateAction = event -> {
    };

    private State state = State.NORMAL;

    public TwoStatesButton() {
        this.setOnAction(event -> {
            switch (state) {
                case NORMAL:
                    normalStateAction.handle(event);
                    setState(State.BOLD);
                    break;
                case BOLD:
                    boldStateAction.handle(event);
                    setState(State.NORMAL);
                    break;
            }
        });
    }

    public void setState(final State state) {
        this.state = state;
        switch (state) {
            case NORMAL:
                this.setStyle("-fx-font-weight: normal;");
                break;
            case BOLD:
                this.setStyle("-fx-font-weight: bold;");
                break;
        }
    }

    public enum State {
        NORMAL,
        BOLD
    }
}
