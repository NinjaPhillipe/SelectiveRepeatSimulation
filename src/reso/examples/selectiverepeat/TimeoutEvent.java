package reso.examples.selectiverepeat;

import reso.common.AbstractTimer;
import reso.scheduler.AbstractScheduler;

/**
 * Classe qui représente un timer.
 */
public class TimeoutEvent extends AbstractTimer {
    private static int count=0;
    private boolean canRun = true;

    private double startTime;

    private int ok;

    int id;
    private SelectiveRepeatProtocol protocol;

    /**
     * Constructeur du timer.
     * @param scheduler scheduler qui gere l'ordonnancement des événements et le temps.
     * @param interval interval de temps avant que le timer se déclenche
     * @param id_ numero de sequence du paquet sur lequel le timer s'execute.
     * @param protocol_
     */
    TimeoutEvent(AbstractScheduler scheduler, double interval, int id_, SelectiveRepeatProtocol protocol_){
        super(scheduler,interval,false);
        id=id_;
        protocol = protocol_;

        ok = count;
        count++;
    }

    @Override
    /**
     *  Action déclenchée lors du timeout d'un paquet
     */
    public void run() throws Exception{
        if(!canRun || protocol.getSend_base()>id) {
            System.out.println("\n TIMEOUT BROKEN");
        }else {
            protocol.timeoutRTO();

            canRun=false;
            protocol.logMSG("TIMEOUT PAQUET " + id );
            System.out.println("//////////////////////////////TIMEOUT paquet :" + id+ "  timer : "+ok);
            protocol.reSend(id);
            protocol.getControl().timeout();
        }
    }

    @Override
    public void start(){
        super.start();
        startTime = scheduler.getCurrentTime();
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

    /**
     * Retourne le temps passer depuis le lancement du timer
     */
    public double getTimeDiff(){
        return scheduler.getCurrentTime()-startTime;
    }

    boolean canRun(){
        return canRun;
    }
}
