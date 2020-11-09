package banking;

import banking.console.ConsoleHelper;
import banking.dao.CreditCardDao;

import java.sql.Connection;
import java.util.Optional;
import java.util.regex.Pattern;

public class ChatBotImpl implements ChatBot {
    private CreditCardDao cardDao;

    public ChatBotImpl(CreditCardDao cardDao) {
        this.cardDao = cardDao;
    }

    public void startMainMenu() {
        while (true) {
            ConsoleHelper.write("1. Create an account\n" +
                    "2. Log into account\n" +
                    "0. Exit");
            String input = ConsoleHelper.read();
            switch (input) {
                case "0":
                    ConsoleHelper.write("");
                    ConsoleHelper.write("Bye!");
                    return;
                case "1":
                    createCardNumber();
                    break;
                case "2":
                    int code = logIn();
                    if (code == 0) {
                        ConsoleHelper.write("");
                        ConsoleHelper.write("Bye!");
                        return;
                    }
                    break;
                default:
                    ConsoleHelper.write("");
                    ConsoleHelper.write("Invalid input! Please, try again.");
                    break;
            }
        }
    }

    private void createCardNumber() {
        ConsoleHelper.write("");
        CreditCard creditCard = cardDao.createCreditCard();

        ConsoleHelper.write("Your card has been created");
        ConsoleHelper.write("Your card number:");
        ConsoleHelper.write(creditCard.getCreditCardNumber());
        ConsoleHelper.write("Your card PIN:");
        ConsoleHelper.write(creditCard.getCreditCardPin());
        ConsoleHelper.write("");
    }

    private int logIn() {
        ConsoleHelper.write("");
        ConsoleHelper.write("Enter your card number:");

        String cardNumber = ConsoleHelper.read();

        ConsoleHelper.write("Enter your PIN:");
        String pin = ConsoleHelper.read();

        Optional<String> pinOptional = cardDao.getPinByCard(cardNumber);

        if (pinOptional.isPresent()) {
            String pinFromRepo = pinOptional.get();

            if (pinFromRepo.equals(pin)) {

                ConsoleHelper.write("");
                ConsoleHelper.write("You have successfully logged in!\n");
                int exitCode = loggedIn(cardNumber);
                if (exitCode == 0) {
                    return 0;
                }
            } else {
                ConsoleHelper.write("");
                ConsoleHelper.write("Wrong card number or PIN!");
            }
        } else {
            ConsoleHelper.write("");
            ConsoleHelper.write("Wrong card number or PIN!");
            ConsoleHelper.write("");
        }


        return 1;
    }

    private int loggedIn(String cardNumber) {
        while (true) {
            ConsoleHelper.write("1. Balance\n" +
                    "2. Add income\n" +
                    "3. Do transfer\n" +
                    "4. Close account\n" +
                    "5. Log out\n" +
                    "0. Exit");
            String input = ConsoleHelper.read();
            switch (input) {
                case "1":
                    balance(cardNumber);
                    break;
                case "2":
                    addIncome(cardNumber);
                    break;
                case "3":
                    transferMoney(cardNumber);
                    break;
                case "4":
                    closeAccount(cardNumber);
                    return 4;
                case "5":
                    ConsoleHelper.write("");
                    ConsoleHelper.write("You have successfully logged out!");
                    return 2;
                case "0":
                    return 0;
                default:
                    ConsoleHelper.write("");
                    ConsoleHelper.write("Invalid input! Please, try again.");
                    break;
            }
        }
    }

    private void transferMoney(String cardFrom) {
        ConsoleHelper.write("Transfer");
        ConsoleHelper.write("Enter card number:");
        String cardTo = ConsoleHelper.read();

        if (isNumeric(cardTo)) {

            boolean isValid = cardDao.checkLuhnValidation(cardTo);
            if (cardTo.equals(cardFrom)) {
                ConsoleHelper.write("You can't transfer money to the same account!");
                ConsoleHelper.write("");
            } else {
                if (isValid) {
                    boolean isPresentCard = cardDao.getCardNumber(cardTo);

                    if (isPresentCard) {
                        ConsoleHelper.write("Enter how much money you want to transfer:");
                        int amountOfMoney = Integer.parseInt(ConsoleHelper.read());

                        if (amountOfMoney > cardDao.getBalanceByCard(cardFrom).get()) {
                            ConsoleHelper.write("Not enough money!");
                            ConsoleHelper.write("");
                        } else {
                            cardDao.makeTransaction(cardFrom, cardTo, amountOfMoney);
                            ConsoleHelper.write("Success!");
                            ConsoleHelper.write("");
                        }

                    } else {
                        ConsoleHelper.write("Such a card does not exist.");
                        ConsoleHelper.write("");
                    }

                } else {
                    ConsoleHelper.write("Probably you made mistake in the card number. Please try again!");
                    ConsoleHelper.write("");
                }
            }

        }
    }

    private void closeAccount(String cardNumber) {

        cardDao.remove(cardNumber);

        ConsoleHelper.write("");
        ConsoleHelper.write("The account has been closed!");
        ConsoleHelper.write("");
    }

    private void addIncome(String cardNumber) {

        ConsoleHelper.write("Enter income: ");
        String amountString = ConsoleHelper.read();

        if (isNumeric(amountString)) {
            int amount = Integer.parseInt(amountString);

            cardDao.addIncomeByCard(cardNumber, amount);
            ConsoleHelper.write("Income was added!");
            ConsoleHelper.write("");
        }
    }

    private void balance(String cardNumber) {
        Optional<Integer> balanceOptional = cardDao.getBalanceByCard(cardNumber);

        if (balanceOptional.isPresent()) {
            ConsoleHelper.write("");
            ConsoleHelper.write("Balance: " + balanceOptional.get());
            ConsoleHelper.write("");
        }
    }

    public static boolean isNumeric(String strNum) {
        Pattern pattern = Pattern.compile("-?\\d+");

        if (strNum == null) {
            return false;
        }
        return pattern.matcher(strNum).matches();
    }
}
