/**
 * 课灵展示页 - 数据驱动 slide 生成
 */
(function () {
  var SLIDES_DATA = [
    {
      title: "课灵 · 让学习变成一场有趣的冒险",
      phonePlaceholder: "Logo · 课灵",
      pageHint: "← → 切换 · <span class=\"shortcut\">End 跳结尾</span>",
      bubbleLeft: { title: "产品定位", content: "专为大学生设计的游戏化学习助手，结合 AI 与个性化推荐，让学习更高效、更有趣。" },
      bubbleRight: { title: "核心价值", content: "将学习任务转化为关卡与成就，用科幻霓虹 UI 打造沉浸式体验。" }
    },
    {
      title: "首页 · 任务与进度",
      phonePlaceholder: "首页",
      pageHint: "1 / 13",
      bubbleLeft: { title: "今日任务", content: "一目了然的待办与进度，游戏化呈现每日目标与完成度。" },
      bubbleRight: { title: "快捷入口", content: "课程、成就、AI 助手等模块快速触达，保持界面简洁。" }
    },
    {
      title: "课程 · 课表与教材",
      phonePlaceholder: "课程",
      pageHint: "2 / 13",
      bubbleLeft: { title: "教务同步", content: "与教务系统对接，智能课表生成与课程进度追踪。" },
      bubbleRight: { title: "AI 创新", content: "教材数字化（OCR）、知识点关联与智能提醒，提升学习效率。" }
    },
    {
      title: "任务 · 三维任务地图",
      phonePlaceholder: "任务地图",
      pageHint: "3 / 13",
      bubbleLeft: { title: "游戏化任务", content: "任务转化为关卡，动态难度与组队协作，进度实时同步。" },
      bubbleRight: { title: "激励体系", content: "经验值、徽章与排行榜，让完成任务像闯关一样有成就感。" }
    },
    {
      title: "成就 · 徽章与排行",
      phonePlaceholder: "成就",
      pageHint: "4 / 13",
      bubbleLeft: { title: "成就徽章", content: "丰富成就体系，持续激励学习与探索。" },
      bubbleRight: { title: "排行榜", content: "与好友或全校对比，激发良性竞争。" }
    },
    {
      title: "学情诊断 · 薄弱点分析",
      phonePlaceholder: "学情",
      pageHint: "5 / 13",
      bubbleLeft: { title: "精准诊断", content: "基于学习数据定位知识薄弱点，给出改进建议。" },
      bubbleRight: { title: "可视化", content: "图表与报告清晰展示，便于制定学习计划。" }
    },
    {
      title: "知识图谱 · 知识点关联",
      phonePlaceholder: "知识图谱",
      pageHint: "6 / 13",
      bubbleLeft: { title: "关联展示", content: "知识点以图谱形式呈现，理解前置与后续关系。" },
      bubbleRight: { title: "AI 驱动", content: "智能构建与更新图谱，辅助复习与拓展。" }
    },
    {
      title: "AI 助手 · 多模态交互",
      phonePlaceholder: "AI 助手",
      pageHint: "7 / 13",
      bubbleLeft: { title: "语音 / 文字 / 手势", content: "多种方式提问，实时解答学习问题。" },
      bubbleRight: { title: "智能推荐", content: "结合学情与图谱，推荐练习与资源。" }
    },
    {
      title: "个人 · 数据看板",
      phonePlaceholder: "个人",
      pageHint: "8 / 13",
      bubbleLeft: { title: "数据汇总", content: "学习时长、完成任务数、成就与等级一目了然。" },
      bubbleRight: { title: "设置与无障碍", content: "字体、对比度、TTS、手势等个性化与无障碍选项。" }
    },
    {
      title: "无障碍 · 包容性设计",
      phonePlaceholder: "无障碍",
      pageHint: "9 / 13",
      bubbleLeft: { title: "视觉 / 听觉 / 运动", content: "字体调节、高对比度、TTS 朗读、简化手势等。" },
      bubbleRight: { title: "科幻 UI", content: "霓虹风格在保证可读性与对比度前提下，兼顾美观。" }
    },
    {
      title: "技术栈 · Kotlin & Compose",
      phonePlaceholder: "技术",
      pageHint: "11 / 13",
      bubbleLeft: { title: "前端", content: ["Kotlin · Jetpack Compose", "MVVM · Hilt · Navigation"] },
      bubbleRight: { title: "数据与组件", content: ["Room · Retrofit · DataStore", "Lottie · Vico · ML Kit · CameraX"] }
    }
  ];

  function renderBubbleContent(content) {
    if (Array.isArray(content)) {
      var html = "<ul>";
      for (var i = 0; i < content.length; i++) {
        html += "<li>" + escapeHtml(content[i]) + "</li>";
      }
      html += "</ul>";
      return html;
    }
    return "<p>" + escapeHtml(content) + "</p>";
  }

  function escapeHtml(s) {
    var div = document.createElement("div");
    div.textContent = s;
    return div.innerHTML;
  }

  function createSlide(data, index) {
    var active = index === 0 ? " active" : "";
    var slide = document.createElement("div");
    slide.className = "slide" + active;
    slide.setAttribute("data-index", String(index));
    slide.innerHTML =
      "<div class=\"slide-inner\">" +
        "<h2 class=\"slide-title\">" + escapeHtml(data.title) + "</h2>" +
        "<div class=\"slide-content-center\">" +
          "<div class=\"phone-wrap\">" +
            "<div class=\"phone-screen\">" +
              "<div class=\"phone-placeholder\">" + escapeHtml(data.phonePlaceholder) + "</div>" +
            "</div>" +
          "</div>" +
        "</div>" +
        "<p class=\"page-hint\">" + data.pageHint + "</p>" +
      "</div>" +
      "<div class=\"bubble bubble-left\">" +
        "<div class=\"bubble-title\">" + escapeHtml(data.bubbleLeft.title) + "</div>" +
        renderBubbleContent(data.bubbleLeft.content) +
      "</div>" +
      "<div class=\"bubble bubble-right\">" +
        "<div class=\"bubble-title\">" + escapeHtml(data.bubbleRight.title) + "</div>" +
        renderBubbleContent(data.bubbleRight.content) +
      "</div>";
    return slide;
  }

  function initSlides() {
    var showcase = document.querySelector(".showcase");
    var slideEnd = document.querySelector(".slide-end");
    if (!showcase || !slideEnd) return;
    for (var i = 0; i < SLIDES_DATA.length; i++) {
      var el = createSlide(SLIDES_DATA[i], i);
      showcase.insertBefore(el, slideEnd);
    }
  }

  if (document.readyState === "loading") {
    document.addEventListener("DOMContentLoaded", initSlides);
  } else {
    initSlides();
  }
})();
