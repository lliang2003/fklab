#!/usr/bin/env python
# @author   FAN Kai (fankai@net.pku.edu.cn), Peking University  
# @date     May 01 04:14:59 PM CST 2009

import numpy, nltk, string, time, copy, re, random 
import psyco

def vsm(texts):
    #print "Caculating VSM similarity ..."
    text_count = len(texts)
    text_tf = [{} for i in range(0, text_count)]
    term_df = {}
    stemmer = nltk.PorterStemmer()
    stopwords = nltk.corpus.stopwords.words('english') + list("?,.;:'\"")

    #print "scaning text ... ", time.ctime()
    for i in range(0, text_count):
        for w in texts[i]:
            w = stemmer.stem(w.lower())
            if w in stopwords: continue
            text_tf[i][w] = (w in text_tf[i]) and text_tf[i][w]+1 or 1
        for w in text_tf[i]: 
            term_df[w] = (w in term_df) and term_df[w]+1 or 1

    #print "calculating tf-idf ...", time.ctime()
    df = numpy.array([term_df.get(w, 0) for w in term_df])
    idf = numpy.log(text_count) - numpy.log(df)
    tf = [[text_tf[i].get(w, 0) for w in term_df] for i in range(0, text_count)]
    tfidf = [numpy.array(tf[i]) * idf for i in range(0, text_count)]
    norm = [numpy.linalg.norm(tfidf[i]) for i in range(0, text_count)]
    #print min(norm)
    #print term_df, df, tf, idf

    #print "claculating similarity ...", time.ctime()
    sim = [[1]*text_count for i in range(text_count)] 
    for i in range(1, text_count):
        for j in range(i):
            if norm[i] == 0 or norm[j] == 0:
                sim[i][j] = sim[j][i] = 0
            else:
                sim[i][j] = sim[j][i] = numpy.dot(tfidf[i], tfidf[j]) / norm[i] / norm[j]
    return sim

# agglomarative clustering
# similarity in [0,1] range
def agglo(sim, threshold):
    assert threshold > 0
    sim = copy.deepcopy(sim)
    groups = [[i] for i in range(0, len(sim))]
    for i in range(1, len(sim)):
        m = mx = my = 0
        for x in range(1, len(sim)):
            for y in range(x):
                if sim[x][y] > m:
                    m, mx, my = sim[x][y], x, y
        #print m, mx, my, sim[mx][my]
        if sim[mx][my] < threshold:
            break
        sim[mx][my] = sim[mx][my] = 0
        for j in range(0, len(sim)):
            lenx = len(groups[mx])
            leny = len(groups[my])
            assert lenx > 0
            assert leny > 0
            m = (lenx*sim[mx][j]+leny*sim[my][j])/(lenx+leny)
            #m = max(sim[mx][j], sim[my][j])
            #m = min(sim[mx][j], sim[my][j])
            sim[my][j] = sim[j][my] = 0
            sim[mx][j] = sim[j][mx] = m
        groups[mx] += groups[my]
        groups[my] = []
    #print i, threshold
    return groups


def kmeans(sim, group_count):
    num = len(sim)
    group_count = min(num, group_count)
    seeds = random.sample(range(0, num), group_count)
    groups = [[seeds[i]] for i in range(group_count)]

    for round in range(0, 10):
        gsim = [[1]*group_count for i in range(num)]
        for i in range(num):
            for g in range(group_count):
                if not groups[g]: continue
                gsim[i][g] = sum([sim[i][x] for x in groups[g]])/len(groups[g])
        #for row in gsim: print row
        new_groups = [[] for g in range(group_count)]
        for i in range(num):
            mg = max(zip(gsim[i], range(group_count)))[1]
            new_groups[mg].append(i)
        if new_groups == groups: break
        groups = new_groups
    #print group_count, round
    return groups

def text_tokenize(text):
    if type(text) == type(u''): text = text.encode('ascii', 'ignore')
    tokens = []
    sents = nltk.tokenize.sent_tokenize(text)
    for sent in sents:
        tokens += nltk.tokenize.word_tokenize(sent)
    return tokens

# Extract summary from text
# case sensitive
def extract_summary(text, keywords):
    if type(text) == type(u''): text = text.encode('ascii', 'ignore')
    sents = nltk.tokenize.sent_tokenize(text)
    summary = []
    for sent in sents:
        tokens = nltk.tokenize.word_tokenize(sent)
        for token in tokens:
            if token in keywords:
                summary += tokens
                break
    return summary

def extract_entity(tagged_text):
    entity_set = {'person':set(), 'organization':set(), 'location':set()}
    for word in tagged_text.split():
        if '/' not in word: continue
        term, tag = word.lower().rsplit('/', 1)
        if tag in entity_set:
            entity_set[tag].add(term)
    return entity_set

# Extract features from text
def extract_re_features(text):
    features = {"link":set(), "mail":set(), "domain":set(), "number":set(), "year":set(), "phone":set()}
    if type(text) == type(u''): text = text.encode('ascii', 'ignore')
    num_pattern = "\d(\d|-)+"
    url_pattern = "(news|telnet|nttp|file|http|ftp|https)://([-A-Za-z0-9]+\.[-A-Za-z0-9\.]+)(:[0-9]*)?(/[-A-Za-z0-9_\$\.\+\!\*\(\),;:@&=\?/~\#\%]+)*/?"
    mail_pattern = "(mailto:)?([-A-Za-z0-9]+@([-A-Za-z0-9]+\.[-A-Za-z0-9\.]+))"
    for num in re.finditer('(\d|-){2,}', text):
        features['number'].add(num.group(0))
    for num in re.finditer("[^0-9]1\d\d\d[^0-9]", text):
        features['year'].add(num.group(0))
    for num in re.finditer("\d(\d|-){5,}", text):
        features['phone'].add(num.group(0))
    for url in re.finditer(url_pattern, text):
        features['link'].add(url.group(0))
        features['domain'].add(url.group(2))
    for mail in re.finditer(mail_pattern, text):
        features['mail'].add(mail.group(2))
        features['domain'].add(mail.group(3))
    return features

def extract_entity_features(words, entity_set):
    features = {"person":set(), "organization":set(), "location":set()}
    for term in words:
        if term in entity_set['person']: features['person'].add(term)
        if term in entity_set['organization']: features['organization'].add(term)
        if term in entity_set['location']: features['location'].add(term)
    return features

agglo_psyco = psyco.proxy(agglo)
extract_entity_features = psyco.proxy(extract_entity_features)
extract_summary = psyco.proxy(extract_summary)
vsm = psyco.proxy(vsm)

if __name__ == "__main__":
    print text_tokenize("I'll be back, soon. Yes I will.")
    print extract_entity("Peking/ORGANIZATION University/ORGANIZATION is/0 in/0 Beijing/LOCATION")
    print vsm([['how','are','you','?','fine','.'], ['how','do','you','do','.','fantastic']])
