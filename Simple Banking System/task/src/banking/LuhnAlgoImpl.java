package banking;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class LuhnAlgoImpl {
    public static int generateLuhnSum(String cardWithoutChecksum) {

        //get array from card string
        int[] cardNumsArray = Stream.of(cardWithoutChecksum.split(""))
                .mapToInt(Integer::parseInt).toArray();

        //get ArrayList
        List<Integer> cardNumsArrayList = intArrayToArrayList(cardNumsArray);

        //multiply odd digits by 2
        multiplyOddBy2(cardNumsArrayList);

        //subtract 9 to numbers over 9
        subtractNine(cardNumsArrayList);

        //sum of all number in array list
        int sum = cardNumsArrayList.stream()
                .mapToInt(a -> a)
                .sum();

        return findCheckSum(sum);
    }

    private static void multiplyOddBy2(List<Integer> cardNumsArrayList) {
        for (int i = 0; i < cardNumsArrayList.size(); i++) {
            Integer tempElement = cardNumsArrayList.get(i);

            if ((i + 1) % 2 != 0) {
                cardNumsArrayList.set(i, tempElement * 2);
            }
        }
    }

    private static void subtractNine(List<Integer> cardNumsArrayList) {

        for (int i = 0; i < cardNumsArrayList.size(); i++) {
            Integer tempElement = cardNumsArrayList.get(i);
            if (tempElement > 9) {
                cardNumsArrayList.set(i, tempElement - 9);
            }
        }
    }

    //find check sum
    private static int findCheckSum(int sum) {
        for (int i = 0; i < 10; i++) {
            if ((sum + i) % 10 == 0) {
                return i;
            }
        }
        return -1;
    }

    private static List<Integer> intArrayToArrayList(int[] cardNumsArray) {
        List<Integer> cardNumsArrayList = new ArrayList<>(cardNumsArray.length);
        for (int i : cardNumsArray) {
            cardNumsArrayList.add(i);
        }
        return cardNumsArrayList;
    }
}
