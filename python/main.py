from git_also.evaluate import Evaluator
from git_also import dataset
from git_also import estimate
from git_also import index
from git_also import table_create
from git_also import make_decision

start = 1483228800
end = 1488326400


def main():
    print("Started")
    pandas_index = index.get_index("pandas")
    ds = dataset.get_dataset("pandas")
    teach = estimate.Estimator(pandas_index, ds, make_decision.get_probability_with_time)
    evaluator = Evaluator(
        {
            "score(A, A)": 1,
            "score(A, B)": -1,
            "score('', '')": 0.3,
            "score('', A)": 0.1,
            "score(A, '')": 0.1
        }
    )
    #teach.predict_for_dataset(evaluator, 2365, 40)
    teach.fit()
    print("Finished")


if __name__ == "__main__":
    main()
