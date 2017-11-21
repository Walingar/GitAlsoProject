from git_also import dataset
from git_also import estimate
from git_also import index

start = 1483228800
end = 1488326400


def main():
    print("Started")
    pandas_index = index.get_index("pandas")
    ds = dataset.get_dataset("pandas")
    teach = estimate.Estimator(pandas_index, ds)
    teach.fit()
    print("Finished")


if __name__ == "__main__":
    main()
