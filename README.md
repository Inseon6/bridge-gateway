# Resource Event Listener
## Written By
Ju In-Seon
## Application Type
Gateway
## gson 사용법
```java
JsonObject requestJsonObject = new Gson().fromJson(requestBody, JsonObject.class);
String extension = requestJsonObject.get("extension").getAsString();

JsonObject kidsJsonObject = requestJsonObject.get("kids").getAsJsonObject();
int number = kidsJsonObject.get("number").getAsInt();
```