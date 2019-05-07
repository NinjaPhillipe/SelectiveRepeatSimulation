package reso.examples.selectiverepeat;

import reso.common.AbstractTimer;
import reso.scheduler.AbstractScheduler;

public class TimeoutEvent extends AbstractTimer {
    public TimeoutEvent(AbstractScheduler scheduler,double interval){
        super(scheduler,interval,false);
    }

    public void run() throws Exception{
        System.out.println("TIMEOUT");
    }
}
