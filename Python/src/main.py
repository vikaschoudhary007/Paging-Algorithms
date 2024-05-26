import math
import random

# importing package
import matplotlib.pyplot as plt
from multiprocessing import Process


# Generate Random Sequence
def generateRandomSequence(k: int, N: int, n: int, e: float) -> list[int]:
    """
        Generates a random sequence of length n with k distinct elements and probability e of repetition.

        Parameters:
            k (int): Number of distinct elements.
            N (int): Upper bound for element values.
            n (int): Length of the sequence.
            e (float): Probability of repeating an element.

        Returns:
            list[int]: Randomly generated sequence.
    """
    ans = []
    L = []
    Y = []
    for i in range(k):
        ans.append(i + 1)
        L.append(i + 1)

    for i in range(k, N):
        Y.append(i + 1)

    for i in range(k, n):
        # print(L)
        prob = random.random()
        ele_x = random.choice(L)
        ele_y = random.choice(Y)
        if (prob <= e):
            ans.append(ele_x)
        else:
            ans.append(ele_y)
            L.remove(ele_x)
            L.append(ele_y)
            Y.remove(ele_y)
            Y.append(ele_x)
    return ans


def generateH(seq: list) -> list:
    """
        Generates the h-sequence from the given sequence.

        Parameters:
            seq (list): Input sequence.

        Returns:
            list[int]: Corresponding h-sequence.
    """
    hseq = []
    for i in range(0, len(seq)):
        ele = seq[i]
        add = len(seq) + 1
        for j in range(i + 1, len(seq)):
            # print(j)
            if (ele == seq[j]):
                add = j + 1
                break
        hseq.append(add)
    return hseq


def addNoise(hseq, t, w) -> list:
    """
        Introduces noise in the h-sequence.

        Parameters:
            hseq (list): Input h-sequence.
            t (float): Probability of noise addition.
            w (int): Width of the noise window.

        Returns:
            list[int]: Modified h-sequence with noise.
    """
    hseq1 = []

    for i in range(0, len(hseq)):
        prob = random.random()
        ele = hseq[i]
        if (prob <= t):
            ele = int(
                random.uniform(max(i + 1 + 1, ele - math.floor(w / 2)), max(i + 1 + 1, ele - math.floor(w / 2)) + w))
        hseq1.append(ele)

    return hseq1


def blindOracle(k, seq, hseq) -> int:
    """
        Simulates the blind oracle cache.

        Parameters:
            k (int): Cache size.
            seq (list): Input sequence.
            hseq (list): Corresponding h-sequence.

        Returns:
            int: Number of page faults.
    """
    cache = []
    cacheH = []
    pageFaults = 0
    for i in range(0, len(seq)):
        if (i < k):
            pageFaults += 1
            cache.append(seq[i])
            cacheH.append(hseq[i])
        else:
            try:
                valueIndex = cache.index(seq[i])
            except:
                valueIndex = -1
            if (valueIndex == -1):
                maxValue = max(cacheH)
                index = cacheH.index(maxValue)
                cache[index] = seq[i]
                cacheH[index] = hseq[i]
                pageFaults += 1
            else:
                cacheH[valueIndex] = hseq[i]

    return pageFaults


def LRU(k, seq) -> int:
    """
        Simulates the LRU cache.

        Parameters:
            k (int): Cache size.
            seq (list): Input sequence.

        Returns:
            int: Number of page faults.
    """
    pageFaults = 0
    cache = []
    cacheH = []
    for i in range(0, len(seq)):
        if (i < k):
            pageFaults += 1
            cache.append(seq[i])
            cacheH.append(i)
        else:
            try:
                valueIndex = cache.index(seq[i])
            except:
                valueIndex = -1
            if (valueIndex == -1):
                minValue = min(cacheH)
                index = cacheH.index(minValue)
                cache[index] = seq[i]
                cacheH[index] = i
                pageFaults += 1
            else:
                cacheH[valueIndex] = i

    # print(cache)
    return pageFaults


def combinedAlg(k, seq, hseq, thr) -> int:
    """
        Simulates the Combined cache.

        Parameters:
            k (int): Cache size.
            seq (list): Input sequence.
            hseq (list): Corresponding h-sequence.
            thr (double): threshold for switch

        Returns:
            int: Number of page faults.
    """
    cache = []
    pageFaultsCombined = 0

    cacheBlind = []
    cacheH = []
    pageFaultsBlind = 0

    cacheLRU = []
    cacheHLRU = []
    pageFaultsLRU = 0

    isLRU = True
    for i in range(0, len(seq)):
        if (i < k):
            pageFaultsBlind += 1
            pageFaultsLRU += 1
            cacheBlind.append(seq[i])
            cacheLRU.append(seq[i])
            cacheH.append(hseq[i])
            cacheHLRU.append(i)
            cache.append(i)
            pageFaultsCombined += 1
        else:

            # print(str(isLRU) + " cache "+ str(cache)+ " LruFault "+str(pageFaultsLRU)+" BlindFault "+str(pageFaultsBlind)+" Combined "+str(pageFaultsCombined))

            # print("pageFaultsLRU: "+str(pageFaultsLRU)+" pageFaultsBlind: "+str(pageFaultsBlind))

            # print(str(isLRU)+" "+str(pageFaultsLRU) +" "+ str(pageFaultsBlind))
            if (isLRU and (pageFaultsLRU > (1 + thr) * pageFaultsBlind)):
                # print("1")
                pageFaultsCombined += k
                isLRU = False
                cache = cacheBlind.copy()

            if ((not isLRU) and (pageFaultsBlind > (1 + thr) * pageFaultsLRU)):
                # print("2")
                pageFaultsCombined += k
                isLRU = True
                cache = cacheLRU.copy()

            try:
                valueIndexBlind = cacheBlind.index(seq[i])
            except:
                valueIndexBlind = -1

            if (valueIndexBlind == -1):
                maxValue = max(cacheH)
                index = cacheH.index(maxValue)
                cacheBlind[index] = seq[i]
                cacheH[index] = hseq[i]
                pageFaultsBlind += 1

                if (not isLRU):
                    cache = cacheBlind.copy()
                    pageFaultsCombined += 1


            else:
                cacheH[valueIndexBlind] = hseq[i]

            try:
                valueIndexLRU = cacheLRU.index(seq[i])
            except:
                valueIndexLRU = -1

            if (valueIndexLRU == -1):
                minValue = min(cacheHLRU)
                index = cacheHLRU.index(minValue)
                cacheLRU[index] = seq[i]
                cacheHLRU[index] = i
                pageFaultsLRU += 1
                if (isLRU):
                    cache = cacheLRU.copy()
                    pageFaultsCombined += 1
            else:
                cacheHLRU[valueIndexLRU] = i

    # print("cal Blind " + str(pageFaultsBlind))
    # print("cal LRU " + str(pageFaultsLRU))
    # print("cal Combined" + str(pageFaultsCombined))
    return pageFaultsCombined




def test13():
    # Varying K
    # LRU > BlindOracle > OPT
    # Regime 1

    k = [5, 10, 15, 20, 25, 30]
    N = [50, 100, 150, 200, 250, 300]
    n = 10000
    e = 0.5
    t = 0.5
    w = 200
    thr = 0.15

    numOpt = []
    numBlindOracle = []
    numLRU = []
    numCombined = []

    trials = 100
    for j in range(0, len(k)):
        print(j)
        avgPageFaultsOpt = 0
        avgPageFaultsBlindOracle = 0
        avgPageFaultsLRU = 0
        avgPageFaultsCombined = 0
        for i in range(0, trials):
            seq = generateRandomSequence(k[j], N[j], n, e)
            hseq = generateH(seq)
            # print(hseq)
            nseq = addNoise(hseq, t, w)
            # print(nseq)

            pageFaultsOpt = blindOracle(k[j], seq, hseq)
            avgPageFaultsOpt += pageFaultsOpt
            pageFaultsBlindOracle = blindOracle(k[j], seq, nseq)
            avgPageFaultsBlindOracle += pageFaultsBlindOracle
            pageFaultsLRU = LRU(k[j], seq)
            avgPageFaultsLRU += pageFaultsLRU
            pageFaultsCombined = combinedAlg(k[j], seq, nseq, thr)
            avgPageFaultsCombined += pageFaultsCombined
        numOpt.append(avgPageFaultsOpt / trials)
        numBlindOracle.append(avgPageFaultsBlindOracle / trials)
        numLRU.append(avgPageFaultsLRU / trials)
        numCombined.append(avgPageFaultsCombined / trials)

    plt.title(label='Pagefaults vs k')
    plt.ylabel('Pagefaults')
    plt.xlabel('K')
    plt.plot(k, numOpt, marker='o', label="OPT")
    plt.plot(k, numLRU, marker='o', label="LRU")
    plt.plot(k, numBlindOracle, marker='o', label="BO")
    plt.plot(k, numCombined, marker='o', label="COM")

    plt.legend()
    plt.show()

    print("pageFaultsOpt: " + str(avgPageFaultsOpt / trials) + " pageFaultsBlindOracle: " + str(
        avgPageFaultsBlindOracle / trials) + " pageFaultsLRU: " + str(
        avgPageFaultsLRU / trials) + " pageFaultsCombined: " + str(avgPageFaultsCombined / trials))



def test14():
    # Varying K
    # BlindOracle > LRU > OPT
    # Regime 2

    k = [5, 10, 15, 20, 25, 30]
    N = [50, 100, 150, 200, 250, 300]
    n = 10000
    e = 0.6
    t = 0.7
    w = 1000
    thr = 0.15

    numOpt = []
    numBlindOracle = []
    numLRU = []
    numCombined = []

    trials = 100
    for j in range(0, len(k)):
        print(j)
        avgPageFaultsOpt = 0
        avgPageFaultsBlindOracle = 0
        avgPageFaultsLRU = 0
        avgPageFaultsCombined = 0
        for i in range(0, trials):
            seq = generateRandomSequence(k[j], N[j], n, e)
            hseq = generateH(seq)
            # print(hseq)
            nseq = addNoise(hseq, t, w)
            # print(nseq)

            pageFaultsOpt = blindOracle(k[j], seq, hseq)
            avgPageFaultsOpt += pageFaultsOpt
            pageFaultsBlindOracle = blindOracle(k[j], seq, nseq)
            avgPageFaultsBlindOracle += pageFaultsBlindOracle
            pageFaultsLRU = LRU(k[j], seq)
            avgPageFaultsLRU += pageFaultsLRU
            pageFaultsCombined = combinedAlg(k[j], seq, nseq, thr)
            avgPageFaultsCombined += pageFaultsCombined
        numOpt.append(avgPageFaultsOpt / trials)
        numBlindOracle.append(avgPageFaultsBlindOracle / trials)
        numLRU.append(avgPageFaultsLRU / trials)
        numCombined.append(avgPageFaultsCombined / trials)

    plt.title(label='Pagefaults vs k')
    plt.ylabel('Pagefaults')
    plt.xlabel('K')
    plt.plot(k, numOpt, marker='o', label="OPT")
    plt.plot(k, numLRU, marker='o', label="LRU")
    plt.plot(k, numBlindOracle, marker='o', label="BO")
    plt.plot(k, numCombined, marker='o', label="COM")

    plt.legend()
    plt.show()

    print("pageFaultsOpt: " + str(avgPageFaultsOpt / trials) + " pageFaultsBlindOracle: " + str(
        avgPageFaultsBlindOracle / trials) + " pageFaultsLRU: " + str(
        avgPageFaultsLRU / trials) + " pageFaultsCombined: " + str(avgPageFaultsCombined / trials))



def test15():
    # Varying w
    # BlindOracle > LRU > OPT
    # Regime 2

    k = 20
    N = 200
    n = 10000
    e = 0.6
    t = 0.7
    w = [10,200,300,500,700,1000]
    thr = 0.15

    numOpt = []
    numBlindOracle = []
    numLRU = []
    numCombined = []

    trials = 100
    for j in range(0, len(w)):
        print(j)
        avgPageFaultsOpt = 0
        avgPageFaultsBlindOracle = 0
        avgPageFaultsLRU = 0
        avgPageFaultsCombined = 0
        for i in range(0, trials):
            seq = generateRandomSequence(k, N, n, e)
            hseq = generateH(seq)
            # print(hseq)
            nseq = addNoise(hseq, t, w[j])
            # print(nseq)

            pageFaultsOpt = blindOracle(k, seq, hseq)
            avgPageFaultsOpt += pageFaultsOpt
            pageFaultsBlindOracle = blindOracle(k, seq, nseq)
            avgPageFaultsBlindOracle += pageFaultsBlindOracle
            pageFaultsLRU = LRU(k, seq)
            avgPageFaultsLRU += pageFaultsLRU
            pageFaultsCombined = combinedAlg(k, seq, nseq, thr)
            avgPageFaultsCombined += pageFaultsCombined
        numOpt.append(avgPageFaultsOpt / trials)
        numBlindOracle.append(avgPageFaultsBlindOracle / trials)
        numLRU.append(avgPageFaultsLRU / trials)
        numCombined.append(avgPageFaultsCombined / trials)

    plt.title(label='Pagefaults vs W')
    plt.ylabel('Pagefaults')
    plt.xlabel('W')
    plt.plot(w, numOpt, marker='o', label="OPT")
    plt.plot(w, numLRU, marker='o', label="LRU")
    plt.plot(w, numBlindOracle, marker='o', label="BO")
    plt.plot(w, numCombined, marker='o', label="COM")

    plt.legend()
    plt.show()

    print("pageFaultsOpt: " + str(avgPageFaultsOpt / trials) + " pageFaultsBlindOracle: " + str(
        avgPageFaultsBlindOracle / trials) + " pageFaultsLRU: " + str(
        avgPageFaultsLRU / trials) + " pageFaultsCombined: " + str(avgPageFaultsCombined / trials))


def test16():
    # Varying w
    # LRU > BlindOracle > OPT
    # Regime 1

    k = 20
    N = 200
    n = 10000
    e = 0.5
    t = 0.5
    w = [10,200,300,500,700,1000]
    thr = 0.15

    numOpt = []
    numBlindOracle = []
    numLRU = []
    numCombined = []

    trials = 100
    for j in range(0, len(w)):
        print(j)
        avgPageFaultsOpt = 0
        avgPageFaultsBlindOracle = 0
        avgPageFaultsLRU = 0
        avgPageFaultsCombined = 0
        for i in range(0, trials):
            seq = generateRandomSequence(k, N, n, e)
            hseq = generateH(seq)
            # print(hseq)
            nseq = addNoise(hseq, t, w[j])
            # print(nseq)

            pageFaultsOpt = blindOracle(k, seq, hseq)
            avgPageFaultsOpt += pageFaultsOpt
            pageFaultsBlindOracle = blindOracle(k, seq, nseq)
            avgPageFaultsBlindOracle += pageFaultsBlindOracle
            pageFaultsLRU = LRU(k, seq)
            avgPageFaultsLRU += pageFaultsLRU
            pageFaultsCombined = combinedAlg(k, seq, nseq, thr)
            avgPageFaultsCombined += pageFaultsCombined
        numOpt.append(avgPageFaultsOpt / trials)
        numBlindOracle.append(avgPageFaultsBlindOracle / trials)
        numLRU.append(avgPageFaultsLRU / trials)
        numCombined.append(avgPageFaultsCombined / trials)

    plt.title(label='Pagefaults vs W')
    plt.ylabel('Pagefaults')
    plt.xlabel('W')
    plt.plot(w, numOpt, marker='o', label="OPT")
    plt.plot(w, numLRU, marker='o', label="LRU")
    plt.plot(w, numBlindOracle, marker='o', label="BO")
    plt.plot(w, numCombined, marker='o', label="COM")

    plt.legend()
    plt.show()

    print("pageFaultsOpt: " + str(avgPageFaultsOpt / trials) + " pageFaultsBlindOracle: " + str(
        avgPageFaultsBlindOracle / trials) + " pageFaultsLRU: " + str(
        avgPageFaultsLRU / trials) + " pageFaultsCombined: " + str(avgPageFaultsCombined / trials))


def test17():
    # Varying e
    # LRU > BlindOracle > OPT
    # Regime 1

    k = 20
    N = 200
    n = 10000
    e = [0.2,0.3,0.5,0.7,0.8]
    t = 0.5
    w = 200
    thr = 0.15

    numOpt = []
    numBlindOracle = []
    numLRU = []
    numCombined = []

    trials = 100
    for j in range(0, len(e)):
        print(j)
        avgPageFaultsOpt = 0
        avgPageFaultsBlindOracle = 0
        avgPageFaultsLRU = 0
        avgPageFaultsCombined = 0
        for i in range(0, trials):
            seq = generateRandomSequence(k, N, n, e[j])
            hseq = generateH(seq)
            # print(hseq)
            nseq = addNoise(hseq, t, w)
            # print(nseq)

            pageFaultsOpt = blindOracle(k, seq, hseq)
            avgPageFaultsOpt += pageFaultsOpt
            pageFaultsBlindOracle = blindOracle(k, seq, nseq)
            avgPageFaultsBlindOracle += pageFaultsBlindOracle
            pageFaultsLRU = LRU(k, seq)
            avgPageFaultsLRU += pageFaultsLRU
            pageFaultsCombined = combinedAlg(k, seq, nseq, thr)
            avgPageFaultsCombined += pageFaultsCombined
        numOpt.append(avgPageFaultsOpt / trials)
        numBlindOracle.append(avgPageFaultsBlindOracle / trials)
        numLRU.append(avgPageFaultsLRU / trials)
        numCombined.append(avgPageFaultsCombined / trials)

    plt.title(label='Pagefaults vs e')
    plt.ylabel('Pagefaults')
    plt.xlabel('e')
    plt.plot(e, numOpt, marker='o', label="OPT")
    plt.plot(e, numLRU, marker='o', label="LRU")
    plt.plot(e, numBlindOracle, marker='o', label="BO")
    plt.plot(e, numCombined, marker='o', label="COM")

    plt.legend()
    plt.show()

    print("pageFaultsOpt: " + str(avgPageFaultsOpt / trials) + " pageFaultsBlindOracle: " + str(
        avgPageFaultsBlindOracle / trials) + " pageFaultsLRU: " + str(
        avgPageFaultsLRU / trials) + " pageFaultsCombined: " + str(avgPageFaultsCombined / trials))

def test18():
    # Varying e
    # BlindOracle > LRU > OPT
    # Regime 2

    k = 20
    N = 200
    n = 10000
    e = [0.2,0.3,0.5,0.7,0.8]
    t = 0.7
    w = 1000
    thr = 0.15

    numOpt = []
    numBlindOracle = []
    numLRU = []
    numCombined = []

    trials = 100
    for j in range(0, len(e)):
        print(j)
        avgPageFaultsOpt = 0
        avgPageFaultsBlindOracle = 0
        avgPageFaultsLRU = 0
        avgPageFaultsCombined = 0
        for i in range(0, trials):
            seq = generateRandomSequence(k, N, n, e[j])
            hseq = generateH(seq)
            # print(hseq)
            nseq = addNoise(hseq, t, w)
            # print(nseq)

            pageFaultsOpt = blindOracle(k, seq, hseq)
            avgPageFaultsOpt += pageFaultsOpt
            pageFaultsBlindOracle = blindOracle(k, seq, nseq)
            avgPageFaultsBlindOracle += pageFaultsBlindOracle
            pageFaultsLRU = LRU(k, seq)
            avgPageFaultsLRU += pageFaultsLRU
            pageFaultsCombined = combinedAlg(k, seq, nseq, thr)
            avgPageFaultsCombined += pageFaultsCombined
        numOpt.append(avgPageFaultsOpt / trials)
        numBlindOracle.append(avgPageFaultsBlindOracle / trials)
        numLRU.append(avgPageFaultsLRU / trials)
        numCombined.append(avgPageFaultsCombined / trials)

    plt.title(label='Pagefaults vs e')
    plt.ylabel('Pagefaults')
    plt.xlabel('e')
    plt.plot(e, numOpt, marker='o', label="OPT")
    plt.plot(e, numLRU, marker='o', label="LRU")
    plt.plot(e, numBlindOracle, marker='o', label="BO")
    plt.plot(e, numCombined, marker='o', label="COM")

    plt.legend()
    plt.show()

    print("pageFaultsOpt: " + str(avgPageFaultsOpt / trials) + " pageFaultsBlindOracle: " + str(
        avgPageFaultsBlindOracle / trials) + " pageFaultsLRU: " + str(
        avgPageFaultsLRU / trials) + " pageFaultsCombined: " + str(avgPageFaultsCombined / trials))


def test19():
    # Varying t
    # BlindOracle > LRU > OPT
    # Regime 2

    k = 20
    N = 200
    n = 10000
    e = 0.6
    t = [0.2,0.3,0.5,0.7,0.8]
    w = 1000
    thr = 0.15

    numOpt = []
    numBlindOracle = []
    numLRU = []
    numCombined = []

    trials = 100
    for j in range(0, len(t)):
        print(j)
        avgPageFaultsOpt = 0
        avgPageFaultsBlindOracle = 0
        avgPageFaultsLRU = 0
        avgPageFaultsCombined = 0
        for i in range(0, trials):
            seq = generateRandomSequence(k, N, n, e)
            hseq = generateH(seq)
            # print(hseq)
            nseq = addNoise(hseq, t[j], w)
            # print(nseq)

            pageFaultsOpt = blindOracle(k, seq, hseq)
            avgPageFaultsOpt += pageFaultsOpt
            pageFaultsBlindOracle = blindOracle(k, seq, nseq)
            avgPageFaultsBlindOracle += pageFaultsBlindOracle
            pageFaultsLRU = LRU(k, seq)
            avgPageFaultsLRU += pageFaultsLRU
            pageFaultsCombined = combinedAlg(k, seq, nseq, thr)
            avgPageFaultsCombined += pageFaultsCombined
        numOpt.append(avgPageFaultsOpt / trials)
        numBlindOracle.append(avgPageFaultsBlindOracle / trials)
        numLRU.append(avgPageFaultsLRU / trials)
        numCombined.append(avgPageFaultsCombined / trials)

    plt.title(label='Pagefaults vs t')
    plt.ylabel('Pagefaults')
    plt.xlabel('t')
    plt.plot(t, numOpt, marker='o', label="OPT")
    plt.plot(t, numLRU, marker='o', label="LRU")
    plt.plot(t, numBlindOracle, marker='o', label="BO")
    plt.plot(t, numCombined, marker='o', label="COM")

    plt.legend()
    plt.show()

    print("pageFaultsOpt: " + str(avgPageFaultsOpt / trials) + " pageFaultsBlindOracle: " + str(
        avgPageFaultsBlindOracle / trials) + " pageFaultsLRU: " + str(
        avgPageFaultsLRU / trials) + " pageFaultsCombined: " + str(avgPageFaultsCombined / trials))

def test20():
    # Varying t
    # LRU > BlindOracle > OPT
    # Regime 1

    k = 20
    N = 200
    n = 10000
    e = 0.5
    t = [0.2,0.3,0.5,0.7,0.8]
    w = 200
    thr = 0.15

    numOpt = []
    numBlindOracle = []
    numLRU = []
    numCombined = []

    trials = 100
    for j in range(0, len(t)):
        print(j)
        avgPageFaultsOpt = 0
        avgPageFaultsBlindOracle = 0
        avgPageFaultsLRU = 0
        avgPageFaultsCombined = 0
        for i in range(0, trials):
            seq = generateRandomSequence(k, N, n, e)
            hseq = generateH(seq)
            # print(hseq)
            nseq = addNoise(hseq, t[j], w)
            # print(nseq)

            pageFaultsOpt = blindOracle(k, seq, hseq)
            avgPageFaultsOpt += pageFaultsOpt
            pageFaultsBlindOracle = blindOracle(k, seq, nseq)
            avgPageFaultsBlindOracle += pageFaultsBlindOracle
            pageFaultsLRU = LRU(k, seq)
            avgPageFaultsLRU += pageFaultsLRU
            pageFaultsCombined = combinedAlg(k, seq, nseq, thr)
            avgPageFaultsCombined += pageFaultsCombined
        numOpt.append(avgPageFaultsOpt / trials)
        numBlindOracle.append(avgPageFaultsBlindOracle / trials)
        numLRU.append(avgPageFaultsLRU / trials)
        numCombined.append(avgPageFaultsCombined / trials)

    plt.title(label='Pagefaults vs t')
    plt.ylabel('Pagefaults')
    plt.xlabel('t')
    plt.plot(t, numOpt, marker='o', label="OPT")
    plt.plot(t, numLRU, marker='o', label="LRU")
    plt.plot(t, numBlindOracle, marker='o', label="BO")
    plt.plot(t, numCombined, marker='o', label="COM")

    plt.legend()
    plt.show()

    print("pageFaultsOpt: " + str(avgPageFaultsOpt / trials) + " pageFaultsBlindOracle: " + str(
        avgPageFaultsBlindOracle / trials) + " pageFaultsLRU: " + str(
        avgPageFaultsLRU / trials) + " pageFaultsCombined: " + str(avgPageFaultsCombined / trials))


# Press the green button in the gutter to run the script.
if __name__ == '__main__':

    processes = []

    try:
        # Trend 1
        # running on diffrent processes to save time
        processes.append(Process(target=test13, args=()))
        processes.append(Process(target=test14, args=()))


        # Trend 2
        # running on diffrent processes to save time
        processes.append(Process(target=test15, args=()))
        processes.append(Process(target=test16, args=()))


        # Trend 3
        # running on diffrent processes to save time
        processes.append(Process(target=test17, args=()))
        processes.append(Process(target=test18, args=()))

        # Trend 4
        # running on diffrent processes to save time
        processes.append(Process(target=test19, args=()))
        processes.append(Process(target=test20, args=()))

        for p in processes:
            p.start()
        
        for p in processes:
            p.join()


    except KeyboardInterrupt:
        print("KeyboardInterrupt detected! Terminating all processes...")
        for p in processes:
            p.terminate()
            p.join()
        print("All processes terminated.")

    # print("All test case passed")
