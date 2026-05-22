package org.example;

import org.openjdk.jcstress.annotations.Actor;
import org.openjdk.jcstress.annotations.Arbiter;
import org.openjdk.jcstress.annotations.Expect;
import org.openjdk.jcstress.annotations.JCStressTest;
import org.openjdk.jcstress.annotations.Outcome;
import org.openjdk.jcstress.annotations.State;
import org.openjdk.jcstress.infra.results.I_Result;

@JCStressTest
@Outcome(id = "2", expect = Expect.ACCEPTABLE)
@Outcome(id = "1", expect = Expect.FORBIDDEN)
@State
public class SynchronizedCounterTest {

    volatile int counter;

    final Object lock = new Object();

    @Actor
    public void actor1() {
        synchronized (lock) {
            counter++;
        }
    }

    @Actor
    public void actor2() {
        synchronized (lock) {
            counter++;
        }
    }

    @Arbiter
    public void arbiter(I_Result r) {
        r.r1 = counter;
    }
}





// RESULT        SAMPLES     FREQ      EXPECT  DESCRIPTION
//     1              0    0,00%   Forbidden  
//     2  2 283 553 414  100,00%  Acceptable  
