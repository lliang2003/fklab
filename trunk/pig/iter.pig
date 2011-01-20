-- set default_parallel 200;
last = LOAD 'trec10/last' USING PigStorage('\t') AS (fromId:int, count:int, toId:int, pr:double);
prs = foreach last generate toId, pr/count as score;
pragg = group prs by toId;
npr = foreach pragg generate group as toId, SUM(prs.score)*0.85+15 as pr;
joined = join npr by toId, last by fromId;
nextround = foreach joined generate last::fromId, last::count, last::toId, npr::pr;
store nextround into 'trec10/next';
