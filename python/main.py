from git_also import index
from git_also import data_set
from git_also import learn
from math import sqrt
import pylab
from mpl_toolkits.mplot3d import Axes3D
import numpy

start = 1483228800
end = 1488326400


def count_by_time(array, time):
    l = -1
    r = len(array)
    while r - l > 1:
        m = (l + r) // 2
        if array[m] <= time:
            l = m
        else:
            r = m
    return l + 1


def get_probability(index, first_file, second_file, time, n, max_by_commit):
    intersection = count_by_time(index[first_file][second_file], time)
    A = count_by_time(index[first_file]["count"], time)
    B = count_by_time(index[second_file]["count"], time)
    union = A + B - intersection
    return (intersection * sqrt(A * B)) / (union * max(n, max_by_commit))

def main():
    print("Started")
    pandas_index = index.get_index("pandas")
    ds = data_set.get_data_set("pandas")
    teach = learn.Teacher(pandas_index, ds, get_probability)

    print("Finished")


if __name__ == "__main__":
    main()
