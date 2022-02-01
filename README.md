# google-play-scraper-kotlin

Library for scraping of the application data from the Google Play Store.

[![Download](https://img.shields.io/maven-central/v/com.arthurivanets/google-play-scraper.svg?label=Download)](https://mvnrepository.com/search?q=com.arthurivanets.google-play-scraper)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

# Related Projects

The library was inspired by the following projects:

> **[google-play-scraper](https://github.com/facundoolano/google-play-scraper)** 
> 
> Node.js module to scrape application data from the Google Play Store.

# Installation

First, make sure that you've added the `mavenCentral()` repository to your top-level `build.gradle` file:

```groovy
allprojects {
    //...
    repositories {
        //...
        mavenCentral()
        //...
    }
    //...
}
```

Then, add the desired version of the `google-play-scraper` as a dependency:

> ***Latest version:*** [![Download](https://img.shields.io/maven-central/v/com.arthurivanets/google-play-scraper.svg?label=Download)](https://mvnrepository.com/search?q=com.arthurivanets.google-play-scraper)

```groovy
dependencies {
    implementation("com.arthurivanets:google-play-scraper:x.y.z")
}
```

# Usage

### Initialization

```kotlin
// default configuration
val scraper = GooglePlayScraper()

//...

// custom configuration
val scraper = GooglePlayScraper(
    GooglePlayScraper.Config(
        throttler = HumanBehaviorRequestThrottler(),
        //...
    )
)
```

### Available methods

<details>
  <summary>
    <b>Categories</b> - retrieves the available app categories.
  </summary>

```kotlin
val response = scraper.getCategories().execute()

if (response.isSuccess) {
    val categories = response.requireResult()
    // do something with the obtained categories
} else {
    val error = response.requireError()
    // do something with the error
}
```
</details>

<details>
  <summary>
    <b>App Details</b> - retrieves the detailed information about a specified app.
  </summary>

**Request Parameters**:
* `appId` - the exact id of the application (e.g. `com.myapp`).
* `language` - language code ([ISO 639-1](https://en.wikipedia.org/wiki/List_of_ISO_639-1_codes)) to define the language of the information to be retrieved (optional, defaults to `EN`).
* `contry` - country code ([ISO 3166](https://en.wikipedia.org/wiki/List_of_ISO_3166_country_codes)) to define the origin of the information to be retrieved (optional, detauls to `US`).

```kotlin
val params = GetAppDetailsParams(
    appId = "com.some.app",
    language = "EN",
    country = "US"
)
val response = scraper.getAppDetails(params).execute()

if (response.isSuccess) {
    val appDetails = response.requireResult()
    // do something with the obtained app details
} else {
    val error = response.requireError()
    // do something with the error
}
```
</details>

<details>
  <summary>
    <b>Developer Apps</b> - retrieves the apps published by a specified developer.
  </summary>

**Request Parameters**:
* `devId` - the exact id of the developer.
* `language` - language code ([ISO 639-1](https://en.wikipedia.org/wiki/List_of_ISO_639-1_codes)) to define the language of the information to be retrieved (optional, defaults to `EN`).
* `contry` - country code ([ISO 3166](https://en.wikipedia.org/wiki/List_of_ISO_3166_country_codes)) to define the origin of the information to be retrieved (optional, detauls to `US`).
* `limit` - maximum number of apps to be retrieved (optional, defaults to `100`).

```kotlin
val params = GetDeveloperAppsParams(
    devId = "Super+Useful+Apps",
    language = "EN",
    country = "US",
    limit = 150
)
val response = scraper.getDeveloperApps(params).execute()

if (response.isSuccess) {
    val apps = response.requireResult()
    // do something with the obtained apps
} else {
    val error = response.requireError()
    // do something with the error
}
```
</details>

<details>
  <summary>
    <b>Similar Apps</b> - retrieves the apps that are deemed similar to a specified app.
  </summary>

**Request Parameters**:
* `appId` - the exact id of the application (e.g. `com.myapp`).
* `language` - language code ([ISO 639-1](https://en.wikipedia.org/wiki/List_of_ISO_639-1_codes)) to define the language of the information to be retrieved (optional, defaults to `EN`).
* `contry` - country code ([ISO 3166](https://en.wikipedia.org/wiki/List_of_ISO_3166_country_codes)) to define the origin of the information to be retrieved (optional, detauls to `US`).
* `limit` - maximum number of apps to be retrieved (optional, defaults to `100`).

```kotlin
val params = GetSimilarAppsParams(
    appId = "com.myapp",
    language = "EN",
    country = "US",
    limit = 150
)
val response = scraper.getSimilarApps(params).execute()

if (response.isSuccess) {
    val similarApps = response.requireResult()
    // do something with the obtained apps
} else {
    val error = response.requireError()
    // do something with the error
}
```
</details>

<details>
  <summary>
    <b>Apps List</b> - retrieves the apps that are associated with a specified category/collection.
  </summary>

**Request Parameters**:
* `category` - the exact category of the apps to be retrieved (optional, defaults to `null`).
* `collection` - the exact collection of the apps to be retrieved (optional, defaults to `Collection.TOP_FREE`).
* `language` - language code ([ISO 639-1](https://en.wikipedia.org/wiki/List_of_ISO_639-1_codes)) to define the language of the information to be retrieved (optional, defaults to `EN`).
* `contry` - country code ([ISO 3166](https://en.wikipedia.org/wiki/List_of_ISO_3166_country_codes)) to define the origin of the information to be retrieved (optional, detauls to `US`).
* `limit` - maximum number of apps to be retrieved (optional, defaults to `100`).

```kotlin
val params = GetAppsParams(
    collection = Collection.TOP_PAID,
    language = "EN",
    country = "US",
    limit = 150
)
val response = scraper.getApps(params).execute()

if (response.isSuccess) {
    val apps = response.requireResult()
    // do something with the obtained apps
} else {
    val error = response.requireError()
    // do something with the error
}
```
</details>

<details>
  <summary>
    <b>App Search</b> - performs an app search. 
  </summary>

**Request Parameters**:
* `query` - app search query (e.g. `todo list`).
* `language` - language code ([ISO 639-1](https://en.wikipedia.org/wiki/List_of_ISO_639-1_codes)) to define the language of the information to be retrieved (optional, defaults to `EN`).
* `contry` - country code ([ISO 3166](https://en.wikipedia.org/wiki/List_of_ISO_3166_country_codes)) to define the origin of the information to be retrieved (optional, detauls to `US`).
* `limit` - maximum number of apps to be retrieved (optional, defaults to `100`).

```kotlin
val params = SearchAppsParams(
    query = "todo list",
    language = "EN",
    country = "US",
    limit = 150
)
val response = scraper.searchApps(params).execute()

if (response.isSuccess) {
    val apps = response.requireResult()
    // do something with the obtained apps
} else {
    val error = response.requireError()
    // do something with the error
}
```
</details>

<details>
  <summary>
    <b>App Permissions</b> - retrieves the list of permissions of a specified app.
  </summary>

**Request Parameters**:
* `appId` - the exact id of the application (e.g. `com.myapp`).
* `language` - language code ([ISO 639-1](https://en.wikipedia.org/wiki/List_of_ISO_639-1_codes)) to define the language of the information to be retrieved (optional, defaults to `EN`).

```kotlin
val params = GetAppPermissionsParams(
    appId = "com.myapp",
    language = "EN",
)
val response = scraper.getAppPermissions(params).execute()

if (response.isSuccess) {
    val appPermissions = response.requireResult()
    // do something with the obtained permissions
} else {
    val error = response.requireError()
    // do something with the error
}
```
</details>

<details>
  <summary>
    <b>App Reviews</b> - retrieves the list of reviews for a specified app.
  </summary>

**Request Parameters**:
* `appId` - the exact id of the application (e.g. `com.myapp`).
* `language` - language code ([ISO 639-1](https://en.wikipedia.org/wiki/List_of_ISO_639-1_codes)) to define the language of the information to be retrieved (optional, defaults to `EN`).
* `contry` - country code ([ISO 3166](https://en.wikipedia.org/wiki/List_of_ISO_3166_country_codes)) to define the origin of the information to be retrieved (optional, detauls to `US`).
* `limit` - maximum number of apps to be retrieved (optional, defaults to `100`).
* `sortingOrder` - sorting order of the app reviews (optional, defaults to `ReviewSortingOrder.NEWEST`).

```kotlin
val params = GetAppReviewsParams(
    appId = "com.myapp",
    language = "EN",
    country = "US",
    limit = 150
)
val response = scraper.getAppReviews(params).execute()

if (response.isSuccess) {
    val reviews = response.requireResult()
    // do something with the obtained reviews
} else {
    val error = response.requireError()
    // do something with the error
}
```
</details>

### Throttling

All scraper methods interact with Google Play services in one way or another, 
which means that all of them get impacted by the request throttling policies imposed by the Google Play itself.
It is quite easy to exhaust the request quota and start getting the `503` responses with requesting entitiy verification captchas which,
if not completed properly, might lead to a temporary ban of the requesting IP address, that usuauly lasts for about an hour.
So, it's a good idea to configure the client-side throttling in order to reduce the risk of running into the aforementioned problems.

Throttling can be configured by providing an appropriate implementation of the `RequestThrottler` during the initialization of the `GooglePlayScraper`.

Available `RequestThrottler` implementations:
* [`HumanBehaviorRequestThrottler`]() - applies an artificial delay to each scraper-specific request (can be configured).
* [`NoRequestThrottling`]() - no request throttling (`default configuration`).