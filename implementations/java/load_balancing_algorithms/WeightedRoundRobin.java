import java.util.List;

/**
 * Weighted Round Robin Load Balancer
 * Higher weight = more requests routed to that server.
 * Example: weights [5, 1, 1] means Server1 gets ~5/7 of requests.
 */
public class WeightedRoundRobin {
    private List<String> servers;
    private List<Integer> weights;
    private int currentIndex;
    private int currentWeight;

    public WeightedRoundRobin(List<String> servers, List<Integer> weights) {
        if (servers.size() != weights.size()) {
            throw new IllegalArgumentException("Servers and weights lists must be the same size");
        }
        this.servers = servers;
        this.weights = weights;
        this.currentIndex = -1;
        this.currentWeight = 0;
    }

    public String getNextServer() {
        while (true) {
            currentIndex = (currentIndex + 1) % servers.size();
            if (currentIndex == 0) {
                currentWeight--;
                if (currentWeight <= 0) {
                    currentWeight = getMaxWeight();
                }
            }
            if (weights.get(currentIndex) >= currentWeight) {
                return servers.get(currentIndex);
            }
        }
    }

    private int getMaxWeight() {
        return weights.stream().max(Integer::compare).orElse(0);
    }

    public static void main(String[] args) {
        List<String> servers = List.of("Server1", "Server2", "Server3");
        List<Integer> weights = List.of(5, 1, 1);
        WeightedRoundRobin weightedRoundRobinLB = new WeightedRoundRobin(servers, weights);

        // Print 7 requests to verify distribution matches weights (5:1:1)
        for (int i = 0; i < 7; i++) {
            System.out.println(weightedRoundRobinLB.getNextServer());
        }
    }
}
