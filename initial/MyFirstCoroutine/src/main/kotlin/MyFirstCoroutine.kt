import kotlinx.coroutines.*
import kotlin.coroutines.ContinuationInterceptor
import kotlin.coroutines.CoroutineContext

val handler1 = CoroutineExceptionHandler { _: CoroutineContext, throwable: Throwable ->
    logMessage("CEH1 handling ${throwable.localizedMessage}")
}

val handler2 = CoroutineExceptionHandler { _: CoroutineContext, throwable: Throwable ->
    logMessage("CEH2 handling ${throwable.localizedMessage}")
}


fun main() = runBlocking {

    val scope = CoroutineScope(Job())

    //Parent 1
    scope.launch {

        //Child 1
        launch {

            //Child 1 of Child 1
            launch {
                try {
                    logContext(">>>Child 1s Child")
                    delay(1)
                    logMessage(">>>Hello from Child 1s Child!")
                } catch (e: Exception) {
                    logMessage(">>>Child 1s Child got ${e.javaClass}  ${e.localizedMessage}")
                }
            }

            logContext(">>Child 1")
            try {
                delay(10)
                logMessage(">>Hello from Child 1")
            } catch (e: Exception) {
                logMessage(">>Child 1 got ${e.javaClass}  ${e.localizedMessage}")
            }
        }

        //Child 2
        launch {

            //Child 1 of Child 2
            launch {
                try {
                    logContext(">>>Child 2s Child")
                    delay(1)
                    logMessage(">>>Hello from Child 2s Child!")
                } catch (e: Exception) {
                    logMessage(">>>Child 2s Child got ${e.javaClass}  ${e.localizedMessage}")
                }
            }


            logContext(">>Child 2")
            throw IndexOutOfBoundsException("oops!")
            delay(10)
            logMessage(">>Hello from Child 2")
        }

        try {
            logContext(">Parent 1")
            delay(30)
            logMessage(">Hello from Parent 1")
        } catch (e: Exception) {
            logMessage(">Parent 1 got ${e.javaClass}  ${e.localizedMessage}")
        }
    }

    //Parent 2
    scope.launch {

        try {
            logContext(">Parent 2")
            delay(100)
            logMessage(">Hello from Parent 2")

        } catch (e: Exception) {
            logMessage(">Parent 2 got ${e.javaClass}  ${e.localizedMessage}")
        }
    }

    delay(1000)
    logMessage("The End.")

}


fun logMessage(msg: String) {
    println("Running on: [${Thread.currentThread().name}] | $msg")
}


fun CoroutineScope.logContext(id: String) {
    coroutineContext.logDetails(id)
}


fun CoroutineContext.logDetails(id: String) {
    sequenceOf(
        Job,
        ContinuationInterceptor,
        CoroutineExceptionHandler,
        CoroutineName
    )
        .mapNotNull { key -> this[key] }
        .forEach { logMessage("id: $id ${it.key} = $it") }
}