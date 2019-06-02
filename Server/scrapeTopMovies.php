<?php
class Film
{
	public $naslov;
	public $rating;
	public $leto;
}
$html=file_get_contents("https://www.imdb.com/chart/top?ref_=nv_mv_250");
$dom = new DOMDocument();
libxml_use_internal_errors(true);
$dom->loadHTML($html);
libxml_clear_errors();
$xpath = new DOMXpath($dom);
$filmi = array();
$naslovi= $xpath->query('//td[@class="titleColumn"]/a');
$ind = 0;
foreach($naslovi as $value) {
	$filmi[$ind]=new Film();
    	$filmi[$ind]->naslov = ($value->textContent);
	$ind++;
}
$ind = 0;
$ratings= $xpath->query('//td[contains(@class, "ratingColumn")]/strong');
foreach($ratings as $value) {
    	$filmi[$ind]->rating = ($value->textContent);
	$ind++;
}
$ind = 0;
$leto= $xpath->query('//td[@class="titleColumn"]/span');
foreach($leto as $value) {
    	$filmi[$ind]->leto= trim(trim(($value->textContent),"("),")");
	$ind++;
}

	echo json_encode($filmi);
?>
