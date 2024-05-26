Cache Simulation with Blind Oracle README

This Python script simulates a cache mechanism with a blind oracle using random sequences and introduces noise. The cache mechanism is simulated to understand its behavior under various conditions.

Files:
- main.py: The main Python script containing the cache simulation functions and test cases.
- README.txt: This README file explaining the code and its usage.

Functions:
1. generateRandomSequence:
    - Generates a random sequence of specified length with a given number of distinct elements and probability of repetition.

2. generateH:
    - Generates the h-sequence from the given sequence. The h-sequence records the next occurrence index of each element in the sequence.

3. addNoise:
    - Introduces noise in the h-sequence with a specified probability and window width.

4. blindOracle:
    - Simulates the behavior of a cache mechanism with a blind oracle. It maintains a cache of a specified size and counts the number of page faults.

5. LRU:
    - Simulates the behavior of a cache mechanism with a LRU. It maintains a cache of a specified size and counts the number of page faults.

6. combinedAlg:
    - Simulates the behavior of a cache mechanism with a combination of LRU and blind oracle. It maintains a cache of a specified size and counts the number of page faults.

Test Cases:
- test1 to test11: Several test cases are provided to ensure the correctness of individual functions and the overall functionality.

Usage:
1. To run the script in python3 environment:
    - Open a terminal.
    - Navigate to the directory containing the script.
    - Run the command: python main.py
//////////////////////////////////
python main.py
//////////////////////////////////


2. Output:
    - The script will execute all test cases and print whether they pass or fail.
    - Additionally, it will print the number of page faults for various configurations of cache parameters.
/////////////////////////////////
Blind Oracle pageFaults for k:5 N:20 n:100 e:0.5 t:0.2 w:7 = 43
Blind Oracle pageFaults for k:5 N:6 n:100 e:0.5 t:0.2 w:7 = 14
LRU pageFaults for k:5 N:6 n:100 e:0.5 t:0.2 w:7 = 26
Combined pageFaults for k:5 N:6 n:100 e:0.5 t:0.2 w:7 = 19
All test case passed
////////////////////////////////


3. Generate Graphs:
    - test13 to test20 creates the graph for respective trend.

4. Generated Graphs:
    - Generated Graphs per Trend are in FIGURE folder.

Dependencies:
- Python 3.x
- math
- random
- matplotlib
