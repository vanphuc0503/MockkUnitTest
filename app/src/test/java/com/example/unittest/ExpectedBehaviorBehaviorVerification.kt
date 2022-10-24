package com.example.unittest

import io.mockk.*
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Test

class ExpectedBehaviorBehaviorVerification {

    /**
     * Argument matching
     */
    @Test
    fun `Argument matching`() {
        val mock = mockk<Mock>()
        every { mock.call(more(5)) } returns 1
        every { mock.call(or(less(5), eq(5))) } returns -1 // or, and <-> more, less, eq

        assertEquals(1, mock.call(10))
        assertEquals(-1, mock.call(5))
    }

    /**
     * Expected answer
     */
    @Test
    fun `Expected answer`() {
        val mock = mockk<Mock>()
        every { mock.call(5) } returnsMany listOf(1, 2, 3) //the same: every { mock.call(5) } returns 1 andThen 2 andThen 3
        // every { mock.call(5) } throws RuntimeException("error happened")
        // every { mock.callReturningUnit(5) } just Runs //return value is Unit

        every { mock.call(10) } answers { arg<Int>(0) + 5 }

        assertEquals(15, mock.call(10))
        println("${mock.call(5)}") // return 1
        println("${mock.call(5)}") // return 2
        println("${mock.call(5)}") // return 3
        println("${mock.call(10)}") // return 15
//        Assert.assertEquals(-1, mock.call(5))
    }

    /**
     * Behavior verification
     */
    @Test
    fun `Behavior verification`() {
        val doc1 = mockk<Dependency1>()
        val doc2 = mockk<Dependency2>()
        val mock = mockk<Mock>()

        every { doc1.value1 } returns 5
        every { doc2.value2 } returns "6"
//        every { mock.call(5) }

        val sut = SystemUnderTest(doc1, doc2)

        assertEquals(11, sut.calculate())
//        verify {
//            doc1.value1
//            doc2.value2
//        }

//        verify(atLeast = 5, atMost = 7) { // mock.call(5) return true when called 5 -> 7 times
//            mock.call(5)
//        }

//        verify(exactly = 5) { // mock.call(5) return true when called exactly 5 times
//            mock.call(5)
//        }

//        verify(exactly = 0) { // mock.call(5) return true when not called
//            mock.call(5)
//        }

 /*       every { mock.call(2) } returns 10

        verify { // mock return true when all func aren't called
            mock.call(2) wasNot Called
        }*/

//        verifyAll { // check total same method ex: call(5) , call(6) => verifyAll {call(5), call(6)}
//            mock.call(5)
//        }

        every { mock.call(1) } returns 1
        every { mock.call(2) } returns 2
        every { mock.call(3) } returns 3

        mock.call(1)
        mock.call(2)
        mock.call(3)

        verifySequence {
            mock.call (1)
            mock.call (2)
            mock.call (3)
        }

        verifyOrder {
            mock.call (1)
            mock.call (3)
        }
    }
}

class Mock {
    fun call(arg: Int): Int {
        return arg
    }

    fun callReturningUnit(arg: Int) {}
}