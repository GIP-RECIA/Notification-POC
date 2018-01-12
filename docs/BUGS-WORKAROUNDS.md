Spring: Issue SPR-16265
-----------------------

[Sending message to specific user on Spring Websocket](https://stackoverflow.com/questions/22367223/sending-message-to-specific-user-on-spring-websocket)

[Issue SPR-16265](https://jira.spring.io/browse/SPR-16265) is causing error logs in Emission Service. Those errors can 
be safely ignored as
[pull request](https://github.com/spring-projects/spring-framework/pull/1616) as been merged. It should be fixed in next Spring Boot Release.


Spring: spring-security-oauth #1015
-----------------------------------

`notification/notification-emission-service/src/main/java/org/esco/notification/emission/config/JwkCasResourceServerConfig.java` includes a reflection hack to access private field that should be configurable.

This problem is referenced as [spring-projects/spring-security-oauth#1015](https://github.com/spring-projects/spring-security-oauth/issues/1015) and should be fixed with next Spring Boot release. 

(The private field will be available as a constructor parameter of the object)


CAS: supported claims are not populated
---------------------------------------

`cas-esco-overlay/cas/src/main/java/org/apereo/cas/oidc/claims/BaseOidcScopeAttributeReleasePolicy.java` is ported from
still to be released [CAS commit 8cd83063c277f7c58a1921953cc1d13cc257a0e9](https://github.com/apereo/cas/commit/8cd83063c277f7c58a1921953cc1d13cc257a0e9#diff-d69bba9db3412876268a0679ae6854e3).

It brings a fix related to OpenID claims that is required for OpenID Connect to work properly.

It should be removed with CAS >=5.3
