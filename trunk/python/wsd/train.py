#!/usr/bin/env python
# @author   FAN Kai (fankai@net.pku.edu.cn), Peking University  
# @date     Mar 30 11:23:36 AM CST 2009

from xml.dom import minidom
import cPickle

data = {}
pos_number = 39
back_cnt = 1
forth_cnt = 1
xmldoc = minidom.parse("train_pos.xml")
#xmldoc = minidom.parse("pos.xml")
lexelts = xmldoc.getElementsByTagName("lexelt")
for lexelt in lexelts:
    token_set=set()
    pos_cnt = {}
    pos_prob = {}
    token_cnt = {}
    token_prob = {}
    item = lexelt.attributes['item'].value
    print item.encode('utf-8')
    data[item] = {}
    instances = lexelt.getElementsByTagName("instance")
    for instance in instances:
        answer = instance.getElementsByTagName("answer")[0]
        context = instance.getElementsByTagName("context")[0]
        position = len(context.childNodes[0].data.strip())
        #print context.childNodes[0].data.encode('utf-8'), position 
        sense = answer.attributes['senseid'].value
        if sense not in data[item]:
            data[item][sense] = {"wordbag":set(), "count":0}
            pos_cnt[sense] = [{} for i in range(0, back_cnt+1+forth_cnt)]
            pos_prob[sense] = [{} for i in range(0, back_cnt+1+forth_cnt)]
            token_cnt[sense] = [{} for i in range(0, back_cnt+1+forth_cnt)]
            token_prob[sense] = [{} for i in range(0, back_cnt+1+forth_cnt)]
        data[item][sense]["count"] += 1
        #print instance.attributes['id'].value.encode("utf-8"), sense
        words = instance.getElementsByTagName("word")
        poss = []
        tokens = []
        current_position = 0
        for word in words:
            if current_position > position:
                index = len(poss) - 1
            pos = word.attributes['pos'].value
            token = word.getElementsByTagName("token")[0].firstChild.data.strip()
            poss.append(pos)
            tokens.append(token)
            token_set.add(token)
            current_position += len(token)
            data[item][sense]["wordbag"].add(token)
        for i in range(0, back_cnt + forth_cnt + 1):
            if index - back_cnt + i < 0:continue
            if index - back_cnt + i >= len(poss):break
            pos_cnt[sense][i].setdefault(poss[index-back_cnt+i], 0)
            pos_cnt[sense][i][poss[index-back_cnt+i]] += 1
            token_cnt[sense][i].setdefault(poss[index-back_cnt+i], 0)
            token_cnt[sense][i][poss[index-back_cnt+i]] += 1
        data[item][sense]["prior"] = float(data[item][sense]["count"])/len(instances)

    #print pos_cnt
    for sense in data[item]:
        for i in range(0, back_cnt + forth_cnt + 1):
            pcnt = data[item][sense]["count"] + pos_number #smooth
            tcnt = data[item][sense]["count"] + len(token_set)
            for pos in pos_cnt[sense][i]:
                pos_prob[sense][i][pos] = float(pos_cnt[sense][i][pos]+1)/pcnt
            for token in token_cnt[sense][i]:
                token_prob[sense][i][token] = float(token_cnt[sense][i][token]+1)/tcnt
        data[item][sense]['pos_prob'] = pos_prob[sense]
        data[item][sense]['token_prob'] = token_prob[sense]
    #print pos_prob
    print len(token_set)

#print data
#print data.keys()
cPickle.dump(data, open('wsd.dat', 'w'))

