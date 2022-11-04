package ch.admin.bar.siardsuite.model;

import java.util.List;

public class UploadSteps {
    public static final List<Step> steps;
    public  static  final List<Step> savedConnectionSteps;

    private final static Step selectDbms = new Step("upload.step.name.dbms", View.UPLOAD_CHOOSE_DBMS, 1, true);
    static {
        steps = List.of(selectDbms);
        savedConnectionSteps = List.of();
    }
}
