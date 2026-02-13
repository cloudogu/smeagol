# used to create switch the dogu to a prerelease namespace
# e.g. official/usermgmt -> prerelease_official/usermgmt

# scan a already build dogu image with trivy
# usage:   make trivysan                               - will scan with severity CRITICAL
#          make SEVERITY="HIGH, CRITICAL" trivysacn    - will scan with different severity options (e.g. HIGH and CRITICAL)
.PHONY: trivyscan
trivyscan:
	build/make/trivyscan.sh scan $(SEVERITY)