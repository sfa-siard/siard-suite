package ch.admin.bar.siard2.api.convertableSiardArchive.Siard22;

import ch.admin.bar.siard2.api.generated.ActionTimeType;
import ch.admin.bar.siard2.api.generated.TriggerType;

public class ConvertableSiard22TriggerType extends TriggerType {


    public ConvertableSiard22TriggerType(String name, String description, String aliasList, String triggeredAction,
                                         String triggerEvent, ActionTimeType actionTime) {
        this.name = name;
        this.description = description;
        this.aliasList = aliasList;
        this.triggeredAction = triggeredAction;
        this.triggerEvent = triggerEvent;
        this.actionTime = actionTime;
    }
}
