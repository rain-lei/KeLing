# 课灵 · 游戏化学习助手

<p align="center">
  <strong>让学习变成一场有趣的冒险</strong>
</p>

## 项目简介

**课灵**是一款专为大学生设计的 Android 游戏化学习助手。通过将学习任务游戏化，结合 AI 与个性化推荐，帮助学生更高效、更有趣地完成学业。

### 核心特性

| 模块 | 功能 |
|------|------|
| 任务系统 | 三维任务地图、动态难度、组队协作、日常任务模板 |
| 成就体系 | 徽章、经验值、排行榜 |
| AI 助手 | 多模态交互、学情诊断、知识图谱、智能推荐 |
| 课程管理 | 教务同步、智能课表、教材数字化（OCR） |
| 无障碍 | 字体调节、高对比度、TTS、手势 |

## 技术栈

| 层级 | 技术 |
|------|------|
| 语言 | Kotlin 1.9.20 |
| UI | Jetpack Compose、Material3 |
| 架构 | MVVM + Clean Architecture |
| DI | Hilt |
| 导航 | Navigation Compose |
| 本地 | Room、DataStore |
| 网络 | Retrofit、OkHttp |
| 组件 | Lottie、Vico、Coil、ML Kit、CameraX、BiometricPrompt |

依赖版本统一由 `gradle/libs.versions.toml` 管理。

## 项目结构

```
KeLing/
├── app/                          # Android 应用
│   └── src/main/
│       ├── java/com/keling/app/
│       │   ├── data/             # 数据层
│       │   │   ├── local/        # Room DAO、Database
│       │   │   ├── model/        # 数据模型（含 DailyTaskTemplate）
│       │   │   ├── remote/      # ApiService
│       │   │   ├── repository/  # Repository 实现
│       │   │   └── task/        # GradeTaskGenerator
│       │   ├── di/              # Hilt 模块
│       │   ├── ui/
│       │   │   ├── components/  # NeonCard、NeonButton 等
│       │   │   ├── navigation/  # KelingNavHost、NavRoutes
│       │   │   ├── screens/     # 各功能页面
│       │   │   └── theme/       # 科幻霓虹主题
│       │   └── util/
│       └── res/
├── KeLingShowcase/               # 产品展示页（静态 HTML）
│   ├── index.html                # 主展示（13 页幻灯片）
│   ├── architecture-and-logo.html
│   ├── css/keling-theme.css      # 共享主题样式
│   └── js/
│       ├── logo-animation.js     # 课灵几何动效
│       └── showcase-slides.js    # 数据驱动 slide 生成
├── gradle/
│   ├── libs.versions.toml        # 版本目录
│   └── wrapper/
└── build.gradle.kts
```

## 快速开始

### 环境要求

- **JDK 17**（AGP 8.13 需 Java 11+）
- Android SDK 34
- Gradle 8.13（项目自带 wrapper）

### 构建与运行

```bash
# 克隆项目
git clone https://github.com/your-org/keling.git
cd keling

# 使用 JDK 17 构建（Windows PowerShell）
$env:JAVA_HOME = "D:\Program Files\Java\jdk-17.0.18"  # 按实际路径调整
.\gradlew.bat assembleDebug

# 或直接运行（若默认 Java 已是 17）
.\gradlew.bat assembleDebug
```

APK 输出路径：`app/build/outputs/apk/debug/app-debug.apk`

### 展示页

在 `KeLingShowcase/` 目录下用浏览器打开 `index.html`，或通过本地 HTTP 服务访问：

```bash
cd KeLingShowcase
python -m http.server 8765
# 访问 http://localhost:8765
```

## 设计规范

### 配色（科幻霓虹）

| 名称 | 色值 | 用途 |
|------|------|------|
| NeonBlue | #00D4FF | 主色 |
| NeonPurple | #BF00FF | 强调 |
| NeonGreen | #00FF88 | 成功 |
| NeonGold | #FFD700 | 成就/奖励 |
| DarkBackground | #0A0E17 | 背景 |
| DarkSurface | #121824 | 卡片 |
| DarkCard | #1E2838 | 组件 |

## 版本历史

### v1.0.0（开发中）

- 核心功能：任务、课程、成就、AI 助手、无障碍
- 科幻霓虹 UI
- 展示页抽离共享 CSS/JS，数据驱动 slide

## 贡献

1. Fork 本仓库
2. 创建分支 `git checkout -b feature/xxx`
3. 提交 `git commit -m 'Add xxx'`
4. 推送并创建 Pull Request

## 许可证

仅供学习与教育使用。

---

<p align="center">Made with ❤️ by 课灵团队</p>
