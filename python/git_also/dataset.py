from random import randrange as rand


def _create_dict_of_commits(index, start_learn_time, end_learn_time):
    commits = {}
    for first_file, files in index.items():
        for time in files["count"][1:]:
            if start_learn_time <= time <= end_learn_time:
                commits.setdefault(time, [])
                commits[time].append(first_file)
    # print(len(list(filter(lambda x: len(x[1]) > 10 or len(x[1]) < 2, commits.items()))))
    return dict(filter(lambda x: 2 <= len(x[1]) <= 10, commits.items()))


def _get_remote_files(files, count_remote_files, time):
    remote_files = set()
    assert len(files) > count_remote_files, \
        "ASSERT: the number of files in commit(%s) is less than you need to remove" % time
    while len(remote_files) != count_remote_files:
        remote_index = rand(len(files))
        if files[remote_index] not in remote_files:
            remote_files.add(files[remote_index])
    return remote_files


def _create_dataset(commits, count_remote_files):
    dataset = []
    for time, files in commits.items():
        for i in range(len(files)):
            remote_files = _get_remote_files(files, count_remote_files, time)
            dataset.append(
                [
                    list(filter(lambda x: x not in remote_files, commits[time])),
                    int(time),
                    list(remote_files)
                ]
            )
            if count_remote_files == 0:
                break
    return dataset


def _create_full_dataset(commits, count_remote_files):
    dataset = []
    for time, files in commits.items():
        for remote_file in files:
            dataset.append(
                [
                    list(filter(lambda x: x != remote_file, commits[time])),
                    int(time),
                    [remote_file]
                ]
            )
    return dataset


def print_dataset(repository_name, dataset):
    with open("data/dataset/" + repository_name + "/dataset.ds", 'w') as file_out:
        for commit in dataset:
            print(*commit[0], sep=", ", end="; ", file=file_out)
            print(commit[1], end="; ", file=file_out)
            print(*commit[2], sep=", ", file=file_out)


def create_dataset(index, start_learn_time, end_learn_time, count_remote_files=1):
    commits = _create_dict_of_commits(index, start_learn_time, end_learn_time)
    return _create_dataset(commits, count_remote_files)


def create_full_dataset(index, start_learn_time, end_learn_time, count_remote_files=1):
    commits = _create_dict_of_commits(index, start_learn_time, end_learn_time)
    return _create_full_dataset(commits, count_remote_files)


def get_dataset(repository_name):
    dataset = []
    with open("data/dataset/" + repository_name + "/dataset.ds") as file_in:
        for commit in file_in:
            files, time, remote_files = commit.strip().split(';')
            time = int(time.strip())
            files = files.strip().split(', ')
            remote_files = list(filter(lambda x: x != "", remote_files.strip().split(', ')))
            dataset.append([files, time, remote_files])
    return dataset
