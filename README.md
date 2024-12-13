<h1 align="center">
    AkariLevel
</h1>

<p align="center" class="shields">
    <img src="https://img.shields.io/badge/dynamic/json?label=Version&amp;query=$.tag_name&amp;url=https://api.github.com/repos/CPJiNan/AkariLevel/releases/latest" alt="Version"/>
    <img src="https://img.shields.io/badge/dynamic/json?label=Date&amp;query=$.created_at&amp;url=https://api.github.com/repos/TabooLib/TabooLib/releases/latest" alt="Date"/>
    <img src='https://img.shields.io/github/commit-activity/t/CPJiNan/AkariLevel' alt="Build Status">
    <img src="https://img.shields.io/github/issues/CPJiNan/AkariLevel.svg" alt="GitHub issues"/>
</p>

- en_US [English](./README.md)
- zh_CN [简体中文](./readme/README.zh_CN.md)

![](./img/AkariLevel-封面图.png)

## Introduction

This is a **highly customizable** Bukkit leveling plugin.

It allows you to create multiple distinct leveling systems for your server. You can set **level names**, **required
experience for leveling up**, **leveling conditions**, and **rewards triggered upon leveling up**.

Why choose **AkariLevel**?

* **Support for multiple leveling systems**
* **Rich and diverse PlaceholderAPI variables**
* **Highly customizable Kether scripts**
* **JavaScript script formulas for experience calculation**
* **DungeonPlus party experience sharing**
* **Convenient configuration for MythicMobs experience drops**
* **Compatibility with experience-enhancing attributes from plugins like AttributePlus and SX-Attribute**
* **Active community and frequent updates**
* **Developer-friendly API and plugin documentation**
* ...

## Links

- **Plugin Docs** [https://cpjinan.github.io/Wiki/AkariLevel/](https://cpjinan.github.io/Wiki/AkariLevel/)
- **Github** [https://github.com/CPJiNan/AkariLevel](https://github.com/CPJiNan/AkariLevel)
- **MCBBS Memorial Edition** [https://www.mcbbs.co/thread-213-1-1.html](https://www.mcbbs.co/thread-213-1-1.html)
- **SpigotMC** [https://www.spigotmc.org/resources/116936/](https://www.spigotmc.org/resources/116936/)
- **QQ Group** [704109949](https://qm.qq.com/q/ZIB5KElIMq)

## Images

![](./img/图片展示-1.png)
![](./img/图片展示-2.png)

## Commands

```
<> - Required [] - Optional

/akarilevel - Main plugin command

/akarilevel exp add <player> <levelGroup> <value> [options] - Grant specified experience points to a player
/akarilevel exp remove <player> <levelGroup> <value> [options] - Remove specified experience points from a player
/akarilevel exp set <player> <levelGroup> <value> [options] - Set a player's experience to a specific value
/akarilevel exp check <player> <levelGroup> - Check a player's experience

/akarilevel level add <player> <levelGroup> <value> [options] - Grant specified levels to a player
/akarilevel level remove <player> <levelGroup> <value> [options] - Remove specified levels from a player
/akarilevel level set <player> <levelGroup> <value> [options] - Set a player's level to a specific value
/akarilevel level check <player> <levelGroup> - Check a player's level

/akarilevel data get <table> <index> <key> - Get the value of a specific key in a table and index
/akarilevel data set <table> <index> <key> <value> - Set a value for a specific key in a table and index

/akarilevel levelup [levelGroup] - Attempt to level up

/akarilevel trace <levelGroup> [options] - Attempt to trace the specified level group

/akarilevel reload - Reload plugin configuration
```

## Permissions

```
akarilevel.command.akarilevel.use - Permission to use plugin commands

akarilevel.admin - Administrator command permission
akarilevel.default - Player command permission
```

## Placeholders

```
%AkariLevel_<levelGroup>_Display% - Level group display name
%AkariLevel_<levelGroup>_MaxLevel% - Maximum level of the level group

%AkariLevel_<levelGroup>_Level% - Player's current level
%AkariLevel_<levelGroup>_LastLevel% - Player's previous level
%AkariLevel_<levelGroup>_NextLevel% - Player's next level

%AkariLevel_<levelGroup>_Exp% - Player's current experience points

%AkariLevel_<levelGroup>_LevelName% - Player's current level name
%AkariLevel_<levelGroup>_LastLevelName% - Name of the player's previous level
%AkariLevel_<levelGroup>_NextLevelName% - Name of the player's next level

%AkariLevel_<levelGroup>_LevelExp% - Experience required to reach the player's current level
%AkariLevel_<levelGroup>_LastLevelExp% - Experience required to reach the player's previous level
%AkariLevel_<levelGroup>_NextLevelExp% - Experience required to reach the player's next level

%AkariLevel_<levelGroup>_ExpProgressBar% - Progress bar for current experience / experience required for the next level
%AkariLevel_<levelGroup>_LevelProgressBar% - Progress bar for current level / maximum level

%AkariLevel_<levelGroup>_ExpProgressPercent% - Progress percentage for current experience / experience required for the next level (integer between 0 and 100)
%AkariLevel_<levelGroup>_LevelProgressPercent% - Progress percentage for current level / maximum level (integer between 0 and 100)
```

## Exp Drops

``` yaml
示例怪物:
  Type: ZOMBIE
  Display: '示例怪物'
  Health: 5
  Damage: 0
  Drops:
  - AkariExp.Example 10 1
  - AkariExp.Example 5 0.5
  - AkariExp.Example 5~10 0.5
  Options:
    MovementSpeed: 0.1
    AlwaysShowName: true
    PreventOtherDrops: true
    MaxCombatDistance: 12
    FollowRange: 6
  Modules:
    ThreatTable: true
```

## Bstats Statistics

> This does not collect private information, so you don't need to worry about data breaches.

![bStats](https://bstats.org/signatures/bukkit/CPJiNan.svg)

## Special Thanks (in no particular order)

Golden_Water, 2000000, liangcha385, xiaochunkun, Sting,
YangXiaoMian, Shanshui2024, InkerXoe, Zarkness, lipind,
q210520993, AlesixDev

## Friendly Links

### XiaoLeBilibili

| Plugin Name            | XiaoLeBilibili                       |
|------------------------|--------------------------------------|
| Official Site          | https://started.ink/aurora-plugins   |
| Plugin Docs            | https://docs.irepo.space/            |
| MCBBS Memorial Edition | https://mcbbs.co/thread-423-1-1.html |

### KalpaDungeon

**<font color=gray>A script extension plugin based on DungeonPlus</font>** _<font color=gray>by 晓劫</font>_

| Plugin Name | KalpaDungeon      |
|-------------|-------------------|
| Plugin Docs | wiki.xiao-jie.top |
| QQ Group    | 939244229         |

## Get the Plugin

### Downloading

1. SpigotMC Download [https://www.spigotmc.org/resources/116936/]()
2. Github Releases & Self-Build [https://github.com/CPJiNan/AkariLevel]()
3. QQ Group 704109949

### Building

**Windows:**

```
gradlew.bat clean build
```

**macOS/Linux:**

```
./gradlew clean build
```

Build artifacts should be found in `./build/libs` folder.

## About Development

The plugin provides a rich variety of API interfaces for developers.  
You can refer to the [Plugin Docs](https://cpjinan.github.io/Wiki/AkariLevel/develop/api.html) for more information.
