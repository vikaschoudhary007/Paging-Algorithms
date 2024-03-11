# COMP 691 – Online Algorithms and Competitive Analysis

## Compilation and Execution Information

## Code Documentation
The `Combined` class provides methods for generating a random page request sequence, creating a modified sequence,
 * adding noise to the modified sequence, and simulating a cache with a modified page eviction algorithm.

 * The main functionality includes:
  - Generating a random page request sequence using the `generateRandomSequence` method.
  - Creating a modified sequence based on the given input using the `generateH` method.
  - Adding noise to the modified sequence with the `addNoise` method.
  - Simulating a cache with a modified page eviction algorithm using the `blindOracle` method.

 * The class also contains helper methods:
  - `chooseRandomPage`: Helper method for randomly selecting a page from a set.
  - `findPageToRemove`: Helper method for finding a page to evict in case of a cache miss.

 * The main method demonstrates the usage of these functionalities with example parameters and prints the generated
 * sequences along with the number of page faults in the blind replacement simulation.


### Compilation Command:

### Compiler Version:

The code was compiled using OpenJDK 21.0.2. Ensure you have the appropriate version installed to successfully compile and run the program.

Make sure you are in folder java, use the following command:

```bash
cd src/main/java
```

To compile the code, use the following command:

```bash
javac org/example/Combined.java
```

### Execution Command:

To run the compiled program, use the following command:

```bash
java org/example/Combined
```


