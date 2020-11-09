package banking.dao;

import banking.CreditCard;

import java.util.Optional;

public interface CreditCardDao {

    CreditCard createCreditCard();

    Optional<String> getPinByCard(String cardNumber);

    Optional<Integer> getBalanceByCard(String cardNumber);

    void addIncomeByCard(String cardNumber, int amount);

    void remove(String cardNumber);

    boolean checkLuhnValidation(String cardTo);

    boolean getCardNumber(String cardNumber);

    void makeTransaction(String cardFrom, String cardTo, int amountOfMoney);

    String getLastCardFromDb();


}
