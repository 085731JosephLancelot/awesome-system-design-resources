import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class RoundRobin {
    private List<String> servers;
    private AtomicInteger index;

    public RoundRobin(List<String> servers) {
        if (servers == null || servers.isEmpty()) {
            throw new IllegalArgumentException("Server list must not be null or empty");
        }
        this.servers = servers;
        // Start at -1 so first incrementAndGet yields index 0
        this.index = new AtomicInteger(-1);
    }

    public String getNextServer() {
        int currentIndex = index.incrementAndGet() % servers.size();
        return servers.get(currentIndex);
    }

    public static void main(String[] args) {
        List<String> servers = List.of("Server1", "Server2", "Server3");
        RoundRobin roundRobinLB = new RoundRobin(servers);

        for (int i = 0; i < 6; i++) {
            System.out.println(roundRobinLB.getNextServer());
        }
    }
}
