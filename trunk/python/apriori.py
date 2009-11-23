
db = []

item_count = {}
cnt = 0


def grow(itemsets):
	r = []
	i = 0
	j = 1
	while i < len(itemsets):
		while j < len(itemsets) and itemsets[i][:-1] == itemsets[j][:-1]:
			j += 1
		for p in range(i, j):
			for q in range(p+1, j):
				r.append(itemsets[p]+itemsets[q][-1:])
		i = j
	return r



def contain(seta, setb):
	i,j = 1, 0
	while j < len(setb):
		if i == len(seta): return False
		if seta[i] > setb[j]: return False
		elif seta[i] < setb[j]: i+=1
		else: i, j = i+1, j+1		
	return True

def check(candidates):
	r = []
	candidates = map(tuple, candidates)
	cnt = {}
	for c in candidates: cnt[c] = 0
	for transaction in db:
#		print transaction
		for candidate in candidates:
#			print transaction, candidate, contain(transaction, candidate)
			if cnt[candidate] < threshold and contain(transaction, candidate):
				cnt[candidate] += 1
				if cnt[candidate] >= threshold:
					r.append(candidate)
					print candidate
	return r

import time
print time.asctime()
for line in open("f:/data/nausea.dat"):
	parts = line.split()
	db.append(parts[:1]+sorted(parts[1:]))
	for item in db[-1][1:]:
#		print item
		item_count.setdefault(item, 0)
		item_count[item] += 1
		cnt += 1
threshold = 600	
fitems = [item for item in item_count if item_count[item] >= threshold]
print len(db)
print len(fitems)
fitemsets = [[item] for item in sorted(fitems)]
print fitemsets

while len(fitemsets) > 0:
	candidates = grow(fitemsets)
	fitemsets = check(candidates)
	print len(candidates), len(fitemsets), fitemsets
	print time.asctime()	
print contain(['f', 1,2, 3], [1,2,3])
	
				
				
	
