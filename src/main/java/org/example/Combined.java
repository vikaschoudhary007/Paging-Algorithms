/**
 * The `Combined` class provides methods for generating a random page request sequence, creating a modified sequence,
 * adding noise to the modified sequence, and simulating a cache with a modified page eviction algorithm.
 *
 * The main functionality includes:
 * - Generating a random page request sequence using the `generateRandomSequence` method.
 * - Creating a modified sequence based on the given input using the `generateH` method.
 * - Adding noise to the modified sequence with the `addNoise` method.
 * - Simulating a cache with a modified page eviction algorithm using the `blindOracle` method.
 *
 * The class also contains helper methods:
 * - `chooseRandomPage`: Helper method for randomly selecting a page from a set.
 * - `findPageToRemove`: Helper method for finding a page to evict in case of a cache miss.
 *
 * The main method demonstrates the usage of these functionalities with example parameters and prints the generated
 * sequences along with the number of page faults in the blind replacement simulation.
 *
 * @author Vikas Choudhary
 * @version 1.0
 */

package org.example;
import java.util.*;

public class Combined {

    /**
     * Generates a random page request sequence.
     *
     * @param k       The cache size.
     * @param N       The total number of pages.
     * @param n       The length of the generated sequence.
     * @param epsilon The probability of selecting a page from the local set.
     * @return A list representing the generated random page request sequence.
     */
    public static List<Integer> generateRandomSequence(int k, int N, int n, double epsilon){

        // initializing empty input list
        List<Integer> input = new ArrayList<>();

        // initializing empty local page set
        Set<Integer> localSet = new HashSet<>();

        // Random function
        Random random = new Random();

        // Initialize the sequence with the first k pages
        for(int i=1; i<= k; i++){
            input.add(i);
            localSet.add(i);
        }

        for(int i=k+1; i<= n; i++){

            // Choose page x from local set L uniformly at random
            int x = chooseRandomPage(localSet);

            // creating non-local set
            Set<Integer> nonLocalSet = new HashSet<>();
            for(int j=1; j <= N; j++){
                if(!localSet.contains(j)){
                    nonLocalSet.add(j);
                }
            }

            // Choose page y from non-local set uniformly at random
            int y = chooseRandomPage(nonLocalSet);

            //With probability epsilon, set pi = x; otherwise, set pi = y
            if(random.nextDouble() < epsilon){
                input.add(x);
            }else{
                input.add(y);

                // updating local set in 1-epsilon probability case
                localSet.remove(x);
                localSet.add(y);
            }

        }

        return input;
    }

    // Helper unction to chooseRandom page from set - part of generateRandomSequence function
    /**
     * Helper method to choose a random page from a given set.
     *
     * @param set The set of pages to choose from.
     * @return A randomly selected page from the set.
     */
    public static int chooseRandomPage(Set<Integer> set){

        List<Integer> list = List.copyOf(set);

        Random random = new Random();
        int randIndex = random.nextInt(list.size());

        return list.get(randIndex);
    }


    /**
     * Creates a modified sequence based on the given input.
     *
     * @param input The original page request sequence.
     * @return A modified sequence generated based on the input.
     */
    public static List<Integer> generateH(List<Integer> input){

        // size of page request sequence
        int n = input.size();

        // map of pages with their next occurrence
        Map<Integer, Integer> nextPageMap = new HashMap<>();

        for(int i= n-1; i>=0; i--){

            // get the page from input request at index i
            int page_at_i = input.get(i);

            // Find the next occurrence of this page
            int j = i+1;

            while(j< n && input.get(j) != page_at_i){
                j++;
            }

            if(j < n){
                nextPageMap.put(page_at_i, j+1);
            }else{
                nextPageMap.put(page_at_i, n+1);
            }
        }

        // Generating the sequence h1, h2, . . . . , hn
        List<Integer> output = input.stream().map(page -> nextPageMap.getOrDefault(page, n+1)).toList();

        return output;
    }


    /**
     * Adds noise to the modified sequence.
     *
     * @param hSequence The modified sequence.
     * @param tau       The probability of using the true value of h for each request.
     * @param w         Noise parameter.
     * @return A list of predicted values with added noise.
     */
    public static List<Integer> addNoise(List<Integer> hSequence, double tau, int w){

        List<Integer> predicted_values = new ArrayList<>();
        int n = hSequence.size();
        Random random = new Random();

        for(int i=0; i< n; i++){

            // getting true value of h for each request
            double h_at_i = hSequence.get(i);

            if(random.nextDouble() < 1-tau){
                // case with probability 1 - tau
                predicted_values.add((int)h_at_i);
            }else{

                // lower bound
                double l = Math.max(i+1, (h_at_i - (double) (w / 2)));
                // upper bound
                double u = l + w;

                // finding the predicted value // l = max(i + 1, hi − w/2) and l + w (inclusive)
                double predicted_h_at_i = l + random.nextDouble(u - l+1);

                // formatting the predicted values upto two decimal points
                predicted_values.add((int)Math.round(predicted_h_at_i));

            }
        }

        return predicted_values;
    }

    /**
     * Simulates a cache with a modified page eviction algorithm.
     *
     * @param k      The cache size.
     * @param seq    The original page request sequence.
     * @param hSeq   The modified sequence with added noise.
     * @return The number of page faults.
     */
    public static int blindOracle(int k, List<Integer> seq, List<Integer> hSeq){

        int pageFaults = 0;

        // creating a hashmap to store cache contents
        Map<Integer, Integer> cache = new HashMap<>();

        for(int i=0; i< seq.size(); i++){

            int pageRequest = seq.get(i);
            int predicted_h = hSeq.get(i);

            if(!cache.containsKey(pageRequest)){
                // cache miss // Page Fault
                pageFaults++;

                if(cache.size() == k){
                    // find page to evict
                    int pageToRemove = findPageToRemove(cache);
                    cache.remove(pageToRemove);
                }

                // cache have space so add the new page
                cache.put(pageRequest, predicted_h);
            }else {
                // update the predicted next time for the existing page
                cache.put(pageRequest, predicted_h);
            }
        }

        return pageFaults;
    }

    // Helper unction to find page to evict in case of cache miss - part of blindOracle function
    /**
     * Helper method to find a page to evict in case of a cache miss.
     *
     * @param cache The current cache contents.
     * @return The page to evict.
     */
    public static int findPageToRemove(Map<Integer, Integer> cache){

        int pageToRemove = -1;
        double maxPredictedValue = Double.MIN_VALUE;

        for(Map.Entry<Integer, Integer> entry : cache.entrySet()){

            if(entry.getValue() > maxPredictedValue){
                maxPredictedValue = entry.getValue();
                pageToRemove = entry.getKey();
            }
        }

        return pageToRemove;
    }

    /** Test Functions **/

    // Test function for generateRandomSequence
    public static void testGenerateRandomSequence() {
        // Test input parameters
        int k = 3;
        int N = 10;
        int n = 7;
        double e = 0.6;

        List<Integer> seq = generateRandomSequence(k, N, n, e);
        assert seq.size() == n : "Generated random sequence length does not match";

        // Ensure the first k elements are from 1 to k
        for (int i = 0; i < k; i++) {
            assert seq.get(i) == i + 1 : "First k elements are not from 1 to k";
        }
    }

    // Test case for generateH function
    public static void testGenerateH() {
        List<Integer> seq = List.of(1, 2, 3, 4, 14, 14, 14, 19, 4, 19);
        List<Integer> hSeq = generateH(seq);

        assert hSeq.size() == seq.size() : "Generated hSeq length does not match";

        // Ensure hi values are correct
        List<Integer> expectedHSeq = List.of(11, 11, 11, 9, 6, 6, 6, 10, 9, 10);
        assert hSeq.equals(expectedHSeq) : "Generated hSeq values are incorrect";
    }

    // Test case for addNoise function
    public static void testAddNoise() {
        List<Integer> hSeq = List.of(11, 11, 11, 9, 6, 6, 6, 10, 9, 10);
        double tau = 0.6;
        int w = 2;

        List<Integer> predictedHSeq = addNoise(hSeq, tau, w);

        assert predictedHSeq.size() == hSeq.size() : "Generated predictedHSeq length does not match";

        // Ensure hi values are within the expected range
        for (int i = 0; i < hSeq.size(); i++) {
            int lowerBound = Math.max(i + 1, hSeq.get(i) - w / 2);
            int upperBound = lowerBound + w;
            assert lowerBound <= predictedHSeq.get(i) && predictedHSeq.get(i) <= upperBound :
                    "Generated predictedHSeq values are out of range";
        }
    }

    // Test function for blindOracle Algorithm
    public static void testBlindOracle1() {
        // Testing for the following input parameters
        int k = 4;
        int N = 20;  // N >> k
        int n = 10;
        double e = 0.7;
        double t = 0.2;
        int w = 2;

        List<Integer> seq = generateRandomSequence(k, N, n, e);
        List<Integer> hSeq = generateH(seq);
        List<Integer> noisyHSeq = addNoise(hSeq, t, w);

        int pageFaults = blindOracle(k, seq, noisyHSeq);
        System.out.println("# pageFaults for k:" + k + " N:" + N + " n:" + n + " e:" + e + " t:" + t + " w:" + w + " = " + pageFaults);
    }

    public static void testBlindOracle2() {
        // Testing for the following input parameters
        int k = 6;
        int N = 20;  // N >> k
        int n = 30;
        double e = 0.9;
        double t = 0.5;
        int w = 4;

        List<Integer> seq = generateRandomSequence(k, N, n, e);
        List<Integer> hSeq = generateH(seq);
        List<Integer> noisyHSeq = addNoise(hSeq, t, w);

        int pageFaults = blindOracle(k, seq, noisyHSeq);
        System.out.println("# pageFaults for k:" + k + " N:" + N + " n:" + n + " e:" + e + " t:" + t + " w:" + w + " = " + pageFaults);
    }


    /**
     * Main method demonstrating the usage of the functionalities with example parameters.
     *
     * @param args Command-line arguments.
     */
    public static void main(String[] args) {

        testGenerateRandomSequence();
        testGenerateH();
        testAddNoise();
        testBlindOracle1();
        testBlindOracle2();

    }

}

