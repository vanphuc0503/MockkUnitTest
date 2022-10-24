@file:JvmName("MockDemoKt") // rename file when rendering
package com.example.unittest

import io.mockk.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.junit.Assert
import org.junit.Test
import kotlin.system.exitProcess

class MockAdvance {
    /**
     * Hierarchical mocking (Van Phuc)
     * https://github.com/BracketCove/SpaceNotes/blob/master/domain/src/test/java/com/wiseassblog/domain/RegisteredNoteSourceTest.kt //File test with coroutine
     */
    @Test
    fun `test address Book`() {
        //Real hierarchies
        val addressBook = mockk<AddressBook> {
            every { contacts } returns listOf(
                mockk {
                    every { name } returns "John"
                    every { telephone } returns "123-456-789"
                    every { address.city } returns "New-York"
                    every { address.zip } returns "123-45"
                },
                mockk {
                    every { name } returns "Alex"
                    every { telephone } returns "789-456-123"
                    every { address } returns mockk {
                        every { city } returns "Wroclaw"
                        every { zip } returns "543-21"
                    }
                }
            )
        }

        /*    val serviceLocator = mockk<ServiceLocator> {
                every { transactionRepository } returns mockk {
                    coEvery {
                        getTransactions()
                    } returns Result.build {
                        listOf(
                            NoteTransaction(
                                creationDate = "28/10/2018",
                                contents = "Content of note1.",
                                transactionType = TransactionType.DELETE
                            ),
                            NoteTransaction(
                                creationDate = "28/10/2018",
                                contents = "Content of note2.",
                                transactionType = TransactionType.DELETE
                            ),
                            NoteTransaction(
                                creationDate = "28/10/2018",
                                contents = "Content of note3.",
                                transactionType = TransactionType.DELETE
                            )
                        )
                    }

                    coEvery {
                        deleteTransactions()
                    } returns Result.build { Unit }
                }

                every { remoteRepository } returns mockk {
                    coEvery { getNotes() } returns Result.build {
                        listOf(
                            Note(
                                creationDate = "28/10/2018",
                                contents = "Content of note1.",
                                upVotes = 0,
                                imageUrl = "",
                                creator = User(
                                    "8675309",
                                    "Ajahn Chah",
                                    ""
                                )
                            ), Note(
                                creationDate = "28/10/2018",
                                contents = "Content of note2.",
                                upVotes = 0,
                                imageUrl = "",
                                creator = User(
                                    "8675309",
                                    "Ajahn Chah",
                                    ""
                                )
                            ), Note(
                                creationDate = "28/10/2018",
                                contents = "Content of note3.",
                                upVotes = 0,
                                imageUrl = "",
                                creator = User(
                                    "8675309",
                                    "Ajahn Chah",
                                    ""
                                )
                            )
                        )
                    }

                    coEvery {
                        synchronizeTransactions(any())
                    } returns Result.build {
                        Unit
                    }
                }
            }*/
    }

    /**
     * Coroutines
     */
    @Test
    fun `test Coroutines`() {
        val mock = mockk<Divider>()
        val slot = slot<Int>()

        coEvery {
            mock.divide(capture(slot), any())
        } coAnswers {
            slot.captured * 11
        }

        val kq = mock.divide(10, 5)
        println("kq: $kq")
    }

    /**
     * Verification timeout
     */
    @Test
    fun `test Verification timeout`() {
        mockk<MockkClass> {
            coEvery { sum(1, 2) } returns 4

            CoroutineScope(Dispatchers.Default).launch {
                delay(2000)
                sum(1, 2)
            }

            verify(timeout = 2500) { sum(1, 2) }
        }

        //coverage all call
//        confirmVerified(object1, object2)
//        excludeRecords { object1.someCall(andArguments) }
    }

    /**
     * Varargs
     */
    @Test
    fun `test Varargs`() {
        val mock = mockk<VarargExample>()
//        every { mock.example(1, 2, 3, more(4), 5) } returns 6
//
//        every { mock.example(*anyIntVararg()) } returns 1
//
//        every { mock.example(1, 2, *anyIntVararg(), 3) } returns 1

        every { mock.example(1, 2, * varargAllInt { it > 5 }, 9) } returns 10
        Assert.assertEquals(10, mock.example(1, 2, 6, 7, 9))

        //mock.example (1, 2, 5, 9)
        //mock.example (1, 2, 5, 6, 9)
        //mock.example (1, 2, 5, 6, 7, 9)
    }

    /**
     * Top-level functions
     */
    @Test
    fun `test top Level Function`() {
        mockkStatic("com.example.unittest.MockDemoKt")
        every { lowercase("A") } returns "lowercase-abc"

        println(lowercase("A"))
        println(lowercase("B"))
    }

    private fun String.concat(other: String): String = this.lowercase() + other.uppercase()

    /**
     * Extension functions attached to class or object
     */
    @Test
    fun `Extension functions attached to class or object`() {
        val mock = mockk<MockAdvance>()
        with(mock) {
            every { any<String>().concat(any()) } returns "result"
            println("dsa".concat("Haha"))
        }
    }

    /**
     * Object & enumeration mocks
     */
    @Test
    fun `Object & enumeration mocks`() {
        mockkObject(ExampleObject) // notWorking
        every {
            ExampleObject.sum(5, 20)
        } returns 15
        ExampleObject.sum(5, 20)

        println(ExampleObject.sum(5, 20))

        verify {
            ExampleObject.sum(5, 20)
        }
    }

    /**
     * Mock relaxed for Unit returning functions
     */
    @Test
    fun `Mock relaxed for Unit returning functions`() {
        val mock = mockk<ExampleClass>(relaxUnitFun = true)
        every {
            mock.sumSalary(10, 20)
        } returns 20

        mock.sumSalary(10, 20)
        mock.getSalary()
//        println(mock.getInfo(10)) return 0 with relax = true relaxUnitFun = true else error

        verify {
            mock.sumSalary(10, 20)
        }
    }

    /**
     * Mocking functions returning Nothing
     */
    @Test
    fun `Mocking functions returning Nothing`() {
        every { quit(1) } throws Exception("this is a test")
    }

    fun quit(status: Int): Nothing {
        exitProcess(status)
    }

    /**
     * Constructor mocks
     */
    @Test
    fun `Constructor mocks`() {
        mockkConstructor(MockkClass::class)
        every { anyConstructed<MockkClass>().sum(1, 2) } returns 4
        println(MockkClass().sum(1, 2)) // returns 4
    }

    /**
     * Private functions mocking
     */
    @Test
    fun `Private functions mocking`() {
        val mock = spyk(PrivateClass(), recordPrivateCalls = true)
        every {
            mock["sum"](any<Int>(), 5) // way 1
//            mock invoke "sum" withArguments listOf(10, 5) //way 2
        } returns 25

        // fun call private fun sun()
        mock.getSum()

        verify {
            mock invoke "sum" withArguments listOf(10, 5)
        }
    }

    /**
     * Properties mocking
     */
    @Test
    fun `Properties mocking`() {
        val mock = mockk<PrivateProperty>()
        every { mock getProperty "speed" } returns 33
        every { mock setProperty "acceleration" value less(5) } just Runs

        println(mock.speed)
        mock.acceleration = 4

        verify { mock getProperty "speed" }
        verify { mock setProperty "acceleration" value less(5) }

        //------------------------------------------------------------

        every {
            mock.speed
        } answers { fieldValue + 6 }
        println(mock.speed)

        //----------------------------------------------

        every {
            mock.acceleration = less(5)
        } propertyType Int::class answers { fieldValue += value }

        mock.acceleration = 4

        //------------------------------------------

        every {
            mock getProperty "acceleration"
        } propertyType Int::class answers { fieldValue }
        println(mock.acceleration)

        //----------------------------------------------

        every {
            mock getProperty "speed"
        } propertyType Int::class answers { fieldValue + 10 }
        println(mock.speed)

        //----------------------------------------------

        every {
            mock setProperty "acceleration" value any<Int>()
        } propertyType Int::class answers  { fieldValue += value }
        mock.acceleration = 16
        println(mock.acceleration)

        //----------------------------------------------

        every {
            mock.acceleration = any()
        } propertyType Int::class answers {
            fieldValue = value + 1
        } andThen {
            fieldValue = value - 1
        }

        mock.acceleration = 30
        println(mock.acceleration)
        mock.acceleration = 30
        println(mock.acceleration)
    }

    /**
     * Class mock & Settings & Cleanup
     */
    fun `class mock `() {
        val mock = mockkClass(ExampleClass::class)
        mock.getSalary()
    }
}

fun lowercase(str: String): String {
    return str
}

interface VarargExample {
    fun example(vararg sequence: Int): Int
}

interface AddressBook {
    val contacts: List<Contact>
}

interface Contact {
    val name: String
    val telephone: String
    val address: Address
}

interface Address {
    val city: String
    val zip: String
}

class MockkClass {
    fun sum(a: Int, b: Int) = a + b
}

class ExampleClass {
    fun sumSalary(a: Int, b: Int) = a + b

    fun getSalary() {
        println("haha")
    }

    fun getInfo(a: Int) = a
}

class PrivateClass {
    fun getSum() {
        sum(10, 5)
    }

    private fun sum(a: Int, b: Int) = a + b
}

class PrivateProperty {
    val speed: Int = 20
    var acceleration: Int = 10
}

object ExampleObject {
    fun sum(a: Int, b: Int) = a + b
}