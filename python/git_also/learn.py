from mpl_toolkits.mplot3d import axes3d
import matplotlib.pyplot as plt
import numpy as np


class Teacher:
    def __init__(self, index, data_set, get_probability):
        self.index = index
        self.data_set = data_set
        self.get_probability = get_probability

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

    def get_scores(self, n, min_probability):
        scores = 0
        points_for_silence = 0
        points_for_correct_prediction = 1
        points_for_incorrect_prediction = 0
        for commit in self.data_set:
            max_by_commit = 0
            predict = ["", -1]
            files = commit[0]
            time = commit[1]
            remote_files = commit[2]
            for file in files:
                temp = self.bin_search(self.index[file]["count"], time)
                if temp >= max_by_commit:
                    max_by_commit = temp
            for file in files:
                file_predict = self.prediction_for_file(file, time, n, max_by_commit)
                if file_predict[1] > predict[1] and file_predict[1] >= min_probability:
                    predict = file_predict
            # print("LOG: Deleted:", *remote_files, " prediction: ", predict, end=" ")

            if predict[0] == '':
                scores += points_for_silence
                # print("So so")
            elif predict[0] in remote_files:
                scores += points_for_correct_prediction
                # print("OK!")
            elif predict[0] not in remote_files:
                scores += points_for_incorrect_prediction
                # print("not OK(")

        return scores / len(self.data_set)

    def learn(self):
        max_scores = -1
        ans = [1, 0]
        plot = []
        for n in range(1, 10):
            for min_prob in range(0, 10):
                scores = self.get_scores(n, min_prob / 10)
                if scores > max_scores:
                    max_scores = scores
                    ans = [n, min_prob]
                print("-" * 10)
                print(n, min_prob)
                plot.append([n, min_prob, scores])

        return [max_scores, ans, plot]

    def prediction_for_file(self, first_file, time, n, max_by_commit):
        predict = ["", -1]
        for second_file, times in self.index[first_file].items():
            if second_file != "count":
                prob = self.get_probability(self.index, first_file, second_file, time, n, max_by_commit)
                if prob > predict[1]:
                    predict = [second_file, prob]
        return predict
