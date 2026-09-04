package com.zzkj.structure

import com.zzkj.structure.util.moshi.MoshiHelper
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.junit.Assert.assertEquals
import org.junit.Test


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun useType() {
        val string: String? = null
        assertEquals(true, string is String)
    }

    @Test
    fun mosh() {
        val json1 = """
            {
            "result": "11111",
            "list":null
            }
        """.trimIndent()
        val json2 = """
{
  "data": "11111",
  "list1": [
    "list1",
    null
  ],
  "list2": [
    {
      "key": 1
    },
    {
      "key": null
    }
  ],
  "list3": [
    {
      "data": "11111",
      "list1": [
        "list1",
        null
      ],
      "list2": [
        {
          "key": 1
        },
        {
          "key": null
        }
      ],
      "list3": [
        {
          "key": 1
        },
        {
          "key": null
        }
      ]
    }
  ]
}
        """.trimIndent()
//        println(json1)
        println(json2)
//        val data1 = MoshiHelper.adapter(TestBean::class.java).fromJson(json1)
        val data2 = MoshiHelper.adapter(TestBean::class.java).fromJson(json2)
//        println(data1)
        println(data2)
    }
}


@JsonClass(generateAdapter = true)
data class TestBean(
    @Json(name = "result")
    val data: String = "default",
    @Json(name = "list1")
    val list1: List<String> = listOf(),
    @Json(name = "list2")
    val list2: List<KeyBean> = listOf(),
    @Json(name = "list3")
    val list3: List<TestBean> = listOf()
)

@JsonClass(generateAdapter = true)
data class KeyBean(
    @Json(name = "key")
    val key: Int = 726
)