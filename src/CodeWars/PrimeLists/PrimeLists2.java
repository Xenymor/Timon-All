package CodeWars.PrimeLists;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PrimeLists2 {
    private final int[] digitsToContain = {1, 2, 3, 4, 5, 6, 7, 8};

    public static void main(String[] args) {
        new PrimeLists2().run();
    }

    private void run() {
        List<Integer> allPrimes = getPrimes();
        System.out.println(allPrimes);
        List<Integer>[] primesWithDigits = getNumbersWithOneDigit(allPrimes, digitsToContain);
        System.out.println(Arrays.toString(primesWithDigits));
        List<List<Integer>> allCombis = getAllCombis(primesWithDigits);
        System.out.println(allCombis);
        removeAllSolutionsContainedTwice(allCombis);
        System.out.println(allCombis);
        List<Integer> result = getAllContainedEverywhere(primesWithDigits, allCombis);
        System.out.println(result);
    }

    private void removeAllSolutionsContainedTwice(final List<List<Integer>> allCombis) {
        List<List<Integer>> result = new ArrayList<>();
        for (List<Integer> combo : allCombis) {
            if (!result.contains(combo)) {
                result.add(combo);
            }
        }
        allCombis.clear();
        allCombis.addAll(result);
    }

    private List<List<Integer>> getAllCombis(final List<Integer>[] primesWithDigits) {
        List<List<Integer>> result = new ArrayList<>();
        addAllCombisRecursive(result, new ArrayList<>(), 0, primesWithDigits);
        return result;
    }

    private void addAllCombisRecursive(final List<List<Integer>> result, final List<Integer> combi, final int i, final List<Integer>[] primesWithDigits) {
        final List<Integer> primesWithDigit = primesWithDigits[i];
        for (Integer integer : primesWithDigit) {
            List<Integer> current = new ArrayList<>(combi);
            if (!containsAny(current, getDigits(integer))) {
                current.add(integer);
            }
            if (i == primesWithDigits.length - 1) {
                if (getDigitCount(current) == digitsToContain.length) {
                    result.add(current);
                }
            } else {
                addAllCombisRecursive(result, current, i + 1, primesWithDigits);
            }
        }
    }

    private int getDigitCount(final List<Integer> current) {
        int sum = 0;
        for (Integer currentNum : current) {
            sum += getDigits(currentNum).length;
        }
        return sum;
    }

    private boolean containsAny(final List<Integer> current, final int[] digits) {
        for (final int digit : digits) {
            for (final Integer other : current) {
                int[] otherDigits = getDigits(other);
                for (final int otherDigit : otherDigits) {
                    if (otherDigit == digit) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void removeAllDoubleDigitCombis(final List<List<Integer>> allCombis) {
        for (int i = allCombis.size() - 1; i >= 0; i--) {
            List<Integer> combo = allCombis.get(i);
            boolean isDoubleCombo = hasDoubleDigit(combo);
            if (isDoubleCombo) {
                allCombis.remove(combo);
            }
        }
    }

    private boolean hasDoubleDigit(final List<Integer> combo) {
        boolean isDoubleCombo = false;
        List<Integer> contained = new ArrayList<>();
        for (Integer num : combo) {
            int[] digits = getDigits(num);
            for (final int digit : digits) {
                if (contained.contains(digit)) {
                    isDoubleCombo = true;
                    break;
                } else {
                    contained.add(digit);
                }
            }
            if (isDoubleCombo) {
                break;
            }
        }
        return isDoubleCombo;
    }

    private List<Integer> getAllContainedEverywhere(final List<Integer>[] primesWithDigits, final List<List<Integer>> allCombis) {
        List<Integer> result = new ArrayList<>();
        for (final List<Integer> primesWithDigit : primesWithDigits) {
            for (final Integer curr : primesWithDigit) {
                if (!result.contains(curr) && isContainedInAll(curr, allCombis)) {
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

    private List<Integer>[] getNumbersWithOneDigit(final List<Integer> allNumbers, final int[] digitsToContain) {
        List<Integer>[] result = new List[digitsToContain.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = new ArrayList<>();
        }
        for (final Integer num : allNumbers) {
            final int[] digits = getDigits(num);
            final int[] containedIndices = getContainedIndices(digits, digitsToContain);
            if (countBiggerZero(containedIndices) >= (Math.log10(num))) {
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

    private int countBiggerZero(final int[] containedIndices) {
        int count = 0;
        for (final int containedIndex : containedIndices) {
            if (containedIndex >= 0) {
                count++;
            }
        }
        return count;
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
        int[] result = new int[(int) (Math.log10(number) + 1)];
        for (int i = result.length - 1; i >= 0; i--) {
            result[i] = number % 10;
            number /= 10;
        }
        return result;
    }

    private List<Integer> getPrimes() {
        List<Integer> result = new ArrayList<>();
        for (int j = 2; j < 100; j++) {
            boolean isPrime = true;
            final double sqrt = Math.sqrt(j);
            for (Integer prime : result) {
                if (prime > sqrt) {
                    break;
                }
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
