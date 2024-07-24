import java.util.*;

class Transaction {
    private String type;
    private double amount;
    private Date date;

    public Transaction(String type, double amount) {
        this.type = type;
        this.amount = amount;
        this.date = new Date();
    }

    public String getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public Date getDate() {
        return date;
    }

    @Override
    public String toString() {
        return String.format("%-15s | %-10.2f | %s", type, amount, date.toString());
    }
}

class Account {
    private double balance;
    private List<Transaction> transactionHistory;

    public Account(double initialBalance) {
        this.balance = initialBalance;
        this.transactionHistory = new ArrayList<>();
    }

    public double getBalance() {
        return balance;
    }

    public void deposit(double amount) {
        if (amount > 0) {
            balance += amount;
            transactionHistory.add(new Transaction("Deposit", amount));
            System.out.printf("Successfully deposited $%.2f. Current balance: $%.2f\n", amount, balance);
        } else {
            System.out.println("Invalid deposit amount.");
        }
    }

    public boolean withdraw(double amount) {
        if (amount > 0 && amount <= balance) {
            balance -= amount;
            transactionHistory.add(new Transaction("Withdrawal", amount));
            System.out.printf("Successfully withdrew $%.2f. Current balance: $%.2f\n", amount, balance);
            return true;
        } else {
            System.out.println("Invalid withdrawal amount or insufficient funds.");
            return false;
        }
    }

    public boolean transfer(Account targetAccount, double amount) {
        if (withdraw(amount)) {
            targetAccount.deposit(amount);
            transactionHistory.add(new Transaction("Transfer", amount));
            System.out.printf("Successfully transferred $%.2f to Account ID: %d\n", amount, targetAccount.hashCode());
            return true;
        }
        return false;
    }

    public void printTransactionHistory() {
        if (transactionHistory.isEmpty()) {
            System.out.println("No transactions found.");
        } else {
            System.out.println("Transaction History:");
            System.out.printf("%-15s | %-10s | %s\n", "Type", "Amount", "Date");
            System.out.println("--------------------------------------------------------");
            for (Transaction transaction : transactionHistory) {
                System.out.println(transaction);
            }
        }
    }
}

class User {
    private String userId;
    private String userPin;
    private Account account;

    public User(String userId, String userPin, double initialBalance) {
        this.userId = userId;
        this.userPin = userPin;
        this.account = new Account(initialBalance);
    }

    public String getUserId() {
        return userId;
    }

    public String getUserPin() {
        return userPin;
    }

    public Account getAccount() {
        return account;
    }
}

class Bank {
    private Map<String, User> users;

    public Bank() {
        users = new HashMap<>();
    }

    public void addUser(User user) {
        users.put(user.getUserId(), user);
    }

    public User authenticateUser(String userId, String userPin) {
        User user = users.get(userId);
        if (user != null && user.getUserPin().equals(userPin)) {
            return user;
        }
        return null;
    }

    public boolean userExists(String userId) {
        return users.containsKey(userId);
    }
}

public class ATM {
    private static Scanner scanner = new Scanner(System.in);
    private static Bank bank = new Bank();

    public static void main(String[] args) {
        boolean isRunning = true;
        System.out.println("Welcome to the ATM System");

        while (isRunning) {
            System.out.println("\n1. Register");
            System.out.println("2. Login");
            System.out.println("3. Quit");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    register();
                    break;
                case 2:
                    login();
                    break;
                case 3:
                    isRunning = false;
                    System.out.println("Thank you for using the ATM System. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private static void register() {
        System.out.println("\n--- Register ---");
        System.out.print("Enter User ID: ");
        String userId = scanner.nextLine();

        if (bank.userExists(userId)) {
            System.out.println("User ID already exists. Please choose a different ID.");
            return;
        }

        System.out.print("Enter User PIN: ");
        String userPin = scanner.nextLine();
        System.out.print("Enter Initial Deposit Amount: ");
        double initialDeposit = scanner.nextDouble();
        scanner.nextLine(); // Consume newline

        User newUser = new User(userId, userPin, initialDeposit);
        bank.addUser(newUser);

        System.out.println("Registration successful! You can now log in.");
    }

    private static void login() {
        System.out.println("\n--- Login ---");
        System.out.print("Enter User ID: ");
        String userId = scanner.nextLine();
        System.out.print("Enter User PIN: ");
        String userPin = scanner.nextLine();

        User currentUser = bank.authenticateUser(userId, userPin);

        if (currentUser != null) {
            System.out.println("Login successful!");
            userMenu(currentUser);
        } else {
            System.out.println("Invalid credentials. Please try again.");
        }
    }

    private static void userMenu(User currentUser) {
        boolean quit = false;
        while (!quit) {
            printUserMenu();
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    currentUser.getAccount().printTransactionHistory();
                    break;
                case 2:
                    processWithdrawal(currentUser);
                    break;
                case 3:
                    processDeposit(currentUser);
                    break;
                case 4:
                    processTransfer(currentUser);
                    break;
                case 5:
                    quit = true;
                    System.out.println("Logged out successfully.");
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private static void printUserMenu() {
        System.out.println("\nUser Menu:");
        System.out.println("1. Transaction History");
        System.out.println("2. Withdraw");
        System.out.println("3. Deposit");
        System.out.println("4. Transfer");
        System.out.println("5. Logout");
        System.out.print("Enter your choice: ");
    }

    private static void processWithdrawal(User user) {
        System.out.print("Enter withdrawal amount: ");
        double amount = scanner.nextDouble();
        scanner.nextLine(); // Consume newline
        boolean success = user.getAccount().withdraw(amount);
        if (success) {
            System.out.printf("Withdrawal of $%.2f was successful. Current balance: $%.2f\n", amount, user.getAccount().getBalance());
        } else {
            System.out.println("Withdrawal failed. Please check your balance and try again.");
        }
    }

    private static void processDeposit(User user) {
        System.out.print("Enter deposit amount: ");
        double amount = scanner.nextDouble();
        scanner.nextLine(); // Consume newline
        user.getAccount().deposit(amount);
        System.out.printf("Deposit of $%.2f was successful. Current balance: $%.2f\n", amount, user.getAccount().getBalance());
    }

    private static void processTransfer(User user) {
        System.out.print("Enter target User ID for transfer: ");
        String targetUserId = scanner.nextLine();

        // Check if the target user ID exists
        if (!bank.userExists(targetUserId)) {
            System.out.println("Target user does not exist. Please check the User ID and try again.");
            return;
        }

        // Check if the user is trying to transfer to themselves
        if (targetUserId.equals(user.getUserId())) {
            System.out.println("Invalid transfer. You cannot transfer money to your own account.");
            return;
        }

        User targetUser = bank.authenticateUser(targetUserId, bank.authenticateUser(targetUserId, user.getUserPin()).getUserPin());

        System.out.print("Enter transfer amount: ");
        double amount = scanner.nextDouble();
        scanner.nextLine(); // Consume newline

        boolean success = user.getAccount().transfer(targetUser.getAccount(), amount);
        if (success) {
            System.out.printf("Transfer of $%.2f to User ID %s was successful.\n", amount, targetUserId);
        } else {
            System.out.println("Transfer failed. Please check your balance and try again.");
        }
    }
}
