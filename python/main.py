from lib import createIndex
from lib import getIndex
from lib import createLearnIndex
from lib import learn
from lib import getLearnIndex

start = 1483228800
end = 1488326400

def create_index():
    creator = createIndex.IndexCreator("pandas")
    creator.get_files()
    creator.print("allIndex.json")


def get_index():
    getter = getIndex.IndexGetter("pandas", "allIndex.json")
    return getter.get()


def create_learn_index(index, times, startLearnTime, endLearnTime):
    getter = createLearnIndex.LearnIndexCreator("pandas", index)
    getter.create(times, startLearnTime, endLearnTime)


def get_learn_index():
    getter = getLearnIndex.LearnIndexGetter("pandas", "learnIndex.json")
    return getter.get()


def teach(index, learnIndex, times):
    teacher = learn.Teacher(index, learnIndex)
    counter = 0
    nice = 0
    for ts in times:
        if start <= ts <= end:
            predict = teacher.prediction(str(ts))
            if predict:
                counter += 1
                if predict[0] == predict[1]:
                    nice += 1
    print(nice / counter)


if __name__ == "__main__":
    print("Started")
    index, times = get_index()
    learnIndex = get_learn_index()
    teach(index, learnIndex, times)
    print("Finished")
