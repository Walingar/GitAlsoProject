from collections import Counter


class Evaluator:
    def __init__(self, scores):
        self.out = open("data/temp_log.log", "w")
        """
        :param scores: [score(A, A),
                        score('', ''),
                        score('', A),
                        score(A, ''),
                        score(A, B)]
        """
        self.scores = scores

    def get_score(self, predict, remote_files, types):
        print("LOG: Deleted:", *remote_files, " prediction: ", predict, file=self.out)
        predicted_files = predict[0]
        if predicted_files == '':
            if remote_files == ['']:
                print("Situation: score('', '')", file=self.out)
                types["score('', '')"] += 1
                return self.scores[1]
            else:
                print("Situation: score('', A)", file=self.out)
                types["score('', A)"] += 1
                return self.scores[2]
        score = 0
        if predicted_files in remote_files:
            print("Situation: score(A, A)", file=self.out)
            types["score(A, A)"] += 1
            score += self.scores[0]
        elif remote_files == ['']:
            print("Situation: score(A, '')", file=self.out)
            types["score(A, '')"] += 1
            score += self.scores[3]
        else:
            print("Situation: score(A, B)", file=self.out)
            types["score(A, B)"] += 1
            score += self.scores[4]
        return score
