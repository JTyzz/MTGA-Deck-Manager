package com.earthdefensesystem.retrorv

import com.earthdefensesystem.retrorv.model.Deck
import org.junit.Test

import org.junit.Assert.*

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
    fun nameCheck(){
        val stringList = listOf("Foot", "Dog", "Foot1")
        var count = 0
        var stringcheck = "Foot"
        fun incrementString(){
            if (stringList.contains(stringcheck)){
                count++
                if (stringList.contains(stringcheck.plus(count))) {
                    incrementString()
                }
            }
        }
        incrementString()
        if (count != 0) {
            stringcheck = stringcheck.plus(count)
        }
        assertEquals(stringcheck, "Foot2")
    }
}
