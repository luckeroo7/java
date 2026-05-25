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
@Outcome(id = "0, 0", expect = Expect.ACCEPTABLE_INTERESTING, 
         desc = "оба чтения выполнились ДО записей другого потока")

@State
public class StoreLoadReorderingTest {

    int x = 0; 
    int y = 0; 

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



// RESULT        SAMPLES     FREQ       EXPECT  DESCRIPTION
// 0, 0  2 896 188 274   46,62%  Interesting  оба чтения выполнились ДО записей другого потока
// 0, 1  1 649 155 623   26,55%   Acceptable  Поток 1 увидел x=1, Поток 0 не увидел y=1
// 1, 0  1 666 488 697   26,83%   Acceptable  Поток 0 увидел y=1, Поток 1 не увидел x=1
// 1, 1         81 780   <0,01%   Acceptable  Оба потока увидели записи