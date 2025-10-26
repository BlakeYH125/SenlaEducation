public enum Status {
    AVAILABLE("свободна"),
    OCCUPIED("занята"),
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