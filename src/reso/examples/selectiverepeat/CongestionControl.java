package reso.examples.selectiverepeat;

import reso.common.Node;

public  abstract class CongestionControl {

    public abstract void control();
    public SelectiveRepeatProtocol protocol;
    public int sstresh = 4;
    public int count = 0;

    public CongestionControl(SelectiveRepeatProtocol srProto){
        protocol = srProto;
    }

    public void reset()
    {
        this.count = 0;
    }

    public void changeReceiveWindowSize()
    {

    }

    public void ACKDuplicate3Times(){
        System.out.println("ACK 3 DUPLI");
        this.sstresh = protocol.getReceiveWindow().size / 2;
        FifoWindow window = this.protocol.getReceiveWindow();
        this.protocol.switchToAdditiveIncrease();

        FifoBuffer buf = window.split();
        buf.fuse(protocol.getBuffer());
//        this.protocol.switchToSlowStart();
    }
    public void timeout(){
        // set size 1
        /*FifoBuffer tmp = new FifoBuffer();
        tmp.head = protocol.getSendingWindow().head.next;
        tmp.tail = protocol.getSendingWindow().tail;
        tmp.fuse(protocol.getBuffer());
        protocol.setBuffer(tmp);
        FifoWindow<SelectiveRepeatMessage> pp = new FifoWindow<>();
*//*
        protocol.getSendingWindow().head.next = null;
*//*
        pp.add(protocol.getSendingWindow().head.data);
        protocol.setSendingWindow(pp);
        //protocol.getSendingWindow().tail = protocol.getSendingWindow().head;

        this.protocol.switchToSlowStart();
        FifoWindow<TimeoutEvent> tim = protocol.getTimeoutBuffer();
        FifoBuffer<TimeoutEvent>.Node tmp2 = tim.head;
        for(int i = 0 ; i< tim.size ; i++){
            tmp2 = tmp2.next;
            if(tmp2!=null && tmp2.data!=null)
                tmp2.data.stop();
        }
        tim.tail = tim.head;
        tim.head.next = null;*/
        System.out.println("NICOLAS");
        FifoWindow<SelectiveRepeatMessage> window = protocol.getSendingWindow();
        window.split(1).fuse(protocol.getBuffer());
        protocol.switchToSlowStart();
        FifoWindow<TimeoutEvent> tim = protocol.getTimeoutBuffer();
        FifoWindow<TimeoutEvent>nul = tim.split(1);
        FifoBuffer<TimeoutEvent>.Node tmp = nul.head;
        while (tmp.next != null)
        {
            tmp.data.stop();
            tmp= tmp.next;
        }

        System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        System.out.println(tim);
        /*for(int i = 0 ; i< tim.size ; i++){
            tmp2 = tmp2.next;
            if(tmp2!=null && tmp2.data!=null)
                tmp2.data.stop();
        }*/






    }

    public int getSstresh() { return sstresh; }
}
