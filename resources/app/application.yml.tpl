stage: production
server:
  servlet:
    contextPath: /smeagol
homeDirectory: {{ .Env.Get "SMEAGOL_HOME" }}
scm:
  url: https://{{ .Env.Get "FQDN" }}/scm
cas:
  url: https://{{ .Env.Get "FQDN" }}/cas
  serviceUrl: https://{{ .Env.Get "FQDN" }}
