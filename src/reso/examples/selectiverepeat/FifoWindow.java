package reso.examples.selectiverepeat;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Fifo buffer with an attribute size
 * @param <E>
 */
public class FifoWindow<E> extends FifoBuffer<E> {


    public int size = 0;


    public FifoWindow<E> split()
    {
       int medium = this.size/ 2;
       FifoWindow<E> window = new FifoWindow<>();
       int j = 0;
       Node tmp = head;
       while ( j < medium -1)
       {
           tmp= tmp.next;
           j++;
       }
       window.head = tmp.next;
       window.tail = this.tail;
       this.tail = tmp;
       tail.next = null;
       return window;
    }

    @Override
    public void add(E data){
        super.add(data);
        size++;
    }

    @Override
    public E pop(){
        if(head !=null){
            E data = head.data;
            head = head.next;
            size--;
            return data;
        }
        return null;
    }

    public void setAck(int i , boolean isAck){
        Node tmp = head;
        if(i<size){
            for(int j = 0 ; j<i ; j++){
                tmp = tmp.next;
            }
            tmp.ack = isAck;
        }
    }

    public boolean isAck(int i){
        Node tmp = head;
        if(i<size){
            for(int j = 0 ; j<i ; j++){
                tmp = tmp.next;
            }
            return tmp.ack;
        }
        return false;
    }

    public void setData(E data, int i){
        if(head == null) {
            head = new Node(data);
            tail = head;
        }else {
        Node tmp = head;

        while( !(i<size) ){
            this.add(null); // ajoute un message null
        }

        for(int j = 0 ; j<i ; j++){
            tmp = tmp.next;
        }
//        if(tmp!=null)
            tmp.data = data;
        }

    }

    public E get(int i ){
        if(i> size)
            return null;
        Node tmp = head;
        for(int j = 0 ; j < i ; j++ ){
//            System.out.println(tmp.data);
            tmp = tmp.next;
        }
        if(tmp !=null)
            return tmp.data;
        return null;
    }


    public static void main ( String [] args)
    {
        FifoWindow<String> pp = new FifoWindow<>();
        pp.add("UN");
        pp.add("deux");
        pp.add("trois");
        pp.add("quatre");
            pp.add("cinq");
            pp.add("six");
        System.out.println(pp);
        FifoWindow<String> ok =pp.split();
        System.out.println(pp);
        System.out.println(ok);
    }
}
