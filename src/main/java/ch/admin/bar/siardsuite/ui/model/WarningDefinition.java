package ch.admin.bar.siardsuite.ui.model;

import ch.admin.bar.siardsuite.framework.i18n.DisplayableText;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class WarningDefinition {
    DisplayableText title;
    DisplayableText message;
}
