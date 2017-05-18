<?php


session_start();
$_SESSION['user_id'] = 'abcd';
if(isset($_POST['submit1']))
{
	if($_FILES["file"]["error"]>0)
		echo("No File selected");
	else {

		move_uploaded_file($_FILES['file']['tmp_name'],"images/".$_SESSION['user_id'].'.jpg');

	}
	header('Location:home.html');
}

else 
{

$filename = $_SESSION['user_id'].'.jpg';
$result = file_put_contents( 'images/'.$filename, file_get_contents('php://input') );
if (!$result) {
	print "ERROR: Failed to write data to $filename, check permissions\n";
	exit();
}

$url = 'http://' . $_SERVER['HTTP_HOST'] . dirname($_SERVER['REQUEST_URI']) . '/images/' . $filename;
print "$url\n";
}


?>
