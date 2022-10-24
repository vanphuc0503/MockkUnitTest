package com.example.unittest

import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Test

class BasicTest {
    @Test
    fun calculateAddsValues() {
//        ex1()
        ex2()
    }

    /**
     * not unit test
     */
    private fun ex1() {
        val doc1 = Dependency1(5)
        val doc2 = Dependency2("6")

        val sut = SystemUnderTest(doc1, doc2)

        assertEquals(11, sut.calculate())
    }

    /**
     * use unit test
     */
    private fun ex2() {
        val doc1 = mockk<Dependency1>()
        val doc2 = mockk<Dependency2>()

        every { doc1.value1 } returns 5
        every { doc2.value2 } returns "6"

        val sut = SystemUnderTest(doc1, doc2)

        assertEquals(11, sut.calculate())
    }
}

class Dependency1(val value1: Int = 0)
class Dependency2(val value2: String = "0")
class Dependency3(var value3: String = "0")

class SystemUnderTest(
    private val dependency1: Dependency1,
    private val dependency2: Dependency2
) {

    fun calculate() =
        dependency1.value1 + dependency2.value2.toInt()
}