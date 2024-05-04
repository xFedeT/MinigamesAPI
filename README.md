# MinigamesAPI
[![Build Status](https://github.com/xFedeT/MinigamesAPI/actions/workflows/gradle.yml/badge.svg)](https://github.com/xFedeT/MinigamesAPI/actions?query=branch%3Amain)
[![Code Climate](https://codeclimate.com/github/xFedeT/MinigamesAPI/badges/gpa.svg)](https://codeclimate.com/github/xFedeT/MinigamesAPI)

A Minigame Plugin/API core that allow to create custom Minigame

- Simple to use
- Lightweight
- Flexible
- Null-safe


## How it works


### Integration
Start using MinigamesAPI by adding this to your pom.xml:
```xml
<repositories>
    <repository>
      <id>JitPack</id>
      <url>https://jitpack.io</url>
    </repository>
  </repositories>

<dependencies>
    <dependency>
        <groupId>com.github.xFedeT</groupId>
        <artifactId>MinigamesAPI</artifactId>
        <version>VERSION</version>
    </dependency>
</dependencies>
```

or build.gradle:
```kotlin
  repositories {
    maven { url 'https://jitpack.io' }
  }

  dependencies {
    implementation 'com.github.xFedeT:MinigamesAPI:VERSION'
  }
```
  
### Getting Started

**Getting MinigamesAPI in you project**
```java
    @Override
    public void onEnable() {
        minigamesAPI = MinigamesProvider.get();
        minigamesAPI.registerMinigame(this);
    }
```
:pencil: Read the full documentation in the [MinigamesAPI Wiki](https://github.com/xFedeT/MinigamesAPI/wiki).

:pencil: See a full working example based on this
[here](https://github.com/xFedeT/MinigamesAPI/tree/main/sumo).
