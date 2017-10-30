import json
from random import randrange as rand


class LearnIndexCreator:
    def __init__(self, name, index):
        self.startLearnTime = 0
        self.endLearnTime = 0
        self.index = index
        self.learnIndex = {}
        self.indexJSON = "resources/indexes/" + name + "/learnIndex.json"
        self.commits = {}

    def create(self, times, startLearnTime, endLearnTime):
        self.startLearnTime = startLearnTime  # 1483228800
        self.endLearnTime = endLearnTime  # 1488326400
        self.commits = self.create_commits(times)
        self.create_learn_index()
        self.print()

    def create_learn_index(self):
        commits = self.commits
        for key, value in commits.items():
            tempIndex = rand(len(value))
            temp = commits[key][tempIndex]
            self.learnIndex[key] = [list(filter(lambda x: x != temp, commits[key])), temp]

    def print(self):
        index = self.learnIndex
        with open(self.indexJSON, 'w') as out:
            print(json.dumps(index, indent=4), file=out)

    def create_commits(self, times):
        index = self.index
        commits = {}
        for i in times:
            if self.startLearnTime <= i <= self.endLearnTime:
                commits[i] = []
        for key, value in index.items():
            for j in index[key]["count"]:
                if j in commits:
                    commits[j].append(key)
        return dict(filter(lambda x: len(x[1]) >= 2, commits.items()))
