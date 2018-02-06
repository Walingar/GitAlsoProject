def parse_git_log_file(filename):
    """

    :param filename: string = name of git log file
    :return: commits: dictionary = {ts of commit: [[type1, file1] .. [typei, filei]]}
    """
    file = open(filename, 'r')
    commits = {}
    cur_time = file.readline().strip()
    while cur_time != '':
        cur_time = int(cur_time)
        commits[cur_time] = []
        cur_line = file.readline().strip()
        while cur_line and not cur_line.isdigit():
            commits[cur_time].append(cur_line.split())
            cur_line = file.readline().strip()
        if len(commits[cur_time]) == 0:
            del commits[cur_time]
        if not cur_line:
            cur_time = file.readline().strip()
        else:
            cur_time = cur_line
    return commits


def prepare_git_log_file(commits):
    prepared_commits = {}
    for commit in sorted(commits.items(), key=lambda x: x[0]):
        time = commit[0]
        temp_files = []
        for file in commit[1]:
            if file[0][0] == 'C' or file[0][0] == 'R':
                temp_files.append(file[2])
            elif len(file) > 1:
                temp_files.append(file[1])
        prepared_commits[time] = temp_files
    return prepared_commits


def add_file(cur_index, file_name, files):
    files[file_name] = cur_index
    return cur_index + 1


def get_index(file_name, cur_index, files):
    if file_name in files:
        return files[file_name]
    return add_file(cur_index, file_name, files)


def add_file_with_time(file_name, time, files, files_with_time):
    files_with_time.setdefault(file_name, {})
    files_with_time[file_name].setdefault(time, 0)
    files_with_time[file_name][time] = files[file_name]


def create_files_indices(commits):
    """

    :param commits: dictionary = {ts of commit: [[type, file]..]}
    :return: files_with_time: dictionary = {file_name: {ts of commit: index}}
    """
    files = {}
    files_with_time = {}
    cur_index = 1
    for commit in sorted(commits.items(), key=lambda x: x[0]):
        time = commit[0]

        # create files dictionary
        for file in commit[1]:
            # file added => index = max_ind + 1
            if file[0][0] == 'A':
                cur_index = add_file(cur_index, file[1], files)
            # file copied => index = max_ind + 1
            elif file[0][0] == 'C':
                cur_index = add_file(cur_index, file[2], files)
            # file renamed => index of new file = index of old file
            elif file[0][0] == 'R':
                if file[1] in files:
                    add_file(files[file[1]], file[2], files)
                    del files[file[1]]
                else:
                    cur_index = add_file(cur_index, file[2], files)
            else:
                if len(file) > 1 and file[1] not in files:
                    cur_index = add_file(cur_index, file[1], files)

        # create files_with_time and indices_with_time
        for file in commit[1]:
            if file[0][0] == 'C' or file[0][0] == 'R':
                add_file_with_time(file[2], time, files, files_with_time)
            elif len(file) > 1:
                add_file_with_time(file[1], time, files, files_with_time)
    return files_with_time

# example
# commits_log = parse_git_log_file("for_index.txt")
# print(create_files_indices(commits_log))
