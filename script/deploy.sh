filename="$(find . -name AngelTalk-*-debug-*.apk)"

curl \
  -F "token=$SLACK_KEY" \
  -F "channels=apk_from_travis" \
  -F "initial_comment=Apk file of AngelTalk is created." \
  -F "file=@$filename" \
  https://slack.com/api/files.upload
