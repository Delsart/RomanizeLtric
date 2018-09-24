import kotlinx.coroutines.experimental.CoroutineStart
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.newFixedThreadPoolContext


internal val Background = newFixedThreadPoolContext(Runtime.getRuntime().availableProcessors() * 2, "coroutine_thread_pool")

fun <T> bg(block: suspend () -> T): Deferred<T> = async(context = Background, start = CoroutineStart.LAZY) { block() }

infix fun <T> Deferred<T>.then(block: suspend (T) -> Unit): Job {
  return launch(context = UI) {
    try {
      block(this@then.await())
    } catch (e: Exception) {
      throw e
    }
  }
}