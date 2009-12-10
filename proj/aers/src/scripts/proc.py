#!/usr/bin/env python
# @author   FAN Kai (fankai@net.pku.edu.cn), Peking University  
# @date     Dec 04 07:42:28 AM EST 2009

import os, string

datdir = "F:/Data/aers/ascii/"
outputdir = datdir+"dat/"

def countCmp(a, b):
    return a[1] - b[1]

def genDrugsFile():
    """ Generate drugs data file """
    fout = open(datdir + "/drugs.txt", "w")
    for f in os.listdir(datdir):
        print f
        if f.startswith("DRUG"):
            for line in open(datdir + "/" + f).read().split("\n")[1:]:
                parts = line.split("$")
                if len(parts) < 13: continue
                print >> fout, parts[0], parts[4], parts[3]
#genDrugsFile()

def genReactionId():
    """ Reaction -> ID """
    reactions = {}
    for f in os.listdir(datdir):
        if f.startswith("REAC"):
            for line in open(datdir + f).read().split("\n")[1:]:
                parts = line.split("$")
                parts = map(string.strip, parts)
                if len(parts) < 2: continue
                reactions.setdefault(parts[1], 0)
                reactions[parts[1]] += 1
            print f, len(reactions)
    fout = open(outputdir+"/reacid-random.txt", "w")
    for i in range(len(reactions)):
        print i, reactions[i]
        fout.write("R%5d %s\n"%(10000+i, sorted_reac[i][0]))
    fout.close()
    fout = open(outputdir+"/reacid-seq.txt", "w")
    sorted_reac = sorted(reactions.items(), countCmp)
    for i in range(len(sorted_reac)):
        print i, sorted_reac[i]
        fout.write("R%5d %s\n"%(10000+i, sorted_reac[i][0]))
    fout.close()
    fout = open(outputdir+"/reacid-rev.txt", "w")
    sorted_reac = sorted(reactions.items(), countCmp, reverse=True)
    for i in range(len(sorted_reac)):
        print i, sorted_reac[i]
        fout.write("R%5d %s\n"%(10000+i, sorted_reac[i][0]))
    fout.close()
genReactionId()

def countDrugTypes():
    """ Count number of drugs types """
    drugs1, drugs2 = {}, {}
    drugcnt = 0
    cases = {}
    for f in os.listdir(datdir):
        if f.startswith("DRUG"):
            for line in open(datdir + "/" + f).read().split("\n")[1:]:
                drugcnt += 1
                parts = line.split("$")
                map(string.strip, parts)
                if len(parts)!=13:continue
                drug = parts[3]
                cases.setdefault(parts[0], [0,0])
                if parts[4] == '1':
                    cases[parts[0]][0] += 1
                    drugs = drugs1
                else:
                    cases[parts[0]][1] += 1
                    drugs = drugs2
                drugs.setdefault(drug, 0)
                drugs[drug] += 1
            print len(drugs1), len(drugs2)
                
    fout = open(datdir + "/validdrugs.txt", "w")
    for drug in drugs1:
        print >> fout, drug
    fout.close()
    
    a,b,c = 0,0,0
    aa,bb,cc = 0,0,0
    for case in cases:
        if cases[case][1] == 0:
            a += 1
            aa += cases[case][0]
        elif cases[case][0] == 0:
            b += 1
            bb += cases[case][1]
        else:
            c += 1
            cc += cases[case][0] +cases[case][1]
    print "cases:valid=%d vabitan=%d both=%d"%(a, b, c) 
    print "count:valid=%d vabitan=%d both=%d"%(aa, bb, cc) 
    print a,b,c 
    print aa,bb,cc
    print len(drugs1), len(drugs2)
    print sum(drugs1.itervalues()), sum(drugs2.itervalues())
    
#countDrugTypes()

def genValidDrugCases():
    """ Count number of drugs types """
    vdrugs = set(open(datdir + "/validdrugs.txt").read().split("\n"))
    drugs1, drugs2, alldrugs = {}, {}, {}
    cases = {}
    cnt = 0
    for line in open(datdir + "/drugs.txt"):
        parts = line.strip().split(" ", 2)
        if len(parts)!=3: continue
        cnt += 1
        if cnt%1000 == 0:print cnt
        drug = parts[2]
        cases.setdefault(parts[0], [0,0,[]])
        cases[parts[0]][2].append(drug)
        if parts[1]=='1': assert drug in vdrugs
        if parts[1]=='1' or drug in vdrugs:
            cases[parts[0]][0] += 1
            drugs = drugs1
        elif parts[1]=='2':
            cases[parts[0]][1] += 1
            drugs = drugs2
        else:
            raise parts[1] 
        drugs.setdefault(drug, 0)
        drugs[drug] += 1
        alldrugs.setdefault(drug, 0)
        alldrugs[drug] += 1
    
    fout = open(datdir + "/validcases.txt", "w")
    fout2 = open(datdir + "/allcases.txt", "w")
    a,b,c = 0,0,0
    aa,bb,cc = 0,0,0
    for case in cases:
        fout2.write("%s$%s\n"%(case, "$".join(cases[case][2])))
        if cases[case][1] == 0:
            a += 1
            aa += cases[case][0]
            fout.write("%s$%s\n"%(case, "$".join(cases[case][2])))
        elif cases[case][0] == 0:
            b += 1
            bb += cases[case][1]
        else:
            c += 1
            cc += cases[case][0] +cases[case][1]
    fout.close()
    fout2.close()
    
    print "cases:valid=%d vabitan=%d both=%d"%(a, b, c) 
    print "count:valid=%d vabitan=%d both=%d"%(aa, bb, cc) 
    print a,b,c 
    print aa,bb,cc
    print len(drugs1), len(drugs2)
    print sum(drugs1.itervalues()), sum(drugs2.itervalues())
    
    fout = open(datdir+"/drugid.txt", "w")
    sorted_drugs = sorted(drugs1.items(), countCmp, reverse=True);
    for i in range(len(sorted_drugs)):
        print i, sorted_drugs[i]
        fout.write("D%4d %s\n"%(1000+i, sorted_drugs[i][0]))
    fout.close()
    
    fout = open(datdir+"/alldrugid.txt", "w")
    sorted_drugs = sorted(alldrugs.items(), countCmp, reverse=True);
    for i in range(len(sorted_drugs)):
        print i, sorted_drugs[i]
        fout.write("D%6d %s\n"%(100000+i, sorted_drugs[i][0]))
    fout.close()
    
#    for s in sorted(drugs2.items(), countCmp):
#        print s[0], '\t', s[1]

#genValidDrugCases()

def genData(drugidfile, casefile, datfile):
    """ Generate final data file for AERS """
    drugid = {}
    for line in open(drugidfile):
        if not line: continue
        id, name = line.strip().split(" ",1)
        drugid[name] = id
    reacid = {}
    for line in open(datdir+"/reacid.txt"):
        if not line: continue
        id, name = line.strip().split(" ",1)
        reacid[name] = id
    cases = {}
    for line in open(casefile):
        parts = line.strip().split("$")
        cases[parts[0]] = [[],[]]
        for name in parts[1:]:
            assert name in drugid
            cases[parts[0]][0].append(drugid[name])
#        print cases[parts[0]]
    for f in os.listdir(datdir):
        if f.startswith("REAC"):
            for line in open(datdir + "/" + f).read().split("\n")[1:]:
                parts = line.strip().split("$")
                parts = map(string.strip, parts)
                if len(parts) < 2: continue
                if parts[0] in cases:
                    cases[parts[0]][1].append(reacid[parts[1]])
    fout = open(datfile, "w")
    for drugs, reacs in cases.itervalues():
        print >>fout, " ".join(drugs+reacs)
    fout.close()
#genData(datdir+"/drugid.txt", datadir+"/vcases.txt", datadir+"/aers.txt")
#genData(datdir+"/alldrugid.txt", datdir+"/allcases.txt", datdir+"/aers2.txt")

def analyzeData(datfile):
    length_cnt = [0]*300
    for line in open(datfile):
        parts = line.strip().split()
        if 'D' not in line or 'R' not in line:
            print line
        length_cnt[len(parts)] += 1
    m = 0
    for n in length_cnt:
        m += n
        print n, m
    
#analyzeData(datdir+"/aers.txt")
#analyzeData(datdir+"/aers2.txt")