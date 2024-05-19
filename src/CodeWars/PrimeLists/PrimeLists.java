package CodeWars.PrimeLists;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PrimeLists {
    private final int[] digitsToContain = {1, 2, 3, 4, 5};

    public static void main(String[] args) {
        new PrimeLists().run();
    }

    private void run() {
        List<Integer> allPrimes = getPrimes();
        System.out.println(allPrimes);
        List<Integer>[] primesWithDigits = getNumbersWithOneDigit(allPrimes, digitsToContain);
        System.out.println(Arrays.toString(primesWithDigits));
        List<List<Integer>> allCombis = getAllCombisContainingOneEach(primesWithDigits);
        System.out.println(allCombis);
        List<Integer> result = getAllContainedEverywhere(primesWithDigits, allCombis);
        System.out.println(result);
    }

    private List<Integer> getAllContainedEverywhere(final List<Integer>[] primesWithDigits, final List<List<Integer>> allCombis) {
        List<Integer> result = new ArrayList<>();
        for (final List<Integer> primesWithDigit : primesWithDigits) {
            for (final Integer curr : primesWithDigit) {
                if (isContainedInAll(curr, allCombis)) {
                    result.add(curr);
                }
            }
        }
        return result;
    }

    private boolean isContainedInAll(final Integer curr, final List<List<Integer>> allCombis) {
        for (List<Integer> combi : allCombis) {
            if (!combi.contains(curr)) {
                return false;
            }
        }
        return true;
    }

    private List<List<Integer>> getAllCombisContainingOneEach(final List<Integer>[] primesWithDigits) {
        List<List<Integer>> result = new ArrayList<>();
        addAllCombisRecursive(result, new ArrayList<>(), 0, primesWithDigits);
        return result;
    }

    private void addAllCombisRecursive(final List<List<Integer>> result, final List<Integer> combi, final int i, final List<Integer>[] primesWithDigits) {
        final List<Integer> primesWithDigit = primesWithDigits[i];
        for (Integer integer : primesWithDigit) {
            List<Integer> current = new ArrayList<>(combi);
            current.add(integer);
            if (i == primesWithDigits.length - 1) {
                result.add(current);
            } else {
                addAllCombisRecursive(result, current, i + 1, primesWithDigits);
            }
        }
    }

    private List<Integer>[] getNumbersWithOneDigit(final List<Integer> allNumbers, final int[] digitsToContain) {
        List<Integer>[] result = new List[digitsToContain.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = new ArrayList<>();
        }
        for (final Integer num : allNumbers) {
            final int[] digits = getDigits(num);
            final int[] containedIndices = getContainedIndices(digits, digitsToContain);
            if (oneBiggerZero(containedIndices)) {
                System.out.println(num + ";\t" + Arrays.toString(digits) + ";\t" + Arrays.toString(containedIndices));
                addToIndices(result, num, containedIndices);
            }
        }
        return result;
    }

    private void addToIndices(final List<Integer>[] result, final Integer integer, final int[] containedIndices) {
        for (int i = 0; i < containedIndices.length; i++) {
            if (containedIndices[i] >= 0) {
                result[i].add(integer);
            }
        }
    }

    private boolean oneBiggerZero(final int[] containedIndices) {
        for (final int containedIndex : containedIndices) {
            if (containedIndex >= 0) {
                return true;
            }
        }
        return false;
    }

    private int[] getContainedIndices(final int[] digits, final int[] digitsToContain) {
        int[] result = new int[digitsToContain.length];
        Arrays.fill(result, -1);
        for (int i = 0; i < digitsToContain.length; i++) {
            final int digitToContain = digitsToContain[i];
            for (int j = 0; j < digits.length; j++) {
                if (digitToContain == digits[j]) {
                    result[i] = j;
                }
            }
        }
        return result;
    }

    private int[] getDigits(int number) {
        int[] result = new int[(int) (Math.log10(number)+1)];
        for (int i = result.length - 1; i >= 0; i--) {
            result[i] = number%10;
            number /= 10;
        }
        return result;
    }

    private List<Integer> getPrimes() {
        List<Integer> result = new ArrayList<>();
        for (int j = 2; j < 100; j++) {
            boolean isPrime = true;
            for (Integer prime : result) {
                if (j % prime == 0) {
                    isPrime = false;
                    break;
                }
            }
            if (isPrime) {
                result.add(j);
            }
        }
        return result;
    }
}
