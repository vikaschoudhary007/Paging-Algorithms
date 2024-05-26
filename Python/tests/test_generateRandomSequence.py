import pytest

from src.main import (
    generateRandomSequence,
    generateH,
    blindOracle,
    LRU,
    addNoise,
    combinedAlg,
)


def test__generateRandomSequence():
    """Test case for generateRandomSequence function."""
    input = {"k": 4, "N": 5, "n": 5, "e": 1}
    seq = generateRandomSequence(input["k"], input["N"], input["n"], input["e"])
    assert len(seq) == input["n"], "Generated sequence length does not match"

    # Ensure the first k elements are from 1 to k
    for i in range(input["k"]):
        assert seq[i] == i + 1, "First k elements are not from 1 to k"


def test__generateH():
    """Test case for generateH function."""
    seq = [1, 2, 3, 4, 5, 1, 2, 3, 4, 1]
    h_seq = generateH(seq)
    # print(h_seq)

    assert len(h_seq) == len(seq), "Generated h_seq length does not match"

    # Ensure hi values are correct
    expected_h_seq = [6, 7, 8, 9, 11, 10, 11, 11, 11, 11]
    assert h_seq == expected_h_seq, "Generated h_seq values are incorrect"


def test__addNoise():
    """Test case for addNoise function."""
    h_seq = [1, 2, 3, 4, 5]
    tau = 0.3
    w = 2

    h_hat_seq = addNoise(h_seq, tau, w)

    assert len(h_hat_seq) == len(h_seq), "Generated h_hat_seq length does not match"

    # Ensure hi values are within the expected range
    for i in range(len(h_seq)):
        assert (
            max(i + 1, h_seq[i] - w // 2)
            <= h_hat_seq[i]
            <= max(i + 1, h_seq[i] - w // 2) + w
        ), "Generated h_hat_seq values are out of range"


def test__blindOracle():
    """Test case for blindOracle function."""
    k = 3
    seq = [1, 2, 3, 4, 1, 2, 5, 1, 2, 3]
    h_seq = generateH(seq)
    pageFaults = 6
    calPageFaults = blindOracle(k, seq, h_seq)
    assert pageFaults == calPageFaults, "BlindOracle page faults count is incorrect"


def test__overall_func1():
    """Test case for overall functionality."""
    k = 5
    N = 20  # N >> k
    n = 100
    e = 0.5
    t = 0.2
    w = 7

    seq = generateRandomSequence(k, N, n, e)
    # print(seq)

    hseq = generateH(seq)
    # print(hseq)

    nseq = addNoise(hseq, t, w)
    # print(nseq)

    pageFaults = blindOracle(k, seq, nseq)
    print(
        "Blind Oracle pageFaults for k:"
        + str(k)
        + " N:"
        + str(N)
        + " n:"
        + str(n)
        + " e:"
        + str(e)
        + " t:"
        + str(t)
        + " w:"
        + str(w)
        + " = "
        + str(pageFaults)
    )


def test__overall_func2():
    """Test case for overall functionality."""
    k = 5
    N = 6  # N >> k
    n = 100
    e = 0.5
    t = 0.2
    w = 7

    seq = generateRandomSequence(k, N, n, e)
    # print(seq)

    hseq = generateH(seq)
    # print(hseq)

    nseq = addNoise(hseq, t, w)
    # print(nseq)

    pageFaults = blindOracle(k, seq, nseq)
    print(
        "Blind Oracle pageFaults for k:"
        + str(k)
        + " N:"
        + str(N)
        + " n:"
        + str(n)
        + " e:"
        + str(e)
        + " t:"
        + str(t)
        + " w:"
        + str(w)
        + " = "
        + str(pageFaults)
    )


def test__LRU_1():
    """Test case for LRU function."""
    k = 4
    seq = [1, 2, 3, 4, 2, 1, 5, 6, 2, 1, 2, 3, 7, 6, 3, 2, 1, 2, 3, 6]
    pageFaults = 10
    calPageFaults = LRU(k, seq)
    # print(calPageFaults)
    assert pageFaults == calPageFaults, "LRU page faults count is incorrect"


def test__LRU_2():
    """Test case for LRU function."""
    k = 3
    seq = [1, 2, 3, 4, 1, 2, 3, 4, 1, 2, 3, 4]
    pageFaults = 12
    calPageFaults = LRU(k, seq)
    # print(calPageFaults)
    assert pageFaults == calPageFaults, "LRU page faults count is incorrect"


def test__combined():
    """Test case for Combined function."""
    k = 3
    seq = [1, 2, 3, 4, 1, 2, 5, 1, 2, 3]
    h_seq = generateH(seq)
    pageFaults = 10

    calPageFaults = combinedAlg(k, seq, h_seq, 0)

    assert pageFaults == calPageFaults, "Combined PageFaults are wrong"


def test__overall_LRU():
    """Test case for Overall LRU function."""
    k = 5
    N = 6  # N >> k
    n = 100
    e = 0.5
    t = 0.2
    w = 7

    seq = generateRandomSequence(k, N, n, e)
    # print(seq)

    hseq = generateH(seq)
    # print(hseq)

    nseq = addNoise(hseq, t, w)
    # print(nseq)

    pageFaults = LRU(k, seq)
    print(
        "LRU pageFaults for k:"
        + str(k)
        + " N:"
        + str(N)
        + " n:"
        + str(n)
        + " e:"
        + str(e)
        + " t:"
        + str(t)
        + " w:"
        + str(w)
        + " = "
        + str(pageFaults)
    )


def test__overall_combined():
    """Test case for Overall Combined function."""
    k = 5
    N = 6  # N >> k
    n = 100
    e = 0.5
    t = 0.2
    w = 7
    thr = 0

    seq = generateRandomSequence(k, N, n, e)
    # print(seq)

    hseq = generateH(seq)
    # print(hseq)

    nseq = addNoise(hseq, t, w)
    # print(nseq)

    pageFaults = combinedAlg(k, seq, nseq, 0)
    print(
        "Combined pageFaults for k:"
        + str(k)
        + " N:"
        + str(N)
        + " n:"
        + str(n)
        + " e:"
        + str(e)
        + " t:"
        + str(t)
        + " w:"
        + str(w)
        + " = "
        + str(pageFaults)
    )
