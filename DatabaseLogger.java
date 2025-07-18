import org.jnetpcap.packet.JLayer;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.structure.IpV4;
import org.jnetpcap.packet.structure.Tcp;
import org.jnetpcap.packet.structure.Udp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class DatabaseLogger {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/network_logs";
    private static final String USER = "siba";
    private static final String PASS = "siba01";

    public static void logPacket(PcapPacket packet) {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
            String query = "INSERT INTO network_traffic (source_ip, destination_ip, packet_size, protocol) VALUES (?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, extractSourceIp(packet));
                stmt.setString(2, extractDestinationIp(packet));
                stmt.setInt(3, packet.size());
                stmt.setString(4, extractProtocol(packet));
                stmt.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String extractSourceIp(PcapPacket packet) {
        JLayer layer = packet.getPacket().getFirstLayer(JLayer.IPV4);
        if (layer != null) {
            IpV4 ipV4Layer = (IpV4) layer;
            return ipV4Layer.getSource();
        }
        return "Unknown";
    }

    private static String extractDestinationIp(PcapPacket packet) {
        JLayer layer = packet.getPacket().getFirstLayer(JLayer.IPV4);
        if (layer != null) {
            IpV4 ipV4Layer = (IpV4) layer;
            return ipV4Layer.getDestination();
        }
        return "Unknown";
    }

    private static String extractProtocol(PcapPacket packet) {
        JLayer layer = packet.getPacket().getFirstLayer(JLayer.IPV4);
        if (layer != null) {
            IpV4 ipV4Layer = (IpV4) layer;
            switch (ipV4Layer.getProtocol()) {
                case IpV4.PROTOCOL_TCP:
                    return "TCP";
                case IpV4.PROTOCOL_UDP:
                    return "UDP";
                case IpV4.PROTOCOL_ICMP:
                    return "ICMP";
                default:
                    return "Unknown";
            }
        }
        return "Unknown";
    }
}
