# Overview 
This project demonstrates that NewRelic fails to link transaction in Spring WebClient (reactor-netty) threads handling an error.
This happens only when WebClient is used within Spring WebFlux application. Standalone WebClient (reactor-netty) works just fine.

# How to run

1. In the project folder execute
```
./mvnw.cmd download:wget spring-boot:run -Dnewrelic.config.license_key=<a key>
```

2. Then open http://localhost:8080/success in your browser, and you will see NewRelic distributed transaction id in server logs 
```
2021-03-13 17:11:11.220 [reactor-http-nio-2       ] [INFO ] [2251bfbf90efadaf8c56dbfd07043201] [c.n.r.DemoController] - in /success handler
2021-03-13 17:11:11.785 [reactor-http-nio-4       ] [INFO ] [2251bfbf90efadaf8c56dbfd07043201] [c.n.r.DemoController] - in doOnSuccess
```

3. Then open http://localhost:8080/error in your browser, and you will see that NewRelic distributed transaction id disappears
```
2021-03-13 17:11:22.457 [reactor-http-nio-7       ] [INFO ] [3569c2fae1491ccf1e84cb49460d5b43] [c.n.r.DemoController] - in /error handler
2021-03-13 17:11:22.529 [reactor-http-nio-7       ] [WARN ] [] [r.netty.http.client.HttpClientConnect] - [id: 0x54edef77, L:/192.168.1.5:53099 - R:www.google.com/64.233.161.104:80] The connection observed an error
io.netty.handler.timeout.ReadTimeoutException: null
2021-03-13 17:11:22.537 [reactor-http-nio-7       ] [INFO ] [] [c.n.r.DemoController] - in doOnError
2021-03-13 17:11:22.545 [reactor-http-nio-7       ] [ERROR] [] [o.s.b.a.w.r.e.AbstractErrorWebExceptionHandler] - [c9e0145d-8]  500 Server Error for HTTP GET "/error"
io.netty.handler.timeout.ReadTimeoutException: null
```
