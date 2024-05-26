# COMP 691 – Online Algorithms and Competitive Analysis

## Description
The `Combined` class provides methods for generating a random page request sequence, creating a modified sequence,
 * adding noise to the modified sequence, and simulating a cache with a modified page eviction algorithm.

 * The main functionality includes:
    - Generating a random page request sequence using the `generateRandomSequence` method.
    - Creating a modified sequence based on the given input using the `generateH` method.
    - Adding noise to the modified sequence with the `addNoise` method.
    - Simulating a cache with a modified page eviction algorithm using the `blindOracle` method.
    - Simulating a cache with `LRU`(Least Recently Used) paging technique.
    - `CombinedAlg` is the implementation of the combined algorithm switching between `blindOracle` and `LRU` to find total number o page faults.

 * The class also contains helper methods:
    - `chooseRandomPage`: Helper method for randomly selecting a page from a set.
    - `findPageToRemove`: Helper method for finding a page to evict in case of a cache miss.
    - `trial`: Helper method to run all 4 algorithms with a set of parameters (k, N, ε, τ, w)
    - `batchTrial`: Helper method to run a batch of trial.
    - `plotPageFaultsVsK & plotPageFaultsVsEpsilon`: Helper functions to generate plots.

 * The main method demonstrates the usage of these functionalities with example parameters and prints the generated
 * sequences along with the number of page faults in the blind replacement simulation.

## Code Documentation
For code documentation launch the Combined.html file in /javadoc/org folder




### Compiler Version:

The code was compiled using OpenJDK 21.0.2. Ensure you have the appropriate version installed to successfully compile and run the program.

Make sure you are in folder java, use the following command:

cd src/main/java

To compile the code, use the following command:

javac org/example/Combined.java

### Execution Command:

To run the compiled program, use the following command:

java org/example/Combined

### Expected Output:

The program will generate random page request sequences, create modified sequences, add noise, and simulate a cache with a modified page eviction algorithm. The output will include the generated sequences and the number of page faults in the blind replacement simulation.
Ensure that the Java runtime environment is properly set up on your machine before running the program.

Example Simulation :
pageRequest : [1, 2, 3, 4, 14, 14, 14, 19, 4, 19]
hSequence : [11, 11, 11, 9, 6, 6, 6, 10, 9, 10]
predicted_hSequence : [11, 11, 11, 9, 6, 6, 6, 10, 9, 10]
pageFaults : 6

Example Simulation for Phase 2 :-

pageRequest : [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 18, 18, 6, 14, 19, 9, 7, 7, 8, 6, 2, 16, 6, 10, 16, 12, 10, 19, 7, 17]
hSequence : [31, 21, 31, 31, 31, 13, 17, 19, 16, 24, 12, 31, 20, 31, 28, 31, 18, 29, 31, 23, 31, 25, 31, 27, 31, 31, 31, 31, 31, 31]
predicted_hSequence : [201, 203, 204, 205, 206, 210, 209, 211, 217, 217, 213, 221, 219, 228, 215, 224, 223, 230, 238, 227, 237, 239, 242, 224, 31, 242, 239, 231, 258, 238]

# pageFaults for k: 10 N:20 n:30 epsilon:0.7 tau:0.9 w:200 threshold:0.1
OPT Page Faults : 16
BlindOracle Page Faults : 18
LRU Page Faults : 17
Total page faults incurred by Combined algorithm: 17

Example Simulation for Phase 3 :-

In Phase 3, following 4 functions are created with some other helper functions to generate plots for all four Trends.
 * The Trends:
    - Generating plot for Trend-1 with `testPlotForK` method.
    - Generating plot for Trend-2 with `testPlotForW` method.
    - Generating plot for Trend-3 with `testPlotForEpsilon` method.
    - Generating plot for Trend-4 with `testPlotForTau` method.



