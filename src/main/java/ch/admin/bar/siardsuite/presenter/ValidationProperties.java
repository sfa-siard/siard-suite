package ch.admin.bar.siardsuite.presenter;

import java.util.List;
import java.util.stream.Collectors;

public class ValidationProperties {
    private final List<ValidationProperty> properties;

    public ValidationProperties(List<ValidationProperty> properties) {
        this.properties = properties;
    }

    public boolean validate() {
        List<Boolean> validationResults = this.properties.stream()
                                                         .map(ValidationProperty::validate)
                                                         .collect(Collectors.toList());
        // do not inline validation results - the stream will stop at the first invalid property and the other
        // props won't be processed.
        return validationResults.stream().allMatch(valid -> valid);
    }
}
