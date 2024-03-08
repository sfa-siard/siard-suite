package ch.admin.bar.siardsuite.framework.steps;

import ch.admin.bar.siardsuite.util.i18n.DisplayableText;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class StepMetaData {
    int index;
    DisplayableText title;
}
