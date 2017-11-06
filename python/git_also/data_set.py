from random import randrange as rand


def _create_dict_of_commits(index, start_learn_time, end_learn_time):
    commits = {}
    for first_file, files in index.items():
        for time in files["count"]:
            if start_learn_time <= time <= end_learn_time:
                commits.setdefault(time, [])
                commits[time].append(first_file)
    return dict(filter(lambda x: len(x[1]) >= 2, commits.items()))


def _get_remote_files(files, count_remote_files, time):
    remote_files = set()
    assert len(files) > count_remote_files, \
        "ASSERT: the number of files in commit(%s) is less than you need to remove" % time
    while len(remote_files) != count_remote_files:
        remote_index = rand(len(files))
        if files[remote_index] not in remote_files:
            remote_files.add(files[remote_index])
    return remote_files


def _create_data_set(commits, count_remote_files):
    data_set = []
    for time, files in commits.items():
        remote_files = _get_remote_files(files, count_remote_files, time)
        data_set.append(
            [
                list(filter(lambda x: x not in remote_files, commits[time])),
                int(time),
                list(remote_files)
            ]
        )
    return data_set


def print_data_set(repository_name, data_set):
    with open("data/data_set/" + repository_name + "/data_set.ds", 'w') as file_out:
        for commit in data_set:
            print(*commit[0], sep=", ", end="; ", file=file_out)
            print(commit[1], end="; ", file=file_out)
            print(*commit[2], sep=", ", file=file_out)


def create_data_set(index, start_learn_time, end_learn_time, count_remote_files=1):
    commits = _create_dict_of_commits(index, start_learn_time, end_learn_time)
    return _create_data_set(commits, count_remote_files)


def get_data_set(repository_name):
    data_set = []
    with open("data/data_set/" + repository_name + "/data_set.ds") as file_in:
        for commit in file_in:
            files, time, remote_files = commit.strip().split(';')
            time = int(time.strip())
            files = files.strip().split(', ')
            remote_files = remote_files.strip().split(', ')
            data_set.append([files, time, remote_files])
    return data_set
