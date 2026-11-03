# Configuration for mockery v3 (https://vektra.github.io/mockery/v3/configuration/).
# This file belongs to the repository and may be adapted freely;
all: true
recursive: true
# "testify" generates mocks with EXPECT() expectations. Alternatively, "matryer" generates
# moq-style mocks with a func field per method (no testify dependency), see
# https://vektra.github.io/mockery/v3.8/template/matryer/
template: testify

dir: "{{.InterfaceDir}}"
filename: "mocks_test.go"
pkgname: "{{.SrcPackageName}}"
structname: "{{.Mock}}{{.InterfaceName | firstUpper }}"

exclude-subpkg-regex:
  - "vendor"

# 'make mocks-init' adds one entry per module (several in a go workspace).
packages:
@PACKAGES@

# Alternative to 'all: true': maintain mocks deliberately per interface, so that only
# mocks a test actually needs are generated. Set 'all: false' and list the interfaces:
#
# all: false
# packages:
#   github.com/cloudogu/my-module/core:
#     interfaces:
#       CacheManager:
#       FileReader:
