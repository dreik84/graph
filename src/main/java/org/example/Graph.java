package org.example;

public class Graph {
    private final int MAX_VERTS = 20;
    private final int INFINITY = 1_000_000;
    private Vertex vertexList[];
    private int adjMat[][];
    private int nVerts;
    private int currentVert;
    private StackX theStack;
    private Queue theQueue;
    private char sortedArray[];
    private PriorityQ thePQ;
    private int nTree;

    public Graph() {
        vertexList = new Vertex[MAX_VERTS];
        adjMat = new int[MAX_VERTS][MAX_VERTS];
        nVerts = 0;
        theStack = new StackX();
        theQueue = new Queue();
        sortedArray = new char[MAX_VERTS];
        thePQ = new PriorityQ();

        for (int j = 0; j < MAX_VERTS; j++)
            for (int k = 0; k < MAX_VERTS; k++)
                adjMat[j][k] = INFINITY;
    }

    public void addVertex(char lab) {
        vertexList[nVerts++] = new Vertex(lab);
    }

    public void addEdge(int start, int end) {
        adjMat[start][end] = 1;
        adjMat[end][start] = 1;
    }

    public void addEdge(int start, int end, int weight) {
        adjMat[start][end] = weight;
        adjMat[end][start] = weight;
    }

    public void addEdgeTopo(int start, int end) {
        adjMat[start][end] = 1;
    }

    public void displayVertex(int v) {
        System.out.print(vertexList[v].label);
    }

    // Depth First Search
    public void dfs() {
        vertexList[0].wasVisited = true;
        displayVertex(0);
        theStack.push(0);

        while (!theStack.isEmpty()) {
            int v = getAdjUnvisitedVertex(theStack.peek());

            if (v == -1) {
                theStack.pop();
            } else {
                vertexList[v].wasVisited = true;
                displayVertex(v);
                theStack.push(v);
            }
        }

        for (int i = 0; i < nVerts; i++) {
            vertexList[i].wasVisited = false;
        }
    }

    // Breadth First Search
    public void bfs() {
        vertexList[0].wasVisited = true;
        displayVertex(0);
        theQueue.insert(0);
        int v2;

        while (!theQueue.isEmpty()) {
            int v1 = theQueue.remove();

            while ((v2 = getAdjUnvisitedVertex(v1)) != -1) {
                vertexList[v2].wasVisited = true;
                displayVertex(v2);
                theQueue.insert(v2);
            }
        }

        for (int i = 0; i < nVerts; i++) {
            vertexList[i].wasVisited = false;
        }
    }

    // Minimum Spanning Tree - Минимальное остовное дерево
    public void mst() {
        vertexList[0].wasVisited = true;
        theStack.push(0);

        while (!theStack.isEmpty()) {
            int currentVertex = theStack.peek();
            int v = getAdjUnvisitedVertex(currentVertex);

            if (v == -1) {
                theStack.pop();
            } else {
                vertexList[v].wasVisited = true;
                theStack.push(v);
                displayVertex(currentVertex);
                displayVertex(v);
                System.out.print(" ");
            }
        }

        for (int i = 0; i < nVerts; i++) {
            vertexList[i].wasVisited = false;
        }
    }

    public int getAdjUnvisitedVertex(int v) {

        for (int i = 0; i < nVerts; i++)
            if (adjMat[v][i] == 1 && vertexList[i].wasVisited == false)
                return i;

        return -1;
    }

    // Topological sorting - Топологическая сортировка
    public void topo() {
        int origNVerts = nVerts;

        while (nVerts > 0) {
            int currentVertex = noSuccessors();

            if (currentVertex == -1) {
                System.out.println("Error: Graph has cycles");
                return;
            }

            sortedArray[nVerts - 1] = vertexList[currentVertex].label;

            deleteVertex(currentVertex);
        }

        System.out.print("Topologically sorted order: ");
        for (int i = 0; i < origNVerts; i++) System.out.print(sortedArray[i]);
        System.out.println();
    }

    public int noSuccessors() {
        boolean isEdge;

        for (int row = 0; row < nVerts; row++) {
            isEdge = false;

            for (int col = 0; col < nVerts; col++) {

                if (adjMat[row][col] > 0) {
                    isEdge = true;
                    break;
                }
            }

            if (!isEdge) return row;
        }

        return -1;
    }

    public void deleteVertex(int delVert) {

        if (delVert != nVerts - 1) {

            for (int j = delVert; j < nVerts - 1; j++)
                vertexList[j] = vertexList[j + 1];

            for (int row = delVert; row < nVerts - 1; row++)
                moveRowUp(row, nVerts);

            for (int col = delVert; col < nVerts - 1; col++)
                moveColLeft(col, nVerts - 1);
        }

        nVerts--;
    }

    private void moveRowUp(int row, int length) {
        for (int col = 0; col < length; col++)
            adjMat[row][col] = adjMat[row + 1][col];
    }

    private void moveColLeft(int col, int length) {
        for (int row = 0; row < length; row++)
            adjMat[row][col] = adjMat[row][col + 1];
    }

    // Построение минимального остовного дерева для взвешенного графа
    public void mstw() {
        currentVert = 0;

        while (nTree < nVerts - 1) {
            vertexList[currentVert].isInTree = true;
            nTree++;

            for (int j = 0; j < nVerts; j++) {
                if (j == currentVert) continue;
                if (vertexList[j].isInTree) continue;
                int distance = adjMat[currentVert][j];
                if (distance == INFINITY) continue;
                putInPQ(j, distance);
            }

            if (thePQ.size() == 0) {
                System.out.println("Graph not connected");
                return;
            }

            Edge theEdge = thePQ.removeMin();
            int sourceVert = theEdge.srcVert;
            currentVert = theEdge.destVert;

            System.out.print(vertexList[sourceVert].label);
            System.out.print(vertexList[currentVert].label);
            System.out.print(" ");
        }

        for (int i = 0; i < nVerts; i++) {
            vertexList[i].isInTree = false;
        }
    }

    public void putInPQ(int newVert, int newDist) {
        int queueIndex = thePQ.find(newVert);

        if (queueIndex != -1) {
            Edge tempEdge = thePQ.peekN(queueIndex);
            int oldDist = tempEdge.distance;

            if (oldDist > newDist) {
                thePQ.removeN(queueIndex);
                Edge theEdge = new Edge(currentVert, newVert, newDist);
                thePQ.insert(theEdge);
            }
        } else {
            Edge theEdge = new Edge(currentVert, newVert, newDist);
            thePQ.insert(theEdge);
        }
    }
}
