<?php
file_put_contents("ratings.tsv.gz", fopen("http://datasets.imdbws.com/title.ratings.tsv.gz", 'r'));
$file = gzopen("ratings.tsv.gz" ,"rb");
$out_file = fopen("ratings.tsv", "wb");
while(!gzeof($file))
{
fwrite($out_file, gzread($file, 4096));
}
fclose($out_file);
gzclose($file);
unlink("ratings.tsv.gz");
?>
