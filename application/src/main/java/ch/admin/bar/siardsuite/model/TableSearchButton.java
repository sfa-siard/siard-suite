package ch.admin.bar.siardsuite.model;

import io.github.palexdev.materialfx.controls.MFXButton;

import java.util.Objects;

public final class TableSearchButton {
    private final MFXButton button;
    private final boolean active;

    public TableSearchButton(MFXButton button, boolean active) {
        this.button = button;
        this.active = active;
    }

    public MFXButton button() {
        return button;
    }

    public boolean active() {
        return active;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        TableSearchButton that = (TableSearchButton) obj;
        return Objects.equals(this.button, that.button) &&
                this.active == that.active;
    }

    @Override
    public int hashCode() {
        return Objects.hash(button, active);
    }

    @Override
    public String toString() {
        return "TableSearchButton[" +
                "button=" + button + ", " +
                "active=" + active + ']';
    }
}
