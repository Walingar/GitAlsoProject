from math import sqrt
import functools
from git_also import evaluate


class Estimator:
    def __init__(self, index, dataset):
        self.index = index
        self.dataset = dataset

    def _memoize(func):
        cache = func.cache = {}

        @functools.wraps(func)
        def memoized_func(*args, **kwargs):
            key = str(args) + str(kwargs)
            if key not in cache:
                cache[key] = func(*args, **kwargs)
            return cache[key]

        return memoized_func

    @staticmethod
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

    @_memoize
    def get_probability(self, first_file, second_file, time, n, max_by_commit):
        intersection = self.bin_search(self.index[first_file][second_file], time) + 1
        A = self.bin_search(self.index[first_file]["count"], time) + 1
        B = self.bin_search(self.index[second_file]["count"], time) + 1
        union = A + B - intersection
        return (intersection * sqrt(A * B)) / (union * max(n, max_by_commit))

    @_memoize
    def predict(self, files, time, n):
        max_by_commit = 0
        predict = ["", -1]
        for file in files:
            temp = self.bin_search(self.index[file]["count"], time)
            if temp >= max_by_commit:
                max_by_commit = temp
        for file in files:
            file_predict = self.prediction_for_file(file, time, n, max_by_commit)
            if file_predict[1] > predict[1]:
                predict = file_predict
        return predict

    @_memoize
    def prediction_for_file(self, first_file, time, n, max_by_commit):
        predict = ["", -1]
        for second_file, times in self.index[first_file].items():
            if second_file != "count":
                prob = self.get_probability(first_file, second_file, time, n, max_by_commit)
                if prob > predict[1]:
                    predict = [second_file, prob]
        return predict

    def fit(self):
        evaluator = evaluate.Evaluator(
            [
                1,
                0.8,
                0.4,
                0.2,
                -0.2
            ]
        )
        max_scores = -1
        ans = [1, 0]
        for n in range(16, 50):
            for min_prob in range(0, 100):
                scores = 0
                for commit in self.dataset:
                    predict = self.predict(commit[0], commit[1], n)
                    if predict[1] < min_prob / 100:
                        predict = ["", -1]
                    scores += evaluator.get_score(predict, commit[2])[1][0]
                if scores > max_scores:
                    max_scores = scores
                    ans = [n, min_prob]
                print("-" * 10)
                print(n, min_prob, scores)
        print(ans)

        return [max_scores, ans]
