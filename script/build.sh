#./gradlew clean createDebugCoverageReport jacocoTestReport sonarqube
./gradlew clean build coverageReport

#-Pandroid.injected.signing.store.file="travis-encrypt/calysigningkey.jks" \
#-Pandroid.injected.signing.store.password=$STORE_PASSWORD \
#-Pandroid.injected.signing.key.alias=$KEY_ALIAS \
#-Pandroid.injected.signing.key.password=$KEY_PASSWORD

file_date=$(date "+%m.%d-%H:%M")

#debug
file_name_debug="app/build/outputs/apk/debug/AngelTalk-debug.apk"
file_location_debug="app/build/outputs/apk/debug/AngelTalk-debug-$file_date.apk"

cp $file_location_debug $file_name_debug

#release
file_name_release="app/build/outputs/apk/release/AngelTalk-release.apk"
file_location_release="app/build/outputs/apk/release/AngelTalk-release-$file_date.apk"

cp $file_location_release $file_name_release
