import git
import json


class IndexCreator:
    def __init__(self, repositoryName=""):
        self.repositoryName = repositoryName
        self.index = {}
        self.repository = git.Repo("resources/repositories/" + repositoryName)
        print("Repository has gotten")

    def ensure_file_count(self, fileA, date):
        self.index \
            .setdefault(fileA, {"count": []})
        self.index[fileA]["count"].append(date)

    def add_files_to_indexes(self, fileA, fileB, date):
        self.index \
            .setdefault(fileA, {"count": []}) \
            .setdefault(fileB, [])
        self.index[fileA][fileB].append(date)

    def get_files(self):
        i = 0
        for commit in list(self.repository.iter_commits()):
            files = list(commit.stats.files)
            i += 1
            if int(i / 100) == i / 100:
                print(i)
            while len(files) != 0:
                fileA = files.pop()
                self.ensure_file_count(fileA, commit.committed_date)
                for fileB in files:
                    self.add_files_to_indexes(fileA, fileB, commit.committed_date)
                    self.add_files_to_indexes(fileB, fileA, commit.committed_date)
        for file, files in self.index.items():
            for fileB, times in files.items():
                times.sort()

    def print(self, fileName):
        with open("resources/indexes/" + self.repositoryName + "/" + fileName, 'w') as out:
            print(json.dumps(self.index, indent=4), file=out)
