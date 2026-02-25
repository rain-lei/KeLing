/**
 * 课灵几何图形结尾动效
 * 供 index.html 与 architecture-and-logo.html 共用
 * 若 canvas 位于 .slide 内，则仅在 slide 为 active 时播放
 */
(function () {
  function initLogoAnimation(canvasSelector) {
    var canvas = document.querySelector(canvasSelector || "#keling-logo-canvas");
    if (!canvas) return;
    var ctx = canvas.getContext("2d");
    var w = canvas.width;
    var h = canvas.height;
    var cx = w / 2;
    var cy = h / 2;
    var colors = ["#00D4FF", "#BF00FF", "#FF0080", "#00FF88", "#FFD700"];
    var points = [];
    var cell = 5;
    var shapeSize = 6;

    function sampleText() {
      var off = document.createElement("canvas");
      off.width = 800;
      off.height = 280;
      var octx = off.getContext("2d");
      octx.fillStyle = "#fff";
      octx.font = "bold 180px \"Microsoft YaHei\", \"PingFang SC\", sans-serif";
      octx.textAlign = "center";
      octx.textBaseline = "middle";
      octx.fillText("课灵", off.width / 2, off.height / 2);
      var imgd = octx.getImageData(0, 0, off.width, off.height);
      var d = imgd.data;
      var idx = 0;
      for (var y = 0; y < off.height; y += cell) {
        for (var x = 0; x < off.width; x += cell) {
          var i = (y * off.width + x) * 4;
          if (d[i + 3] > 80) {
            var tx = (x / off.width) * w * 0.85 + w * 0.075;
            var ty = (y / off.height) * h * 0.85 + h * 0.075;
            points.push({
              tx: tx, ty: ty,
              color: idx % colors.length,
              shape: idx % 3,
              delay: Math.random() * 1.2,
              duration: 0.4 + Math.random() * 0.3
            });
            idx++;
          }
        }
      }
    }
    sampleText();

    var useFallback = false;
    if (points.length === 0) {
      useFallback = true;
      var cols = 24;
      var rows = 12;
      var padX = w * 0.15;
      var padY = h * 0.2;
      var cellW = (w - padX * 2) / cols;
      var cellH = (h - padY * 2) / rows;
      for (var row = 0; row < rows; row++) {
        for (var col = 0; col < cols; col++) {
          var inLeft = (col < cols * 0.45 && (row < 2 || row > rows - 3 || col < 3 || col > cols * 0.4 - 2 || (row > 3 && row < 8 && col > 8)));
          var inRight = (col >= cols * 0.55 && (row < 2 || row > rows - 3 || col < cols * 0.55 + 2 || col > cols - 3 || (row > 4 && row < 9)));
          if (inLeft || inRight) {
            var tx = padX + (col + 0.5) * cellW;
            var ty = padY + (row + 0.5) * cellH;
            points.push({
              tx: tx, ty: ty,
              color: (row + col) % colors.length,
              shape: (row + col) % 3,
              delay: Math.random() * 1.2,
              duration: 0.4 + Math.random() * 0.3
            });
          }
        }
      }
      if (points.length === 0) {
        for (var i = 0; i < 120; i++) {
          points.push({
            tx: w * 0.2 + (i % 12) * (w * 0.6 / 12) + (w * 0.6 / 24),
            ty: h * 0.3 + Math.floor(i / 12) * (h * 0.5 / 10) + (h * 0.05),
            color: i % colors.length,
            shape: i % 3,
            delay: Math.random() * 1.2,
            duration: 0.4 + Math.random() * 0.3
          });
        }
      }
    }

    var startTime = null;
    var slideEl = canvas.closest(".slide");
    var waitForSlide = !!slideEl;

    function easeOut(t) {
      return 1 - (1 - t) * (1 - t);
    }

    function drawShape(ctx, x, y, shape, size, color, glow) {
      ctx.save();
      ctx.fillStyle = color;
      ctx.strokeStyle = color;
      ctx.lineWidth = 1.5;
      ctx.shadowColor = color;
      ctx.shadowBlur = glow || 8;
      var s = size;
      if (shape === 0) {
        ctx.beginPath();
        ctx.arc(x, y, s, 0, Math.PI * 2);
        ctx.fill();
        ctx.stroke();
      } else if (shape === 1) {
        ctx.fillRect(x - s, y - s, s * 2, s * 2);
        ctx.strokeRect(x - s, y - s, s * 2, s * 2);
      } else {
        ctx.beginPath();
        ctx.moveTo(x, y - s);
        ctx.lineTo(x + s, y + s);
        ctx.lineTo(x - s, y + s);
        ctx.closePath();
        ctx.fill();
        ctx.stroke();
      }
      ctx.restore();
    }

    function frame(now) {
      var logoActive = !waitForSlide || (slideEl && slideEl.classList.contains("active"));
      if (logoActive && startTime === null) startTime = now;
      if (!logoActive) startTime = null;
      var t = startTime ? (now - startTime) / 1000 : 0;

      ctx.clearRect(0, 0, w, h);
      if (useFallback) {
        ctx.save();
        ctx.font = "bold 140px \"Microsoft YaHei\", \"PingFang SC\", sans-serif";
        ctx.textAlign = "center";
        ctx.textBaseline = "middle";
        var g = ctx.createLinearGradient(0, 0, w, 0);
        g.addColorStop(0, "#00D4FF");
        g.addColorStop(0.5, "#BF00FF");
        g.addColorStop(1, "#00FF88");
        ctx.fillStyle = g;
        ctx.globalAlpha = 0.25;
        ctx.shadowColor = "#00D4FF";
        ctx.shadowBlur = 30;
        ctx.fillText("课灵", cx, cy);
        ctx.restore();
      }
      var allDone = true;
      for (var k = 0; k < points.length; k++) {
        var pt = points[k];
        var lt = (t - pt.delay) / pt.duration;
        if (lt < 1) { allDone = false; break; }
      }
      var pulse = 0.7 + 0.3 * Math.sin(t * 1.8);
      for (var i = 0; i < points.length; i++) {
        var p = points[i];
        var localT = Math.max(0, (t - p.delay) / p.duration);
        var eased = easeOut(Math.min(1, localT));
        var x = cx + (p.tx - cx) * eased;
        var y = cy + (p.ty - cy) * eased;
        var scale = eased * (allDone ? pulse : 1);
        var size = shapeSize * scale;
        var color = colors[p.color];
        drawShape(ctx, x, y, p.shape, size, color, 4 + pulse * 6);
      }
      requestAnimationFrame(frame);
    }
    requestAnimationFrame(frame);
  }

  if (document.readyState === "loading") {
    document.addEventListener("DOMContentLoaded", function () {
      initLogoAnimation("#keling-logo-canvas");
    });
  } else {
    initLogoAnimation("#keling-logo-canvas");
  }
})();
