# jwt-demo

## Shared secret key working mechanism:

    A: curl --request GET --url http://localhost:8080/jwt/generateSecretKey/HS256

Configure response of A in application.yml under secretKeys

    B: curl --request GET --url 'http://localhost:8080/jwt/generateToken?secret=<Response of A>&issuer=ifmruser&audience=demo-server&id=<Random/Unique ID>'

    C: curl --request GET --url http://localhost:8080/api/sayHello --header 'Authorization: Secret <Response of B>'

## Asymmetric key working mechanism:

    A: curl --request GET --url http://localhost:8080/jwt/generateAsymmetricKeys/RS256

Configure response of A.publicKey in application.yml under publicKeys

    B: curl --request GET --url 'http://localhost:8080/jwt/generatePrivateToken?privateKey=<Response of A.privateKey>&issuer=ifmruser&audience=demo-server&id=<Random/Unique ID>'

    C: curl --request GET --url http://localhost:8080/api/sayHello --header 'Authorization: Public <Response of B>'