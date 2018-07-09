package predict

import commitInfo.Commit
import commitInfo.CommittedFile
import junit.framework.TestCase.assertTrue
import org.junit.Test
import predict.current.TimePredictionProvider

class PredictTest {
    @Test
    fun testCommitPredictForGoodPredictableRepo() {
        val (file1, file2, file3) = arrayOf(CommittedFile(1), CommittedFile(2), CommittedFile(3))
        val commit1 = Commit(1522196236)
        val commit2 = Commit(1522296236)
        val commit3 = Commit(1522396236)
        val commit4 = Commit(1522496236)
        val commit5 = Commit(1522596236)

        file1.committed(commit1, "a")
        file2.committed(commit1, "b")
        file1.committed(commit2, "a")
        file2.committed(commit2, "b")
        file1.committed(commit3, "a")
        file2.committed(commit3, "b")
        file1.committed(commit4, "a")
        file2.committed(commit4, "b")
        file3.committed(commit4, "c")
        file1.committed(commit5, "a")


        val predictionProvider = TimePredictionProvider(14, 0.4)
        val prediction = predictionProvider.commitPredict(commit5)
        assertTrue(file2 in prediction)

        print(prediction)

    }


}