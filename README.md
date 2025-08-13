<h1 align="center">
    AkariLevel
</h1>

<p align="center" class="shields">
    <img src="https://img.shields.io/badge/dynamic/json?label=Version&amp;query=$.tag_name&amp;url=https://api.github.com/repos/CPJiNan/AkariLevel/releases/latest" alt="Version"/>
    <img src="https://img.shields.io/badge/dynamic/json?label=Date&amp;query=$.created_at&amp;url=https://api.github.com/repos/CPJiNan/AkariLevel/releases/latest" alt="Date"/>
    <img src='https://img.shields.io/github/commit-activity/t/CPJiNan/AkariLevel' alt="Build Status">
    <img src="https://img.shields.io/github/issues/CPJiNan/AkariLevel.svg" alt="GitHub issues"/>
</p>

![](./img/AkariLevel.png)

## 构建发行版本

发行版本用于正常使用, 不含 TabooLib 本体。

```
./gradlew build
```

## 构建开发版本

开发版本包含 TabooLib 本体, 用于开发者使用, 但不可运行。

```
./gradlew taboolibBuildApi -PDeleteCode
```

> 参数 -PDeleteCode 表示移除所有逻辑代码以减少体积。