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
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.*;
import java.util.List;

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


    // PHASE 3 STARTS HERE

    // function to execute one single trial with given values of  (k, N, n, ɛ, τ, w)
    // will return a list of pageFaults for OPT, BlindOracle, LRU, Combined

    public static int[] trial(int k, int N, int n, double epsilon, double tau, int w, double threshold){

        List<Integer> sequence = generateRandomSequence(k, N, n, epsilon);
        List<Integer> hSequence = generateH(sequence);
        List<Integer> noisyHSequence = addNoise(hSequence, tau, w);
        int optPageFaults = blindOracle(k, sequence, hSequence);
        int blindPageFaults = blindOracle(k, sequence, noisyHSequence);
        int lruPageFaults = LRU(k, sequence);
        int combinedPageFaults = combinedAlg(k, sequence, noisyHSequence, threshold);

        return new int[]{optPageFaults, blindPageFaults, lruPageFaults, combinedPageFaults};
    }

    // Function to execute trials of batch size = 100
    // will return a list of results for each trial
    public static int[] batchTrial(int batchSize, int k, int N, int n, double epsilon, double tau, int w, double threshold){

        int[][] results = new int[batchSize][4];
        int opt = 0;
        int blind = 0;
        int lru = 0;
        int combined = 0;

        for(int i = 0; i< batchSize; i++) {
            results[i] = trial(k, N, n, epsilon, tau, w, threshold);

        }

        for(int i=0; i<results.length; i++){
            opt += results[i][0];
            blind += results[i][1];
            lru += results[i][2];
            combined += results[i][3];
        }

        int avg_opt = opt/batchSize;
        int avg_blind = blind/batchSize;
        int avg_lru = lru/batchSize;
        int avg_combined = combined/batchSize;

        return new int[]{avg_opt, avg_blind, avg_lru, avg_combined};
    }

    public static void testPlotForK(){
        String name = "k";
        List<Integer> kValues = new ArrayList<>(List.of(5,10,15,20));
        int N = 100;
        int n = 10000;
        double epsilon = 0.9;
        double tau = 0.8;
        int w = 100;
        double threshold = 0.1;

        List<Integer> optValues = new ArrayList<>();
        List<Integer> blindValues = new ArrayList<>();
        List<Integer> lruValues = new ArrayList<>();
        List<Integer> combinedValues = new ArrayList<>();

        for(int i=0; i< kValues.size(); i++){

            int[] result = trial(kValues.get(i), N, n, epsilon, tau, w, threshold);
            optValues.add(result[0]);
            blindValues.add(result[1]);
            lruValues.add(result[2]);
            combinedValues.add(result[3]);
        }

        plotPageFaultsVsK(optValues, blindValues, lruValues, combinedValues, kValues, name);

    }

    public static void testPlotForEpsilon(){

        String name = "Epsilon";
        List<Double> epsilonValues = new ArrayList<>(List.of(0.4,0.5,0.6,0.7));
        int k=10;
        int N = 100;
        int n = 10000;
        double tau = 0.3;
        int w = 1000;
        double threshold = 0.1;

        List<Integer> optValues = new ArrayList<>();
        List<Integer> blindValues = new ArrayList<>();
        List<Integer> lruValues = new ArrayList<>();
        List<Integer> combinedValues = new ArrayList<>();


        for(int i=0; i< epsilonValues.size(); i++){

            int[] result = trial(k, N, n, epsilonValues.get(i), tau, w, threshold);
            optValues.add(result[0]);
            blindValues.add(result[1]);
            lruValues.add(result[2]);
            combinedValues.add(result[3]);
        }

        plotPageFaultsVsEpsilon(optValues, blindValues, lruValues, combinedValues, epsilonValues, name);

    }



    public static void plotPageFaultsVsK(List<Integer> optPageFaults, List<Integer> blindPageFaults, List<Integer> lruPageFaults, List<Integer> combinedPageFaults, List<Integer> kValues, String name) {
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
                "Page Faults vs. "+name, // chart title
                name, // x axis label
                "Page Faults", // y axis label
                dataset
        );

        // Customize chart
        chart.setBackgroundPaint(Color.white);

        // Create and set up the frame
        JFrame frame = new JFrame("Page Faults vs. "+name);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create chart panel and add it to frame
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(800, 600));
        frame.setContentPane(chartPanel);

        // Display the frame
        frame.pack();
        frame.setVisible(true);
    }


    public static void plotPageFaultsVsEpsilon(List<Integer> optPageFaults, List<Integer> blindPageFaults, List<Integer> lruPageFaults, List<Integer> combinedPageFaults, List<Double> eValues, String name) {
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
                "Page Faults vs. "+name, // chart title
                name, // x axis label
                "Page Faults", // y axis label
                dataset
        );

        // Customize chart
        chart.setBackgroundPaint(Color.white);

        // Create and set up the frame
        JFrame frame = new JFrame("Page Faults vs. "+name);
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



        int k = 50;
        int N = 100;
        int n = 10000;
        double epsilon = 0.6;
        double tau = 0.7;
        int w = 200;
        double threshold = 0.1;
        int batchSize = 100;



//        int [] results = batchTrial(batchSize, k, N, n, epsilon, tau, w, threshold);
//
//        System.out.println(Arrays.toString(results));



        List<Integer> sequence = generateRandomSequence(k, N, n, epsilon);
        System.out.println("Random Sequence");
        System.out.println(sequence);

        List<Integer> hSequence = generateH(sequence);
        System.out.println("H Sequence");
        System.out.println(hSequence);

        List<Integer> noisyHSequence = addNoise(hSequence, tau, w);
        System.out.println("Noisy hSequence");
        System.out.println(noisyHSequence);

        System.out.println("\nk: " + k + " N:" + N + " n:" + n + " epsilon:" + epsilon + " tau:" + tau + " w:" + w + " threshold:"+ threshold);

        int pageFaultsOpt = blindOracle(k, sequence, hSequence);
        System.out.println("\nOPT Page Faults : " + pageFaultsOpt);


        int pageFaultsBlind = blindOracle(k, sequence, noisyHSequence);
        System.out.println("\nBlindOracle Page Faults : " + pageFaultsBlind);

        int lruPageFaults = LRU(k, sequence);
        System.out.println("\nLRU Page Faults : "+ lruPageFaults);

        int totalFaults = combinedAlg(k, sequence, noisyHSequence, threshold);
        System.out.println("\nTotal page faults incurred by Combined algorithm: " + totalFaults);


        testPlotForK();
        testPlotForEpsilon();
    }


}

