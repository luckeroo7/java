package org.example;

import org.openjdk.jcstress.annotations.Actor;
import org.openjdk.jcstress.annotations.Arbiter;
import org.openjdk.jcstress.annotations.Expect;
import org.openjdk.jcstress.annotations.JCStressTest;
import org.openjdk.jcstress.annotations.Outcome;
import org.openjdk.jcstress.annotations.State;
import org.openjdk.jcstress.infra.results.I_Result;

@JCStressTest
@Outcome(id = "1", expect = Expect.ACCEPTABLE_INTERESTING)
@Outcome(id = "2", expect = Expect.ACCEPTABLE)
@State
public class DataRaceCounterTest {

    volatile int counter;

    @Actor
    public void actor1() {
        counter++;
    }

    @Actor
    public void actor2() {
        counter++;
    }

    @Arbiter
    public void arbiter(I_Result r) {
        r.r1 = counter;
    }
}




// RESULT        SAMPLES     FREQ       EXPECT  DESCRIPTION
//     1  3 354 875 101   47,11%  Interesting  
//     2  3 766 535 593   52,89%   Acceptable 