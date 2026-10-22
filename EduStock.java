import javax.swing.*;

// ======================= MAIN CLASS =======================
public class EduStock {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {
            // fall back to default look and feel
        }
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}
