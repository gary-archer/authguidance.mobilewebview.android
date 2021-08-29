# authguidance.mobileweb.android

[![Codacy Badge](https://app.codacy.com/project/badge/Grade/2e549c0565ad41c08aef8b9e514b2dca)](https://www.codacy.com/gh/gary-archer/oauth.mobileweb.android/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=gary-archer/oauth.mobileweb.android&amp;utm_campaign=Badge_Grade)

[![Known Vulnerabilities](https://snyk.io/test/github/gary-archer/oauth.mobileweb.android/badge.svg?targetFile=app/build.gradle)](https://snyk.io/test/github/gary-archer/oauth.mobileweb.android?targetFile=app/build.gradle)

### Overview

* An Android Sample using OAuth and Open Id Connect, referenced in my blog at https://authguidance.com
* **The goal of this sample is to integrate Secured Web Content into an Open Id Connect Secured Android App**

### Details

* See the [Overview Page](https://authguidance.com/2020/06/17/mobile-web-integration-goals/) for a summary and instructions on how to run the code
* See the post on [Coding Key Points](https://authguidance.com/2020/06/18/mobile-web-integration-coding-key-points/) for design aspects

### Technologies and Behaviour

* Kotlin and Jetpack are used to develop an app that consumes Secured Web Content
* Secured ReactJS SPA views can be run from the mobile app, without a second login 
* SPA views can execute in a web view and call back the mobile app to get tokens
* SPA views can alternatively execute in a system browser and rely on Single Sign On cookies

### Middleware Used

* The [AppAuth-Android Library](https://github.com/openid/AppAuth-Android) implements Authorization Code Flow (PKCE) via a Claimed HTTPS Scheme
* AWS API Gateway is used to host our sample OAuth Secured API
* AWS Cognito is used as the default Authorization Server
* The Android Key Store is used to store encrypted tokens on the device after login
* AWS S3 and Cloudfront are used to serve mobile deep linking asset files and interstitial web pages
