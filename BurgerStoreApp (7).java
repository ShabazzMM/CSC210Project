//Haider Shafiq, Shabaz Middleton, Micheal Leister
//CSC 210 Group Project
//12/6/2023
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class BurgerStoreApp extends Application {
    private BurgerStore burgerStore;
    private Label totalCostLabel;

    @Override
    public void start(Stage primaryStage) {
        burgerStore = new BurgerStore();

        primaryStage.setTitle("BurgerStore Login");

        GridPane grid = createLoginForm();

        Scene scene = new Scene(grid, 400, 300);
        primaryStage.setScene(scene);

        primaryStage.show();
    }

    private GridPane createLoginForm() {
        GridPane grid = new GridPane();
        grid.setAlignment(javafx.geometry.Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Label usernameLabel = new Label("Username:");
        grid.add(usernameLabel, 0, 1);

        TextField usernameField = new TextField();
        grid.add(usernameField, 1, 1);

        Label passwordLabel = new Label("Password:");
        grid.add(passwordLabel, 0, 2);

        PasswordField passwordField = new PasswordField();
        grid.add(passwordField, 1, 2);

        Button loginButton = new Button("Login");
        grid.add(loginButton, 1, 3);

        Label messageLabel = new Label();
        grid.add(messageLabel, 1, 4);

        loginButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            if (burgerStore.validateUser(username, password)) {
                messageLabel.setText("Login successful!");
                showMenuPage(); // Navigate to the menu page
            } else {
                messageLabel.setText("Invalid username or password. Please try again.");
                usernameField.clear();
                passwordField.clear();
            }
        });

        return grid;
    }

    private void showMenuPage() {
        Stage stage = new Stage();
        stage.setTitle("BurgerStore Menu");

        GridPane grid = createMenuGrid(stage);

        Scene scene = new Scene(grid, 400, 300);
        stage.setScene(scene);

        stage.show();
    }

    private GridPane createMenuGrid(Stage stage) {
        GridPane grid = new GridPane();
        grid.setAlignment(javafx.geometry.Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Label menuLabel = new Label("Burger Menu:");
        grid.add(menuLabel, 0, 0, 2, 1);

        // Add burger items
        String[] burgerItems = burgerStore.getItems();
        for (int i = 0; i < burgerItems.length; i++) {
            Label burgerLabel = new Label(burgerItems[i]);
            grid.add(burgerLabel, 0, i + 1);

            // Add quantity TextField for each item
            TextField quantityField = new TextField();
            quantityField.setPromptText("Quantity");
            grid.add(quantityField, 1, i + 1);
        }

        // Add Checkout button
        Button checkoutButton = new Button("Checkout");
        grid.add(checkoutButton, 1, burgerItems.length + 1);

        // Add Label for displaying total cost
        totalCostLabel = new Label("Total Cost: $0.00");
        grid.add(totalCostLabel, 0, burgerItems.length + 2, 2, 1);

        // Add Detailed View button
        Button detailedViewButton = new Button("Detailed View");
        grid.add(detailedViewButton, 1, burgerItems.length + 3);

        checkoutButton.setOnAction(e -> {
            // Handle checkout process
            handleCheckout(grid);
        });

        detailedViewButton.setOnAction(e -> {
            // Show detailed view
            showDetailedView(grid);
        });

        return grid;
    }

 private void handleCheckout(GridPane grid) {
    double totalCost = 0.0;
    double totalTax = 0.0;

    String[] burgerItems = burgerStore.getItems();
    for (int i = 0; i < burgerItems.length; i++) {
        Label burgerLabel = (Label) grid.getChildren().get(i * 2 + 1);
        TextField quantityField = (TextField) grid.getChildren().get((i + 1) * 2);

        String itemName = burgerLabel.getText();
        int quantity = 0;

        String inputText = quantityField.getText().trim();  // Trim to handle white spaces

        if (!inputText.isEmpty()) {
            try {
                quantity = Integer.parseInt(inputText);

                if (quantity < 0) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException ex) {
                // Handle the case where the quantity is not a valid positive integer
                showErrorAlert("Please input only positive numbers for quantity!");
                return;
            }
        }

        double cost = burgerStore.calculateCost(itemName, quantity);
        double taxes = burgerStore.calculateTaxes(cost);
        double totalItemCost = cost + taxes;

        totalCost += totalItemCost;
        totalTax += taxes;
    }

    // Display the total cost breakdown and tax on the Menu page
    totalCostLabel.setText(String.format("Total Cost: $%.2f", totalCost));
}

private void showErrorAlert(String errorMessage) {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle("Error");
    alert.setHeaderText(null);
    alert.setContentText(errorMessage);
    alert.showAndWait();
}

private void showDetailedView(GridPane grid) {
    double totalCost = 0.0;
    double totalTax = 0.0;

    StringBuilder detailedMessage = new StringBuilder("Detailed View:\n\n");

    // Add headings for each column
    detailedMessage.append(String.format("%-15s %-8s %-10s %-10s %-15s\n",
            "Items", "Quantity", "Cost", "Tax", "Total Amount"));

    String[] burgerItems = burgerStore.getItems();
    for (int i = 0; i < burgerItems.length; i++) {
        Label burgerLabel = (Label) grid.getChildren().get(i * 2 + 1);
        TextField quantityField = (TextField) grid.getChildren().get((i + 1) * 2);

        String itemName = burgerLabel.getText();
        int quantity = 0;

        try {
            quantity = Integer.parseInt(quantityField.getText());
        } catch (NumberFormatException ex) {
            // Handle the case where the quantity is not a valid integer
        }

        double cost = burgerStore.calculateCost(itemName, quantity);
        double taxes = burgerStore.calculateTaxes(cost);
        double totalItemCost = cost + taxes;

        totalCost += totalItemCost;
        totalTax += taxes;

        if (quantity > 0) {
            detailedMessage.append(String.format("%-15s %-8d $%-10.2f $%-10.2f $%-15.2f\n",
                    itemName, quantity, cost, taxes, totalItemCost));
        }
    }

    // Display the total cost breakdown and tax in the alert
    detailedMessage.append(String.format("\n%-15s %-8s %-10s\n", "", "", ""));
    detailedMessage.append(String.format("%-33s %-10.2f\n", "Tax:", totalTax));
    detailedMessage.append(String.format("%-33s %-10.2f\n", "Total Cost:", totalCost));

    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("Detailed View");
    alert.setHeaderText(null);
    alert.setContentText(detailedMessage.toString());
    alert.showAndWait();
}

    public static void main(String[] args) {
        launch(args);
    }
}
