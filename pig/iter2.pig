set default_parallel 200;
last = LOAD 'trec2/last' USING PigStorage('\t') AS (fromId:int, pr:double);
count = LOAD 'trec2/count' USING PigStorage('\t') AS (fromId:int, count:int);
link = LOAD 'trec2/link' USING PigStorage('\t') AS (fromId:int, toId:int);

info = join last by fromId, count by fromId, link by fromId;
prs = foreach info generate link::toId as toId, last::pr/count as score;
pragg = group prs by toId;
npr = foreach pragg generate group as toId, SUM(prs.score)*0.85+15 as pr;
store npr into 'trec2/next';
