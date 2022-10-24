package com.example.unittest

import io.mockk.*
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.impl.annotations.SpyK
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class MockFeature {
    /**
     * Capturing
     * using CapturingSlot<Int> vs MutableList<Int>
     */
    @Test
    fun `Capturing Slot`() {
        val slot = slot<Int>()
        val list = mutableListOf<Int>()
        val mock = mockk<Divider>()
        every { mock.divide(capture(slot), any()) } returns 22

        //code test
        val mockTest = mock.divide(5, 2)
        println("mock Test: $mockTest")

        assertEquals(5, slot.captured)

        // use a slot in an answer
        every {
            mock.divide(capture(slot), any())
        } answers {
            slot.captured * 11
        }

        //code test2
        val mockTest2 = mock.divide(5, 2)
        println("mock Test2: $mockTest2")

        // code test mutableList
        every { mock.divide(capture(list), any()) } returns 30//code test
        val mockTest3 = mock.divide(6, 8)
        val mockTest4 = mock.divide(8, 10)
        println("mock Test3: $mockTest3")
        println("mock Test4: $mockTest4 ")
        assertEquals(6, list[0])
        assertEquals(8, list[1])
    }

    /**
     * Relaxed mocks
     */
    @Test
    fun `Relaxed mocks`() {
        val mock = mockk<Divider>(relaxed = true)
        mock.divide(5, 2) // returns 0 <> relaxed = false -> error
        mock.call1(1,2,3).call2(4,5,6)

        // use relaxedMock so test true
        verify {
            mock.divide(5, 2)
        }
        verify { mock.call1(1,2,3).call2(4,5,6) }
    }

    /**
     * Spies
     */
    @Test
    fun `Spies`() {
        val spy = spyk<Adder>()
        val addTest = spy.add(4, 5)
        assertEquals(9, addTest)
        println("add Test: $addTest")

        every {
            spy.magnify(any())
        } answers {firstArg<Int>() * 2}

        assertEquals(14, spy.add(4, 5))

        verify { spy.add(4, 5) }
        verify { spy.magnify(5) }
    }
}

class Divider {
    fun divide(a: Int, b: Int) = a / b

    fun call1(a: Int, b: Int, c: Int) = apply { }

    fun call2(a: Int, b: Int, c: Int) = apply { }
}

class Adder {
    fun magnify(a: Int) = a

    fun add(a: Int, b: Int) = a + magnify(b)
}

class Annotations {
    @MockK
    lateinit var doc1: Dependency1

    @RelaxedMockK
    lateinit var doc2: Dependency2

    @SpyK
    var doc3 = Dependency3()

    @Before
    fun setup() = MockKAnnotations.init(this)

    @Test
    fun calculateAddsValues1() {
        every { doc1.value1 } returns 5
        every { doc2.value2 } returns "6"
        every { doc3.value3 } returns "7"

        val sut = SystemUnderTest(doc1, doc2)
        assertEquals(11, sut.calculate())
    }
}
