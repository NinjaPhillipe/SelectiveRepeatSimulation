package reso.examples.selectiverepeat;

import reso.common.AbstractApplication;
import reso.ip.IPHost;
import reso.ip.IPLayer;

/**
 * Classe qui recevra les données
 */
public class AppReceiver
	extends AbstractApplication
{

	private final IPLayer ip;

	static String recv = "";

	AppReceiver(IPHost host) {
		super(host, "receiver");
		ip= host.getIPLayer();
    }
	
	public void start() {
    	ip.addListener(SelectiveRepeatProtocol.IP_PROTO_SR, new SelectiveRepeatProtocol((IPHost) host));
    }
	
	public void stop() {}
	
}
