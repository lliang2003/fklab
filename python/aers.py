'''
Created on 2009-9-21

@author: fankai
'''

def analysis():
    line_cnt = 0
    cnt = [0]*30
    for line in open("f:/data/prr.dat"):
        line_cnt += 1
        if len(line.split()) < 10:
            cnt[len(line.split())] += 1
    print cnt
    print sum(cnt), line_cnt
#analysis()

def proc_fda_data():
    of = open("f:/data/prr.dat", "wb")
    drug_set = {}
    reaction_set = {}
    line_cnt = 0
    record_cnt = 0
    
    for line in open("f:/data/input.dat"):
        line_cnt += 1
        parts = line.split(",")
        if len(parts) != 10: continue
        drugs, reactions, outcomes = parts[3:6]
#        if len(drugs.split(";")) > 5: continue
#        if len(reactions.split(";")) > 5: continue
        for drug in drugs.split(";"):
            drug = drug.lstrip().split(" ")[0]
            if drug not in drug_set:
                drug_set[drug] = len(drug_set)
            print >>of, "D%d"%drug_set[drug], 
        for reaction in reactions.split(";"):
            reaction = reaction.lstrip().split(" ")[0]
            if reaction not in reaction_set:
                reaction_set[reaction] = len(reaction_set)
            print >>of, "R%d"%reaction_set[reaction], 
        print >>of
        record_cnt += 1
                
    print len(drug_set), line_cnt, record_cnt

proc_fda_data()


def proc_prr_data():
    of = open("f:/data/prr2.dat", "wb")
    for line in open("f:/data/prr.dat"):
        s = set()
        for item in line.strip().split(" "):
            s.add(item)
        for item in sorted(list(s)):
            print >>of, item,
        print >>of
        
proc_prr_data()

def chi_square(a, b):
    f = sum(a)/float(sum(b))
    b = [i*f for i in b]
    r = 0
    for i in range(len(a)):
        print a[i], b[i], (a[i]-b[i])**2/b[i]
        r += (a[i]-b[i])**2/b[i]
    return r

#print chi_square([41,55,754,591958], [55*754/float(591958),55,754,591958])

