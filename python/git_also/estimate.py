from git_also.make_decision import bin_search
from git_also.make_decision import get_probability
from git_also import evaluate
from git_also import memoize
from random import randrange as rand


class Estimator:
    def __init__(self, index, dataset):
        self.index = index
        self.dataset = dataset

    def _predict_for_dataset(self, evaluator, n, min_prob):
        scores = 0
        for commit in self.dataset:
            predict = self.predict(commit[0], commit[1], n)
            if predict[1] < min_prob / 100:
                predict = ("", -1)
            scores += evaluator.get_score(predict, commit[2])[1][0]
        return scores

    @memoize.memoize
    def predict(self, files, time, n):
        max_by_commit = 0
        predict = ["", -1]
        for file in files:
            temp = bin_search(self.index[file]["count"], time)
            if temp >= max_by_commit:
                max_by_commit = temp
        for file in files:
            file_predict = self._prediction_for_file(file, time, n, max_by_commit)
            if file_predict[1] > predict[1]:
                predict = file_predict
        return predict

    @memoize.memoize
    def _prediction_for_file(self, first_file, time, n, max_by_commit):
        predict = ["", -1]
        for second_file, times in self.index[first_file].items():
            if second_file != "count":
                prob = get_probability(self.index[first_file]["count"],
                                       self.index[second_file]["count"],
                                       self.index[first_file][second_file],
                                       time, n, max_by_commit)
                if prob > predict[1]:
                    predict = [second_file, prob]
        return predict

    def _full_search(self, evaluator, n_max, min_prob_max):
        max_scores = -1
        ans_for_n_and_min_prob = (1, 0)

        for n in range(1, n_max):
            for min_prob in range(0, min_prob_max):
                scores = self._predict_for_dataset(evaluator, n, min_prob)
                print("-" * 10)
                print(n, min_prob, scores)
                if scores > max_scores:
                    max_scores = scores
                    ans_for_n_and_min_prob = (n, min_prob)
        print(ans_for_n_and_min_prob)
        return tuple([max_scores, ans_for_n_and_min_prob])

    def _hill_climb(self, evaluator, n_min, n_max, min_prob_max, max_scores, ans_for_n_and_min_prob, height=0):
        if abs(n_min - n_max) <= 2 or height >= 20:
            return tuple([max_scores, ans_for_n_and_min_prob])
        number_of_points = rand(1, 10)
        prev_scores = max_scores

        for i in range(number_of_points):
            n = rand(n_min, n_max)
            for min_prob in range(0, min_prob_max):
                scores = self._predict_for_dataset(evaluator, n, min_prob)
                print("-" * 10)
                print(n, min_prob, scores)
                if scores > max_scores:
                    max_scores = scores
                    ans_for_n_and_min_prob = (n, min_prob)
        if prev_scores == scores:
            return self._hill_climb(evaluator, 1, 10000, 100, max_scores, ans_for_n_and_min_prob, height + 1)
        mid = (n_min + n_max) // 2
        return self._hill_climb(evaluator,
                                max(1, ans_for_n_and_min_prob[1] - mid),
                                min(10000, ans_for_n_and_min_prob[1] + mid),
                                100, max_scores, ans_for_n_and_min_prob, height + 1)

    def fit(self):
        evaluator = evaluate.Evaluator(
            [
                1,
                0.3,
                0.1,
                0.1,
                -0.2
            ]
        )
        n_max = 10000
        min_prob_max = 100
        return self._hill_climb(evaluator, 1, n_max, min_prob_max, -1, ("", 0))
