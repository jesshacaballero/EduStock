// ======================= INHERITANCE: child classes =======================
class Notebook extends Item {
    private int pages;

    public Notebook(String id, String name, double price, int quantity, int pages)
            throws InvalidInputException {
        super(id, name, price, quantity);
        setPages(pages);
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) throws InvalidInputException {
        if (pages <= 0) {
            throw new InvalidInputException("Number of pages must be greater than 0.");
        }
        this.pages = pages;
    }

    @Override
    public String getCategory() {
        return "Notebook";
    }

    @Override
    public String getDetails() {
        return pages + " pages";
    }
}
