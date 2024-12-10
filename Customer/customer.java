package Customer;

import javax.swing.*;
import java.io.File;

public class customer extends JFrame {
    public customer(String folderPath) {
        setTitle("Customer Interface");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JLabel label = new JLabel("Select an Action:");
        JButton createOrderButton = new JButton("Create New Order");
        JButton modifyOrderButton = new JButton("Modify Existing Order");
        JButton trackOrderButton = new JButton("Track Order Status");

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(label);
        panel.add(Box.createVerticalStrut(20));
        panel.add(createOrderButton);
        panel.add(Box.createVerticalStrut(10));
        panel.add(modifyOrderButton);
        panel.add(Box.createVerticalStrut(10));
        panel.add(trackOrderButton);

        createOrderButton.addActionListener(e -> new CustomerInterface(folderPath, "create"));
        modifyOrderButton.addActionListener(e -> new CustomerInterface(folderPath, "modify"));
        trackOrderButton.addActionListener(e -> new CustomerInterface(folderPath, "track"));

        add(panel);
        setVisible(true);
    }
}
