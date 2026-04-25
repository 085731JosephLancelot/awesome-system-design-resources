import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Least Connections load balancing algorithm.
 * Routes each new request to the server with the fewest active connections.
 */
public class LeastConnections {
    private Map<String, Integer> serverConnections;

    public LeastConnections(List<String> servers) {
        serverConnections = new HashMap<>();
        for (String server : servers) {
            serverConnections.put(server, 0);
        }
    }

    public String getNextServer() {
        return serverConnections.entrySet().stream()
                .min(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    // Increments the connection count for the given server
    public void acquireConnection(String server) {
        serverConnections.computeIfPresent(server, (k, v) -> v + 1);
    }

    public void releaseConnection(String server) {
        serverConnections.computeIfPresent(server, (k, v) -> v > 0 ? v - 1 : 0);
    }

    public static void main(String[] args) {
        List<String> servers = List.of("Server1", "Server2", "Server3");
        LeastConnections leastConnectionsLB = new LeastConnections(servers);

        for (int i = 0; i < 6; i++) {
            String server = leastConnectionsLB.getNextServer();
            System.out.println("Request " + (i + 1) + " -> " + server);
            leastConnectionsLB.acquireConnection(server);
            leastConnectionsLB.releaseConnection(server);
        }
    }
}
