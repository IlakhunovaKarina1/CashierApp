package com.grappim.cashier.core.extensions

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer

class CombinedLiveData<T, K, S>(source1: LiveData<T>, source2: LiveData<K>, private val combine: (data1: T?, data2: K?) -> S) : MediatorLiveData<S>() {

    private var data1: T? = null
    private var data2: K? = null

    init {
        super.addSource(source1) {
            data1 = it
            value = combine(data1, data2)
        }
        super.addSource(source2) {
            data2 = it
            value = combine(data1, data2)
        }
    }

    override fun <T : Any?> addSource(source: LiveData<T>, onChanged: Observer<in T>) {
        throw UnsupportedOperationException()
    }

    override fun <T : Any?> removeSource(toRemote: LiveData<T>) {
        throw UnsupportedOperationException()
    }
}

fun <T, K, R> LiveData<T>.combineWith(
    liveData: LiveData<K>,
    block: (T?, K?) -> R
): LiveData<R> {
    val result = MediatorLiveData<R>()
    result.addSource(this) {
        result.value = block(this.value, liveData.value)
    }
    result.addSource(liveData) {
        result.value = block(this.value, liveData.value)
    }
    return result
}

fun <T, V, R> LiveData<T>.combineWithNotNull(
    liveData: LiveData<V>,
    block: (T, V) -> R
): LiveData<R> {
    val result = MediatorLiveData<R>()
    result.addSource(this) {
        this.value?.let { first ->
            liveData.value?.let { second ->
                result.value = block(first, second)
            }
        }
    }
    result.addSource(liveData) {
        this.value?.let { first ->
            liveData.value?.let { second ->
                result.value = block(first, second)
            }
        }
    }

    return result
}

fun <T1, T2> combineTuple(f1: LiveData<T1>, f2: LiveData<T2>): LiveData<Pair<T1?, T2?>> = MediatorLiveData<Pair<T1?, T2?>>().also { mediator ->
    mediator.value = Pair(f1.value, f2.value)

    mediator.addSource(f1) { t1: T1? ->
        val (_, t2) = mediator.value!!
        mediator.value = Pair(t1, t2)
    }

    mediator.addSource(f2) { t2: T2? ->
        val (t1, _) = mediator.value!!
        mediator.value = Pair(t1, t2)
    }
}