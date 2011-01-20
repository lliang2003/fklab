-- set the default task number for each job
-- set default_parallel 200;

dat = LOAD 'dat' USING PigStorage('\t');
SPLIT dat INTO id IF $1 matches '[0-9]+', link IF $1 matches 'http.*';
id2 = foreach id generate $0 AS id, $2 AS url;
link2 = FOREACH link GENERATE $0 AS fromId, $1 AS toUrl, $2 AS atext;
-- this file is a large id->url map
STORE id2 INTO 'trec10/id';

joined = JOIN id2 BY url, link2 BY toUrl;
idlink = FOREACH joined GENERATE fromId, id AS toId;
-- this file stores the link map
STORE idlink INTO 'trec10/link';

fgrouped = GROUP joined BY $2; 
inited = FOREACH fgrouped GENERATE group as fromId, COUNT(joined) as count, FLATTEN(joined.id) as toId, 100.0;
-- this is the initial file used in the first round
STORE inited INTO 'trec10/last';

-- the following is a simple try for generating anchor text, but the output format remains a problem
grouped = group link2 by toUrl;
atext = foreach grouped generate link2.atext;
STORE atext INTO 'trec10/atext';
