package org.example;

import org.openjdk.jcstress.annotations.Actor;
import org.openjdk.jcstress.annotations.Arbiter;
import org.openjdk.jcstress.annotations.Expect;
import org.openjdk.jcstress.annotations.JCStressTest;
import org.openjdk.jcstress.annotations.Outcome;
import org.openjdk.jcstress.annotations.State;
import org.openjdk.jcstress.infra.results.I_Result;

@JCStressTest
@Outcome(id = "42", expect = Expect.ACCEPTABLE)
@Outcome(id = "0", expect = Expect.ACCEPTABLE)
@State
public class HappensBeforeTransitivityTest {

    int x;
    int y;

    volatile boolean ready;

    @Actor
    public void actor1() {
        x = 42;
        ready = true;
    }

    @Actor
    public void actor2() {
        if (ready) {
            y = x;
        }
    }

    @Arbiter
    public void arbiter(I_Result r) {
        r.r1 = y;
    }
}





// RESULT        SAMPLES     FREQ      EXPECT  DESCRIPTION
//     0  3 514 553 560   56,20%  Acceptable  
//     42  2 739 088 814   43,80%  Acceptable