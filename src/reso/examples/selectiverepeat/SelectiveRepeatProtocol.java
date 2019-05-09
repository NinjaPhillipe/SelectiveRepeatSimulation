package reso.examples.selectiverepeat;

import reso.ip.*;
import reso.scheduler.AbstractScheduler;

public class SelectiveRepeatProtocol implements IPInterfaceListener {

	private CongestionControl control ;
	public static final int IP_PROTO_SR = Datagram.allocateProtocolNumber("SelectiveRepeat");
	public static AbstractScheduler scheduler;
	
	private final IPHost host;

	private IPAddress dst;

	private int size = 6;

	private double TIMEOUT = 10;

	//congestion control
	private int expectedSeq = 0;
	private int count = 0;

	public void windowControl()
	{
		control.control();
	}

	public void switchToSlowStart()
	{
		CongestionControl slowStart = new SlowStart();
		this.control = slowStart;
	}
	public void switchToAdditiveIncrease()
	{
		CongestionControl additiveIncrease = new AdditiveIncrease();
		this.control = additiveIncrease ;
	}

	// SR sender
	private int send_base = 0;
	private int next_seq_num = 0;
	private FifoWindow<SelectiveRepeatMessage> sendingWindow = new FifoWindow<>();

	// SR receiver
	private int recv_base = 0;
	private FifoWindow<SelectiveRepeatMessage> receiveWindow = new FifoWindow<>();


	// BUFFER de paquets
	private FifoBuffer<SelectiveRepeatMessage> buffer = new FifoBuffer<>();

	// TIMER
	private FifoWindow<TimeoutEvent> timeoutBuffer = new FifoWindow<>();

	public SelectiveRepeatProtocol(IPHost host) {
		this.host= host;
		for(int i = 0 ; i < size ; i++ ){
			receiveWindow.add(null);
		}
	}
	
	@Override
	public void receive(IPInterfaceAdapter src, Datagram datagram) throws Exception {

    	SelectiveRepeatMessage msg= (SelectiveRepeatMessage) datagram.getPayload();
		System.out.println("\n +++++++++++++++++++++++++++++++++++++\nreceive" + msg+ "\n------------------\n");

		if(!msg.isAck){ // c'est le receveur qui recoit un packet

			///////////////////////////////////////////////////////////////////////////////////////////////////
			//                             Si le receveur recoit un paquet                                   //
			///////////////////////////////////////////////////////////////////////////////////////////////////
			if(recv_base <= msg.num  && msg.num <= recv_base+size-1 ){
				if(Math.random()<0.2) {
					System.out.println("PACKET LOST"+msg);
				}else {
					System.out.println("JE RENTRE DANS LA BOUCLE");
					host.getIPLayer().send(IPAddress.ANY, datagram.src, IP_PROTO_SR, new SelectiveRepeatMessage(msg.num, recv_base));
					receiveWindow.setData(msg, msg.num - recv_base);
					if (recv_base == msg.num) {
						System.out.println("\nRECEIVE WINDOW: \n" + receiveWindow + "\n");

						// delivrer
						AppReceiver.recv += receiveWindow.pop().data;
						receiveWindow.add(null);
						System.out.println("ReceiverData : " + AppReceiver.recv);

						recv_base++;

						while (receiveWindow.head.data != null) {

							System.out.println("\nRECEIVE WINDOW: \n" + receiveWindow + "\n");

							// delivrer
							AppReceiver.recv += receiveWindow.pop().data;
							receiveWindow.add(null);

							System.out.println("ReceiverData: " + AppReceiver.recv);

							recv_base++;
						}
					} else {
						System.out.println("Packet in Receiver buffer ");
						//receiveWindow.setData(msg, msg.num);
					}
				}
			}else if(recv_base-size <= msg.num && msg.num <= recv_base-1){
				// renvoi un ACK qui a du etre perdu
				host.getIPLayer().send(IPAddress.ANY, datagram.src, IP_PROTO_SR, new SelectiveRepeatMessage(msg.num,recv_base));
			}
//			System.out.println("\n is not ack " + msg.num);
		}else { // si c'est un ACK

			///////////////////////////////////////////////////////////////////////////////////////////////////
			//                             Si l'envoyeur recoit un ACK                                       //
			///////////////////////////////////////////////////////////////////////////////////////////////////


			// Ajout d'un facteur aléatoire de perte de paquets
			if (Math.random() < 0.2) { //
				System.out.println("ACK not received");

				// reenvoie le paquet
//					host.getIPLayer().sendData(IPAddress.ANY, datagram.src, IP_PROTO_SR, new SelectiveRepeatMessage(msg.num, true));
			}
			else if(msg.num >=send_base && msg.num <= send_base+size-1) { // si le packet est bien dans la fenetre d'envoi

				////////////////////////////////////congestion control/////////////////////////////////////////////

				if( msg.expected == expectedSeq ){
					count++;
//					System.out.println("duplicate expected: "+expectedSeq+"  count:"+count);
				}else {
//					System.out.println("CANCEL DUPLIC -***********************************************");
					count= 0;
					expectedSeq = msg.expected;
				}

//				if(count == 3)
//					System.out.println("INFO"+ (timeoutBuffer.get(expectedSeq-send_base).canRun())+" "+( send_base <= expectedSeq && expectedSeq <= send_base+size-1) );


				if(count == 3 && timeoutBuffer.get(expectedSeq-send_base).canRun() && ( send_base <= expectedSeq && expectedSeq <= send_base+size-1) ){
					// on renvoie le paquet
					System.out.println("RESEND PACKET before timer expiration " + expectedSeq);
					timeoutBuffer.get(expectedSeq-send_base).stop();
					reSend(expectedSeq);
				}

				///////////////////////////////////////////////////////////////////////////////////////////////////

				// stop timer
				timeoutBuffer.get(msg.num-send_base).stop();
				System.out.println("STOP TIMER");
				// on set l'ACK a true
				System.out.println(msg.num);
				sendingWindow.setAck(msg.num-send_base,true);

				System.out.println("accepted ACK " + msg.num);
				if(msg.num == send_base) {
					while (sendingWindow.head != null && sendingWindow.head.ack) {

						send_base++;
						// on eneleve le premier element
						sendingWindow.pop();
						timeoutBuffer.pop().stop();


						// on rajoute le prochain message du buffer a la fenetre
						if (buffer.head != null) {
							SelectiveRepeatMessage msgTmp = buffer.pop();
							sendingWindow.add(msgTmp);
//							if(sendingWindow.size< sendingWindow.size)
//								timeoutBuffer.add(null);
//							timeoutBuffer.add(new TimeoutEvent(scheduler, TIMEOUT, msgTmp.num, this));
//							timeoutBuffer.tail.data.start();

//							System.out.println("========================================="+sendingWindow.tail.data.num);
							send(sendingWindow.tail.data.num);
						}


//						if (!allPcktSendedOnce) // si tout les paquets n'ont pas encore ete envoyer envoyer le suivant
//							send(sendingWindow.size + send_base - 1);
//						if (buffer.head == null)
//							allPcktSendedOnce = true;

						System.out.println("\nSENDER WINDOW: \n" + sendingWindow + "\n");
					}
				}
			}
		}
	}

	public void sendData(IPAddress dst_, String data) throws  Exception {
		dst = dst_;

		int id = next_seq_num;

		final int sizeData = 4;

		int nbPackets = data.length()/sizeData;
		if(data.length()%sizeData > 0 )
			nbPackets++;

		int j = 0;
		for(int i = 0 ; i < nbPackets ; i++ ){
			System.out.println(i);
			String tmp = "";
			for(int k = 0 ; k < sizeData && j<data.length() ; k++){
				tmp+= data.toCharArray()[j];
				j++;
			}

			SelectiveRepeatMessage msg = new SelectiveRepeatMessage(id,tmp);
			id++;
//			System.out.println(next_seq_num+"<"+send_base+"+"+size);
			if(next_seq_num < send_base + size){
				System.out.println(i+"ok");

				// ajoute paquet dans la fenetre
				sendingWindow.add(msg);
				// ajoute un timeout
//				timeoutBuffer.add(new TimeoutEvent(scheduler,TIMEOUT,msg.num,this));
//				timeoutBuffer.tail.data.start();

//				System.out.println("=============================================\n"+timeoutBuffer);
//				System.out.println(sendingWindow+"\n=============================================\n");

//				host.getIPLayer().send(IPAddress.ANY, dst_, IP_PROTO_SR, msg);

				send(i-send_base);
//				System.out.println("Packet send : "+i + "  " +sendingWindow.get(i-send_base));
				// start timer
//				next_seq_num++;
			}else{
				buffer.add(msg);
			}
			// ajouter les paquets a un buffer pour pouvoir les envoyer quand la fenetre se decalera

		}
	}

	public void send(int n) throws Exception{
		if(send_base <= n && n <= send_base+size){
			host.getIPLayer().send(IPAddress.ANY, dst, IP_PROTO_SR, sendingWindow.get(n-send_base));

			System.out.println("Packet send : "+n + "  " +sendingWindow.get(n-send_base));

//			if(timeoutBuffer.get(n-send_base)!=null)
//				timeoutBuffer.get(n-send_base).stop();
			TimeoutEvent tmp = new TimeoutEvent(scheduler,TIMEOUT,n,this);
			timeoutBuffer.setData(tmp,n-send_base);
			tmp.start(); // remets le timer
			System.out.println(timeoutBuffer.get(n-send_base));

//			if(n == next_seq_num+1)
			next_seq_num++;
		}
	}
	public void reSend(int n) throws  Exception{
		System.out.println("Resend packet :"+sendingWindow.get(n-send_base));
		host.getIPLayer().send(IPAddress.ANY, dst, IP_PROTO_SR, sendingWindow.get(n-send_base));
		TimeoutEvent tmp = new TimeoutEvent(scheduler,TIMEOUT,n,this);
		timeoutBuffer.setData(tmp,n-send_base);
		tmp.start(); // remets le timer
		/*timeoutBuffer.setData(new TimeoutEvent(scheduler,TIMEOUT,n,this),n-send_base);
		timeoutBuffer.get(n-send_base).start(); // remets le timer*/
	}

	public FifoWindow<SelectiveRepeatMessage> getReceiveWindow() {
		return receiveWindow;
	}
	public FifoBuffer<SelectiveRepeatMessage> getBuffer() {return buffer;}

}
