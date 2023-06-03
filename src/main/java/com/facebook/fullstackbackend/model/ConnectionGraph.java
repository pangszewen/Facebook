package com.facebook.fullstackbackend.model;

import java.util.*;
import java.sql.*;

public class ConnectionGraph<T extends Comparable<String>> {
    String graphFile = "graphFile.csv";
    Vertex<String> head;
    int size;

    public ConnectionGraph() {
        head = null;
        size = 0;
    }

    public ConnectionGraph<String> getGraph(ConnectionGraph<String> graph) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/database", "root", "");
            pstmt = conn.prepareStatement("SELECT * FROM graph");
            ResultSet resultSet =pstmt.executeQuery();
    
            // Add each vertex to graph
            while (resultSet.next()) {
                String vertex = resultSet.getString("user");
                graph.addVertex(graph, vertex);
            }
            pstmt.close();
            conn.close();

            // After adding vertex, only add edges for each vertex
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/database", "root", "");
            pstmt = conn.prepareStatement("SELECT * FROM graph");
            resultSet =pstmt.executeQuery();
            while (resultSet.next()) {
                String vertex = resultSet.getString("user");
                String edges = resultSet.getString("friends");
    
                if (edges != null) {    
                    String[] edgeData = edges.split(",");
                    for (int i=edgeData.length-1; i>=0; i--) {
                        graph.addEdge(graph, vertex, edgeData[i]);
                    }
                }
            }

            return graph;
    
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    
        System.out.println("Failed to get graph.");
        return graph;
    }

    public ConnectionGraph<String> clear(ConnectionGraph<String> graph) {
        graph.head = null;
        graph.size = 0;
        return graph;
    }

    public int getSize(ConnectionGraph<String> graph) {
        return graph.size;
    }

    public int getIndeg(ConnectionGraph<String> graph, String v) {
        if (graph.hasVertex(graph,v)) {
            Vertex<String> temp = graph.head;
            while (temp != null) {
                if (temp.vertexInfo.compareTo(v) == 0)
                    return temp.indeg;
                temp = temp.nextVertex;
            }
        }
        return -1;
    }

    public int getOutdeg(ConnectionGraph<String> graph, String v) {
        if (graph.hasVertex(graph, v)) {
            Vertex<String> temp = graph.head;
            while (temp != null) {
                if (temp.vertexInfo.compareTo(v) == 0)
                    return temp.outdeg;
                temp = temp.nextVertex;
            }
        }
        return -1;
    }

    public boolean hasVertex(ConnectionGraph<String> graph, String v) {
        if (graph.head == null)
            return false;
        Vertex<String> temp = graph.head;
        while (temp != null) {
            if (temp.vertexInfo.compareTo(v) == 0)
                return true;
            temp = temp.nextVertex;
        }
        return false;
    }

    public boolean addVertex(ConnectionGraph<String> graph, String v){
        if(hasVertex(graph, v)==false){
            Vertex<String> temp = head;
            Vertex<String> newVertex = new Vertex<>(v, null);
            if(graph.head==null)
                graph.head = newVertex;
            else{
                Vertex<String> previous = graph.head;
                while(temp!=null){
                    previous = temp;
                    temp = temp.nextVertex;
                }
                previous.nextVertex = newVertex;
            }
            graph.size++;
            return true;
        }else
            return false;
    }


    public ConnectionGraph<String> registerVertex(ConnectionGraph<String> graph, String v) {
        boolean status = addVertex(graph, v);
        if (status) {
            try {
                Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/database", "root", "");
                PreparedStatement statement = connection.prepareStatement("INSERT INTO graph (user, friends) VALUES (?,?)");
                statement.setString(1, v);
                statement.setString(2, "");
                statement.executeUpdate();
                statement.close();
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Failed to add vertex");
        }
        return graph;
    }

    public int getIndex(ConnectionGraph<String> graph, String v) {
        Vertex<String> temp = graph.head;
        int pos = 0;
        while (temp != null) {
            if (temp.vertexInfo.compareTo(v) == 0)
                return pos;
            temp = temp.nextVertex;
            pos += 1;
        }
        return -1;
    }

    public ArrayList<String> getAllVertexObjects(ConnectionGraph<String> graph) {
        ArrayList<String> list = new ArrayList<>();
        Vertex<String> temp = graph.head;
        while (temp != null) {
            list.add(temp.vertexInfo);
            temp = temp.nextVertex;
        }
        return list;
    }

    public ArrayList<String> getAllVertices(ConnectionGraph<String> graph) {
        ArrayList<String> list = new ArrayList<>();
        Vertex<String> temp = graph.head;
        while (temp != null) {
            list.add(temp.vertexInfo);
            temp = temp.nextVertex;
        }
        return list;
    }

    public String getVertex(ConnectionGraph<String> graph, String v) {
        if (graph.head == null)
            return null;
        Vertex<String> temp = graph.head;
        while (temp != null) {
            if (temp.vertexInfo.compareTo(v) == 0)
                return temp.vertexInfo;
            temp = temp.nextVertex;
        }
        return null;
    }

    public ConnectionGraph<String> addEdge(ConnectionGraph<String> graph, String source, String destination) {
        if (graph.head == null) 
            return graph;
        if (!hasVertex(graph, source) || !hasVertex(graph, destination)) 
            return graph;
        Vertex<String> sourceVertex = graph.head;
        while (sourceVertex != null) {
            if (sourceVertex.vertexInfo.compareTo(source) == 0) {
                // Reached source vertex, look for destination now
                Vertex<String> destinationVertex = graph.head;
                while (destinationVertex != null) {
                    if (destinationVertex.vertexInfo.compareTo(destination) == 0) {
                        // Reached destination vertex, add edge here
                        Edge<String> currentEdge = sourceVertex.firstEdge;
                        Edge<String> newEdge = new Edge<>(destinationVertex, currentEdge);
                        sourceVertex.firstEdge = newEdge;
                        sourceVertex.outdeg++;
                        destinationVertex.indeg++;
                        return graph;
                    }
                    destinationVertex = destinationVertex.nextVertex;
                }
            }
            sourceVertex = sourceVertex.nextVertex;
        }
        System.out.println("Failed to add edge.");
        return graph;
    }
    public ConnectionGraph<String> addUndirectedEdge(ConnectionGraph<String> graph, String v1, String v2) {
        ConnectionGraph<String> temp = graph.addEdge(graph, v1, v2);
        updateFriend(graph, v1, v2);
        temp = temp.addEdge(temp, v2, v1);
        updateFriend(graph, v2, v1);
        return temp;
    }

    public boolean hasEdge(ConnectionGraph<String> graph, String source, String destination) {
        if (graph.head == null)
            return false;
        if (!hasVertex(graph, source) || !hasVertex(graph, destination))
            return false;
        Vertex<String> sourceVertex = graph.head;
        while (sourceVertex != null) {
            if (sourceVertex.vertexInfo.compareTo(source) == 0) {
                // Reached source vertex, look for destination now
                Edge<String> currentEdge = sourceVertex.firstEdge;
                while (currentEdge != null) {
                    // destination vertex found
                    if (currentEdge.toVertex.vertexInfo.compareTo(destination) == 0)
                        return true;
                    currentEdge = currentEdge.nextEdge;
                }
            }
            sourceVertex = sourceVertex.nextVertex;
        }
        return false;
    }

    public ConnectionGraph<String> removeEdge(ConnectionGraph<String> graph, String source, String destination) {
        if (graph.head == null)
            return graph;
        if (!(hasVertex(graph, source) && hasVertex(graph, destination)))
            return graph;
        Vertex<String> sourceVertex = graph.head;
        while (sourceVertex != null) {
            if (sourceVertex.vertexInfo.compareTo(source) == 0) {
                // Reached source vertex, look for destination now
                Edge<String> currentEdge = sourceVertex.firstEdge;
                Edge<String> tempEdge = new Edge<>();
                while (currentEdge != null) {
                    // destination vertex found
                    if (currentEdge.toVertex.vertexInfo.compareTo(destination) == 0) {
                        if(currentEdge.equals(sourceVertex.firstEdge)){
                            sourceVertex.firstEdge = currentEdge.nextEdge;
                        }else
                            tempEdge.nextEdge = currentEdge.nextEdge;
                        removeFriend(graph, source, destination);
                        return graph;
                    }
                    tempEdge = currentEdge;
                    currentEdge = currentEdge.nextEdge;
                }
            }
            sourceVertex = sourceVertex.nextVertex;
        }
        System.out.println("Failed to remove edge.");
        return graph;
    }
    public ConnectionGraph<String> removeUndirectedEdge(ConnectionGraph<String> graph, String v1, String v2) {
        ConnectionGraph<String> temp = graph.removeEdge(graph, v1, v2);
        temp = temp.removeEdge(temp, v2, v1);
        return temp;
    }

    public ArrayList<String> getNeighbours(ConnectionGraph<String> graph, String v) {
        if (!hasVertex(graph, v))
            return null;
        ArrayList<String> list = new ArrayList<>();
        Vertex<String> temp = graph.head;
        while (temp != null) {
            if (temp.vertexInfo.compareTo(v) == 0) {
                // Reached vertex, look for destination now
                Edge<String> currentEdge = temp.firstEdge;
                while (currentEdge != null) {
                    list.add(currentEdge.toVertex.vertexInfo);
                    currentEdge = currentEdge.nextEdge;
                }
            }
            temp = temp.nextVertex;
        }
        return list;
    }

    public void printEdges(ConnectionGraph<String> graph) {
        Vertex<String> temp = graph.head;
        while (temp != null) {
            System.out.print("# " + temp.vertexInfo + " : ");
            Edge<String> currentEdge = temp.firstEdge;
            while (currentEdge != null) {
                System.out.print("[" + temp.vertexInfo + "," + currentEdge.toVertex.vertexInfo + "] ");
                currentEdge = currentEdge.nextEdge;
            }
            System.out.println();
            temp = temp.nextVertex;
        }
    }    

    public void updateFriend(ConnectionGraph<String> graph, String u1, String u2){
        ArrayList<String> friends = graph.getNeighbours(graph, u1);
        Connection conn = null;
        PreparedStatement pstmt = null;
    
        try {
            // Establish connection
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/database", "root", "");
    
            // Update UserAccount table
            String accountSql = "UPDATE graph SET friends=? WHERE user=?";
            pstmt = conn.prepareStatement(accountSql);
            pstmt.setString(1, String.join(",", friends));
            pstmt.setString(2, u1);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void removeFriend(ConnectionGraph<String> graph, String u1, String u2){
        ArrayList<String> friends = graph.getNeighbours(graph, u1);
        Connection conn = null;
        PreparedStatement pstmt = null;
    
        try {
            // Establish connection
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/database", "root", "");
    
            // Update UserAccount table
            String accountSql = "UPDATE graph SET friends=? WHERE user=?";
            pstmt = conn.prepareStatement(accountSql);
            pstmt.setString(1, String.join(",", friends));
            pstmt.setString(2, u1);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}

class Vertex<T extends Comparable<T>> {
    T vertexInfo;
    int indeg;
    int outdeg;
    Vertex<T> nextVertex;
    Edge<T> firstEdge;

    public Vertex() {
        vertexInfo = null;
        indeg = 0;
        outdeg = 0;
        nextVertex = null;
        firstEdge = null;
    }

    public Vertex(T vInfo, Vertex<T> next) {
        vertexInfo = vInfo;
        indeg = 0;
        outdeg = 0;
        nextVertex = next;
        firstEdge = null;
    }
}

class Edge<T extends Comparable<T>> {
	Vertex<T> toVertex;
	Edge<T> nextEdge;
	
	public Edge()	{
		toVertex = null;
		nextEdge = null;
	}
	
	public Edge(Vertex<T> destination, Edge<T> a)	{
		toVertex = destination;
		nextEdge = a;
	}

}
