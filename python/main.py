from git_also import index
from git_also import dataset
from git_also import estimate

start = 1483228800
end = 1488326400


def createDS(index):
    ds0 = dataset.create_dataset(index, start, end, 0)
    ds1 = dataset.create_dataset(index, start, end, 1)
    dataset.print_dataset("pandas", ds0 + ds1)


def main():
    print("Started")
    pandas_index = index.get_index("pandas")
    # createDS(pandas_index)
    ds = dataset.get_dataset("pandas")
    teach = estimate.Estimator(pandas_index, ds)
    teach.fit()
    print("Finished")


if __name__ == "__main__":
    main()
