import json
import git_also.git_log_parse as git


def _ensure_file_count(index, first_file, date):
    index.setdefault(first_file, {"count": []})
    index[first_file]["count"].append(date)


def _add_files_to_indices(index, first_file, second_file, date):
    index \
        .setdefault(first_file, {"count": []}) \
        .setdefault(second_file, [])
    index[first_file][second_file].append(date)


def create_index(repository_name):
    index = {}
    commits = git.parse_git_log_file("data/repository/" + repository_name + "/git_log_for_index.txt")
    files_with_time = git.create_files_indices(commits)
    commits = git.prepare_git_log_file(commits)
    print("Commits have gotten")
    for time, files in sorted(commits.items(), key=lambda x: x[0]):
        print(time)
        while len(files) != 0:
            first_file = files.pop()
            print("hihi: ", len(files))
            _ensure_file_count(index, files_with_time[first_file][time], time)
            for second_file in files:
                _add_files_to_indices(index, files_with_time[first_file][time], files_with_time[second_file][time],
                                      time)
                _add_files_to_indices(index, files_with_time[second_file][time], files_with_time[first_file][time],
                                      time)
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

    return index
