CREATE OR REPLACE VIEW produktanalyse AS 
   SELECT DISTINCT b1.pid AS 'pid',
      (SELECT b3.pid FROM 
         (bestpos b2 JOIN bestpos b3) 
            WHERE b2.bid=b3.bid AND b1.pid=b2.pid AND b2.pid<>b3.pid 
            GROUP BY b2.pid, b3.pid 
            ORDER BY count(0) DESC, 1 
            LIMIT 1) 
      AS 'HaeufigGekauftMit' FROM bestpos b1;
