package util;

import java.io.FileWriter;
import java.io.IOException;

import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.io.IntegerComponentNameProvider;
import org.jgrapht.io.StringComponentNameProvider;
import org.jgrapht.*;

import util.PairEdge;
import xic.XicException;

/** A IR control flow graph. */
@SuppressWarnings("serial")
public class PairEdgeGraph<V, E> extends DefaultDirectedGraph<V, PairEdge<V, E>> {
    public V start;
    
    public PairEdgeGraph(V start, EdgeFactory<V, PairEdge<V, E>> edgeFactory) {
        super(edgeFactory);
        this.start = start;
        addVertex(start);
    }

    public void exportCfg(String filename) throws XicException {
        IntegerComponentNameProvider<V> idProvider = new IntegerComponentNameProvider<>();
        StringComponentNameProvider<V> nameProvider = new StringComponentNameProvider<>();
        PairEdgeDotExporter<V, E> export = new PairEdgeDotExporter<>(idProvider, nameProvider);

        try {
            export.exportGraph(this, new FileWriter(filename));
        } catch (IOException e) {
            throw XicException.write(filename);
        }
    }
    
}
