// ======================= ABSTRACTION (abstract class) =======================
// Parent class. Cannot be instantiated: you can't have a plain "Item",
// only a specific kind (Notebook, Pen, ...).
abstract class Item implements Stockable {

    // ---------- ENCAPSULATION: all fields are private ----------
    private String id;
    private String name;
    private double price;
    private int quantity;

    private static final int LOW_STOCK_LIMIT = 5;

    public Item(String id, String name, double price, int quantity) throws InvalidInputException {
        // Constructor uses the setters so validation is always applied
        setId(id);
        setName(name);
        setPrice(price);
        setQuantity(quantity);
    }

    // ---------- ENCAPSULATION: getters ----------
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    // ---------- ENCAPSULATION: setters with validation ----------
    public void setId(String id) throws InvalidInputException {
        if (id == null || id.trim().isEmpty()) {
            throw new InvalidInputException("Item ID is required.");
        }
        this.id = id.trim().toUpperCase();
    }

    public void setName(String name) throws InvalidInputException {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidInputException("Item name is required.");
        }
        this.name = name.trim();
    }

    public void setPrice(double price) throws InvalidInputException {
        if (price < 0) {
            throw new InvalidInputException("Price cannot be negative.");
        }
        this.price = price;
    }

    public void setQuantity(int quantity) throws InvalidInputException {
        if (quantity < 0) {
            throw new InvalidInputException("Quantity cannot be negative.");
        }
        this.quantity = quantity;
    }

    // ---------- Implementing the Stockable interface ----------
    @Override
    public void addStock(int amount) throws InvalidInputException {
        if (amount <= 0) {
            throw new InvalidInputException("Stock-in amount must be greater than 0.");
        }
        this.quantity += amount;
    }

    @Override
    public void removeStock(int amount) throws InvalidInputException {
        if (amount <= 0) {
            throw new InvalidInputException("Stock-out amount must be greater than 0.");
        }
        if (amount > quantity) {
            throw new InvalidInputException("Not enough stock. Only " + quantity + " left.");
        }
        this.quantity -= amount;
    }

    @Override
    public boolean isLowStock() {
        return quantity <= LOW_STOCK_LIMIT;
    }

    // Shared behavior (inherited by all children)
    public double calculateValue() {
        return price * quantity;
    }

    // ---------- POLYMORPHISM: each child gives its OWN version ----------
    public abstract String getCategory();

    public abstract String getDetails();
}
