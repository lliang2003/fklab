set default_parallel 200;
dat = LOAD 'dat' USING PigStorage('\t');
SPLIT dat INTO id IF $1 matches '[0-9]+', link IF $1 matches 'http.*';
id2 = foreach id generate $0 AS id, $2 AS url;
link2 = FOREACH link GENERATE $0 AS fromId, $1 AS toUrl;
STORE id2 INTO 'trec2/id';

joined = JOIN id2 BY url, link2 BY toUrl;
idlink = FOREACH joined GENERATE fromId, id AS toId;
STORE idlink INTO 'trec2/link';

fgrouped = GROUP joined BY $2; 
count = FOREACH fgrouped GENERATE group as fromId, COUNT(joined) as count;
init = FOREACH fgrouped GENERATE group as fromId, 100 as pr;
STORE count INTO 'trec2/count';
STORE init INTO 'trec2/last';

