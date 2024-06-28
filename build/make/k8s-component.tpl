# Use the property .spec.deployNamespace to define the namespace the component should be deployed to.
# Make environment variable 'COMPONENT_DEPLOY_NAMESPACE' is responsible for that.
# If 'COMPONENT_DEPLOY_NAMESPACE' is empty the property 'deployNamespace' will be deleted.
apiVersion: k8s.cloudogu.com/v1
kind: Component
metadata:
  name: NAME
  labels:
    app: ces
spec:
  name: NAME
  namespace: NAMESPACE
  version: VERSION