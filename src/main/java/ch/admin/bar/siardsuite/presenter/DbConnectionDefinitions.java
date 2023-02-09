package ch.admin.bar.siardsuite.presenter;

import java.util.List;
import java.util.stream.Collectors;

public class DbConnectionDefinitions {
    private final List<ValidationProperty> definitions;

    public DbConnectionDefinitions(List<ValidationProperty> definitions) {
        this.definitions = definitions;
    }

    public boolean validate() {
        List<Boolean> validationResults = this.definitions.stream()
                                                .map(ValidationProperty::validate)
                                                .collect(Collectors.toList());
        // do not inline validation results - the stream will stop at the first invalid property and the other
        // props won't be processed.
        return validationResults.stream().allMatch(valid -> valid);
    }
}
