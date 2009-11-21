#!/usr/bin/env python
# @author   FAN Kai (fankai@net.pku.edu.cn), Peking University  
# @date     Mar 30 21:38:34 CST 2009


from xml.dom import minidom
import cPickle
import math

data = cPickle.load(open('wsd.dat'))
answer = open('test.key')
right_cnt = 0
wrong_cnt = 0
back_cnt = 1
forth_cnt = 1

xmldoc = minidom.parse("test_pos.xml")
#xmldoc = minidom.parse("tpos.xml")
lexelts = xmldoc.getElementsByTagName("lexelt")
for lexelt in lexelts:
    item = lexelt.attributes['item'].value
    print item.encode('utf-8')
    instances = lexelt.getElementsByTagName("instance")
    for instance in instances:
        instance_id = instance.attributes["id"].value
        # set p(sense)
        prob = {}
        for sense in data[item]:
            prob[sense] = math.log(data[item][sense]['prior'])

        # calc p(sense) * p(x|sense)
        context = instance.getElementsByTagName("context")[0]
        position = len(context.childNodes[0].data.strip())
        words = instance.getElementsByTagName("word")
        current_position = 0
        poss = []
        tokens = []
        for word in words:
            if current_position > position:
                index = len(poss) - 1
            pos = word.attributes['pos'].value
            poss.append(pos)
            token = word.getElementsByTagName("token")[0].firstChild.data.strip()
            tokens.append(token)
            current_position += len(token)
        for sense in data[item].keys():
            #print data[item][sense]
            for i in range(0, back_cnt + forth_cnt + 1):
                if index - back_cnt + i < 0:continue
                if index - back_cnt + i >= len(poss):break
                pos = poss[index-back_cnt+i]
                token = tokens[index-back_cnt+i]
                prob[sense] += math.log(data[item][sense]['pos_prob'][i].get(pos, .01))
                prob[sense] += math.log(data[item][sense]['token_prob'][i].get(token, .001))
            #print prob[sense]
        print prob

        # select best sense
        maxprob = -9999999999 
        for sense in data[item].keys():
            if prob[sense] > maxprob:
                maxprob = prob[sense]
                bestsense = sense
        #print maxprob

        # judge
        rightsense = answer.readline().split()[2]
        print item.encode("utf-8"), instance_id.encode("utf-8"), bestsense
        if bestsense == rightsense:
            right_cnt += 1
        else:
            wrong_cnt += 1
print right_cnt, wrong_cnt
