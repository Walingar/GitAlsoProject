import json


class LearnIndexGetter:
    def __init__(self, name="", fileIndexName=""):
        self.index = {}
        self.indexJSON = open("resources/indexes/" + name + "/" + fileIndexName)
        print("Log with indexes has gotten")

    def get(self):
        self.index = json.load(self.indexJSON)
        print("Indexes have gotten")
        return self.index
