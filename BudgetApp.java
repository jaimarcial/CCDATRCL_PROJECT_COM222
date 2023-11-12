import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;

class ExpenseNode {
    String itemName;
    double itemPrice;
    double itemDate;
    ExpenseNode next;

    public ExpenseNode(String itemName, double itemPrice, double itemDate) {
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.itemDate = itemDate;
        this.next = null;
    }
}

public class BudgetApp implements ActionListener {
    private JButton addButton, removeButton, removeAllButton, printButton;
    private JTextField itemNameField, itemPriceField, itemDateField;
    private JLabel budgetLabel;
    private JFrame frame;
    private JTextArea budgetTextArea;
    private ExpenseNode head;

    public BudgetApp() {
        head = null;
    }

    public void createAndShowGUI() {
        frame = new JFrame("Expense Tracker");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        addButton = new JButton("Add");
        removeButton = new JButton("Remove");
        removeAllButton = new JButton("Remove All");
        printButton = new JButton("Print");

        itemNameField = new JTextField(20);
        itemPriceField = new JTextField(20);
        itemDateField = new JTextField(18);

        budgetLabel = new JLabel("Total Price: P0.00");

        addButton.addActionListener(this);
        removeButton.addActionListener(this);
        removeAllButton.addActionListener(this);
        printButton.addActionListener(this);

        budgetTextArea = new JTextArea(15, 27);
        budgetTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(budgetTextArea);

        JPanel panel = new JPanel();
        panel.add(new JLabel("Item Name:"));
        panel.add(itemNameField);
        panel.add(new JLabel("Item Price:"));
        panel.add(itemPriceField);
        panel.add(new JLabel("Date: "));
        panel.add(itemDateField);
        panel.add(addButton);
        panel.add(removeButton);
        panel.add(removeAllButton);
        panel.add(printButton);
        panel.add(budgetLabel);
        panel.add(scrollPane);
        panel.add(printButton);
        panel.setBackground(Color.PINK);

        frame.getContentPane().add(panel);
        frame.pack();
        frame.setSize(300, 500);
        frame.setVisible(true);
    }

    public String getBudgetTextAreaContent() {
        return budgetTextArea.getText();
    }

    public void StoredData(String expensesdata) {
        try (BufferedWriter write = new BufferedWriter(new FileWriter(expensesdata, true))) {
            double totalPrices = 0;
            ExpenseNode current = head;

            while (current != null) {
                write.write("Date: " + current.itemDate + "\n");
                write.write(current.itemName + ": P" + current.itemPrice + "\n");
                totalPrices += current.itemPrice;
                current = current.next;
            }

            DecimalFormat df = new DecimalFormat("0.00");
            write.write("Total Price: P" + df.format(totalPrices) + "\n\n");
            JOptionPane.showMessageDialog(frame, "Your Data is stored at " + expensesdata);
            budgetTextArea.setText("");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void RemoveStoredData(String expensesdata) {
        try (BufferedWriter write = new BufferedWriter(new FileWriter(expensesdata, false))) {
            write.close();
            JOptionPane.showMessageDialog(frame, "All your data has been deleted");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addButton) {
            try {
                String name = itemNameField.getText();
                double price = Double.parseDouble(itemPriceField.getText());
                double date = Double.parseDouble(itemDateField.getText());

                ExpenseNode newNode = new ExpenseNode(name, price, date);
                newNode.next = head;
                head = newNode;

                updateBudgetPrice();
                updateBudgetTextArea();

                itemDateField.setText("");
                itemNameField.setText("");
                itemPriceField.setText("");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Invalid price input.");
            }
        } else if (e.getSource() == removeButton) {
            removeExpenseGUI();
        } else if (e.getSource() == printButton) {
            printExpenseDetails();
            StoredData("expensesdata.txt");
            clearExpenseData();
        } else if (e.getSource() == removeAllButton) {
            RemoveStoredData("expensesdata.txt");
            clearExpenseData();
        }
    }

    private void updateBudgetPrice() {
        double totalPrice = 0;
        ExpenseNode current = head;

        while (current != null) {
            totalPrice += current.itemPrice;
            current = current.next;
        }

        DecimalFormat df = new DecimalFormat("0.00");
        budgetLabel.setText("Total Price: P" + df.format(totalPrice));
    }

    private void updateBudgetTextArea() {
        StringBuilder budgetItemsText = new StringBuilder("Expenses:\n");
        ExpenseNode current = head;

        while (current != null) {
            budgetItemsText.append("Date: ").append(current.itemDate).append("\n");
            budgetItemsText.append(current.itemName).append(": P").append(current.itemPrice).append("\n");
            current = current.next;
        }

        budgetTextArea.setText(budgetItemsText.toString());
        itemDateField.setText("");
        itemNameField.setText("");
        itemPriceField.setText("");
    }

    private void printExpenseDetails() {
        StringBuilder message = new StringBuilder();
        double totalPrices = 0;
        ExpenseNode current = head;

        while (current != null) {
            message.append("Date: ").append(current.itemDate).append("\n");
            message.append(current.itemName).append(": P").append(current.itemPrice).append("\n");
            totalPrices += current.itemPrice;
            current = current.next;
        }

        DecimalFormat df = new DecimalFormat("0.00");
        message.append("Total Price: P").append(df.format(totalPrices));

        JOptionPane.showMessageDialog(frame, message.toString(), "Expense Details", JOptionPane.INFORMATION_MESSAGE);
    }

    private void clearExpenseData() {
        head = null;
        updateBudgetPrice();
        updateBudgetTextArea();
    }

    // Prompt the user to select and remove an expense
    private void removeExpenseGUI() {
        if (head == null) {
            JOptionPane.showMessageDialog(frame, "List is empty!");
            return;
        }

        DefaultListModel<String> listModel = new DefaultListModel<>();
        ExpenseNode current = head;

        while (current != null) {
            listModel.addElement("Date: " + current.itemDate + " - " + current.itemName + ": P" + current.itemPrice);
            current = current.next;
        }

        JList<String> list = new JList<>(listModel);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        int choice = JOptionPane.showOptionDialog(frame, list, "Select an expense to remove",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null);

        if (choice == JOptionPane.OK_OPTION) {
    int selectedIndex = list.getSelectedIndex();

    if (selectedIndex != -1) {
        // Remove the selected expense
        removeExpense(selectedIndex);
    }
        }
        }
// ...

private void removeExpense(int index) {
    if (index == 0) {
        // If the user wants to remove the first expense (head), update the head
        head = head.next;
    } else {
        // Find the node before the one to be removed
        ExpenseNode previous = head;
        for (int i = 0; i < index - 1; i++) {
            previous = previous.next;
        }

        // Remove the expense by updating the 'next' reference of the previous node
        if (previous.next != null) {
            previous.next = previous.next.next;
        }
    }

    // Update the display and total price
    updateBudgetPrice();
    updateBudgetTextArea();
}
}
    




