# Alviora Android Multi-Module Project

本仓库为 Android 多模块工程，当前根目录尚未存在独立的 `README.md`，现新增项目说明文档用于记录结构、技术栈与架构。

## 1. 项目概览

| 项目项 | 说明 |
|---|---|
| 项目类型 | Android App（Gradle 多模块） |
| 根项目名 | `Alviora` |
| 应用包名 | `com.alviora.generator` |
| 版本号 | `versionName=101`，`versionCode=101` |
| 最低/目标 SDK | `minSdk 24`，`targetSdk 36` |
| 编译 SDK | `compileSdk 36` |
| Java/Kotlin | Kotlin 2.2.0 + Java 21（source/target 兼容） |

## 2. 技术栈

| 层级 | 技术项 |
|---|---|
| Build 工具链 | Gradle Wrapper `8.14.5`，AGP `8.13.2` |
| Android 特性 | DataBinding、BuildConfig、`application` 模块打包 |
| 架构组件 | AndroidX、Lifecycle、ViewModel、Room、Navigation、Paging |
| 网络层 | Retrofit `3.0.0`、OkHttp `4.12.0`、Moshi、Gson |
| 图片/媒体 | Coil、Glide、Picasso、FFmpeg 相关 SDK、CameraX 1.4.1 |
| 存储与解析 | Room、MMKV（注释中未启用）、自定义工具库 |
| 推送/统计/分析 | Firebase（Analytics / Crashlytics / Messaging / Auth / NDK） |
| 广告与归因 | AppLovin（含多广告源 Adapter）、Singular、Install Referrer |
| 代码生成/注解 | Kotlin KAPT + KSP (`2.2.0-2.0.2`) |
| 混淆与打包安全 | AabResGuard、XmlClassGuard |

## 3. 项目结构

```text
Alviora
├─ app/               # 应用壳模块（聚合入口）
├─ a/                 # 业务组合模块
├─ key/               # 第三方 key/config 相关库模块
├─ module/
│  ├─ architecture/   # 基础架构能力层（Base/Net/UI/工具）
│  ├─ face/           # 核心业务模块（AI 人脸/相机/特效/分享等）
│  ├─ main/           # 启动与主流程模块
│  ├─ openzoomlib/    # 图片查看库适配封装
│  ├─ drawboard/      # 绘制/画布相关能力模块
│  └─ selector/       # 资源选择（含图片/媒体选择能力）模块
├─ .github/           # GitHub Actions 打包流水线
└─ gradle/, build.gradle, settings.gradle
```

### 3.1 模块级资源结构（高层）

| 模块 | 代码目录 | 资源目录（节选） |
|---|---|---|
| app | 无 `java` / `kotlin` 代码 | `src/main/res/values` |
| a | `a/src/main/java/com/a` 等 | `color / drawable / layout / menu / mipmap* / values` |
| key | `key/src/main/java/com/key` | `drawable-* / mipmap-* / raw / values` |
| module/architecture | `src/main/java/com/zzkj/structure` | `anim / drawable* / layout / values / xml` |
| module/face | `src/main/java/com/face`，包含 `ad/ adapter/ bean/ database/ ui/ net/ util/ view/ viewmodel` 等 | `anim / color / drawable / layout / menu / values*（多语言） / xml` |
| module/main | `src/main/java/com/face/main` | `layout / values` |
| module/openzoomlib | `src/main/java/com/flyjingfish` | `drawable / layout / mipmap* / values` |
| module/drawboard | `src/main/java/com/king/drawboard` | `values` |
| module/selector | `src/main/java/com/luck/picture/lib` | `anim / drawable* / layout / values* / xml` |

### 3.2 依赖关系图（当前配置）

```text
app
└─> module:main
    ├─> :a
    └─> module:face
        ├─> :key
        ├─> module:architecture
        ├─> module:selector
        ├─> module:openzoomlib
        └─> module:drawboard
a
└─> module:face
    ├─> module:architecture
    └─> module:drawboard
```

说明：`:app` 为聚合壳工程，仅直接依赖 `:module:main`，核心能力逐层向下分发到具体业务模块。

## 4. 架构与职责（当前可观测）

| 层级 | 模块 | 观察到的职责 |
|---|---|---|
| 应用壳 | app | 应用签名与发布参数、Gradle 插件与资源混淆流程、签名配置 |
| 启动与主流程 | module/main | 提供 `WelcomeActivity` 与应用启动入口（`MANIFEST` 中的 `MAIN/LAUNCHER`） |
| 业务聚合 | a | 业务壳/业务入口模块，聚合并复用 `face` |
| 核心业务 | module/face | 主要 UI/业务逻辑模块，包含多个 `ui`、`adapter`、`viewmodel` 与网络子包 |
| 基础设施 | module/architecture | base/lifecycle/net/ui/util 等基础设施和公共能力 |
| 公共库 | module/openzoomlib / selector / drawboard / key | 图片查看、选择、画布、广告/统计 key 相关能力 |

## 5. 构建与打包（已配置）

```bash
# 当前项目默认构建/发布链路来自 settings 与 scripts/Gradle 配置
./gradlew :app:assembleGooglePlayDebug
./gradlew :app:bundleRelease
```

GitHub Actions 的 `.github/workflows/main.yml` 定义了 `pack` 分支上的打包与 Telegram 通知流程（包括 `bundleRelease` + bundle 转 APK）。

## 6. 备注

- 项目资源中出现了 `storePassword` 与私钥路径相关配置（`app/build.gradle` 中的签名信息），实际提交/迁移时建议转移到 CI/环境变量或本地安全配置中管理，避免敏感信息明文化。  
- `app` 模块未直接放置业务代码，代码主要落在 `:module/*` 与 `:a`、`:`key 模块中，便于能力拆分与复用。  

后续若需要，我可以再按“运行-部署-问题清单”方向补充：
1. 各模块职责的更细颗粒说明文档（按 UI/网络/数据/服务分层）  
2. 运行/发布手册（常用 `gradlew` 命令 + 常见失败排查）  
3. 依赖版本清单（可转 CSV 或 BOM 对齐建议）
