from math import sqrt


class Teacher:
    def __init__(self, index, learnIndex):
        self.index = index
        self.learnIndex = learnIndex
        self.maxByCommit = 0

    def prediction(self, time):
        self.maxByCommit = 0
        predict = ["", -1]
        if time not in self.learnIndex:
            return
        commit, deleted = self.learnIndex[time]
        for file in commit:
            temp = self.bin_search(self.index[file]["count"], int(time))
            if temp + 1 > self.maxByCommit:
                self.maxByCommit = temp
        for file in commit:
            filePredict = self.prediction_for_file(file, time)
            if filePredict[1] > predict[1]:
                predict = filePredict
        print("LOG for commit: ", *predict, "   Deleted:", deleted)
        return [predict[0], deleted]

    def prediction_for_file(self, file, time):
        predict = ["", -1]
        for fileB, times in self.index[file].items():
            if fileB != "count":
                curPredict = self.p(file, fileB, int(time))
                if curPredict > predict[1]:
                    predict = [fileB, curPredict]
        return predict

    def bin_search(self, a, time):
        l = -1
        r = len(a)
        while r - l > 1:
            m = (l + r) // 2
            if a[m] <= time:
                l = m
            else:
                r = m
        return l

    def count_by_time(self, array, time):
        return self.bin_search(array, time) + 1

    def p(self, fileA, fileB, time):
        intersection = self.count_by_time(self.index[fileA][fileB], time)
        A = self.count_by_time(self.index[fileA]["count"], time)
        B = self.count_by_time(self.index[fileB]["count"], time)
        union = A + B - intersection
        return (intersection * sqrt(A * B)) / (union * max(4, self.maxByCommit))
