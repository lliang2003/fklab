#!/usr/bin/env python
# @author   FAN Kai (fankai@net.pku.edu.cn), Peking University  
# @date     Apr 23 04:51:25 PM CST 2009


import sys, os, xml.dom.minidom, pickle, optparse
from nlp import *

def write_xml_result(groups, ranks, discarded, xml_file):
    #print 'Writing clustering result to', xml_file
    dom_impl = xml.dom.minidom.getDOMImplementation()
    dom_xml = dom_impl.createDocument(None, "clustering", None)
    dom_root = dom_xml.documentElement
    entity_count = 0

    for group in groups:
        if not group: continue
        dom_entity = dom_xml.createElement("entity")
        dom_entity.setAttribute("id", str(entity_count))
        dom_root.appendChild(dom_entity)
        entity_count += 1
        for item in group:
            dom_doc = dom_xml.createElement("doc")
            dom_doc.setAttribute("rank", str(ranks[item]))
            dom_entity.appendChild(dom_doc)

    dom_discarded = dom_xml.createElement("discarded")
    dom_root.appendChild(dom_discarded)
    for item in discarded:
        dom_doc = dom_xml.createElement("doc")
        dom_doc.setAttribute("rank", str(item))
        dom_discarded.appendChild(dom_doc)

    open(xml_file, "w").write(dom_xml.toxml())

def calc_feature_sim(features, key, jaccard=False):
    from nltk.metrics import jaccard_distance
    num = len(features) 
    sim = [[1]*num for i in range(num)]

    for i in range(1, num):
        for j in range(i):
            set1, set2 = features[i][key], features[j][key]
            if len(set1) == 0 or len(set2) == 0: 
                score = 0
            else:
                score = jaccard and 1-jaccard_distance(set1,set2) or len(set1.intersection(set2))
            sim[i][j] = sim[j][i] = score

    return sim

def process_person(dir, name):
    print 'Processing data for %s in %s ...'%(name, dir), time.ctime()
    features = {'fulltext':[], 'metadata':[], 'summary':[]}
    truth = Truth("%s/truth_files/%s.clust.xml"%(dir, name))
    # parse description file, generate texts 
    dom_corpus = xml.dom.minidom.parse("%s/description_files/%s.xml"%(dir, name)).firstChild
    ranks, discarded = [], []
    entity_set = extract_entity(open("%s/web_pages/%s/all.ner"%(dir, name)).read())
    #print "search_string =", dom_corpus.getAttribute("search_string") , time.ctime()
    for dom_doc in dom_corpus.getElementsByTagName("doc"):
        rank = dom_doc.getAttribute("rank")
        #print >>sys.stderr, rank,
        try:
            snippet = dom_doc.getElementsByTagName("snippet")[0].firstChild.nodeValue
            title = dom_doc.getAttribute("title")
        except:
            snippet = title = ""
        url = dom_doc.getAttribute("url")
        path = "%s/web_pages/%s/%s"%(dir, name, rank.rjust(3, "0"))
        if not os.path.exists(path+'.text'):
            discarded.append(rank)
            continue
        fulltext = open(path+".text").read().lower()
        linktext = open(path+".link").read().lower()
        metatext = title + snippet + url.replace('/', ' ')
        summary = extract_summary(fulltext, name.lower().split('_'))
        if len(summary) < 5:
        #if truth.discarded(rank):
            discarded.append(rank)
        else:
            ranks.append(rank)
            tokens = text_tokenize(fulltext)
            features['fulltext'].append(tokens)
            features['metadata'].append(text_tokenize(metatext))
            features['summary'].append(summary)
            re_features = extract_re_features(fulltext + linktext)
            for key in re_features:
                features.setdefault(key, [])
                features[key].append(re_features[key])
            entity_features = extract_entity_features(tokens, entity_set)
            for key in entity_features:
                features.setdefault(key, [])
                features[key].append(entity_features[key])

    #print "\tdoc_count = %d"%len(ranks),
    #print "\tdiscarded = %s"%(discarded), time.ctime()

    data = {'ranks':ranks, 'discarded':discarded, 'sim':{}}
    for key in features.keys():
        data['sim'][key] = vsm(features[key])
    pickle.dump(data, open("tmp/pickle/%s.pickle"%name, 'wb'))

def cluster(name, sim, sim_type, ranks, discarded):
    #print name, sim_type

    # use agglomerative clustering
    for i in range(10, 100, 5):
        threshold = i/100.0
        groups = agglo(sim, threshold)
        result_dir = "tmp/result/%s-agglo-%.2f"%(sim_type, threshold);
        if not os.path.exists(result_dir): os.mkdir(result_dir)
        write_xml_result(groups, ranks, discarded, "%s/%s.clust.xml"%(result_dir, name))
        #break

    return

    # use k-means clustering
    for group_count in range(10, 80, 5):
        groups = kmeans(copy.deepcopy(sim), group_count)
        result_dir = "tmp/result/%s-kmeans-%d"%(sim_type, group_count);
        if not os.path.exists(result_dir): os.mkdir(result_dir)
        write_xml_result(groups, ranks, discarded, "%s/%s.clust.xml"%(result_dir, name))

class Truth:
    def __init__(self, truth_file):
        self.groups = []
        dom = xml.dom.minidom.parse(truth_file)
        clustering = dom.firstChild
        for entity in clustering.getElementsByTagName("entity"):
            self.groups.append([doc.getAttribute("rank") for doc in entity.getElementsByTagName("doc")])
        discarded_group = clustering.getElementsByTagName("discarded")
        if discarded_group:
            self.discarded_docs = [doc.getAttribute("rank") for doc in discarded_group[0].getElementsByTagName("doc")]
        else:
            self.discarded_docs = []
    def discarded(self, rank):
        return rank in self.discarded_docs
    def similar(self, rank1, rank2):
        for g in self.groups:
            if rank1 in g and rank2 in g: return True
        return False
    def count(self):
        return sum(map(len, self.groups))+len(self.discarded_docs)

def write_arff_data(arff, sims, ranks, truth):
    num = len(ranks)
    #print num
    for i in range(1, num):
        for j in range(i):
            ri, rj = ranks[i], ranks[j]
            s =  truth.similar(ri, rj) and 1 or 0
            for type in sims.keys():
                #print type, len(sims[type]), len(sims[type][i]), i, j
                arff.write("%f "%sims[type][i][j])
            arff.write("%d \n"%s)

def write_arff_head(arff, sims, name):
    arff.write("@relation sim.%s\n"%name)
    for type in sims.keys():
        arff.write("@attribute %s real\n"%type)
    arff.write("@attribute sim {0,1}\n@data\n")

def make_arff():
    print "Generating arff files ...", time.ctime()
    tarff = open("tmp/training.arff", "w")
    first = True
    for name in os.listdir("training/web_pages"):
        #print name
        data = pickle.load(open("tmp/pickle/%s.pickle"%name))
        if first:
            write_arff_head(tarff, data['sim'], 'training')
            first  = False
        write_arff_data(tarff, data['sim'], data['ranks'], Truth("training/truth_files/%s.clust.xml"%name))
        arff = open("tmp/arff/%s.arff"%name, "w")
        write_arff_head(arff, data['sim'], name)
        write_arff_data(arff, data['sim'], data['ranks'], Truth("training/truth_files/%s.clust.xml"%name))
        arff.close()
    tarff.close()
    for name in os.listdir("test/web_pages"):
        #print name
        data = pickle.load(open("tmp/pickle/%s.pickle"%name))
        arff = open("tmp/arff/%s.arff"%name, "w")
        write_arff_head(arff, data['sim'], name)
        write_arff_data(arff, data['sim'], data['ranks'], Truth("test/truth_files/%s.clust.xml"%name))
        arff.close()
 
def extract_score(num, filename):
    s = open(filename).readlines()[5:]
    sim = [[1]*num for i in range(num)] 
    k = 0
    for i in range(1, num):
        for j in range(i):
            #print s[k]
            score = float(s[k][35:])
            if s[k][27] == '0': score = 1-score
            sim[i][j] = sim[j][i] = score
            k += 1
            #print i, j, score
    return sim

def weps(argv):
    parser = optparse.OptionParser(usage='%prog [options]',
            description='Utility for weps.')
    parser.add_option('-p', action='store_true', dest='proc', default=False, 
            help='Process documents and calculate basic similarity.')
    parser.add_option('-m', action='store_true', dest='ml', default=False, 
            help='Use machine learing to generate similarity.')
    parser.add_option('-c', action='store_true', dest='cluster', default=False, 
            help='do clustering')
    parser.add_option('-t', '--test', dest='test', default='test', 
            help='Specify test directory')
    parser.add_option('-T', '--training', dest='training', default='training', 
            help='Specify training directory')
    options, args = parser.parse_args()
    #print options, args

    if not os.path.exists('tmp'): os.mkdir('tmp')
    if not os.path.exists('tmp/pickle'): os.mkdir('tmp/pickle')
    if not os.path.exists('tmp/arff'): os.mkdir('tmp/arff')
    if not os.path.exists('tmp/predict'): os.mkdir('tmp/predict')
    if not os.path.exists('tmp/result'): os.mkdir('tmp/result')
            

    if options.proc:
        for name in os.listdir("test/web_pages"):
            process_person("test", name)
        for name in os.listdir("training/web_pages"):
            process_person("training", name)
    if options.ml:
        make_arff()
        print "Learining similarity ...", time.ctime()
        classifiers = {'me':'functions.Logistic', 'nb':'bayes.NaiveBayes', 'dt':'trees.J48'}
        for c in classifiers.keys():
            cmd = "java -Xmx1024M -cp weka.jar weka.classifiers.%s -t tmp/training.arff -d tmp/%s.model"%(classifiers[c], c)
            print cmd
            #os.system(cmd)
            for f in os.listdir("tmp/arff"):
                cmd = "java -cp weka.jar weka.classifiers.%s -T tmp/arff/%s -l tmp/%s.model -p 0 > tmp/predict/%s.%s.predict"%(classifiers[c], f, c, f, c)
                #print cmd
                os.system(cmd)

        print "Clustering learnt similarity ...", time.ctime()  
        #for name in os.listdir("test/web_pages")+os.listdir("training/web_pages"):
        for name in os.listdir("test/web_pages"):
            print name
            data = pickle.load(open("tmp/pickle/%s.pickle"%name))
            ranks, discarded = data['ranks'], data['discarded']
            for c in classifiers.keys():
                data['sim'][c] = extract_score(len(data['ranks']), "tmp/predict/%s.arff.%s.predict"%(name,c))
                cluster(name, data['sim'][c], c, ranks, discarded)

    if options.cluster:
        print "Clustering feature similarity ...", time.ctime()  
        #for name in os.listdir("test/web_pages")+os.listdir("training/web_pages"):
        for name in os.listdir("test/web_pages"):
        #for name in os.listdir("training/web_pages"):
            print name
            data = pickle.load(open("tmp/pickle/%s.pickle"%name))
            ranks, discarded = data['ranks'], data['discarded']
            #for key in data['sim'].keys():
            for key in ['organization', 'summary']:
                cluster(name, data['sim'][key], key, ranks, discarded)

if __name__ == '__main__':
    weps(sys.argv)
