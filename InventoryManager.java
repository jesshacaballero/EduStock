import java.util.ArrayList;
import java.util.List;

// ======================= BUSINESS LOGIC (no GUI code here) =======================
class InventoryManager {
    private final List<Item> items = new ArrayList<>();

    // Factory: creates the correct child object based on category (polymorphism in action)
    public static Item createItem(String category, String id, String name, double price,
            int qty, String extra) throws InvalidInputException {
        switch (category) {
            case "Notebook":
                int pages;
                try {
                    pages = Integer.parseInt(extra.trim());
                } catch (NumberFormatException e) {
                    throw new InvalidInputException("Pages must be a whole number.");
                }
                return new Notebook(id, name, price, qty, pages);
            case "Pen":
                return new Pen(id, name, price, qty, extra);
            case "Paper":
                return new Paper(id, name, price, qty, extra);
            case "Bag":
                return new Bag(id, name, price, qty, extra);
            default:
                throw new InvalidInputException("Please choose a valid category.");
        }
    }

    // ADD
    public void addItem(Item item) throws InvalidInputException {
        if (findById(item.getId()) != null) {
            throw new InvalidInputException("Item ID '" + item.getId() + "' already exists.");
        }
        items.add(item);
    }

    // VIEW
    public List<Item> getAllItems() {
        return new ArrayList<>(items);
    }

    // SEARCH (by ID, name, or category)
    public List<Item> search(String keyword) {
        List<Item> results = new ArrayList<>();
        String key = keyword.trim().toLowerCase();
        for (Item item : items) {
            if (item.getId().toLowerCase().contains(key)
                    || item.getName().toLowerCase().contains(key)
                    || item.getCategory().toLowerCase().contains(key)) {
                results.add(item);
            }
        }
        return results;
    }

    public Item findById(String id) {
        for (Item item : items) {
            if (item.getId().equalsIgnoreCase(id.trim())) {
                return item;
            }
        }
        return null;
    }

    // UPDATE (replace the old object that has the same ID)
    public void updateItem(Item updated) throws InvalidInputException {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getId().equals(updated.getId())) {
                items.set(i, updated);
                return;
            }
        }
        throw new InvalidInputException("Item ID '" + updated.getId() + "' was not found.");
    }

    // DELETE
    public void deleteItem(String id) throws InvalidInputException {
        Item found = findById(id);
        if (found == null) {
            throw new InvalidInputException("Item not found.");
        }
        items.remove(found);
    }

    // CALCULATE
    public double getTotalValue() {
        double total = 0;
        for (Item item : items) {
            total += item.calculateValue(); // polymorphic call on Item references
        }
        return total;
    }

    public int getLowStockCount() {
        int count = 0;
        for (Item item : items) {
            if (item.isLowStock()) {
                count++;
            }
        }
        return count;
    }
}
