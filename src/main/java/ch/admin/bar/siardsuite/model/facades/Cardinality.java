package ch.admin.bar.siardsuite.model.facades;

public class Cardinality {

    private final int cardinality;

    public Cardinality(int cardinality) {
        this.cardinality = cardinality;
    }

    public String format() {
        if (cardinality == -1) return "";
        return String.valueOf(cardinality);
    }
}
