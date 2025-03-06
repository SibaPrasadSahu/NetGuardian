import java.util.HashMap;
import java.util.Map;

public class AlertManager {
    private static final int DDOS_THRESHOLD = 100; 
    private static final int BANDWIDTH_THRESHOLD_MBPS = 100; 
    private static final int PORT_SCANNING_THRESHOLD = 10; // Example threshold

    private static Map<String, Integer> ipRequestCount = new HashMap<>();
    private static Map<String, Integer> ipPortCount = new HashMap<>();
    private static long lastCheckTime = System.currentTimeMillis();
    private static long totalBandwidth = 0;

    public static void checkForAlerts(PcapPacket packet) {
        long currentTime = System.currentTimeMillis();

        String sourceIp = DatabaseLogger.extractSourceIp(packet);
        ipRequestCount.put(sourceIp, ipRequestCount.getOrDefault(sourceIp, 0) + 1);

        if ((currentTime - lastCheckTime) >= 1000) { 
            ipRequestCount.forEach((ip, count) -> {
                if (count > DDOS_THRESHOLD) {
                    triggerAlert("Possible DDoS attack detected from IP: " + ip);
                }
            });
            ipRequestCount.clear();
            lastCheckTime = currentTime;
        }

        totalBandwidth += packet.size();
        if ((currentTime - lastCheckTime) >= 1000) {
            if (totalBandwidth > BANDWIDTH_THRESHOLD_MBPS * 1024 * 1024) {
                triggerAlert("Bandwidth overutilization detected.");
            } else if (totalBandwidth < BANDWIDTH_THRESHOLD_MBPS * 0.1 * 1024 * 1024) { 
                triggerAlert("Bandwidth underutilization detected.");
            }
            totalBandwidth = 0;
        }

        String protocol = DatabaseLogger.extractProtocol(packet);
        if ("TCP".equals(protocol)) {
            JLayer layer = packet.getPacket().getFirstLayer(JLayer.TCP);
            if (layer != null) {
                Tcp tcpLayer = (Tcp) layer;
                String destinationIp = DatabaseLogger.extractDestinationIp(packet);
                ipPortCount.put(destinationIp + ":" + tcpLayer.getDestination(), ipPortCount.getOrDefault(destinationIp + ":" + tcpLayer.getDestination(), 0) + 1);
                if (ipPortCount.get(destinationIp + ":" + tcpLayer.getDestination()) > PORT_SCANNING_THRESHOLD) {
                    triggerAlert("Possible port scanning detected on IP: " + destinationIp);
                }
            }
        }
    }

    private static void triggerAlert(String message) {
        System.out.println("ALERT: " + message);
        NetworkMonitorGUI.updateAlerts(message);
    }
}
