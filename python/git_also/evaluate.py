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

    def get_score(self, predict, remote_files):
        print("LOG: Deleted:", *remote_files, " prediction: ", predict, file=self.out)
        predicted_files = predict[0]
        types = {i: 0 for i in range(len(self.scores))}
        if predicted_files == '':
            if remote_files == ['']:
                print("Situation: score('', '')", file=self.out)
                types[1] += 1
                return [self.scores[1], types]
            else:
                print("Situation: score('', A)", file=self.out)
                types[2] += 1
                return [self.scores[2], types]
        score = 0
        if predicted_files in remote_files:
            print("Situation: score(A, A)", file=self.out)
            types[0] += 1
            score += self.scores[0]
        elif remote_files == ['']:
            print("Situation: score(A, '')", file=self.out)
            types[3] += 1
            score += self.scores[3]
        else:
            print("Situation: score(A, B)", file=self.out)
            types[4] += 1
            score += self.scores[4]
        return [score, types]
