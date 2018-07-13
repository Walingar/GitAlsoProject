import com.google.gson.reflect.TypeToken
import junit.framework.TestCase.assertTrue
import com.jetbrains.gitalso.storage.log.Action
import com.jetbrains.gitalso.storage.log.LogEvent
import org.junit.Test
import com.jetbrains.gitalso.json.GitAlsoJsonSerializer

class JsonTest {

    private data class TestClass(val Success: Boolean, val Message: String, val s: List<Int>)

    @Test
    fun fromJsonToClass() {
        val json = "{\"Success\":true,\"Message\":\"Invalid access token.\", 's':[1, 2]}"
        val tmp = GitAlsoJsonSerializer.fromJsonToClass(json, TestClass::class.java)
        print(tmp)
        assertTrue(true)
    }

    @Test
    fun fromJsonToType() {
        val json = "{\"Success\":true,\"Message\":\"Invalid access token.\", 's':[1, 2]}"
        val tmp = GitAlsoJsonSerializer.fromJsonToType<Map<String, Any>>(json, object : TypeToken<Map<String, Any>>() {}.type)
        print(tmp)
    }

    @Test
    fun fromEventToJsonAndBackCheckFailures() {
        val event = LogEvent(
                "uid1",
                "1",
                "1",
                10,
                Action.CANCEL,
                hashMapOf(
                        "x_y" to hashMapOf(
                                "A" to 1.0,
                                "B" to 1.1,
                                "intersection" to 1.2,
                                "A with time" to 1.3),
                        "z_y" to hashMapOf(
                                "A" to 1.4,
                                "B" to 1.5,
                                "intersection" to 1.6,
                                "A with time" to 1.7)
                )
        )
        val json = event.toString()
        LogEvent.Companion.fromString(json)

        assertTrue(true)
    }

    @Test
    fun precisionTest() {
        val jsonObject = hashMapOf(
                "0.1" to 0.11111111111,
                "0.2" to 0.22222222222,
                "0.3" to 0.333333333333333,
                "1.3" to 1.245543463464567
        )

        val json = GitAlsoJsonSerializer.toJson(jsonObject)
        print(json)
    }
}