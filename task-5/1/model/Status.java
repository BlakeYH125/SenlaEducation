public enum Status {
    AVAILABLE("свободный"),
    OCCUPIED("занят"),
    IN_SERVICE("обслуживается");

    private final String label;

    Status(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}