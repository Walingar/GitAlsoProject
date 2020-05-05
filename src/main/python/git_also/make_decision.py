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


week = 604800


def get_time_rate(current_time, start_time):
    parameter = sqrt((start_time - current_time) // week)
    if parameter == 0:
        return sqrt(week)
    return 1 / parameter


def get_rate_for_times(times, start_time):
    ans = 1
    for time in times:
        if time >= start_time:
            break
        ans += get_time_rate(time, start_time)
    return ans


def get_probability_with_time(index, first_file, second_file, time, n, max_by_commit):
    intersection = get_rate_for_times(index[first_file][second_file], time)
    A = get_rate_for_times(index[first_file]["count"], time)
    B = get_rate_for_times(index[second_file]["count"], time)
    union = A + B - intersection
    return (intersection * sqrt(A * B)) / (union * max(n, max_by_commit))


def get_probability(index, first_file, second_file, time, n, max_by_commit):
    intersection = bin_search(index[first_file][second_file], time) + 1
    A = bin_search(index[first_file]["count"], time) + 1
    B = bin_search(index[second_file]["count"], time) + 1
    union = A + B - intersection
    return (intersection * sqrt(A * B)) / (union * max(n, max_by_commit))
