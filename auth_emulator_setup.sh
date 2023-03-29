curl 'http://localhost:9099/identitytoolkit.googleapis.com/v1/accounts:signUp?key=AIzaSyBOJwSYjN-2BJDGUcSVwQnoW5uV72Ohj-w' -H 'Content-Type: application/json' --data-binary '{"email":"andy.murray@epfl.ch","password":"ScotlandBelongsToItself","returnSecureToken":true}'
./gradlew connectedAndroidTest
