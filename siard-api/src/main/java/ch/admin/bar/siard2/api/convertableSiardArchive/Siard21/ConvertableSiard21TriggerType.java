package ch.admin.bar.siard2.api.convertableSiardArchive.Siard21;

import ch.admin.bar.siard2.api.convertableSiardArchive.Siard22.ConvertableSiard22TriggerType;
import ch.admin.bar.siard2.api.convertableSiardArchive.Siard22.Siard21ToSiard22Transformer;
import ch.admin.bar.siard2.api.generated.old21.TriggerType;

public class ConvertableSiard21TriggerType extends TriggerType {
    public ConvertableSiard21TriggerType(TriggerType trigger) {
        super();
        this.name = trigger.getName();
        this.description = trigger.getDescription();
        this.aliasList = trigger.getAliasList();
        this.triggeredAction = trigger.getTriggeredAction();
        this.triggerEvent = trigger.getTriggerEvent();
        this.actionTime = trigger.getActionTime();
    }

    public ConvertableSiard22TriggerType accept(Siard21ToSiard22Transformer visitor) {
        return visitor.visit(this);
    }
}
