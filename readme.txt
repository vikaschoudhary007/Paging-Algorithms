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
pageRequest : [1, 2, 3, 3, 1, 3, 3, 1, 3, 3, 3, 8, 2, 9, 3, 9, 3, 9, 9, 1]
hSequence : [5, 13, 4, 4, 5, 4, 4, 5, 4, 4, 4, 21, 13, 16, 4, 16, 4, 16, 16, 5]
predicted_hSequence : [5, 13, 4, 5, 5, 4, 4, 5, 11, 4, 4, 21, 13, 16, 4, 16, 4, 16, 16, 5]
# pageFaults for k:3 N:10 n:20 e:0.7 t:0.2 w:2 = 6
LRU Page Faults : 8
Total page faults incurred by Combined algorithm: 45

