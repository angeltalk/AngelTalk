# AngelTalk Plus - AAC for Beginners [![Build Status](https://travis-ci.com/lab-act/AngelTalk.svg?branch=master)](https://travis-ci.com/lab-act/AngelTalk)

![AngelIcon](github_title.png)

AngelTalk Plus is built to help the children with complex communication needs talk with their parents and caregivers quickly and pleasantly. It is suitable for those who just decided to start using Augmentative and Alternative Communication Tools.

엔젤톡 Plus는 언어 표현에 어려움을 겪는 아이들이 주변 사람들과 쉽고 빠르게 대화하는 것을 도와드리기 위해  만들어진 앱입니다. 특히 이제 막  AAC(보완대체 의사소통-Augmentative and Alternative Communication)를 처음 사용하기 시작하는 아이들에게 유용합니다.

[www.angeltalk.info](http://angeltalk.info)


## Features

##### Real image and voice based AAC
We use picture card as a communication tool. Please add a real image or short video clip to the card, and add your voice over it.

#####  Cards on lock screen of your smartphone
Parents and Caregivers, you don’t need to worry about handing your smartphone to your child. This app uses the lock screen of a smartphone to prevent your child from moving to other apps by pressing the home button. If you are not with your child, just pull down the notification drawer and press the ‘OFF’ button in the notification bar of AngelTalk. Then it won’t appear on the lock screen.

#####  Instant share your cards with others
Share your card with family members, school teachers or language pathologists. To share cards you don’t need to make an account or install other apps, just use existing messages app. It’s easy and simple.

##### 실제 사진과 음성으로 구성된 AAC
엔젤톡 Plus는 의사소통 보완 도구로 그림 카드를 이용하도록 개발되었습니다. 실제 사진과 음성으로 카드를 만들어 보세요. 짧은 동영상으로 사진을 대체할 수도 있습니다.

##### 잠금화면에 나타나는 카드
아이들에게 스마트폰을 건네기가 주저되시죠? 엔젤톡 Plus는 스마트폰의 잠금화면을 사용합니다. 홈버튼을 눌러도 엔젤톡 앱에서 나가지 않습니다. 만약 아이와 같이 있지 않아 잠금화면에 엔젤톡이 나타나지 않게 하시려면, 스마트폰 윗부분을 터치한 상태에서 아래로 내려보세요. 엔젤톡 바가 나옵니다. 바에 있는 ‘OFF’을 누르시면 더 이상 잠금화면에 나타나지 않습니다.

##### 다른 사람과 카드 공유
가족 또는 학교 선생님, 언어 치료사분들과 카드를 공유해 보세요. 별도의 계정을 만들거나 다른 앱을 설치하실 필요가 없습니다. 이미 사용하고 계시는 메신저 앱을 통해 카드를 간편하게 공유할 수 있습니다.

## Development Environment
The app is written entirely in Java and uses the Gradle build system.
To build the app, use the gradlew build command or use "Import Project" in Android Studio.

For more detail, join [angeltalk workspace in slack](https://angeltalk-team.slack.com)

AngelTalk Plus 는 Java 로 작성되었고 Gradle 빌드 시스템을 사용하였습니다.
gradlew build 명령어를 실행하거나 Android Studio 에서 Import Project 메뉴를 선택하여 앱을 빌드 할 수 있습니다.

자세한 정보는 [엔젤톡 Slack 워크스페이스](https://angeltalk-team.slack.com)에서 확인할 수 있습니다.

#### Dependency

* We used [Dagger2](https://github.com/google/dagger) for dependency injection.
* We used [Firebase](https://firebase.google.com/) for storage, database, function and hosting the web page.
* We used [KakaoLink](https://developers.kakao.com/docs/android-reference/com/kakao/KakaoLink.html) for sharing card feature using KakaoTalk.
* We used [Retrofit](https://github.com/square/retrofit) for calling API to make shorten url.
* We used [Butterknife](https://github.com/JakeWharton/butterknife) for field and method binding for Android views
* We used [Lombok](https://github.com/rzwitserloot/lombok/) to abstract away boiler-plate code such as constructor, getter, setter and builder of VO class.
* We used [Robolectric](https://github.com/robolectric/robolectric), **jUnit4** and [Mockito](https://github.com/mockito/mockito) for unit testing.
* We used [Espresso](https://github.com/espressomd/espresso) for basic instrumentation tests.
* We used [Jacoco](https://github.com/jacoco/jacoco) and [sonarqube](https://github.com/SonarSource/sonarqube) for reporting test coverage.


## Contribution

AngelTalk welcomes the interest and participation of developers and designers!

엔젤톡은 개발자, 디자이너 여러분의 관심과 참여를 환영합니다.

[Check here!](contributor.markdown)
