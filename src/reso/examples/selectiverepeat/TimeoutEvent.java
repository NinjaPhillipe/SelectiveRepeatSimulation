package reso.examples.selectiverepeat;

import reso.common.AbstractTimer;
import reso.scheduler.AbstractScheduler;

public class TimeoutEvent extends AbstractTimer {
    public int id;
    private SelectiveRepeatProtocol protocol;

    public TimeoutEvent(AbstractScheduler scheduler,double interval,int id_,SelectiveRepeatProtocol protocol_){
        super(scheduler,interval,false);
        id=id_;
        protocol = protocol_;
    }

    public void run() throws Exception{
        System.out.println("TIMEOUT paquet :" + id);
        protocol.send(id);
    }
}
