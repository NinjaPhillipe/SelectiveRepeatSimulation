package reso.examples.selectiverepeat;

import reso.common.Link;
import reso.common.Network;
import reso.ethernet.EthernetAddress;
import reso.ethernet.EthernetFrame;
import reso.ethernet.EthernetInterface;
import reso.examples.static_routing.AppSniffer;
import reso.ip.IPAddress;
import reso.ip.IPEthernetAdapter;
import reso.ip.IPHost;
import reso.scheduler.AbstractScheduler;
import reso.scheduler.Scheduler;
import reso.utilities.NetworkBuilder;

public class Demo
{

	/* Enable or disable packet capture (can be used to observe ARP messages) */
	private static final boolean ENABLE_SNIFFER= false;

    public static void main(String [] args) {
		AbstractScheduler scheduler= new Scheduler();
		Network network= new Network(scheduler);
    	try {
    		final EthernetAddress MAC_ADDR1= EthernetAddress.getByAddress(0x00, 0x26, 0xbb, 0x4e, 0xfc, 0x28);
    		final EthernetAddress MAC_ADDR2= EthernetAddress.getByAddress(0x00, 0x26, 0xbb, 0x4e, 0xfc, 0x29);
    		final IPAddress IP_ADDR1= IPAddress.getByAddress(192, 168, 0, 1);
    		final IPAddress IP_ADDR2= IPAddress.getByAddress(192, 168, 0, 2);

    		IPHost host1= NetworkBuilder.createHost(network, "H1", IP_ADDR1, MAC_ADDR1);
    		host1.getIPLayer().addRoute(IP_ADDR2, "eth0");
    		if (ENABLE_SNIFFER)
				host1.addApplication(new AppSniffer(host1, new String [] {"eth0"}));

    		AppSender sender = new AppSender(host1, IP_ADDR2, 5);
    		host1.addApplication(sender);

    		IPHost host2= NetworkBuilder.createHost(network,"H2", IP_ADDR2, MAC_ADDR2);
    		host2.getIPLayer().addRoute(IP_ADDR1, "eth0");
    		host2.addApplication(new AppReceiver(host2));

    		EthernetInterface h1_eth0= (EthernetInterface) host1.getInterfaceByName("eth0");
    		EthernetInterface h2_eth0= (EthernetInterface) host2.getInterfaceByName("eth0");
    		
    		// Connect both interfaces with a 5000km long link
    		new Link<EthernetFrame>(h1_eth0, h2_eth0, 5000000, 100000);

    		// degeu
			((IPEthernetAdapter)host2.getIPLayer().getInterfaceByName("eth0")).addARPEntry(IP_ADDR1,MAC_ADDR1);
			((IPEthernetAdapter)host1.getIPLayer().getInterfaceByName("eth0")).addARPEntry(IP_ADDR2,MAC_ADDR2);

			SelectiveRepeatProtocol.scheduler = scheduler;

    		host1.start();
    		host2.start();


    		scheduler.run();
			System.out.println(AppReceiver.recv);



		} catch (Exception e) {
    		System.err.println(e.getMessage());
    		e.printStackTrace(System.err);
    	}
    }

}
