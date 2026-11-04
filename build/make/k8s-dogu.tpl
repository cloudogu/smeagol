apiVersion: k8s.cloudogu.com/v2
kind: Dogu
metadata:
  name: NAME
  labels:
    app: ces
    k8s.cloudogu.com/dogu.name: NAME
spec:
  name: NAMESPACE/NAME
  version: VERSION