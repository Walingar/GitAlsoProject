from git_also import memoize
from math import sqrt


def bin_search(a, time):
    l = -1
    r = len(a)
    while r - l > 1:
        m = (l + r) // 2
        if a[m] <= time:
            l = m
        else:
            r = m
    return l


def get_probability(first_file_times, second_file_times, intersection_times, time, n, max_by_commit):
    intersection = bin_search(intersection_times, time) + 1
    A = bin_search(first_file_times, time) + 1
    B = bin_search(second_file_times, time) + 1
    union = A + B - intersection
    return (intersection * sqrt(A * B)) / (union * max(n, max_by_commit))
