# oauth2-bitbucket-jwt-example

__How to test.__
* Register new OAuth consumer on [Bitbucket](https://bitbucket.org). Enter http://localhost:8080 as callback URL.
* Edit server/src/main/resources/application.yaml.
  * Amend spring.security.oauth2.client.registration.bitbucket.client-id property.
  * Amend spring.security.oauth2.client.registration.bitbucket.client-secret property.
* Run server/src/main/java/com/nbondarchuk/oauth2/server/OAuth2JwtExampleServer.java.
* Run client/src/main/java/com/nbondarchuk/oauth2/client/OAuth2JwtExampleClient.java.
* Open http://localhost:8080/auth/login?redirect_uri=http://localhost:8081/oauth2/redirect in browser and authenticate.
* Check clinet console for output.
