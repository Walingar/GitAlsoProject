class Evaluator:
    def __init__(self, scores):
        """
        :param scores: {score(A, A),
                        score('', ''),
                        score('', A),
                        score(A, ''),
                        score(A, B)}
                first - prediction; second - truth
        """
        self.scores = scores

    # TODO: add some logs
    @staticmethod
    def log_prediction(remote_files, predicted_files):
        out = open("D:/temp_log.log", "a")
        print("LOG: DELETED:", *remote_files, "; PREDICTION: ", *predicted_files, file=out)
        out.close()

    def update_counter(self, remote_files, predicted_files, types):
        # self.log_prediction(remote_files, predicted_files)
        if len(predicted_files) == 0:
            if len(remote_files) == 0:
                types["score('', '')"] += 1
            else:
                types["score('', A)"] += 1
            return

        if len(remote_files) == 0:
            types["score(A, '')"] += 1
            return

        for file in predicted_files:
            if file[0] in remote_files:
                types["score(A, A)"] += 1
                return
        types["score(A, B)"] += 1

    def get_score(self, types):
        ans = 0
        for score_name, count in types.items():
            ans += self.scores[score_name] * count
        return ans
