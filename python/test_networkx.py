#!/usr/bin/env python
# @author   FAN Kai (fankai@net.pku.edu.cn), Peking University  
# @date     May 23 05:29:50 PM CST 2009

import networkx as nx

def draw(g):
    import matplotlib.pyplot as plt
    #nx.draw(g)
    #nx.draw_random(g)
    nx.draw_circular(g)
    #nx.draw_spectral(g)
    plt.show()

def gen():
    petersen = nx.petersen_graph()
    tutte = nx.tutte_graph()
    maze = nx.sedgewick_maze_graph()
    k5 = nx.complete_graph(5)
    k35 = nx.complete_bipartite_graph(3,5)
    draw(k35)

def basic():
    g = nx.Graph()
    g.add_node(9)
    g.add_nodes_from(range(5))
    #g.add_node('n')
    #g.add_node('m')

    g.add_edge(2,3)
    g.add_edge(1,3)
    g.add_edge(1,4)
    g.add_edge('n', 'm', 'red')
    g.remove_node(0)

    print g.nodes()
    print g.edges()
    print g.edges(data=True)
    print g.edges(2,3)
    print g.edges(2,1)
    print nx.connected_components(g)
    print nx.degree(g)
    print g.degree(with_labels=True)
    print nx.clustering(g)

    print g.size(), g.number_of_edges()

    g.remove_node('n')
    g.remove_node('m')
    #mst = kruskal_mst(g)

basic()
gen()
