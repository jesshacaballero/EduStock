import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

// ======================= GUI (Java Swing) =======================
class MainFrame extends JFrame {

    private final InventoryManager manager = new InventoryManager();
    private List<Item> displayed = new ArrayList<>(); // items currently shown in the table

    private JTextField txtId, txtName, txtPrice, txtQty, txtExtra, txtSearch;
    private JComboBox<String> cmbCategory;
    private JLabel lblExtra, lblStatus;
    private DefaultTableModel model;
    private JTable table;

    private static final Font FONT = new Font("SansSerif", Font.PLAIN, 14);
    private static final Font FONT_BOLD = new Font("SansSerif", Font.BOLD, 14);
    private static final Color PRIMARY = new Color(41, 98, 160);

    public MainFrame() {
        setTitle("EduStock: School Supplies Inventory Management System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1150, 650);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(8, 8));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildFormPanel(), BorderLayout.WEST);
        add(buildTablePanel(), BorderLayout.CENTER);
        add(buildStatusBar(), BorderLayout.SOUTH);

        loadSampleData();
        refreshTable(manager.getAllItems());
    }

    // ---------- UI builders ----------
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PRIMARY);
        header.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        JLabel title = new JLabel("EduStock");
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        title.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel("School Supplies Inventory Management System");
        subtitle.setFont(FONT);
        subtitle.setForeground(new Color(220, 232, 245));

        header.add(title, BorderLayout.NORTH);
        header.add(subtitle, BorderLayout.SOUTH);
        return header;
    }

    private JPanel buildFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(8, 12, 8, 4),
                BorderFactory.createTitledBorder("Item Details")));
        panel.setPreferredSize(new Dimension(320, 0));

        txtId = new JTextField();
        txtName = new JTextField();
        txtPrice = new JTextField();
        txtQty = new JTextField();
        txtExtra = new JTextField();
        cmbCategory = new JComboBox<>(new String[] { "Notebook", "Pen", "Paper", "Bag" });
        lblExtra = new JLabel("Pages:");

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 8, 5, 8);
        g.fill = GridBagConstraints.HORIZONTAL;

        addRow(panel, g, 0, "Item ID:", txtId);
        addRow(panel, g, 1, "Name:", txtName);
        addRow(panel, g, 2, "Category:", cmbCategory);
        addRow(panel, g, 3, "Price:", txtPrice);
        addRow(panel, g, 4, "Quantity:", txtQty);

        lblExtra.setFont(FONT_BOLD);
        txtExtra.setFont(FONT);
        g.gridx = 0;
        g.gridy = 5;
        g.weightx = 0;
        panel.add(lblExtra, g);
        g.gridx = 1;
        g.weightx = 1;
        panel.add(txtExtra, g);

        cmbCategory.addActionListener(e -> updateExtraLabel());

        // Buttons (2 per row)
        JPanel buttons = new JPanel(new GridLayout(4, 2, 8, 8));
        buttons.add(makeButton("Add", new Color(46, 139, 87), e -> onAdd()));
        buttons.add(makeButton("Update", new Color(41, 98, 160), e -> onUpdate()));
        buttons.add(makeButton("Delete", new Color(192, 57, 43), e -> onDelete()));
        buttons.add(makeButton("Clear", new Color(110, 110, 110), e -> clearForm()));
        buttons.add(makeButton("Stock In", new Color(39, 174, 96), e -> onStock(true)));
        buttons.add(makeButton("Stock Out", new Color(230, 126, 34), e -> onStock(false)));
        buttons.add(new JLabel()); // spacer
        buttons.add(new JLabel());

        g.gridx = 0;
        g.gridy = 6;
        g.gridwidth = 2;
        g.insets = new Insets(18, 8, 5, 8);
        panel.add(buttons, g);

        // push everything to the top
        g.gridy = 7;
        g.weighty = 1;
        panel.add(new JLabel(), g);

        return panel;
    }

    private void addRow(JPanel panel, GridBagConstraints g, int row, String label, JComponent field) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(FONT_BOLD);
        field.setFont(FONT);
        g.gridx = 0;
        g.gridy = row;
        g.weightx = 0;
        panel.add(lbl, g);
        g.gridx = 1;
        g.weightx = 1;
        panel.add(field, g);
    }

    private JButton makeButton(String text, Color color, java.awt.event.ActionListener action) {
        JButton b = new JButton(text);
        b.setFont(FONT_BOLD);
        b.setBackground(color);
        b.setForeground(Color.WHITE);
        b.setOpaque(true);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.addActionListener(action);
        return b;
    }

    private JPanel buildTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(6, 6));
        panel.setBorder(BorderFactory.createEmptyBorder(8, 4, 8, 12));

        // Search bar
        JPanel searchBar = new JPanel(new BorderLayout(6, 0));
        txtSearch = new JTextField();
        txtSearch.setFont(FONT);
        JPanel searchButtons = new JPanel(new GridLayout(1, 2, 6, 0));
        searchButtons.add(makeButton("Search", PRIMARY, e -> onSearch()));
        searchButtons.add(makeButton("Show All", new Color(110, 110, 110), e -> {
            txtSearch.setText("");
            refreshTable(manager.getAllItems());
        }));
        JLabel lblSearch = new JLabel("Search (ID / name / category):");
        lblSearch.setFont(FONT_BOLD);
        searchBar.add(lblSearch, BorderLayout.WEST);
        searchBar.add(txtSearch, BorderLayout.CENTER);
        searchBar.add(searchButtons, BorderLayout.EAST);
        txtSearch.addActionListener(e -> onSearch());

        // Table
        String[] cols = { "ID", "Name", "Category", "Price", "Qty", "Details", "Total Value" };
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        table = new JTable(model);
        table.setFont(FONT);
        table.setRowHeight(28);
        table.getTableHeader().setFont(FONT_BOLD);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Color-coded rows: low stock = red, otherwise by category
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                    boolean selected, boolean focus, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, value, selected, focus, row, col);
                if (!selected && row < displayed.size()) {
                    Item item = displayed.get(row);
                    c.setBackground(item.isLowStock()
                            ? new Color(255, 205, 205)
                            : categoryColor(item.getCategory()));
                    c.setForeground(Color.BLACK);
                }
                return c;
            }
        });

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                fillFormFromSelection();
            }
        });

        JLabel legend = new JLabel("Red row = low stock (5 or less)");
        legend.setFont(FONT);
        legend.setForeground(new Color(180, 40, 40));

        panel.add(searchBar, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(legend, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildStatusBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBorder(BorderFactory.createEmptyBorder(6, 16, 8, 16));
        lblStatus = new JLabel();
        lblStatus.setFont(FONT_BOLD);
        bar.add(lblStatus, BorderLayout.WEST);
        return bar;
    }

    private Color categoryColor(String category) {
        switch (category) {
            case "Notebook":
                return new Color(224, 238, 255);
            case "Pen":
                return new Color(221, 245, 226);
            case "Paper":
                return new Color(255, 247, 214);
            case "Bag":
                return new Color(238, 226, 250);
            default:
                return Color.WHITE;
        }
    }

    // ---------- helpers ----------
    private void updateExtraLabel() {
        String cat = (String) cmbCategory.getSelectedItem();
        switch (cat) {
            case "Notebook":
                lblExtra.setText("Pages:");
                break;
            case "Pen":
                lblExtra.setText("Ink Color:");
                break;
            case "Paper":
                lblExtra.setText("Paper Size:");
                break;
            case "Bag":
                lblExtra.setText("Bag Type:");
                break;
            default:
                lblExtra.setText("Extra:");
        }
    }

    private void refreshTable(List<Item> list) {
        displayed = list;
        model.setRowCount(0);
        for (Item item : list) {
            model.addRow(new Object[] {
                    item.getId(),
                    item.getName(),
                    item.getCategory(), // polymorphic call
                    String.format("%.2f", item.getPrice()),
                    item.getQuantity(),
                    item.getDetails(), // polymorphic call
                    String.format("%.2f", item.calculateValue())
            });
        }
        lblStatus.setText("Items shown: " + list.size()
                + "   |   Low-stock items: " + manager.getLowStockCount()
                + "   |   Total inventory value: " + String.format("%.2f", manager.getTotalValue()));
    }

    private void clearForm() {
        txtId.setText("");
        txtName.setText("");
        txtPrice.setText("");
        txtQty.setText("");
        txtExtra.setText("");
        cmbCategory.setSelectedIndex(0);
        table.clearSelection();
        txtId.requestFocus();
    }

    private void fillFormFromSelection() {
        int row = table.getSelectedRow();
        if (row < 0 || row >= displayed.size()) {
            return;
        }
        Item item = displayed.get(row);
        txtId.setText(item.getId());
        txtName.setText(item.getName());
        cmbCategory.setSelectedItem(item.getCategory());
        txtPrice.setText(String.valueOf(item.getPrice()));
        txtQty.setText(String.valueOf(item.getQuantity()));
        // Pull the extra value out of the child object
        if (item instanceof Notebook) {
            txtExtra.setText(String.valueOf(((Notebook) item).getPages()));
        } else if (item instanceof Pen) {
            txtExtra.setText(((Pen) item).getInkColor());
        } else if (item instanceof Paper) {
            txtExtra.setText(((Paper) item).getPaperSize());
        } else if (item instanceof Bag) {
            txtExtra.setText(((Bag) item).getBagType());
        }
    }

    // Reads + validates the form, then builds the right Item object
    private Item readItemFromForm() throws InvalidInputException {
        String id = txtId.getText().trim();
        String name = txtName.getText().trim();
        String priceText = txtPrice.getText().trim();
        String qtyText = txtQty.getText().trim();
        String extra = txtExtra.getText().trim();

        if (id.isEmpty() || name.isEmpty() || priceText.isEmpty() || qtyText.isEmpty() || extra.isEmpty()) {
            throw new InvalidInputException("Please fill in all fields.");
        }

        double price;
        int qty;
        try {
            price = Double.parseDouble(priceText);
        } catch (NumberFormatException e) {
            throw new InvalidInputException("Price must be a valid number.");
        }
        try {
            qty = Integer.parseInt(qtyText);
        } catch (NumberFormatException e) {
            throw new InvalidInputException("Quantity must be a whole number.");
        }

        String category = (String) cmbCategory.getSelectedItem();
        return InventoryManager.createItem(category, id, name, price, qty, extra);
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showInfo(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    // ---------- button actions ----------
    private void onAdd() {
        try {
            Item item = readItemFromForm();
            manager.addItem(item);
            txtSearch.setText("");
            refreshTable(manager.getAllItems());
            clearForm();
            showInfo("Item added successfully.");
        } catch (InvalidInputException ex) {
            showError(ex.getMessage());
        }
    }

    private void onUpdate() {
        try {
            Item item = readItemFromForm();
            manager.updateItem(item);
            txtSearch.setText("");
            refreshTable(manager.getAllItems());
            clearForm();
            showInfo("Item updated successfully.");
        } catch (InvalidInputException ex) {
            showError(ex.getMessage());
        }
    }

    private void onDelete() {
        String id = txtId.getText().trim();
        if (id.isEmpty()) {
            showError("Select an item from the table (or type its ID) to delete.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete item " + id.toUpperCase() + "?", "Confirm Delete",
                JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            manager.deleteItem(id);
            txtSearch.setText("");
            refreshTable(manager.getAllItems());
            clearForm();
            showInfo("Item deleted.");
        } catch (InvalidInputException ex) {
            showError(ex.getMessage());
        }
    }

    private void onSearch() {
        String keyword = txtSearch.getText().trim();
        if (keyword.isEmpty()) {
            refreshTable(manager.getAllItems());
            return;
        }
        List<Item> results = manager.search(keyword);
        refreshTable(results);
        if (results.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No items matched \"" + keyword + "\".",
                    "Search", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void onStock(boolean stockIn) {
        int row = table.getSelectedRow();
        if (row < 0 || row >= displayed.size()) {
            showError("Please select an item in the table first.");
            return;
        }
        Item item = displayed.get(row);
        String input = JOptionPane.showInputDialog(this,
                (stockIn ? "Stock IN" : "Stock OUT") + " amount for " + item.getName() + ":");
        if (input == null) {
            return; // user cancelled
        }
        try {
            int amount = Integer.parseInt(input.trim());
            if (stockIn) {
                item.addStock(amount);
            } else {
                item.removeStock(amount);
            }
            String keyword = txtSearch.getText().trim();
            refreshTable(keyword.isEmpty() ? manager.getAllItems() : manager.search(keyword));
            clearForm();
            if (item.isLowStock()) {
                JOptionPane.showMessageDialog(this,
                        item.getName() + " is low on stock (" + item.getQuantity() + " left).",
                        "Low Stock Warning", JOptionPane.WARNING_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            showError("Amount must be a whole number.");
        } catch (InvalidInputException ex) {
            showError(ex.getMessage());
        }
    }

    // ---------- starter data so the table isn't empty ----------
    private void loadSampleData() {
        try {
            manager.addItem(new Notebook("NB001", "Spiral Notebook", 45.00, 30, 80));
            manager.addItem(new Notebook("NB002", "Composition Notebook", 35.50, 4, 100));
            manager.addItem(new Pen("PN001", "Ballpoint Pen", 8.00, 120, "Blue"));
            manager.addItem(new Pen("PN002", "Gel Pen", 15.00, 60, "Black"));
            manager.addItem(new Paper("PP001", "Bond Paper", 280.00, 12, "A4"));
            manager.addItem(new Bag("BG001", "School Backpack", 650.00, 3, "Backpack"));
        } catch (InvalidInputException e) {
            System.out.println("Sample data error: " + e.getMessage());
        }
    }
}
