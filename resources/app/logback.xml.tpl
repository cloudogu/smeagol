<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE configuration>

<configuration>
  <import class="ch.qos.logback.classic.encoder.PatternLayoutEncoder"/>
  <import class="ch.qos.logback.core.ConsoleAppender"/>

  <appender name="STDOUT" class="ConsoleAppender">
    <encoder class="PatternLayoutEncoder">
      <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger - %msg%n</pattern>
    </encoder>
  </appender>
  
  <logger name="com.cloudogu" level='{{ .Config.GetOrDefault "logging/root" "WARN"}}' />

  <root level='{{ .Config.GetOrDefault "logging/root" "WARN"}}'>
    <appender-ref ref="STDOUT" />
  </root>
  
</configuration>
