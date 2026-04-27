package implementations.java.consistent_hashing;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class ConsistentHashing {
    private final int numReplicas; // Number of virtual nodes per server
    private final TreeMap<Long, String> ring; // Hash ring storing virtual nodes
    private final Set<String> servers; // Set of physical servers

    // Note: Higher numReplicas = more even distribution but more memory usage.
    // 150 virtual nodes is a commonly recommended default in production systems.
    // I've seen some sources recommend as low as 100 or as high as 200 depending
    // on cluster size — keeping 150 as a reasonable middle ground here.
    private static final int DEFAULT_REPLICAS = 150;

    public ConsistentHashing(List<String> servers) {
        this(servers, DEFAULT_REPLICAS);
    }

    public ConsistentHashing(List<String> servers, int numReplicas) {
        if (numReplicas <= 0) {
            throw new IllegalArgumentException("numReplicas must be greater than 0");
        }
        this.numReplicas = numReplicas;
        this.ring = new TreeMap<>();
        this.servers = new HashSet<>();

        // Add each server to the hash ring
        for (String server : servers) {
            addServer(server);
        }
    }

    private long hash(String key) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(key.getBytes());
            byte[] digest = md.digest();
            return ((long) (digest[0] & 0xFF) << 24) |
                   ((long) (digest[1] & 0xFF) << 16) |
                   ((long) (digest[2] & 0xFF) << 8) |
                   ((long) (digest[3] & 0xFF));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 algorithm not found", e);
        }
    }

    public void addServer(String server) {
        servers.add(server);
        for (int i = 0; i < numReplicas; i++) {
            long hash = hash(server + "-" + i); // Unique hash for each virtual node
            ring.put(hash, server);
        }
    }

    public void removeServer(String server) {
        if (servers.remove(server)) {
            for (int i = 0; i < numReplicas; i++) {
                long hash = hash(server + "-" + i);
                ring.remove(hash);
            }
        }
    }

    public String getServer(String key) {
        if (ring.isEmpty()) {
            return null; // No servers available
        }

        long hash = hash(key);
        // Find the closest server in a clockwise direction
        Map.Entry<Long, String> entry = ring.ceilingEntry(hash);
        if (entry == null) {
            // If we exceed the highest node, wrap around to the first node
            entry = ring.firstEntry();
        }
        return entry.getValue();
    }

    public static void main(String[] args) {
        List<String> servers = Arrays.asList("S0", "S1", "S2", "S3", "S4", "S5");
        ConsistentHashing ch = new ConsistentHashing(servers, 3);

        // Step 2: Assign requests (keys) to servers
        System.out.println("UserA is assigned to: " + ch.getServer("UserA"));
        System.out.println("UserB is assigned to: " + ch.getServer("UserB"));

        // Step 
