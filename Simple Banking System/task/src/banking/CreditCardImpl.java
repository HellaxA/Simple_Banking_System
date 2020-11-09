package banking;

import banking.dao.CreditCardDao;
import banking.dao.CreditCardDaoImpl;

import java.util.Random;

public class CreditCardImpl implements CreditCard {
    public static final String FIRST_ID_NUMBER = "000000000";
    public static final String ZERO = "0";
    public static final int IIN = 400000;

    private String creditCardNumber;
    private String creditCardPin;

    public CreditCardImpl() {

    }

    public CreditCardImpl(String creditCardNumber, String creditCardPin) {
        this.creditCardNumber = creditCardNumber;
        this.creditCardPin = creditCardPin;
    }

    @Override
    public String getCreditCardNumber() {
        return creditCardNumber;
    }

    public void setCreditCardNumber(String creditCardNumber) {
        this.creditCardNumber = creditCardNumber;
    }

    @Override
    public String getCreditCardPin() {
        return creditCardPin;
    }

    public void setCreditCardPin(String creditCardPin) {
        this.creditCardPin = creditCardPin;
    }

    @Override
    public String getPin() {
        Random random = new Random();
        return String.valueOf(random.nextInt(9000) + 1000);
    }

    @Override
    public String generateCardNumber() {
        String number = CreditCardDaoImpl.getInstance().getLastCardFromDb();

        String newCardNumber = "";
        if (number.equals("")) {
            newCardNumber = addIinAndCheckSum(FIRST_ID_NUMBER);
        } else {
            String extractedIdString = number.substring(6, 15);
            int extractedIdNumber = Integer.parseInt(extractedIdString);
            extractedIdNumber++;

            String idNumberWithZeroesBefore = "";

            if (String.valueOf(extractedIdNumber).length() <= 9) {
                idNumberWithZeroesBefore = addZeroesBefore(extractedIdNumber);
            }

            newCardNumber = addIinAndCheckSum(idNumberWithZeroesBefore);
        }

        return newCardNumber;

    }

    private String addZeroesBefore(int extractedIdNumber) {
        String stringValueOfExtractIdNumber = String.valueOf(extractedIdNumber);
        for (int i = stringValueOfExtractIdNumber.length(); i < 9; i++) {
            stringValueOfExtractIdNumber = ZERO + stringValueOfExtractIdNumber;
        }
        return stringValueOfExtractIdNumber;
    }

    private String addIinAndCheckSum(String idNumber) {
        String cardNumberWithoutChecksum = IIN + idNumber;

        int checksum = LuhnAlgoImpl.generateLuhnSum(cardNumberWithoutChecksum);

        return cardNumberWithoutChecksum + checksum;
    }
}
