package org.example;

import org.openjdk.jcstress.annotations.Actor;
import org.openjdk.jcstress.annotations.Expect;
import org.openjdk.jcstress.annotations.JCStressTest;
import org.openjdk.jcstress.annotations.Outcome;
import org.openjdk.jcstress.annotations.State;
import org.openjdk.jcstress.infra.results.II_Result;

@JCStressTest
@Outcome(id = "1, 1", expect = Expect.ACCEPTABLE, 
         desc = "Оба потока увидели записи друг друга")
@Outcome(id = "1, 0", expect = Expect.ACCEPTABLE, 
         desc = "Поток 0 увидел y=1, Поток 1 не увидел x=1")
@Outcome(id = "0, 1", expect = Expect.ACCEPTABLE, 
         desc = "Поток 1 увидел x=1, Поток 0 не увидел y=1")
@Outcome(id = "0, 0", expect = Expect.FORBIDDEN, 
         desc = "StoreLoad reordering запрещён")
@State
public class StoreLoadVolatileTest {

    volatile int x = 0;
    volatile int y = 0;

    @Actor
    public void thread0(II_Result r) {
        x = 1; 
        r.r1 = y;  
    }

    @Actor
    public void thread1(II_Result r) {
        y = 1;     
        r.r2 = x;  
    }
}


// RESULT        SAMPLES     FREQ      EXPECT  DESCRIPTION
// 0, 0              0    0,00%   Forbidden  StoreLoad reordering запрещён
// 0, 1  1 370 515 997   50,28%  Acceptable  Поток 1 увидел x=1, Поток 0 не увидел y=1
// 1, 0  1 349 454 265   49,50%  Acceptable  Поток 0 увидел y=1, Поток 1 не увидел x=1
// 1, 1      6 012 592    0,22%  Acceptable  Оба потока увидели записи друг друга