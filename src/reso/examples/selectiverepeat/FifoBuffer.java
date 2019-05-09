package reso.examples.selectiverepeat;

public class FifoBuffer <E>{

    class Node{
        public E data;
        public Node next;
        public boolean ack = false;
        public Node(E data){
            this.data = data;
        }
    }

    public Node head;
    public Node tail;

    public FifoBuffer(){

    }

    public void add(E data){
        if(head == null){
            head = new Node(data);
            tail = head;
        }else{
//            System.out.println(tail);
            tail.next = new Node(data);
            tail = tail.next;
        }
    }
    public void addHead(E data)
    {
        if(head == null){
            head = new Node(data);
            tail = head;
        }else{
            Node tmp = new Node(data);
            tmp.next = head;
            head = tmp;
        }
    }

    public void fuse(FifoBuffer buf)
    {
        this.tail.next = buf.head;
        this.tail = buf.tail;
    }

    public E pop(){
        if(head !=null){
            E data = head.data;
            head = head.next;
            return data;
        }
        return null;
    }

    public static void main(String[] args){
        FifoBuffer<SelectiveRepeatMessage> fif = new FifoBuffer();
        fif.add(new SelectiveRepeatMessage(1));
        fif.add(new SelectiveRepeatMessage(2));

        System.out.println(fif.head.data); // 1
        System.out.println(fif.tail.data); // 2

        System.out.println(fif.pop());     // 1

        System.out.println(fif.head.data); // 2

    }




}
