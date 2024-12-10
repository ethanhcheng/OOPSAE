package Worker;

import javax.swing.*;

public class StatusGUI extends JFrame {

    public StatusGUI() {
        setTitle("Status for Each Item");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Dummy implementation for now
        JLabel label = new JLabel("Status GUI: To be implemented");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        add(label);
        setVisible(true);
    }
}
