<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<!DOCTYPE html>
<html>
<head>
    <title>Home</title>
    <script>
        // Recupera il token dalla sessione
        var token = '${jwt}'; // Assicurati che il token sia correttamente codificato

        // Funzione per inviare una richiesta quando la pagina è caricata
        function sendProtectedRequest() {

            fetch('/home', {
                method: 'GET',
                headers: {
                    'Authorization': 'Bearer ' + token // Imposta il token nell'header Authorization
                }
            });
        }

        // Chiama la funzione al caricamento della pagina
        window.onload = sendProtectedRequest;
    </script>
</head>
<body>
    <h1>Benvenuto!</h1>
    
</body>
</html>