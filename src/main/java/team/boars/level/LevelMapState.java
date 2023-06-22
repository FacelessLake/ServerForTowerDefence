package team.boars.level;

import com.badlogic.gdx.math.Vector2;
import team.boars.framework.LevelView;
import team.boars.framework.TileType;

import java.util.*;

public class LevelMapState {
    public final Tile[][] mapArr;

    public LevelMapState(TileType[][] mapArr) {
        this.mapArr = new Tile[mapArr.length][mapArr[0].length];
        for (int i = 0; i < mapArr.length; i++) {
            for (int j = 0; j < mapArr[0].length; j++) {
                this.mapArr[i][j] = new Tile(mapArr[i][j], LevelView.TilE_SIZE * i + LevelView.GRID_CORNER_X,
                        LevelView.TilE_SIZE * j + LevelView.GRID_CORNER_Y, i, j);
            }
        }
    }

    public Vector2 getPath(Vector2 startPosition, Vector2 targetPosition) {
        Queue<SearchNode> frontier = new PriorityQueue<>(10, (o1, o2) -> (int) (o1.priority - o2.priority));

        Tile startNode = positionToTile(startPosition.x, startPosition.y);
        Tile targetNode = positionToTile(targetPosition.x, targetPosition.y);

        frontier.add(new SearchNode(startNode, null, 0));
        HashMap<Tile, Float> costSoFar = new HashMap<>();
        costSoFar.put(startNode, 0f);

        while (!frontier.isEmpty()) {
            SearchNode q = frontier.remove();

            if (q.node == targetNode) {
                List<Vector2> path = new LinkedList<>();
                while (q.parent != null) {
                    path.add(new Vector2(q.node.x, q.node.y));
                    q = q.parent;
                }
                path.add(new Vector2(q.node.x, q.node.y));
                Collections.reverse(path);
                if (path.size() > 1)
                    return path.get(1);
                return path.get(0);
            }

            if (q.node.type == TileType.Plot && startNode != q.node) continue; //ignore children of building plots.

            List<Tile> neighborTiles = findNeighbors(q.node.gridX, q.node.gridY);

            for (Tile next : neighborTiles) {
                Vector2 nextPos = new Vector2(next.x, next.y);
                float newCost = costSoFar.get(q.node) + nextPos.dst(q.node.x, q.node.y);
                if (!costSoFar.containsKey(next) || newCost < costSoFar.get(next)) {
                    costSoFar.put(next, newCost);
                    float priority = newCost + nextPos.dst(targetPosition);
                    frontier.add(new SearchNode(next, q, priority));
                }
            }
        }

        return null;
    }

    public Tile positionToTile(float x, float y) {
        int gridX = (int) ((x - LevelView.GRID_CORNER_X) / LevelView.TilE_SIZE);
        int gridY = (int) ((y - LevelView.GRID_CORNER_Y) / LevelView.TilE_SIZE);
        return mapArr[gridX][gridY];
    }

    private List<Tile> findNeighbors(int x, int y) {
        List<Tile> neighborTiles = new LinkedList<>();
        if (y + 1 < mapArr[0].length) {
            if (mapArr[x][y + 1].type != TileType.Background)
                neighborTiles.add(mapArr[x][y + 1]);
        }
        if (y - 1 >= 0) {
            if (mapArr[x][y - 1].type != TileType.Background)
                neighborTiles.add(mapArr[x][y - 1]);
        }
        if (x + 1 < mapArr.length) {
            if (mapArr[x + 1][y].type != TileType.Background)
                neighborTiles.add(mapArr[x + 1][y]);
        }
        if (x - 1 >= 0) {
            if (mapArr[x - 1][y].type != TileType.Background)
                neighborTiles.add(mapArr[x - 1][y]);
        }
        return neighborTiles;
    }

    private static class SearchNode {

        public SearchNode(Tile node, SearchNode parent, float priority) {
            this.node = node;
            this.parent = parent;
            this.priority = priority;
        }

        Tile node;
        SearchNode parent;
        float priority;
    }
}
