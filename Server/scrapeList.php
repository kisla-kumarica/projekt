<?php
function get_http_response_code($url) {
    $headers = get_headers($url);
    return substr($headers[0], 9, 3);
}
class Film
{
	public $naslov;
	public $rating;
	public $leto;
	public $id;
	public $imglink;
}
if(get_http_response_code("https://www.imdb.com/list/" . $_GET["id"])==404)
	echo "404";
else
{
$html=file_get_contents("https://www.imdb.com/list/" . $_GET["id"]);
$dom = new DOMDocument();
libxml_use_internal_errors(true);
$dom->loadHTML($html);
libxml_clear_errors();
$xpath = new DOMXpath($dom);
$filmi = array();
$naslovi= $xpath->query('//h3[@class="lister-item-header"]/a');
$ind = 0;
foreach($naslovi as $value) {
	$filmi[$ind]=new Film();
	$filmi[$ind]->naslov = ($value->textContent);
	$filmi[$ind]->id = (explode("/",$value->getAttribute("href"))[2]);
	$ind++;
}
$ind = 0;
$links= $xpath->query('//div[contains(@class,"lister-item-image")]/a/img');
foreach($links as $value) {
	$filmi[$ind]->imglink= ($value->getAttribute("loadlate"));
	$ind++;
}
$ind = 0;
$ratings= $xpath->query('//div[@class="ipl-rating-widget"]/div/span[2]');
foreach($ratings as $value) {
	$filmi[$ind]->rating = ($value->textContent);
	$ind++;
}
$ind = 0;
$leto= $xpath->query('//span[contains(@class,"lister-item-year")]');
foreach($leto as $value) {
	$filmi[$ind]->leto= trim(trim(($value->textContent),"("),")");
	$ind++;
}

echo json_encode($filmi);
}
?>
