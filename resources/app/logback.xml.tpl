<?xml version="1.0" encoding="UTF-8"?>

<configuration>

  <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
    <!-- encoders are  by default assigned the type
         ch.qos.logback.classic.encoder.PatternLayoutEncoder -->
    <encoder>
      <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger - %msg%n</pattern>
    </encoder>
  </appender>
  
  <logger name="com.cloudogu" level='{{ .Config.GetOrDefault "logging/root" "WARN"}}' />

  <root level='{{ .Config.GetOrDefault "logging/root" "WARN"}}'>
    <appender-ref ref="STDOUT" />
  </root>
  
</configuration>