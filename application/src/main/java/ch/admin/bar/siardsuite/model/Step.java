package ch.admin.bar.siardsuite.model;

import java.util.Objects;

public final class Step {
    private final String key;
    private final View contentView;
    private final Integer position;
    private final Boolean visible;

    public Step(String key, View contentView, Integer position, Boolean visible) {
        this.key = key;
        this.contentView = contentView;
        this.position = position;
        this.visible = visible;
    }

    public String key() {
        return key;
    }

    public View contentView() {
        return contentView;
    }

    public Integer position() {
        return position;
    }

    public Boolean visible() {
        return visible;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        Step that = (Step) obj;
        return Objects.equals(this.key, that.key) &&
                Objects.equals(this.contentView, that.contentView) &&
                Objects.equals(this.position, that.position) &&
                Objects.equals(this.visible, that.visible);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, contentView, position, visible);
    }

    @Override
    public String toString() {
        return "Step[" +
                "key=" + key + ", " +
                "contentView=" + contentView + ", " +
                "position=" + position + ", " +
                "visible=" + visible + ']';
    }


}
