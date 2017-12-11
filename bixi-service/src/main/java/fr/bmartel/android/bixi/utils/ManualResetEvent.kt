package fr.bmartel.android.bixi.utils

/**
 * **Notifies one or more waiting threads that an event has occurred (otherwise
 * timing waiting)**
 *
 *
 *
 * href="http://stackoverflow.com/questions/1064596/what-is-javas-equivalent-of-manualresetevent"
 *
 */
class ManualResetEvent {

    /**
     * monitor is an object : it permits to synchronized both WaitOne methods
     * that are using both the variable open.
     */
    private val monitor = java.lang.Object()

    /** variable used to keep or release the wait timer  */
    @Volatile private var open = false

    /**
     * set timer parameter false put timer in wait mode if waitOne(X) functions
     * are called
     */
    constructor(open: Boolean) {
        this.open = open
    }

    /** wait for the required time. And return true only if time has elapsed  */
    @Throws(InterruptedException::class)
    fun waitOne(milliseconds: Long): Boolean {
        synchronized(monitor) {
            if (open)
                return true
            monitor.wait(milliseconds)
            return open
        }
    }

    /**
     * notify monitor object to get up and resume action
     */
    fun set() {
        synchronized(monitor) {
            open = true
            monitor.notifyAll()
        }
    }

    /**
     * reset is putting the variableopen back to its default value. As a
     * consequence, timer is reinitialized
     */
    fun reset() {
        open = false
    }
}