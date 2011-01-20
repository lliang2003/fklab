class Edge:
    def __init__(self,U,V,w):
        self.source = U
        self.to = V
        self.capacity = w
    def __repr__(self):
        return str(self.source) + "->" + str(self.to) + " : " + str(self.capacity)
 
class FlowNetwork(object):
    def __init__(self):
        self.adj, self.flow, = {},{}
 
    def add_vertex(self, vertex):
        self.adj[vertex] = []
 
    def get_edges(self, v):
        return self.adj[v]
 
    def add_edge(self, u,v,w=0):
        assert(u != v)
        edge = Edge(u,v,w)
        redge = Edge(v,u,0)
        edge.redge = redge
        redge.redge = edge
        self.adj[u].append(edge)
        self.adj[v].append(redge)
        self.flow[edge] = self.flow[redge] = 0
 
    def find_path(self, source, sink, path):
        if source == sink:
            return path
        for edge in self.get_edges(source):
            residual = edge.capacity - self.flow[edge]
            if residual > 0 and not (edge,residual) in path:
                result = self.find_path(edge.to, sink, path + [ (edge,residual) ]) 
                if result != None:
                    return result
 
    def max_flow(self, source, sink):
        path = self.find_path(source, sink, [])
        while path != None:
            print path
            flow = min(res for edge,res in path)
            for edge,res in path:
                self.flow[edge] += flow
                self.flow[edge.redge] -= flow
            path = self.find_path(source, sink, [])
        print self.flow
        return sum(self.flow[edge] for edge in self.get_edges(source))

g=FlowNetwork()
map(g.add_vertex, ['s','o','p','q','r','t'])
g.add_edge('s','p',1)
g.add_edge('s','q',1)
g.add_edge('r','o',1)
g.add_edge('t','o',1)
g.add_edge('p','r',1)
g.add_edge('p','t',1)
g.add_edge('q','r',1)
print g.max_flow('s','o')
