import json


class IndexGetter:
    def __init__(self, name="", fileIndexName=""):
        self.index = {}
        self.indexJSON = open("resources/indexes/" + name + "/" + fileIndexName)
        print("Log with indexes has gotten")

    def get(self):
        self.index = json.load(self.indexJSON)
        index = {}
        times = set()
        for key, i in self.index.items():
            for j in i["count"]:
                if j not in times:
                    times.add(j)
            if key[-2:] == "py":
                index[key] = {}
                for subkey, j in i.items():
                    if subkey[-2:] == "py" or subkey == "count":
                        index[key][subkey] = j
        print("Indexes have gotten")
        return [index, times]
