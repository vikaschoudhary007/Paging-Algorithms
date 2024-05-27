package org.example; /**
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
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Combined {

    // PHASE 1 STARTS HERE

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
        List<Integer> hseq = new ArrayList<>();

        for(int i= 0; i<n; i++){

            // get the page from input request at index i
            int page_at_i = input.get(i);
            int next = n+1;

            for(int j=i+1; j<n; j++){
                if(page_at_i == input.get(j)){
                    next = j+1;
                    break;
                }
            }

            hseq.add(next);
        }

        return hseq;
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

            double prob = random.nextDouble();
            int nextVal = hSequence.get(i);

            if(prob <= tau){
                nextVal = (int) (Math.random() * (Math.max(i + 1 + 1, nextVal - Math.floorDiv(w, 2))) +
                        Math.max(i + 1, nextVal - Math.floorDiv(w, 2)) + w);
            }
            predicted_values.add(nextVal);
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


    // PHASE 2 STARTS HERE

    // LRU - Least Recently Used(Paging Algorithm)

    /**
     * Simulates a cache using the Least Recently Used (LRU) paging algorithm.
     *
     * This method simulates a cache by implementing the Least Recently Used (LRU) paging algorithm,
     * which evicts the least recently used page when the cache is full and a new page needs to be inserted.
     * The LRU algorithm keeps track of the order in which pages are accessed and evicts the page that has been
     * least recently accessed when the cache is full.
     *
     * @param k          The cache size.
     * @param inputSeq   The original page request sequence.
     * @return The number of page faults incurred by the LRU algorithm.
     */
    public static int LRU(int k, List<Integer> inputSeq){

        // Linked HashMap is used to maintain the order and access Order of elements
        LinkedHashMap<Integer, Integer> cache = new LinkedHashMap<>(k, 0.75f, true);

        int pageFaults = 0;

        for(int page : inputSeq){

            if(!cache.containsKey(page)){
                pageFaults++;

                if(cache.size() == k){
                    Iterator<Map.Entry<Integer, Integer>> iterator = cache.entrySet().iterator();
                    // get the first entry (least recently used)
                    iterator.next();
                    iterator.remove();
                }
            }

            // put page in cache
            cache.put(page, 0);
        }

        return pageFaults;

    }

    // Combined Algorithm - Switching b/w BlindOracle & LRU

    /**
     * Simulates a cache with a combined algorithm that switches between BlindOracle and LRU based on a threshold.
     *
     * @param k         The cache size.
     * @param seq       The original page request sequence.
     * @param hSeq      The modified sequence with added noise.
     * @param thr The threshold value for switching between BlindOracle and LRU.
     * @return The total number of page faults incurred by the combined algorithm.
     */
    public static int combinedAlg(int k, List<Integer> seq, List<Integer> hSeq, double thr) {
        List<Integer> cache = new ArrayList<>();
        int pageFaultsCombined = 0;

        List<Integer> cacheBlind = new ArrayList<>();
        List<Integer> cacheH = new ArrayList<>();
        int pageFaultsBlind = 0;

        List<Integer> cacheLRU = new ArrayList<>();
        List<Integer> cacheHLRU = new ArrayList<>();
        int pageFaultsLRU = 0;

        boolean isLRU = true;
        for (int i = 0; i < seq.size(); i++) {
            if (i < k) {
                pageFaultsBlind += 1;
                pageFaultsLRU += 1;
                cacheBlind.add(seq.get(i));
                cacheLRU.add(seq.get(i));
                cacheH.add(hSeq.get(i));
                cacheHLRU.add(i);
                cache.add(i);
                pageFaultsCombined += 1;
            } else {
                if (isLRU && (pageFaultsLRU > (1 + thr) * pageFaultsBlind)) {
                    pageFaultsCombined += k;
                    isLRU = false;
                    cache = new ArrayList<>(cacheBlind);
                }

                if ((!isLRU) && (pageFaultsBlind > (1 + thr) * pageFaultsLRU)) {
                    pageFaultsCombined += k;
                    isLRU = true;
                    cache = new ArrayList<>(cacheLRU);
                }

                int valueIndexBlind = cacheBlind.indexOf(seq.get(i));
                if (valueIndexBlind == -1) {
                    int maxValue = getMaxValue(cacheH);
                    int index = cacheH.indexOf(maxValue);
                    cacheBlind.set(index, seq.get(i));
                    cacheH.set(index, hSeq.get(i));
                    pageFaultsBlind += 1;
                    if (!isLRU) {
                        cache = new ArrayList<>(cacheBlind);
                        pageFaultsCombined += 1;
                    }
                } else {
                    cacheH.set(valueIndexBlind, hSeq.get(i));
                }

                int valueIndexLRU = cacheLRU.indexOf(seq.get(i));
                if (valueIndexLRU == -1) {
                    int minValue = getMinValue(cacheHLRU);
                    int index = cacheHLRU.indexOf(minValue);
                    cacheLRU.set(index, seq.get(i));
                    cacheHLRU.set(index, i);
                    pageFaultsLRU += 1;
                    if (isLRU) {
                        cache = new ArrayList<>(cacheLRU);
                        pageFaultsCombined += 1;
                    }
                } else {
                    cacheHLRU.set(valueIndexLRU, i);
                }
            }
        }
        return pageFaultsCombined;
    }

    private static int getMaxValue(List<Integer> list) {
        int max = Integer.MIN_VALUE;
        for (int value : list) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }

    private static int getMinValue(List<Integer> list) {
        int min = Integer.MAX_VALUE;
        for (int value : list) {
            if (value < min) {
                min = value;
            }
        }
        return min;
    }


    // PHASE 3 STARTS HERE

    // function to execute one single trial with given values of  (k, N, n, ɛ, τ, w)
    // will return a list of pageFaults for OPT, BlindOracle, LRU, Combined

    /**
     * Executes a single trial with given values of (k, N, n, ε, τ, w).
     *
     * @param k          The cache size.
     * @param N          The factor for generating the working set size.
     * @param n          The size of the reference string.
     * @param epsilon    The threshold for the Blind Oracle algorithm.
     * @param tau        The threshold for adding noise to the sequence.
     * @param w          The working set window size.
     * @param threshold  The threshold for the Combined algorithm.
     * @return An array containing the page faults for OPT, Blind Oracle, LRU, and Combined algorithms.
     */
    public static int[] singleTrial(int k, int N, int n, double epsilon, double tau, int w, double threshold){

        List<Integer> sequence = generateRandomSequence(k, N, n, epsilon);
        List<Integer> hSequence = generateH(sequence);
        List<Integer> noisyHSequence = addNoise(hSequence, tau, w);
        int optPageFaults = blindOracle(k, sequence, hSequence);
        int blindPageFaults = blindOracle(k, sequence, noisyHSequence);
        int lruPageFaults = LRU(k, sequence);
        int combinedPageFaults = combinedAlg(k, sequence, noisyHSequence, threshold);

        return new int[]{optPageFaults, blindPageFaults, lruPageFaults, combinedPageFaults};
    }

    // Function to execute trials of batch size
    // will return a list of results for each trial

    /**
     * Function to execute trials of a given batch size and return a list of results for each trial.
     *
     * @param batchSize  The number of trials to execute.
     * @param k          The cache size.
     * @param N          The factor for generating the working set size.
     * @param n          The size of the reference string.
     * @param epsilon    The threshold for the Blind Oracle algorithm.
     * @param tau        The threshold for the Combined algorithm.
     * @param w          The working set window size.
     * @param threshold  The threshold for the Combined algorithm.
     * @return An array containing the total page faults for each algorithm over all trials.
     */
    public static int[] batchTrial(int batchSize, int k, int N, int n, double epsilon, double tau, int w, double threshold) {
        int[] totalPageFaults = new int[4];
        for (int i = 0; i < batchSize; i++) {
            int[] pageFaults = singleTrial(k, N, n, epsilon, tau, w, threshold);
            for (int j = 0; j < 4; j++) {
                totalPageFaults[j] += pageFaults[j];
            }
        }
        for (int j = 0; j < 4; j++) {
            totalPageFaults[j] /= batchSize;
        }
        return totalPageFaults;
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
     * Test function for the LRU (Least Recently Used) algorithm.
     */
    public static void testLRU() {
        // Test input parameters
        int k = 3;
        List<Integer> sequence = List.of(1, 2, 3, 4, 1, 2, 5, 1, 2, 3, 4, 5);

        int pageFaults = LRU(k, sequence);

        // Ensure the correct number of page faults are detected
        assert pageFaults == 9 : "Incorrect number of page faults for LRU algorithm";
        System.out.println("# pageFaults for k:" + k + " = " + pageFaults);

    }

    /**
     * Test function for the combined algorithm, switching between LRU and BlindOracle.
     */
    public static void testCombinedAlg() {
        // Test input parameters
        int k = 3;
        double threshold = 0.2;
        List<Integer> sequence = List.of(1, 2, 3, 4, 1, 2, 5, 1, 2, 3, 4, 5);
        List<Integer> hSequence = List.of(11, 12, 13, 14, 11, 12, 15, 11, 12, 13, 14, 15);

        int totalFaults = combinedAlg(k, sequence, hSequence, threshold);

        // Ensure the correct total number of page faults are detected
        assert totalFaults == 9 : "Incorrect total number of page faults for Combined algorithm";
        System.out.println("# pageFaults for k:" + k + " threshold:" + threshold + " = " + totalFaults);

    }

    /**
     * Test function for the overall functionality for Phase2.
     */
    public static void testPhase2() {
        int k = 4;
        double threshold = 0.3;
        List<Integer> sequence = List.of(1, 2, 3, 4, 1, 2, 5, 1, 2, 3, 4, 5);
        List<Integer> hSequence = List.of(11, 12, 13, 14, 11, 12, 15, 11, 12, 13, 14, 15);

        // LRU page faults
        int lruFaults = LRU(k, sequence);
        System.out.println("LRU Page Faults: " + lruFaults);

        int combinedFaults = combinedAlg(k, sequence, hSequence, threshold);
        System.out.println("Combined Algorithm Page Faults: " + combinedFaults);

    }



    /**
     * Method to test and plot page faults vs. cache size (k) for various caching algorithms.
     */
    public static void test13(){

        //  Varying K
        //  LRU > BlindOracle > OPT
        //  Regime 1

        String name = "K - Regime 1";
        List<Integer> kValues = new ArrayList<>(List.of(5,10,15,20,25,30));
        int n = 10000;
        double epsilon = 0.5;
        double tau = 0.5;
        int w = 200;
        double threshold = 0.1;
        int factor = 10;
        int batchSize = 100;

        List<Integer> optValues = new ArrayList<>();
        List<Integer> blindValues = new ArrayList<>();
        List<Integer> lruValues = new ArrayList<>();
        List<Integer> combinedValues = new ArrayList<>();

        for(int i=0; i< kValues.size(); i++){
            int N = factor*(kValues.get(i));
            int[] result = batchTrial(batchSize, kValues.get(i), N, n, epsilon, tau, w, threshold);
            optValues.add(result[0]);
            blindValues.add(result[1]);
            lruValues.add(result[2]);
            combinedValues.add(result[3]);
        }

        plotPageFaultsVsKorW(optValues, blindValues, lruValues, combinedValues, kValues, name);

    }


    public static void test14(){

        // Varying K
        // BlindOracle > LRU > OPT
        // Regime 2

        String name = "K - Regime 2";
        List<Integer> kValues = new ArrayList<>(List.of(5,10,15,20));
        int n = 10000;
        double epsilon = 0.7;
        double tau = 0.9;
        int w = 200;
        double threshold = 0.1;
        int factor = 10;
        int batchSize = 100;

        List<Integer> optValues = new ArrayList<>();
        List<Integer> blindValues = new ArrayList<>();
        List<Integer> lruValues = new ArrayList<>();
        List<Integer> combinedValues = new ArrayList<>();

        for(int i=0; i< kValues.size(); i++){
            int N = factor*(kValues.get(i));
            int[] result = batchTrial(batchSize, kValues.get(i), N, n, epsilon, tau, w, threshold);
            optValues.add(result[0]);
            blindValues.add(result[1]);
            lruValues.add(result[2]);
            combinedValues.add(result[3]);
        }

        plotPageFaultsVsKorW(optValues, blindValues, lruValues, combinedValues, kValues, name);

    }

    /**
     * Method to test and plot page faults vs. working set size (w) for various caching algorithms.
     */
    public static void test15(){

        // Varying w
        // LRU > BlindOracle > OPT
        // Regime 1

        String name = "w - Regime 1";
        List<Integer> wValues = new ArrayList<>(List.of(50,100,200,500, 1000));
        int k = 20;
        int n = 10000;
        int batchSize = 100;
        int N = 100;
        double epsilon = 0.5;
        double tau = 0.5;
        double threshold = 0.1;

        List<Integer> optValues = new ArrayList<>();
        List<Integer> blindValues = new ArrayList<>();
        List<Integer> lruValues = new ArrayList<>();
        List<Integer> combinedValues = new ArrayList<>();

        for(int i=0; i< wValues.size(); i++){
            int[] result = batchTrial(batchSize, k, N, n, epsilon, tau, wValues.get(i), threshold);
            optValues.add(result[0]);
            blindValues.add(result[1]);
            lruValues.add(result[2]);
            combinedValues.add(result[3]);
        }

        plotPageFaultsVsKorW(optValues, blindValues, lruValues, combinedValues, wValues, name);

    }


    public static void test16(){

        // Varying w
        // BlindOracle > LRU > OPT
        // Regime 2

        String name = "w - Regime 2";
        List<Integer> wValues = new ArrayList<>(List.of(50,100,200,500, 1000));
        int k = 20;
        int n = 10000;
        int batchSize = 100;
        int N = 100;
        double epsilon = 0.7;
        double tau = 0.9;
        double threshold = 0.1;

        List<Integer> optValues = new ArrayList<>();
        List<Integer> blindValues = new ArrayList<>();
        List<Integer> lruValues = new ArrayList<>();
        List<Integer> combinedValues = new ArrayList<>();

        for(int i=0; i< wValues.size(); i++){
            int[] result = batchTrial(batchSize, k, N, n, epsilon, tau, wValues.get(i), threshold);
            optValues.add(result[0]);
            blindValues.add(result[1]);
            lruValues.add(result[2]);
            combinedValues.add(result[3]);
        }

        plotPageFaultsVsKorW(optValues, blindValues, lruValues, combinedValues, wValues, name);

    }

    /**
     * Method to test and plot page faults vs. epsilon for various caching algorithms.
     */
    public static void test17(){

        // Varying epsilon
        // LRU > BlindOracle > OPT
        // Regime 1

        String name = "ε - Regime 1";
        List<Double> epsilonValues = new ArrayList<>(List.of(0.2, 0.3, 0.4, 0.6, 0.7));
        int k = 20;
        int N = 100;
        int n = 10000;
        int w = 200;
        int batchSize = 100;
        double tau = 0.5;
        double threshold = 0.1;

        List<Integer> optValues = new ArrayList<>();
        List<Integer> blindValues = new ArrayList<>();
        List<Integer> lruValues = new ArrayList<>();
        List<Integer> combinedValues = new ArrayList<>();



        for(int i=0; i< epsilonValues.size(); i++){
            int[] result = batchTrial(batchSize, k, N, n, epsilonValues.get(i), tau, w, threshold);
            optValues.add(result[0]);
            blindValues.add(result[1]);
            lruValues.add(result[2]);
            combinedValues.add(result[3]);
        }


        plotPageFaultsVsEpsilonorTau(optValues, blindValues, lruValues, combinedValues, epsilonValues, name);

    }

    public static void test18(){

        // Varying epsilon
        // BlindOracle > LRU > OPT
        // Regime 2

        String name = "ε - Regime 2";
        List<Double> epsilonValues = new ArrayList<>(List.of(0.2, 0.3, 0.4, 0.6, 0.7));
        int k = 20;
        int N = 100;
        int n = 10000;
        int w = 200;
        int batchSize = 100;
        double tau = 0.9;
        double threshold = 0.1;

        List<Integer> optValues = new ArrayList<>();
        List<Integer> blindValues = new ArrayList<>();
        List<Integer> lruValues = new ArrayList<>();
        List<Integer> combinedValues = new ArrayList<>();



        for(int i=0; i< epsilonValues.size(); i++){
            int[] result = batchTrial(batchSize, k, N, n, epsilonValues.get(i), tau, w, threshold);
            optValues.add(result[0]);
            blindValues.add(result[1]);
            lruValues.add(result[2]);
            combinedValues.add(result[3]);
        }


        plotPageFaultsVsEpsilonorTau(optValues, blindValues, lruValues, combinedValues, epsilonValues, name);

    }


    /**
     * Method to test and plot page faults vs. tau for various caching algorithms.
     */
    public static void test19(){

        // Varying tau
        // LRU > BlindOracle > OPT
        // Regime 1

        String name = "τ - regime 1";
        List<Double> tauValues = new ArrayList<>(List.of(0.45, 0.55, 0.7, 0.8, 0.9));
        int k = 20;
        int N = 100;
        int n = 10000;
        int w = 200;
        int batchSize = 100;
        double epsilon = 0.45;
        double threshold = 0.1;

        List<Integer> optValues = new ArrayList<>();
        List<Integer> blindValues = new ArrayList<>();
        List<Integer> lruValues = new ArrayList<>();
        List<Integer> combinedValues = new ArrayList<>();

        for(int i=0; i< tauValues.size(); i++){
            int[] result = batchTrial(batchSize, k, N, n, epsilon, tauValues.get(i), w, threshold);
            optValues.add(result[0]);
            blindValues.add(result[1]);
            lruValues.add(result[2]);
            combinedValues.add(result[3]);
        }

        plotPageFaultsVsEpsilonorTau(optValues, blindValues, lruValues, combinedValues, tauValues, name);

    }


    public static void test20(){

        // Varying tau
        // BlindOracle > LRU > OPT
        // Regime 2

        String name = "τ - regime 2";
        List<Double> tauValues = new ArrayList<>(List.of(0.45, 0.55, 0.7, 0.8, 0.9));
        int k = 20;
        int N = 100;
        int n = 10000;
        int w = 200;
        int batchSize = 100;
        double epsilon = 0.7;
        double threshold = 0.1;

        List<Integer> optValues = new ArrayList<>();
        List<Integer> blindValues = new ArrayList<>();
        List<Integer> lruValues = new ArrayList<>();
        List<Integer> combinedValues = new ArrayList<>();

        for(int i=0; i< tauValues.size(); i++){
            int[] result = batchTrial(batchSize, k, N, n, epsilon, tauValues.get(i), w, threshold);
            optValues.add(result[0]);
            blindValues.add(result[1]);
            lruValues.add(result[2]);
            combinedValues.add(result[3]);
        }

        plotPageFaultsVsEpsilonorTau(optValues, blindValues, lruValues, combinedValues, tauValues, name);

    }


    /**
     * Generates plots for page faults vs. the cache size (k).
     * This method tests the page fault performance of various caching algorithms
     * (OPT, Blind Oracle, LRU, Combined) against different cache sizes (k).
     *
     * @param optPageFaults    List of page faults for the OPT algorithm.
     * @param blindPageFaults  List of page faults for the Blind Oracle algorithm.
     * @param lruPageFaults    List of page faults for the LRU algorithm.
     * @param combinedPageFaults  List of page faults for the Combined algorithm.
     * @param kValues          List of cache sizes (k).
     * @param name             The name of the parameter being varied (k in this case).
     */
    public static void plotPageFaultsVsKorW(List<Integer> optPageFaults, List<Integer> blindPageFaults, List<Integer> lruPageFaults, List<Integer> combinedPageFaults, List<Integer> kValues, String name) {
        // Create a dataset
        XYSeriesCollection dataset = new XYSeriesCollection();

        // Add series for each algorithm
        XYSeries optSeries = new XYSeries("OPT");
        XYSeries blindSeries = new XYSeries("Blind Oracle");
        XYSeries lruSeries = new XYSeries("LRU");
        XYSeries combinedSeries = new XYSeries("Combined");

        for (int i = 0; i < kValues.size(); i++) {
            optSeries.add(kValues.get(i), optPageFaults.get(i));
            blindSeries.add(kValues.get(i), blindPageFaults.get(i));
            lruSeries.add(kValues.get(i), lruPageFaults.get(i));
            combinedSeries.add(kValues.get(i), combinedPageFaults.get(i));
        }

        // Add the series to the dataset
        dataset.addSeries(optSeries);
        dataset.addSeries(blindSeries);
        dataset.addSeries(lruSeries);
        dataset.addSeries(combinedSeries);

        // Create the chart
        JFreeChart chart = ChartFactory.createXYLineChart(
                "Page Faults vs. " + name, // chart title
                name, // x axis label
                "Page Faults", // y axis label
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        // Customize chart
        chart.setBackgroundPaint(Color.WHITE);
        XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint(Color.WHITE);

        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();


        renderer.setSeriesPaint(0, Color.BLUE);  // OPT
        renderer.setSeriesPaint(1, Color.RED);   // Blind Oracle
        renderer.setSeriesPaint(2, Color.GREEN); // LRU
        renderer.setSeriesPaint(3, Color.ORANGE);// Combined
        renderer.setSeriesStroke(0, new BasicStroke(2.0f)); // OPT line thickness
        renderer.setSeriesStroke(1, new BasicStroke(2.0f)); // Blind Oracle line thickness
        renderer.setSeriesStroke(2, new BasicStroke(2.0f)); // LRU line thickness
        renderer.setSeriesStroke(3, new BasicStroke(2.0f)); // Combined line thickness

        // Show points on the lines
        renderer.setSeriesShapesVisible(0, true); // OPT
        renderer.setSeriesShapesVisible(1, true); // Blind Oracle
        renderer.setSeriesShapesVisible(2, true); // LRU
        renderer.setSeriesShapesVisible(3, true); // Combined

        plot.setRenderer(renderer);


        // Customize legend
        LegendItemCollection legendItems = new LegendItemCollection();
        legendItems.add(new LegendItem("OPT", null, null, null, Plot.DEFAULT_LEGEND_ITEM_BOX, Color.BLUE));
        legendItems.add(new LegendItem("Blind Oracle", null, null, null, Plot.DEFAULT_LEGEND_ITEM_BOX, Color.RED));
        legendItems.add(new LegendItem("LRU", null, null, null, Plot.DEFAULT_LEGEND_ITEM_BOX, Color.GREEN));
        legendItems.add(new LegendItem("Combined", null, null, null, Plot.DEFAULT_LEGEND_ITEM_BOX, Color.ORANGE));
        plot.setFixedLegendItems(legendItems);

        // Create and set up the frame
        JFrame frame = new JFrame("Page Faults vs. " + name);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create chart panel and add it to frame
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(800, 600));
        frame.setContentPane(chartPanel);

        // Display the frame
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * Generates plots for page faults vs. the cache size (epsilon).
     * This method tests the page fault performance of various caching algorithms
     * (OPT, Blind Oracle, LRU, Combined) against different cache sizes (epsilon).
     *
     * @param optPageFaults    List of page faults for the OPT algorithm.
     * @param blindPageFaults  List of page faults for the Blind Oracle algorithm.
     * @param lruPageFaults    List of page faults for the LRU algorithm.
     * @param combinedPageFaults  List of page faults for the Combined algorithm.
     * @param eValues          List of locality parameter values (epsilon).
     * @param name             The name of the parameter being varied (epsilon in this case).
     */

    public static void plotPageFaultsVsEpsilonorTau(List<Integer> optPageFaults, List<Integer> blindPageFaults, List<Integer> lruPageFaults, List<Integer> combinedPageFaults, List<Double> eValues, String name) {
        // Create a dataset
        XYSeriesCollection dataset = new XYSeriesCollection();

        // Add series for each algorithm
        XYSeries optSeries = new XYSeries("OPT");
        XYSeries blindSeries = new XYSeries("Blind Oracle");
        XYSeries lruSeries = new XYSeries("LRU");
        XYSeries combinedSeries = new XYSeries("Combined");

        for (int i = 0; i < eValues.size(); i++) {
            optSeries.add(eValues.get(i), optPageFaults.get(i));
            blindSeries.add(eValues.get(i), blindPageFaults.get(i));
            lruSeries.add(eValues.get(i), lruPageFaults.get(i));
            combinedSeries.add(eValues.get(i), combinedPageFaults.get(i));
        }

        // Add the series to the dataset
        dataset.addSeries(optSeries);
        dataset.addSeries(blindSeries);
        dataset.addSeries(lruSeries);
        dataset.addSeries(combinedSeries);

        // Create the chart
        JFreeChart chart = ChartFactory.createXYLineChart(
                "Page Faults vs. " + name, // chart title
                name, // x axis label
                "Page Faults", // y axis label
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        // Customize chart
        chart.setBackgroundPaint(Color.WHITE);
        XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint(Color.WHITE);

        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();


        renderer.setSeriesPaint(0, Color.BLUE);  // OPT
        renderer.setSeriesPaint(1, Color.RED);   // Blind Oracle
        renderer.setSeriesPaint(2, Color.GREEN); // LRU
        renderer.setSeriesPaint(3, Color.ORANGE);// Combined
        renderer.setSeriesStroke(0, new BasicStroke(2.0f)); // OPT line thickness
        renderer.setSeriesStroke(1, new BasicStroke(2.0f)); // Blind Oracle line thickness
        renderer.setSeriesStroke(2, new BasicStroke(2.0f)); // LRU line thickness
        renderer.setSeriesStroke(3, new BasicStroke(2.0f)); // Combined line thickness

        // Show points on the lines
        renderer.setSeriesShapesVisible(0, true); // OPT
        renderer.setSeriesShapesVisible(1, true); // Blind Oracle
        renderer.setSeriesShapesVisible(2, true); // LRU
        renderer.setSeriesShapesVisible(3, true); // Combined

        plot.setRenderer(renderer);


        // Customize legend
        LegendItemCollection legendItems = new LegendItemCollection();
        legendItems.add(new LegendItem("OPT", null, null, null, Plot.DEFAULT_LEGEND_ITEM_BOX, Color.BLUE));
        legendItems.add(new LegendItem("Blind Oracle", null, null, null, Plot.DEFAULT_LEGEND_ITEM_BOX, Color.RED));
        legendItems.add(new LegendItem("LRU", null, null, null, Plot.DEFAULT_LEGEND_ITEM_BOX, Color.GREEN));
        legendItems.add(new LegendItem("Combined", null, null, null, Plot.DEFAULT_LEGEND_ITEM_BOX, Color.ORANGE));
        plot.setFixedLegendItems(legendItems);

        // Create and set up the frame
        JFrame frame = new JFrame("Page Faults vs. " + name);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create chart panel and add it to frame
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(800, 600));
        frame.setContentPane(chartPanel);

        // Display the frame
        frame.pack();
        frame.setVisible(true);
    }




    /**
     * Main method demonstrating the usage of the functionalities with example parameters.
     *
     * @param args Command-line arguments.
     */
    public static void main(String[] args) {

//        testGenerateRandomSequence();
//        testGenerateH();
//        testAddNoise();
//        testBlindOracle1();
//        testBlindOracle2();
//        testLRU();
//        testCombinedAlg();
//        testPhase2();

        int numOfThreads = 8;
        ExecutorService executorService = Executors.newFixedThreadPool(numOfThreads);

        //Trend 1 - Page Faults vs K
        executorService.submit(Combined::test13);
        executorService.submit(Combined::test14);

        //Trend 2 - Page Faults vs W
        executorService.submit(Combined::test15);
        executorService.submit(Combined::test16);

        //Trend 3 - Page Faults vs Epsilon
        executorService.submit(Combined::test17);
        executorService.submit(Combined::test18);

        //Trend 1 - Page Faults vs Tau
        executorService.submit(Combined::test19);
        executorService.submit(Combined::test20);

        // Shutdown the executor and wait for the tasks to complete
        executorService.shutdown();

        try{
            if(!executorService.awaitTermination(1, TimeUnit.HOURS)){
                // Forcefully shutdown if tasks exceed timeout
                executorService.shutdown();
            }
        } catch (InterruptedException e) {
            // Forcefully shutdown if the thread was interrupted
            executorService.shutdown();
            Thread.currentThread().interrupt();
        }

    }
}

