#./gradlew clean createDebugCoverageReport jacocoTestReport sonarqube
./gradlew clean build coverageReport

#-Pandroid.injected.signing.store.file="travis-encrypt/calysigningkey.jks" \
#-Pandroid.injected.signing.store.password=$STORE_PASSWORD \
#-Pandroid.injected.signing.key.alias=$KEY_ALIAS \
#-Pandroid.injected.signing.key.password=$KEY_PASSWORD