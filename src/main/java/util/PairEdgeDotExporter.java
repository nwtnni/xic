package util;

import org.jgrapht.io.*;

public class PairEdgeDotExporter<V, E> extends DOTExporter<V, PairEdge<V, E>> {
    public PairEdgeDotExporter(ComponentNameProvider<V> vertexIDProvider, ComponentNameProvider<V> vertexLabelProvider) {
        super(vertexIDProvider, vertexLabelProvider, null);
    }
}