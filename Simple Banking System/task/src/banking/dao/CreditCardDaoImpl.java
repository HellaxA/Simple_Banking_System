package banking.dao;

import banking.CreditCard;
import banking.CreditCardImpl;
import banking.LuhnAlgoImpl;
import banking.console.ConsoleHelper;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CreditCardDaoImpl implements CreditCardDao {

    private static final String DB_NAME = "card.s3db";
    private static final String DB_URL = "jdbc:sqlite:card.s3db";;

    private static CreditCardDao instance;

    public static CreditCardDao getInstance() {
        if (instance == null) {
            instance = new CreditCardDaoImpl();
        }
        return instance;
    }

    private CreditCardDaoImpl() {
        if (!checkIfDBExists()) {
            createNewTable();
        }
    }

    @Override
    public CreditCard createCreditCard() {
        CreditCard creditCard = new CreditCardImpl();
        String pin = creditCard.getPin();
        String cardNumber = creditCard.generateCardNumber();
        insertNewCardInTable(cardNumber, pin);

        return new CreditCardImpl(cardNumber, pin);
    }

    @Override
    public Optional<String> getPinByCard(String cardNumber) {
        String sql = "SELECT pin FROM card WHERE number = ?";
        ResultSet resultSet;
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, cardNumber);

            resultSet = pstmt.executeQuery();

            if (resultSet.next()) {
                return Optional.ofNullable(resultSet.getString(1));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Optional<Integer> getBalanceByCard(String cardNumber) {
        String sql = "SELECT balance FROM card WHERE number = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, cardNumber);
            ResultSet res = pstmt.executeQuery();

            if (res.next()) {
                return Optional.of(res.getInt(1));
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return Optional.empty();
    }

    @Override
    public void addIncomeByCard(String cardNumber, int amount) {
        String sql = "UPDATE card SET balance = balance + ? WHERE number = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, amount);
            pstmt.setString(2, cardNumber);

            pstmt.executeUpdate();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public void remove(String cardNumber) {
        String sql = "DELETE FROM card WHERE number = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, cardNumber);

            pstmt.executeUpdate();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public boolean checkLuhnValidation(String cardTo) {
        if (cardTo.length() != 16) {
            return false;
        }

        int checkSum = Integer.parseInt(cardTo.substring(15));
        String uniqueId = cardTo.substring(0, 15);

        return LuhnAlgoImpl.generateLuhnSum(uniqueId) == checkSum;

    }


    @Override
    public boolean getCardNumber(String cardNumber) {
        String sql = "SELECT number FROM card where number = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, cardNumber);

            ResultSet res = pstmt.executeQuery();

            if (res.next()) {
                return true;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    @Override
    public void makeTransaction(String cardFrom, String cardTo, int amountOfMoney) {

        String sqlSubtract = "UPDATE card SET balance = (balance - ?) WHERE number = ?";
        String sqlAdd = "UPDATE card SET balance = (balance + ?) WHERE number = ?";

        try {
            Connection conn = DriverManager.getConnection(DB_URL);
            conn.setAutoCommit(false);

            try (PreparedStatement pstmtSubtract = conn.prepareStatement(sqlSubtract);
                 PreparedStatement pstmtAdd = conn.prepareStatement(sqlAdd)) {

                pstmtSubtract.setInt(1, amountOfMoney);
                pstmtSubtract.setString(2, cardFrom);

                pstmtAdd.setInt(1, amountOfMoney);
                pstmtAdd.setString(2, cardTo);


                pstmtSubtract.executeUpdate();
                pstmtAdd.executeUpdate();
                conn.commit();

            } catch (SQLException e) {
                conn.rollback();
            }
        } catch (SQLException throwables) {

            throwables.printStackTrace();
        }


    }


    @Override
    public String getLastCardFromDb() {
        String sql = "SELECT number FROM card ORDER BY id DESC LIMIT 1";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            ResultSet res = pstmt.executeQuery();

            if (res.next()) {
                return res.getString(1);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return "";
    }

    private void insertNewCardInTable(String cardNumber, String pin) {
        String sql2 = "INSERT INTO card(number, pin, balance) VALUES (?,?,?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql2)) {

            pstmt.setString(1, cardNumber);
            pstmt.setString(2, pin);
            pstmt.setInt(3, 0);

            pstmt.executeUpdate();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private void createNewTable() {
        String sql = "CREATE TABLE card (\n"
                + "	id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "	number text, "
                + "	pin text, "
                + " balance INTEGER DEFAULT 0);";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            // create a new table
            stmt.execute(sql);
        } catch (SQLException e) {
            ConsoleHelper.write(e.getMessage());
        }
    }

    private boolean checkIfDBExists() {
        List<String> fileNames = allFilesInDirectory();
        for (String name : fileNames) {
            if (name.equals(DB_NAME)) {
                return true;
            }
        }
        return false;
    }

    private List<String> allFilesInDirectory() {
        List<String> fileNames = new ArrayList<>();
        File file = new File("../Simple Banking System/");
        File[] directories = file.listFiles();
        if (directories != null) {
            for (int i = 0; i < directories.length; i++) {
                fileNames.add(directories[i].getName());
            }
        }

        return fileNames;
    }
}
