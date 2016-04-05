package mil.army.usace.ehlschlaeger.digitalpopulations.csv2kml;

import java.util.List;

import mil.army.usace.ehlschlaeger.rgik.util.JTSUtil;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.index.SpatialIndex;
import com.vividsolutions.jts.index.strtree.Boundable;



/**
 * Build a quad tree quickly from a JTS SpatialIndex. Method is considered
 * "fast" due to leaf nodes having unpredictable numbers of elements. No effort
 * is made to balance or optimize the tree. Tile bounds are also not shrunk to
 * the actual contents of the tile.
 * <P>
 * Branch nodes are empty, and leaf node have no more than the given number of
 * points (usually much less.) No empty objects are returned; branch nodes have
 * null contents, and children are null if empty. Leaf nodes have null children.
 * 
 * @author William R. Zwicky
 */
public class FastQuadTree {
    private int maxPointsPerTile;
    
    
    public FastQuadTree() {
        maxPointsPerTile = 500;
    }
    
    public FastQuadTree(int maxPointsPerTile) {
        this.maxPointsPerTile = maxPointsPerTile;
    }

    /**
     * Create GIS point tiles from points in a sub-range. If there are too many
     * points, the range will be subdivided til the number of points is
     * reasonable.
     * <P>
     * The only useful spatial index is STRtree, as the only other
     * two-dimensional index is JTS' own QuadTree, which doesn't work correctly.
     * RGIK has a QuadTree class, but it places points in the branch nodes.
     * 
     * @param index
     *            spatial index to scan. MUST contain instances of Boundable.
     *            Will be empty when method finishes.
     *            com.vividsolutions.jts.index.strtree.STRtree is HIGHLY
     *            recommended.
     * @param range
     *            area to extract points from. SpatialIndex has no getBounds,
     *            plus caller can specify an area smaller than whole index.
     * 
     * @return quad tree containing points as a new Tile<Boundable>
     */
    public Tile<Boundable> createTiles(SpatialIndex index, Envelope range) {
        List<Boundable> houses = JTSUtil.query(index, range);

        if (houses.size() > maxPointsPerTile) {
            // Too many points in container; divide into 4 tiles and recurse.
            houses = null; // conserve memory

            Tile<Boundable> tile = new Tile<Boundable>();
            tile.bounds = range;
            Coordinate mid = range.centre();

            tile.nw = createTiles(index, new Envelope(range.getMinX(), mid.x, mid.y, range.getMaxY()));
            tile.ne = createTiles(index, new Envelope(mid.x, range.getMaxX(), mid.y, range.getMaxY()));
            tile.sw = createTiles(index, new Envelope(range.getMinX(), mid.x, range.getMinY(), mid.y));
            tile.se = createTiles(index, new Envelope(mid.x, range.getMaxX(), range.getMinY(), mid.y));
            
            return tile;
        } else if (houses.size() > 0) {
            Tile<Boundable> tile = new Tile<Boundable>();
            tile.bounds = range;
            tile.contents = houses;
            
            // Remove from tree. Doesn't save much time if any, but it ensures
            // each point appears in only one tile.
            JTSUtil.removeAll(index, houses);
            
            return tile;
        }
        else {
            // No points.
            return null;
        }
    }
}
