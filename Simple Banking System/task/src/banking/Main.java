package banking;

import banking.dao.CreditCardDao;
import banking.dao.CreditCardDaoImpl;

public class Main {
    public static void main(String[] args) {
        CreditCardDao dao = CreditCardDaoImpl.getInstance();
        ChatBot chatBot = new ChatBotImpl(dao);
        chatBot.startMainMenu();
    }
}