package ch.admin.bar.siardsuite.presenter;

import ch.admin.bar.siardsuite.component.IconView;
import ch.admin.bar.siardsuite.component.LabelIcon;
import javafx.collections.ObservableList;
import javafx.scene.Node;

// understands a progress item (downlaod, preview, export)
public class ProgressItem {
    private int pos;
    private String text;
    private ObservableList<Node> children;
    private LabelIcon labelIcon;

    private IconView.IconType loading = IconView.IconType.LOADING;
    private IconView.IconType ok = IconView.IconType.OK;

    public ProgressItem(int pos, String text) {
        this.pos = pos;
        this.text = text;
        this.labelIcon = new LabelIcon(text, pos, IconView.IconType.LOADING);
    }

    public LabelIcon icon() {
        return this.labelIcon;
    }

    public void ok() {
        this.labelIcon.setGraphic(new IconView(this.pos, ok));
    }
}