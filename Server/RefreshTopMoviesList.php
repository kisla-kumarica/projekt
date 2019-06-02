<?php
class Film 
{
	public $id;
	public $rating;
	public $num;
}
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
$filmi=Array();
for($i=0;$i<250;$i++)
{
	$filmi[$i]=new Film();
	$filmi[$i]->id=0;
	$filmi[$i]->rating=0;
	$filmi[$i]->num=0;
}
$file = fopen("ratings.tsv", "r");
fgets($file);
while (($line = fgets($file)) !== false) {
	$film=new Film();
	$exp=explode("\t", $line);

	$film->id=$exp[0];
	$film->rating=$exp[1];
	$film->num=$exp[2];
	addToArray($film,$filmi);
}

fclose($file);

for($i=0;$i<250;$i++)
	echo "<br />".$filmi[$i]->id;
echo "\nDONE";
function addToArray($film, &$filmi)
{
	if(min(array_map(create_function('$i', 'return $i->rating;'), $filmi))<=$film->rating&&$film->num>1000)
	{
		$zamikind=0;
		for($i=0;$i<count($filmi);$i++)
			if($filmi[$i]->rating<$film->rating||($filmi[$i]->rating==$film->rating&&$filmi[$i]->num<$film->num))
			{
				$zamikind=$i;
				break;
			}
		$prejsn=$filmi[$zamikind+1];
		for($i=$zamikind+2;$i<count($filmi);$i++)
		{
			$prejsn=$filmi[$i];
			$filmi[$i]=$filmi[$i-1];
		}
		$filmi[$zamikind]=$film;
		if(count($filmi)>250)
			array_pop($filmi);
	}
}
?>
