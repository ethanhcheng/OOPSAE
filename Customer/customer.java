package Customer;

import javax.swing.*;

public class customer extends JFrame {
    public customer() {
        setTitle("Customer Interface");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JLabel label = new JLabel("Customer Interface (Not Implemented)");
        add(label);

        setVisible(true);
    }
}

