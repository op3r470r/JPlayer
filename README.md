# JPlayer

## 描述

基于 JavaFX 的MP3音乐播放器，用于练手和学习。


## 界面

![player](ReadmeFile/player.png)

![drawerList](ReadmeFile/drawerList.png)

![animation](ReadmeFile/animation.gif)

## 依赖

- JDK 8
- [JFoenix](https://github.com/sshahine/JFoenix) : UI 控件
- [mp3agic](https://github.com/mpatric/mp3agic) ：获取 mp3 元数据

## 设计

使用 MVC 设计模式，并且遵循：

- 只提供一个单一的 Model 给 Controller 和 View，并使用单例模式实现唯一实例。
- Controller 只更新 Model 的数据，不直接更新 View 的信息，也不与其他 Controller 交互。
- View 的更新来自于 Model，使用属性绑定实现。

