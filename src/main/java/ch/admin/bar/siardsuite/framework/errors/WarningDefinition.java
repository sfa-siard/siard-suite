package ch.admin.bar.siardsuite.framework.errors;

import ch.admin.bar.siardsuite.framework.i18n.DisplayableText;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class WarningDefinition { // TODO Rename
    DisplayableText title;
    DisplayableText message;
}
