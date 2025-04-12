<?php
$servername = "localhost";
$username = "root"; // predeterminado en XAMPP
$password = "";     // predeterminado en XAMPP
$dbname = "appregistro";

// Crear conexión
$conn = new mysqli($servername, $username, $password, $dbname);

// Verificar conexión
if ($conn->connect_error) {
    die("Conexión fallida: " . $conn->connect_error);
}

// Obtener datos POST
$nombre = $_POST['nombre'];
$edad = $_POST['edad'];
$genero = $_POST['genero'];
$escuela = $_POST['escuela'];
$carreras = $_POST['carreras'];
$correo = $_POST['correo'];
$telefono = $_POST['telefono'];

// Insertar en la base de datos
$sql = "INSERT INTO registros (nombre, edad, genero, escuela, carreras, correo, telefono)
        VALUES ('$nombre', '$edad', '$genero', '$escuela', '$carreras', '$correo', '$telefono')";

if ($conn->query($sql) === TRUE) {
    echo "Registro exitoso";
} else {
    echo "Error: " . $sql . "<br>" . $conn->error;
}

// Cerramos la conexión
$conn->close();
?>
