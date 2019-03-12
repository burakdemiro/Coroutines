import kotlinx.coroutines.*

fun main() {
    exampleWithContext()
}

suspend fun calculateHardThings(time: Int): Int {
    delay(1000)
    return 10 * time
}

suspend fun printlnDelayed(message: String) {
    // Complex calculation
    delay(1000)
    println(message)
}

fun exampleBlocking() = runBlocking {
    println("one")
    printlnDelayed("two")
    println("three")
}

// Running on another thread but still blocking the main thread
fun exampleBlockingDispatcher() {
    runBlocking(Dispatchers.Default) {
        println("one - from thread ${Thread.currentThread().name}")
        printlnDelayed("two - from thread ${Thread.currentThread().name}")
    }
    // Outside of runBlocking to show that it's running in the blocked main thread
    println("three - from thread ${Thread.currentThread().name}")
    // It still runs only after the runBlocking is fully executed.
}

fun exampleLaunchGlobal() = runBlocking {
    println("one - from thread ${Thread.currentThread().name}")

    // Launches new coroutine without blocking current thread
    GlobalScope.launch {
        printlnDelayed("two - from thread ${Thread.currentThread().name}")
    }

    println("three - from thread ${Thread.currentThread().name}")
    delay(3000) // if delay is not here "two" is not executed and process finished
}

fun exampleLaunchGlobalWaiting() = runBlocking {
    println("one - from thread ${Thread.currentThread().name}")

    // Launches new coroutine without blocking current thread
    val job = GlobalScope.launch {
        printlnDelayed("two - from thread ${Thread.currentThread().name}")
    }

    println("three - from thread ${Thread.currentThread().name}")
    job.join() // something like the delay but it waits for job finish and then quits the program
}

fun exampleLaunchCoroutineScope() = runBlocking {
    println("one - from thread ${Thread.currentThread().name}")

    // Local Coroutine
    this.launch(Dispatchers.Default) {
        printlnDelayed("two - from thread ${Thread.currentThread().name}")
    }

    println("three - from thread ${Thread.currentThread().name}")
}

// async return type Deferred<T>
// Multiple things -> Concurrently
fun exampleAsynAwait() = runBlocking {
    val startTime = System.currentTimeMillis()

    // Eğer async'leri her birinin önüne "await" koyarsan işlem 3 saniye sürer
    // Diğer türlü 3 işlem paralel bir şekilde işler ve 1 saniyede biter
    val deferred1 = async { calculateHardThings(10) }
    val deferred2 = async { calculateHardThings(20) }
    val deferred3 = async { calculateHardThings(30) }

    // Sonucu yazdırmadan önce tamamlanmasını bekle(await)
    val sum = deferred1.await() + deferred2.await() + deferred3.await()
    println("async/await result = $sum")

    val endTime = System.currentTimeMillis()
    println("Time taken = ${endTime - startTime}")
}

// Used for not concurrently process
fun exampleWithContext() = runBlocking {
    val startTime = System.currentTimeMillis()

    // it'same thing set each and every async after await
    val result1 = withContext(Dispatchers.Default) { calculateHardThings(10) }
    val result2 = withContext(Dispatchers.Default) { calculateHardThings(20) }
    val result3 = withContext(Dispatchers.Default) { calculateHardThings(30) }

    val sum = result1 + result2 + result3
    println("async/await result = $sum")

    val endTime = System.currentTimeMillis()
    println("Time taken = ${endTime - startTime}")
}

