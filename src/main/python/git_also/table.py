def print_table(min_prob, n, count, table):
    with open("data/logs/log.md", "a") as file_out:
        print("min_prob = {:d}%".format(min_prob), file=file_out)
        print("N =", int(n), file=file_out)
        print("count = {:d}\n".format(count), file=file_out)
        print(table, file=file_out)
        print("\n", file=file_out)


def create_table(types):
    table = ""

    table += "| truth\\prediction | nothing | fileA | fileB |\n"
    table += "| --- | --- | --- | --- |\n"
    table += "| nothing | {:d} | {:d} | - |\n".format(types["score('', '')"], types["score('', A)"])
    table += "| fileA | {:d} | {:d} | {:d} | \n".format(types["score(A, '')"],
                                                        types["score(A, A)"],
                                                        types["score(A, B)"]
                                                        )
    return table
