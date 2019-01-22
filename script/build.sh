#./gradlew clean createDebugCoverageReport jacocoTestReport sonarqube
./gradlew clean build coverageReport exportVersionName

#-Pandroid.injected.signing.store.file="travis-encrypt/calysigningkey.jks" \
#-Pandroid.injected.signing.store.password=$STORE_PASSWORD \
#-Pandroid.injected.signing.key.alias=$KEY_ALIAS \
#-Pandroid.injected.signing.key.password=$KEY_PASSWORD

file_date=$(date "+%m.%d-%H:%M")
version_name=$(VERSION_NAME)

#debug
file_name_debug="app/build/outputs/apk/debug/AngelTalk-$version_name-debug.apk"
file_location_debug="app/build/outputs/apk/debug/AngelTalk-$version_name-debug-$file_date.apk"

cp $file_location_debug $file_name_debug

#release
file_name_release="app/build/outputs/apk/release/AngelTalk-$version_name-release.apk"
file_location_release="app/build/outputs/apk/release/AngelTalk-$version_name-release-$file_date.apk"

cp $file_location_release $file_name_release
