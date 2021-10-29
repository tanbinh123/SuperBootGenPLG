# http://localhost:8761/
server:
  port: 8761

spring:
  application:
    name: eureka-server

eureka:
  instance:
    hostname: localhost
    prefer-ip-address: true
    instance-id: ${r'${spring.cloud.client.ip-address}'}:${r'${server.port}'}
  client:
    register-with-eureka: false
    fetch-registry: false
    service-url:
      defaultZone: http://${r'${eureka.instance.hostname}'}:${r'${server.port}'}/eureka/