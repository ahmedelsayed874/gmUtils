package gmutils.collections

import java.lang.ref.WeakReference


typealias FirstIndex = Int
typealias LastIndex = Int
typealias Indexes = List<Int>

typealias AddObserver<E> = (list: ObservableList<E>, firstIndex: FirstIndex, lastIndex: LastIndex) -> Unit
typealias RemoveObserver<E> = (list: ObservableList<E>, removedElements: List<E>, removedElementsIndexes: Indexes) -> Unit

class ObservableList<E> : Iterable<E> {
    private val items = mutableListOf<E>()

    private var _addingObserver: WeakReference<AddObserver<E>>? = null
    private var _removingObserver: WeakReference<RemoveObserver<E>>? = null

    var addingObserver: AddObserver<E>?
        get() {
            return _addingObserver?.get()
        }
        set(v) {
            _addingObserver = WeakReference(v)
        }

    var removingObserver: RemoveObserver<E>?
        get() {
            return _removingObserver?.get()
        }
        set(v) {
            _removingObserver = WeakReference(v)
        }

    fun add(element: E): Int {
        items.add(element)
        val i = size - 1
        addingObserver?.invoke(this, i, i)
        return i
    }

    fun add(elements: List<E>): IntRange {
        if (elements.isEmpty()) return -1..-1
        val i0 = if (size == 0) 0 else size
        items.addAll(elements)
        val i1 = size - 1
        addingObserver?.invoke(this, i0, i1)
        return IntRange(i0, i1)
    }

    fun insert(element: E, @androidx.annotation.IntRange(from = 0, to = Long.MAX_VALUE) position: Int) {
        items.add(position, element)
        addingObserver?.invoke(this, position, position)
    }

    fun insert(elements: List<E>, @androidx.annotation.IntRange(from = 0, to = Long.MAX_VALUE) position: Int) {
        if (elements.isEmpty()) return
        items.addAll(position, elements)
        addingObserver?.invoke(this, position, position + elements.size)
    }

    fun removeAt(index: Int): Boolean {
        val s0 = size
        val e = items[index]
        items.removeAt(index)
        removingObserver?.invoke(this, listOf(e), listOf(index))
        return s0 > size
    }

    fun remove(element: E): Boolean {
        val index = items.indexOf(element)
        return if (index >= 0) removeAt(index)
        else false
    }

    fun remove(elements: List<E>): Int {
        val e = mutableListOf<E>()
        val idx = mutableListOf<Int>()
        elements.forEach {
            val index = items.indexOf(it)
            if (index >= 0) {
                e.add(items[index])
                idx.add(index)
            }
        }

        var x = 0

        idx.forEach {index ->
            items.removeAt(index - x)
            x++
        }

        removingObserver?.invoke(this, e, idx)

        return idx.count()
    }

    fun removeAll() {
        val s = size
        val e = mutableListOf<E>()
        e.addAll(items)
        items.clear()
        removingObserver?.invoke(this, e, (0 until s).toList())
    }

    fun get(index: Int): E {
        return items[index]
    }

    fun indexOf(element: E): Int {
        return items.indexOf(element)
    }

    val size: Int get() = items.size

    override fun iterator(): Iterator<E> {
        return items.iterator()
    }

    fun <V : Comparable<V>> sort(selector: (E) -> V?) {
        items.sortBy { selector(it) }
    }

    fun dispose() {
        items.clear()
        _addingObserver = null
        _removingObserver = null
    }
}