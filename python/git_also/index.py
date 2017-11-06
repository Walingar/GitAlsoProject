import git
import json


def _print_number_of_completed(counter, periodicity=100):
    if int(counter / periodicity) == counter / periodicity:
        print(counter)
    counter += 1
    return counter


def _ensure_file_count(index, first_file, date):
    index.setdefault(first_file, {"count": []})
    index[first_file]["count"].append(date)


def _add_files_to_indexes(index, first_file, second_file, date):
    index \
        .setdefault(first_file, {"count": []}) \
        .setdefault(second_file, [])
    index[first_file][second_file].append(date)


def create_index(repository_name):
    index = {}
    repository = git.Repo("data/repositories/" + repository_name)
    print("Repository has gotten")
    counter = 0
    for commit in list(repository.iter_commits()):
        files = list(commit.stats.files)
        counter = _print_number_of_completed(counter)
        while len(files) != 0:
            first_file = files.pop()
            _ensure_file_count(index, first_file, commit.committed_date)
            for second_file in files:
                _add_files_to_indexes(index, first_file, second_file, commit.committed_date)
                _add_files_to_indexes(index, second_file, first_file, commit.committed_date)
    for first_file, files in index.items():
        for second_file, times in files.items():
            times.sort()
    return index


def print_index(index, repository_name=""):
    with open("data/index/" + repository_name + "/index.json", 'w') as out:
        print(json.dumps(index, indent=4), file=out)


def get_index(repository_name):
    file_with_index = open("data/index/" + repository_name + "/index.json")
    print("Log with index has gotten")
    index = json.load(file_with_index)
    index_answer = {}
    for first_file, files in index.items():
        if first_file[-2:] == "py":
            index_answer[first_file] = {}
            for second_file, times in files.items():
                if second_file[-2:] == "py" or second_file == "count":
                    index_answer[first_file][second_file] = times

    return index_answer
