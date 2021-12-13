package gmutils.collections

import org.junit.Assert.*
import org.junit.Test


class ObservableListTest {

    @Test
    fun testAddingToObservableList() {
        val lst = ObservableList<Int>()

        var count = 0
        var firstIndex = 0
        var lastIndex = 0

        lst.addingObserver = { l, fi, li ->
            count += (li - fi) + 1
            firstIndex = fi
            lastIndex = li
        }

        lst.add(1)
        assertEquals(1, count)
        assertEquals(lst.size, count)
        assertEquals(0, firstIndex)
        assertEquals(0, lastIndex)

        lst.add(listOf(2, 3, 4, 5))
        assertEquals(5, count)
        assertEquals(lst.size, count)
        assertEquals(1, firstIndex)
        assertEquals(4, lastIndex)

        lst.add(listOf(6, 7, 8, 9, 10))
        assertEquals(10, count)
        assertEquals(lst.size, count)
        assertEquals(5, firstIndex)
        assertEquals(9, lastIndex)

    }

    @Test
    fun testRemovingToObservableList() {
        val lst = ObservableList<Int>()

        val nums = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10) // 10 elements
        var count = nums.size
        var removedElements: List<Int>? = null
        var removedElementsIndexes: Indexes? = null

        lst.removingObserver = { l, rL, rLI ->
            count -= rL.size
            removedElements = rL
            removedElementsIndexes = rLI
        }

        lst.add(nums)

        lst.removeAt(0) // (2, 3, 4, 5, 6, 7, 8, 9, 10)
        assertEquals(9, count)
        assertEquals(lst.size, count)
        assertEquals(listOf(1), removedElements)
        assertEquals(listOf(0), removedElementsIndexes)

        lst.removeAt(8) // (2, 3, 4, 5, 6, 7, 8, 9)
        assertEquals(8, count)
        assertEquals(lst.size, count)
        assertEquals(listOf(10), removedElements)
        assertEquals(listOf(8), removedElementsIndexes)

        lst.remove(listOf(2, 4, 6, 8, 10))  // (3, 5, 7, 9)
        assertEquals(4, count)
        assertEquals(lst.size, count)
        assertEquals(listOf(2, 4, 6, 8), removedElements)
        assertEquals(listOf(0, 2, 4, 6), removedElementsIndexes)

        //check if desired element only was removed
        val result = mutableListOf<Int>()
        lst.forEach { result.add(it) }
        assertEquals(listOf(3, 5, 7, 9), result)

        lst.removeAll() //()
        assertEquals(0, count)
        assertEquals(lst.size, count)
        assertEquals(listOf(3, 5, 7, 9), removedElements)
        assertEquals(listOf(0, 1, 2, 3), removedElementsIndexes)
    }

    @Test
    fun testSortAndIterationObservableList() {
        val lst = ObservableList<Int>()

        val nums = listOf(11, 2, 133, 4, 5, 6, 27, 18, 9, 10)
        //val sortedNums = listOf(2, 4, 5, 6, 9, 10, 11, 18, 27, 133)
        val sortedNums = nums.sortedBy { it }

        lst.add(nums)

        lst.sort { it }

        val result = mutableListOf<Int>()

        lst.forEach { result.add(it) }

        assertEquals(sortedNums, result)
    }
}