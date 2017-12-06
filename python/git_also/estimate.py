from git_also.make_decision import bin_search
from git_also.evaluate import Evaluator
from git_also.memoize import memoize
from random import randrange as rand
from collections import Counter
from git_also.table_create import *


class Estimator:
    def __init__(self, index, dataset, get_probability):
        """

        :param index: format: {first_file: {second_file: [meet_time]}}
        :param dataset: [{files in commit}, time, {remote files}]
        :param get_probability: decision f(first_file, second_file, current_time, N, max_by_commit)
        """
        self.index = index
        self.dataset = dataset
        self.get_probability = get_probability
        self.extreme_prob = 0.1
        self.log = ""

    def predict_for_dataset(self, evaluator, n, min_prob):
        """

        :param evaluator: class, which can give scores
        :param n: parameter for decision function
        :param min_prob: parameter for decision function
        :return: tuple of set([commit, predicted files]) and Counter for types of prediction
        """
        types = Counter()
        predicted = []
        for commit in self.dataset:
            predict = self._predict(commit[0], commit[1], n)
            ans_predict = []
            for file in predict:
                if file[1] > min_prob / 100:
                    ans_predict.append(file)
            predicted.append([commit, ans_predict])
            evaluator.update_counter(commit[2], ans_predict, types)
        if min_prob % 10 == 0:
            self.log += create_confusion_matrix(min_prob, n, types)
            create_html_file("log", self.log)
        return tuple([predicted, types])

    @memoize
    def _predict(self, files, time, n):
        """

        :param files: list of files which are in commit
        :param time: current time of commit
        :param n: parameter for decision function
        :return list of predicted files for commit
        """
        max_by_commit = 0
        predict = []
        for file in files:
            temp = bin_search(self.index[file]["count"], time)
            if temp >= max_by_commit:
                max_by_commit = temp

        for file in files:
            file_predict = self._prediction_for_file(file, time, n, max_by_commit)
            for file_to_add in file_predict:
                if file_to_add[0] not in files:
                    predict.append(file_to_add)
        return predict

    @memoize
    def _prediction_for_file(self, first_file, time, n, max_by_commit):
        """

        :param first_file: file fow which we are looking for predicted files
        :param time: current time of commit
        :param n: parameter for decision function
        :param max_by_commit: parameter for decision function
        :return: list of predicted files for first_file
        """
        predict = []
        for second_file, times in self.index[first_file].items():
            if second_file != "count":
                prob = self.get_probability(self.index,
                                            first_file,
                                            second_file,
                                            time, n, max_by_commit)
                if prob > self.extreme_prob:
                    predict.append(tuple([second_file, prob]))
        return predict

    def _full_search(self, evaluator, n_max, min_prob_max):
        max_scores = -1
        ans_for_n_and_min_prob = (1, 0)

        for n in range(1, n_max):
            for min_prob in range(0, min_prob_max):
                scores = self.predict_for_dataset(evaluator, n, min_prob)
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
        number_of_points = max(10, (n_max - n_min) // 100)
        prev_scores = max_scores

        for point in range(number_of_points):
            n = rand(n_min, n_max)
            for min_prob in range(0, min_prob_max):
                predict = self.predict_for_dataset(evaluator, n, min_prob)
                scores = evaluator.get_score(predict[1])
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
        evaluator = Evaluator(
            {
                "score(A, A)": 1,
                "score(A, B)": -1,
                "score('', '')": 0.3,
                "score('', A)": 0.1,
                "score(A, '')": 0.1
            }
        )
        n_max = 10000
        min_prob_max = 100
        return self._hill_climb(evaluator, 1, n_max, min_prob_max, -1, ("", 0))
