#!/usr/bin/env python
# @author   FAN Kai (fankai@net.pku.edu.cn), Peking University  
# @date     Dec 04 07:42:28 AM EST 2009

# Generate two data sets, one contains only valid cases, the other contains all cases
# Each transaction is made up with a set of sorted unique integers.
# Integer between 100001 and 999999 means verbatim drug names.
# Integer between 1001 and 9999 means valid drug names.
# Integer larger than 1000000 means reactions.

import os, string

datdir = "F:/Data/aers/ascii/"
outputdir = "F:/Data/aers/dat/"

def countCmp(a, b):
    return a[1] - b[1]

def genDrugsFile():
    """ Generate drugs data file, all information about drugs is here """
    print "generating drugs file ..."
    fout = open(outputdir + "drugs.txt", "w")
    for f in os.listdir(datdir):
        print f
        if f.startswith("DRUG"):
            for line in open(datdir + "/" + f).read().split("\n")[1:]:
                parts = line.split("$")
                if len(parts) < 13: continue
                print >> fout, parts[0], parts[4], parts[3]
    fout.close()
#genDrugsFile()

def genReactionId():
    """ Reaction -> ID, each ID is a number above 1000000 """
    print "generating reaction id ..."
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
    fout = open(outputdir + "reacid-random.txt", "w")
    reac = reactions.items()
    for i in range(len(reac)):
        print i, reac[i]
        fout.write("%d %d %s\n" % (1000001 + i, reac[i][1], reac[i][0]))
    fout.close()
    fout = open(outputdir + "reacid-sorted.txt", "w")
    sorted_reac = sorted(reactions.items(), countCmp)
    for i in range(len(sorted_reac)):
        print i, sorted_reac[i]
        fout.write("%d %d %s\n" % (1000001 + i, sorted_reac[i][1], sorted_reac[i][0]))
    fout.close()
    fout = open(outputdir + "reacid-rsorted.txt", "w")
    sorted_reac = sorted(reactions.items(), countCmp, reverse=True)
    for i in range(len(sorted_reac)):
        print i, sorted_reac[i]
        fout.write("%d %d %s\n" % (1000001 + i, sorted_reac[i][1], sorted_reac[i][0]))
    fout.close()
#genReactionId()

def countDrugTypes():
    """ Count number of drugs types """
    print "count drug types ..."
    drugs1, drugs2 = {}, {}
    drugcnt = 0
    cases = {}
    for f in os.listdir(datdir):
        if f.startswith("DRUG"):
            for line in open(datdir + "/" + f).read().split("\n")[1:]:
                drugcnt += 1
                parts = line.split("$")
                map(string.strip, parts)
                if len(parts) != 13:continue
                drug = parts[3]
                cases.setdefault(parts[0], [0, 0])
                if parts[4] == '1':
                    cases[parts[0]][0] += 1
                    drugs = drugs1
                else:
                    cases[parts[0]][1] += 1
                    drugs = drugs2
                drugs.setdefault(drug, 0)
                drugs[drug] += 1
            print len(drugs1), len(drugs2)
                
    fout = open(outputdir + "vdrugnames.txt", "w")
    for drug in drugs1:
        print >> fout, drug
    fout.close()
    
    a, b, c = 0, 0, 0
    aa, bb, cc = 0, 0, 0
    for case in cases:
        if cases[case][1] == 0:
            a += 1
            aa += cases[case][0]
        elif cases[case][0] == 0:
            b += 1
            bb += cases[case][1]
        else:
            c += 1
            cc += cases[case][0] + cases[case][1]
    print "cases:valid=%d verbatim=%d both=%d" % (a, b, c) 
    print "count:valid=%d verbatim=%d both=%d" % (aa, bb, cc) 
    print a, b, c 
    print aa, bb, cc
    print len(drugs1), len(drugs2)
    print sum(drugs1.itervalues()), sum(drugs2.itervalues())
#countDrugTypes()

def genDurgId():
    """ Count number of drugs types """
    print "generating drug id ..."
    vdrugs = set(open(outputdir + "vdrugnames.txt").read().split("\n"))
    drugs1, drugs2, alldrugs = {}, {}, {}
    cases = {}
    cnt = 0
    for line in open(outputdir + "drugs.txt"):
        parts = line.strip().split(" ", 2)
        if len(parts) != 3: continue
        cnt += 1
        if cnt % 1000000 == 0:print cnt
        drug = parts[2]
        cases.setdefault(parts[0], [0, 0, []])
        cases[parts[0]][2].append(drug)
        if parts[1] == '1': assert drug in vdrugs
        if parts[1] == '1' or drug in vdrugs:
            cases[parts[0]][0] += 1
            drugs = drugs1
        elif parts[1] == '2':
            cases[parts[0]][1] += 1
            drugs = drugs2
        else:
            raise parts[1] 
        drugs.setdefault(drug, 0)
        drugs[drug] += 1
        alldrugs.setdefault(drug, 0)
        alldrugs[drug] += 1
    
    fout = open(outputdir + "vcases.txt", "w")
    fout2 = open(outputdir + "cases.txt", "w")
    a, b, c = 0, 0, 0
    aa, bb, cc = 0, 0, 0
    for case in cases:
        fout2.write("%s$%s\n" % (case, "$".join(cases[case][2])))
        if cases[case][1] == 0:
            a += 1
            aa += cases[case][0]
            fout.write("%s$%s\n" % (case, "$".join(cases[case][2])))
        elif cases[case][0] == 0:
            b += 1
            bb += cases[case][1]
        else:
            c += 1
            cc += cases[case][0] + cases[case][1]
    fout.close()
    fout2.close()
    
    print "cases:valid=%d verbatim=%d both=%d" % (a, b, c) 
    print "count:valid=%d verbatim=%d both=%d" % (aa, bb, cc) 
    print a, b, c 
    print aa, bb, cc
    
    print len(drugs1), len(drugs2)
    print sum(drugs1.itervalues()), sum(drugs2.itervalues())
    
    fout = open(outputdir + "/vdrugid.txt", "w")
    sorted_drugs = sorted(drugs1.items(), countCmp);
    for i in range(len(sorted_drugs)):
#        print i, sorted_drugs[i]
        fout.write("%d %d %s\n" % (1001 + i, sorted_drugs[i][1], sorted_drugs[i][0]))
    fout.close()
    
    fout = open(outputdir + "/drugid.txt", "w")
    sorted_drugs = sorted(alldrugs.items(), countCmp);
    for i in range(len(sorted_drugs)):
#        print i, sorted_drugs[i]
        fout.write("%d %d %s\n" % (100001 + i, sorted_drugs[i][1], sorted_drugs[i][0]))
    fout.close()
    
#    for s in sorted(drugs2.items(), countCmp):
#        print s[0], '\t', s[1]

#genDurgId()

def genData(drugidfile, ractidfile, casefile, datfile):
    """ Generate final data file for AERS """
    print "generating final data ..."
    drugid = {}
    for line in open(drugidfile):
        if not line: continue
        id, cnt, name = line.strip().split(" ", 2)
        drugid[name] = id
    reacid = {}
    for line in open(ractidfile):
        if not line: continue
        id, cnt, name = line.strip().split(" ", 2)
        reacid[name] = id
    cases = {}
    for line in open(casefile):
        parts = line.strip().split("$")
        cases[parts[0]] = set()
        for name in parts[1:]:
            assert name in drugid
            cases[parts[0]].add(drugid[name])
#        print cases[parts[0]]
    for f in os.listdir(datdir):
        if f.startswith("REAC"):
            for line in open(datdir + "/" + f).read().split("\n")[1:]:
                parts = line.strip().split("$")
                parts = map(string.strip, parts)
                if len(parts) < 2: continue
                if parts[0] in cases:
                    cases[parts[0]].add(reacid[parts[1]])
    fout = open(datfile, "w")
    for case in cases.itervalues():
        items = sorted(map(int, list(case)))
        print >> fout, " ".join(map(str,items))
    fout.close()
genData(outputdir + "vdrugid.txt", outputdir + "reacid-sorted.txt", outputdir + "vcases.txt", outputdir + "/vaers.txt")
genData(outputdir + "drugid.txt", outputdir + "reacid-sorted.txt", outputdir + "cases.txt", outputdir + "/aers.txt")

def analyzeData(datfile):
    length_cnt = [0] * 300
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
