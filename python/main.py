from git_also.evaluate import Evaluator
from git_also import dataset
from git_also import estimate
from git_also import index
from git_also import make_decision

start = 1483228800
end = 1488326400
rep = "intellij-community"

# можно вес коммита в прошлом(встретились эти 2 из большого комита)
# взвешивать, что чем в более большом коммите встречался файл, то тем меньше у него
# колегативная фильтрация
# ассоциативные правила

# берем пары для пары смотрим какой файл(файлы с min_confidence) с этой парой боальше всего встречался
# тем самым перебираем все пары файлов и просматриваем индекс на число встречи этой пары с (A, B) с другим файлом C
#

# датасет -> смотрим может быть в некст коммитаз есть предсказанный файл и ничего не удалять

def get_rules(path):
    rules = []
    with open(path, "r") as inp:
        for rule in inp:
            rule = rule.strip()
            files_need, rule = rule.split("), (")
            files_need = list(map(lambda x: int(x.strip()[1:-1]), filter(lambda x: x, files_need[3:].split(","))))
            predict, prob = rule.split(")), ")
            prob = float(prob.strip()[:-1])
            predict = list(map(lambda x: int(x.strip()[1:-1]), filter(lambda x: x, predict.split(","))))
            rules.append([files_need, predict, prob])
    return rules


def main():
    print("Started")
    idea_index = index.create_index(rep)
    #index.print_index(idea_index, rep)
    #idea_index = index.get_index(rep)
    # dataset.print_dataset(rep,
    #                       dataset.create_full_dataset(idea_index, start, end) +
    #                       dataset.create_dataset(idea_index, start, end, 0))

    #ds = dataset.get_dataset(rep)
    # for i, commit in enumerate(ds):
    #     for j, file in enumerate(ds[i][0]):
    #         ds[i][0][j] = int(ds[i][0][j])
    #     ds[i][0] = set(ds[i][0])
    #     for j, file in enumerate(ds[i][2]):
    #         ds[i][2][j] = int(ds[i][2][j])
    # teach = estimate.Estimator(idea_index, ds, make_decision.get_probability_with_time)
    # evaluator = Evaluator(
    #     {
    #         "score(A, A)": 1,
    #         "score(A, B)": -1,
    #         "score('', '')": 0.3,
    #         "score('', A)": 0.1,
    #         "score(A, '')": 0.1
    #     }
    # )
    # print(len(pandas_index))
    # ассоциативные правила, для каждой пары.
    #

    # rules = get_rules("git_also/b.txt")
    # print(len(rules))
    # print(rules)

    # teach.predict_from_rules(rules)
    # teach.fit()

    print("Finished")


if __name__ == "__main__":
    main()
