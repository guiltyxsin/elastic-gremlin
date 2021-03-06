package org.elasticgremlin.elastic;

import org.apache.commons.configuration.Configuration;
import org.apache.tinkerpop.gremlin.LoadGraphWith;
import org.apache.tinkerpop.gremlin.structure.*;
import org.elasticgremlin.ElasticGraphGraphProvider;
import org.elasticgremlin.queryhandler.elasticsearch.helpers.TimingAccessor;
import org.junit.*;

import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;

public class PerformanceTests {

    TimingAccessor sw = new TimingAccessor();
    private Graph graph;

    @Before
    public void startUp() throws InstantiationException, IOException, ExecutionException, InterruptedException {
        ElasticGraphGraphProvider elasticGraphProvider = new ElasticGraphGraphProvider();
        final Configuration configuration = elasticGraphProvider.newGraphConfiguration("testGraph", this.getClass(), "performanceTests", LoadGraphWith.GraphData.MODERN);
        this.graph = elasticGraphProvider.openTestGraph(configuration);
    }

    @Test
    public void profile() {
        startWatch("add vertices");
        int count = 10000;
        for(int i = 0; i < count; i++)
            graph.addVertex();
        stopWatch("add vertices");

        startWatch("vertex iterator");
        Iterator<Vertex> vertexIterator = graph.vertices();
        stopWatch("vertex iterator");

        startWatch("add edges");
        vertexIterator.forEachRemaining(v -> v.addEdge("bla", v));
        stopWatch("add edges");

        startWatch("edge iterator");
        Iterator<Edge> edgeIterator = graph.edges();
        stopWatch("edge iterator");

        sw.print();
        System.out.println("-----");
    }


    private void stopWatch(String s) {
        sw.timer(s).stop();
    }

    private void startWatch(String s) {
        sw.timer(s).start();
    }
}
