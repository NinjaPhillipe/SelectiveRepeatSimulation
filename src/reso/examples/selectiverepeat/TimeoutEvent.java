package reso.examples.selectiverepeat;

import reso.common.AbstractTimer;
import reso.scheduler.AbstractScheduler;

public class TimeoutEvent extends AbstractTimer {
    private static int count=0;
    private boolean canRun = true;


    private int ok;

    public int id;
    private SelectiveRepeatProtocol protocol;

    public TimeoutEvent(AbstractScheduler scheduler,double interval,int id_,SelectiveRepeatProtocol protocol_){
        super(scheduler,interval,false);
        id=id_;
        protocol = protocol_;

//        if(count>30)
//            System.exit(1);

        ok = count;
        count++;
//        System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<Timer created<"+ok + " for packet "+id);
    }

    public void run() throws Exception{
        if(!canRun || protocol.getSend_base()>id) {
            System.out.println("\n TIMEOUT BROKEN");
            return;
        }
        canRun=false;
        System.out.println("//////////////////////////////TIMEOUT paquet :" + id+ "  timer : "+ok);
        System.out.println("send base " + protocol.getSend_base());
        System.out.println("recv base " + protocol.getRecv_base());
        System.out.println(protocol.getSendingWindow());
        System.out.println(protocol.getReceiveWindow());
//        protocol.send(id);
        protocol.reSend(id);
    }

    @Override
    public void stop() {
        super.stop();
        canRun = false;
        System.out.println("STOP TIMER "+ok + " PACKET : "+id);
    }
    public String toString() {
        return "Timer : "+ok +" packet= " + id ;
    }

    public boolean canRun(){
        return canRun;
    }
}
