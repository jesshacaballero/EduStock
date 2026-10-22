// ======================= ABSTRACTION (interface) =======================
// Defines WHAT stock-related objects must do, not HOW.
interface Stockable {
    void addStock(int amount) throws InvalidInputException;

    void removeStock(int amount) throws InvalidInputException;

    boolean isLowStock();
}
