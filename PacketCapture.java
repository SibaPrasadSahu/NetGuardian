import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PcapPacketHandler;

import java.util.ArrayList;
import java.util.List;

public class PacketCapture {
    private Pcap pcap;
    private List<PcapPacket> packets = new ArrayList<>();

    public PacketCapture(String device) throws Exception {
        PcapIf deviceHandle = Pcap.lookupDev(device);
        pcap = Pcap.openLive(deviceHandle.getName(), 65535, Pcap.MODE_PROMISCUOUS, 1000);
        pcap.loop(-1, new PacketHandler());
    }

    private class PacketHandler implements PcapPacketHandler<PcapPacket> {
        @Override
        public void nextPacket(PcapPacket packet, Object o) {
            packets.add(packet);
            System.out.println("Captured packet: " + packet);
            DatabaseLogger.logPacket(packet);
            AlertManager.checkForAlerts(packet);
        }
    }

    public static void main(String[] args) throws Exception {
        new PacketCapture("eth0");
    }
}
