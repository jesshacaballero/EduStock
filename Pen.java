class Pen extends Item {
    private String inkColor;

    public Pen(String id, String name, double price, int quantity, String inkColor)
            throws InvalidInputException {
        super(id, name, price, quantity);
        setInkColor(inkColor);
    }

    public String getInkColor() {
        return inkColor;
    }

    public void setInkColor(String inkColor) throws InvalidInputException {
        if (inkColor == null || inkColor.trim().isEmpty()) {
            throw new InvalidInputException("Ink color is required.");
        }
        this.inkColor = inkColor.trim();
    }

    @Override
    public String getCategory() {
        return "Pen";
    }

    @Override
    public String getDetails() {
        return "Ink color: " + inkColor;
    }
}
