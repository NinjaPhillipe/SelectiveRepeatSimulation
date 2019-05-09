package reso.examples.selectiverepeat;

/**
 * Fifo buffer with an attribute size
 * @param <E>
 */
public class FifoWindow<E> extends FifoBuffer<E> {


    public int size = 0;


    public FifoBuffer split(int i)
    {
        FifoBuffer<E> buf = new FifoBuffer<>();
        for (int j = 0 ; j < this.size ; j++)
            if (j >  i)
        {
            buf.add(this.get(j));
            //pas fini
        }
        return buf;
    }

    public void remove(E data)
    {
        //pas fini
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
        tmp.data = data;}

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

    public String toString() {
        String res = "{";
        Node tmp = head;
        while (tmp != null){
            res+= ( tmp.data +" " + tmp.ack + " , ");
            tmp = tmp.next;
        }
        res += "}";
        return res;
    }

}
