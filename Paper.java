class Paper extends Item {
    private String paperSize;

    public Paper(String id, String name, double price, int quantity, String paperSize)
            throws InvalidInputException {
        super(id, name, price, quantity);
        setPaperSize(paperSize);
    }

    public String getPaperSize() {
        return paperSize;
    }

    public void setPaperSize(String paperSize) throws InvalidInputException {
        if (paperSize == null || paperSize.trim().isEmpty()) {
            throw new InvalidInputException("Paper size is required.");
        }
        this.paperSize = paperSize.trim();
    }

    @Override
    public String getCategory() {
        return "Paper";
    }

    @Override
    public String getDetails() {
        return "Size: " + paperSize;
    }
}
