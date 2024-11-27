import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class MetroShortestRoute {

    // Graph representation using adjacency list
    private Map<String, List<Edge>> metroMap = new HashMap<>();

    // Adds a station to the metro map
    public void addStation(String station) {
        metroMap.putIfAbsent(station, new ArrayList<>());
    }

    // Adds a connection between stations with a given weight (distance/time)
    public void addConnection(String from, String to, int weight) {
        metroMap.get(from).add(new Edge(to, weight));
        metroMap.get(to).add(new Edge(from, weight)); // Assuming bidirectional connection
    }

    // Finds the shortest route using Dijkstra's algorithm
    public List<String> findShortestRoute(String start, String destination) {
        Map<String, Integer> distances = new HashMap<>();
        Map<String, String> previous = new HashMap<>();
        PriorityQueue<Edge> pq = new PriorityQueue<>(Comparator.comparingInt(edge -> edge.weight));

        for (String station : metroMap.keySet()) {
            distances.put(station, Integer.MAX_VALUE);
        }
        distances.put(start, 0);
        pq.add(new Edge(start, 0));

        while (!pq.isEmpty()) {
            Edge current = pq.poll();
            String currentStation = current.node;

            for (Edge edge : metroMap.get(currentStation)) {
                int newDistance = distances.get(currentStation) + edge.weight;
                if (newDistance < distances.get(edge.node)) {
                    distances.put(edge.node, newDistance);
                    previous.put(edge.node, currentStation);
                    pq.add(new Edge(edge.node, newDistance));
                }
            }
        }

        List<String> route = new ArrayList<>();
        for (String at = destination; at != null; at = previous.get(at)) {
            route.add(at);
        }
        Collections.reverse(route);

        if (route.size() == 1 && !route.get(0).equals(start)) {
            return Collections.emptyList(); // No route found
        }
        return route;
    }

    // Edge class for graph edges
    static class Edge {
        String node;
        int weight;

        Edge(String node, int weight) {
            this.node = node;
            this.weight = weight;
        }
    }

    // GUI Implementation
    public static void main(String[] args) {
        MetroShortestRoute metro = new MetroShortestRoute();

        JFrame frame = new JFrame("Metro Shortest Route Finder");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 500);
        frame.setLayout(new BorderLayout(10, 10));

        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder("Add Stations and Connections"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Components for adding stations
        JLabel stationLabel = new JLabel("Station Name:");
        JTextField stationField = new JTextField(15);
        JButton addStationButton = new JButton("Add Station");

        gbc.gridx = 0; gbc.gridy = 0;
        inputPanel.add(stationLabel, gbc);
        gbc.gridx = 1;
        inputPanel.add(stationField, gbc);
        gbc.gridx = 2;
        inputPanel.add(addStationButton, gbc);

        // Components for adding connections
        JLabel fromLabel = new JLabel("From Station:");
        JTextField fromField = new JTextField(10);
        JLabel toLabel = new JLabel("To Station:");
        JTextField toField = new JTextField(10);
        JLabel weightLabel = new JLabel("Distance/Time:");
        JTextField weightField = new JTextField(5);
        JButton addConnectionButton = new JButton("Add Connection");

        gbc.gridx = 0; gbc.gridy = 1;
        inputPanel.add(fromLabel, gbc);
        gbc.gridx = 1;
        inputPanel.add(fromField, gbc);
        gbc.gridx = 2;
        inputPanel.add(toLabel, gbc);
        gbc.gridx = 3;
        inputPanel.add(toField, gbc);
        gbc.gridx = 4;
        inputPanel.add(weightLabel, gbc);
        gbc.gridx = 5;
        inputPanel.add(weightField, gbc);
        gbc.gridx = 6;
        inputPanel.add(addConnectionButton, gbc);

        // Components for finding the shortest route
        JPanel routePanel = new JPanel(new GridLayout(3, 2, 10, 10));
        routePanel.setBorder(BorderFactory.createTitledBorder("Find Shortest Route"));

        JLabel startLabel = new JLabel("Start Station:");
        JTextField startField = new JTextField();
        JLabel destinationLabel = new JLabel("Destination Station:");
        JTextField destinationField = new JTextField();
        JButton findRouteButton = new JButton("Find Route");
        JTextArea resultArea = new JTextArea(5, 30);
        resultArea.setEditable(false);

        routePanel.add(startLabel);
        routePanel.add(startField);
        routePanel.add(destinationLabel);
        routePanel.add(destinationField);
        routePanel.add(findRouteButton);

        // Adding components to frame
        frame.add(inputPanel, BorderLayout.NORTH);
        frame.add(routePanel, BorderLayout.CENTER);
        frame.add(new JScrollPane(resultArea), BorderLayout.SOUTH);

        // Action Listeners
        addStationButton.addActionListener(e -> {
            String station = stationField.getText().trim();
            if (!station.isEmpty()) {
                metro.addStation(station);
                resultArea.append("Station added: " + station + "\n");
                stationField.setText("");
            } else {
                resultArea.append("Station name cannot be empty.\n");
            }
        });

        addConnectionButton.addActionListener(e -> {
            String from = fromField.getText().trim();
            String to = toField.getText().trim();
            String weightStr = weightField.getText().trim();
            try {
                int weight = Integer.parseInt(weightStr);
                if (!from.isEmpty() && !to.isEmpty() && weight > 0) {
                    metro.addConnection(from, to, weight);
                    resultArea.append("Connection added: " + from + " -> " + to + " [" + weight + "]\n");
                    fromField.setText("");
                    toField.setText("");
                    weightField.setText("");
                } else {
                    resultArea.append("Invalid connection details.\n");
                }
            } catch (NumberFormatException ex) {
                resultArea.append("Weight must be a positive integer.\n");
            }
        });

        findRouteButton.addActionListener(e -> {
            String start = startField.getText().trim();
            String destination = destinationField.getText().trim();
            if (!start.isEmpty() && !destination.isEmpty()) {
                List<String> route = metro.findShortestRoute(start, destination);
                if (route.isEmpty()) {
                    resultArea.append("No route found between " + start + " and " + destination + ".\n");
                } else {
                    resultArea.append("Shortest route: " + String.join(" -> ", route) + "\n");
                }
                startField.setText("");
                destinationField.setText("");
            } else {
                resultArea.append("Start and destination stations cannot be empty.\n");
            }
        });

        frame.setVisible(true);
    }
}

