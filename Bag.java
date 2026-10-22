class Bag extends Item {
    private String bagType;

    public Bag(String id, String name, double price, int quantity, String bagType)
            throws InvalidInputException {
        super(id, name, price, quantity);
        setBagType(bagType);
    }

    public String getBagType() {
        return bagType;
    }

    public void setBagType(String bagType) throws InvalidInputException {
        if (bagType == null || bagType.trim().isEmpty()) {
            throw new InvalidInputException("Bag type is required.");
        }
        this.bagType = bagType.trim();
    }

    @Override
    public String getCategory() {
        return "Bag";
    }

    @Override
    public String getDetails() {
        return "Type: " + bagType;
    }
}
