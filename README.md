# Service Routing by User

<img align="center" src="diag.png">

```yml
zuul:
  routes:
    test-service:
      url: "1.1.19@http://localhost:8080;1.2.3@http://localhost:8081"
    user-service:
      url: "http://localhost:8079"
```

