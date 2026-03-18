<div align="center">

# 🍏 Fridget Server
**AI 驱动的智能食谱与食材管理引擎**

[English](./README.md) | **简体中文**

[![Java](https://img.shields.io/badge/Java-11-ED8B00?logo=openjdk&logoColor=white)](#)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.7.11-6DB33F?logo=springboot&logoColor=white)](#)
[![Python](https://img.shields.io/badge/Python-3.x-3776AB?logo=python&logoColor=white)](#)
[![Flask](https://img.shields.io/badge/Flask-2.x-000000?logo=flask&logoColor=white)](#)
[![Qwen AI](https://img.shields.io/badge/AI-Qwen3-412991?logo=alibabacloud&logoColor=white)](#)
[![MongoDB](https://img.shields.io/badge/MongoDB-4.x-47A248?logo=mongodb&logoColor=white)](#)
[![Redis](https://img.shields.io/badge/Redis-6.x-DC382D?logo=redis&logoColor=white)](#)

*一款开箱即用、解决“今天吃什么”终极难题的商业级开源应用。*

<br/>

<!-- 💡 建议：您可以稍后使用 https://shots.so/ 将这张手机拍摄的照片套上一个漂亮的浏览器或手机外壳，视觉效果会立刻提升到商业级！ -->
<img width="800" alt="Fridget UI Preview" src="https://github.com/54853315/fridget-frontend/blob/main/frontend/static/images/dev-stage-preview-image.jpeg?raw=true" />

<br/>

👉 **[探索 Fridget 前端应用仓库](https://github.com/54853315/fridget-frontend)**

</div>

---

## 📖 简介

**Fridget** 是一个基于大语言模型构建的智能化食谱推荐引擎。它不仅能帮您解决“每天不知道吃什么”的烦恼，更是 **“零食物浪费 (Zero Food Waste)”** 生活方式的倡导者。

只需输入冰箱里的剩余食材，Fridget 的智能分析引擎就能瞬间为您生成图文并茂、步骤详尽的定制食谱。

> **致敬**: 本项目的核心业务灵感来源于 Sinae Hong 等团队在 YouTube 上的极客探索。我们在此基础上进行了本地化调整与 AI 引擎升级，打造出了这款成熟、稳定的开源应用。

## ✨ 核心特性

- ⚡️ **极速的本土化 AI 引擎**：已将底层大模型无缝迁移至 **Qwen3 (通义千问)**，专为亚太地区网络环境优化，提供毫秒级的内容生成与极高性价比的响应。
- 🎨 **多媒体沉浸式体验**：原生集成 PEXELS API，根据 AI 生成的菜品自动匹配高质量商业级配图，告别枯燥的纯文本食谱。
- 🧠 **智能缓存调度机制**：24 小时 AI 结果缓存策略（基于 Redis），在保障推荐多样性的同时，大幅削减 API 调用成本及系统延迟。
- 🧩 **高扩展性的混合微服务架构**：
  - **核心业务网关**：基于 Java Spring Boot 构建，提供健壮的鉴权、数据持久化 (MongoDB) 与高并发处理能力。
  - **AI 推理服务**：基于 Python Flask 打造，结合 `scikit-learn` 与 `spaCy` 进行 NLP 处理与向量匹配，解耦重度计算逻辑。
- ⚙️ **精细化用户偏好 (Cooking Preference)**：API 层全面支持深度定制饮食禁忌与口味偏好，实现千人千面的菜谱生成。

## 🏛️ 系统架构

极简而高效的数据流转设计，确保核心业务与 AI 算力完美解耦。

<div align="center">
  <img src="./System-Architecture-Diagram.png" alt="Fridget Architecture" width="800" />
</div>

---

## 🚀 服务启动指南

### 1. 启动 Redis
```bash
brew services start redis
```

### 2. 启动 MongoDB

```bash
brew tap mongodb/brew
brew services start mongodb-community
```

### 3. 启动 Flask 服务

(1) 创建并激活虚拟环境
```bash
cd FridgetServer/flask
python3 -m venv venv
source venv/bin/activate
```

(2) 安装必要的依赖
  
```bash
# 使用 Python 3
pip install flask requests spacy scikit-learn
python -m spacy download zh_core_web_md
# 如果使用 Python 3.11+，需要手动安装 spaCy 模型进行本地安装
# pip3 install zh_core_web_md-3.8.0-py3-none-any.whl
```

(3) 根据 `generate_recipes_flask.py` 中的注释设置 `ALI_API_KEY`和`PEXELS_API_KEY`的环境变量

```bash
export ALI_API_KEY=''
export PEXELS_API_KEY=''
```

**重要**: 请将 `ALI_API_KEY` 和 `PEXELS_API_KEY` 作为环境变量进行配置，以避免密钥泄露。

(4) 启动 Flask 服务

```bash
python -m flask --app generate_recipes_flask run --host=0.0.0.0 --port=5001 #--debug
```

### 4. 启动 Spring Boot 服务
```bash
cd FridgetServer/
./gradlew compileJava #--stacktrace
./gradlew build
java -jar build/libs/fridget-0.0.1-SNAPSHOT.jar
```

### 4.1 本地二次开发

```bash
cd FridgetServer/
./gradlew compileJava --stacktrace
./gradlew build --continuous
./gradlew bootRun
```