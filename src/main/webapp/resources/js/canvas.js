const canvas = document.getElementById('areaCanvas');
const ctx = canvas ? canvas.getContext('2d') : null;

const WIDTH = 500;
const HEIGHT = 500;
const CENTER_X = WIDTH / 2;
const CENTER_Y = HEIGHT / 2;

let currentR = 2;
let allResultsData = [];

function updateResultsFromHidden() {
    const hidden = document.querySelector('[id$="allResultsJson"]');
    if (!hidden) {
        console.warn('[SYNC] allResultsJson not found');
        allResultsData = [];
        return;
    }

    const raw = hidden.value ? hidden.value : '[]';

    try {
        const parsed = JSON.parse(raw);
        if (Array.isArray(parsed)) {
            allResultsData = parsed;
        } else {
            console.warn('[SYNC] allResultsJson parsed but not array');
            allResultsData = [];
        }
        console.log('[SYNC] Points loaded:', allResultsData.length);
    } catch (e) {
        console.error('[SYNC] JSON parse error:', e);
        console.error('[SYNC] Raw value:', raw);
        allResultsData = [];
    }
}

function getCurrentR() {
    const rInput = document.querySelector('[id$="rInput_input"]');
    if (rInput && rInput.value) {
        const r = parseFloat(rInput.value);
        if (!isNaN(r) && r >= 0.1 && r <= 3) {
            return r;
        }
    }

    const canvasR = document.querySelector('[id$="canvasR"]');
    if (canvasR && canvasR.value) {
        const r = parseFloat(canvasR.value);
        if (!isNaN(r)) {
            return r;
        }
    }

    return currentR;
}

function updateCurrentR() {
    currentR = getCurrentR();
}

function drawCanvas() {
    if (!ctx) return;

    const R = getCurrentR();
    currentR = R;

    const maxRadius = Math.min(WIDTH, HEIGHT) / 2 - 50;
    const scale = maxRadius / R;

    ctx.clearRect(0, 0, WIDTH, HEIGHT);

    ctx.fillStyle = '#f5f7fa';
    ctx.fillRect(0, 0, WIDTH, HEIGHT);

    ctx.fillStyle = 'rgba(100, 149, 237, 0.5)';
    ctx.strokeStyle = 'rgba(100, 149, 237, 0.8)';
    ctx.lineWidth = 2;

    // Треугольник
    ctx.beginPath();
    ctx.moveTo(CENTER_X, CENTER_Y);
    ctx.lineTo(CENTER_X + R * scale, CENTER_Y);
    ctx.lineTo(CENTER_X, CENTER_Y - R / 2 * scale);
    ctx.closePath();
    ctx.fill();
    ctx.stroke();

    // Сектор
    ctx.beginPath();
    ctx.arc(CENTER_X, CENTER_Y, R / 2 * scale, Math.PI / 2, Math.PI, false);
    ctx.lineTo(CENTER_X, CENTER_Y);
    ctx.closePath();
    ctx.fill();
    ctx.stroke();

    // Квадрат
    ctx.fillRect(CENTER_X, CENTER_Y, R * scale, R * scale);
    ctx.strokeRect(CENTER_X, CENTER_Y, R * scale, R * scale);

    drawAxes(R, scale);
    drawPoints();
}

function drawAxes(R, scale) {
    ctx.strokeStyle = '#333';
    ctx.lineWidth = 2;
    ctx.fillStyle = '#333';
    ctx.font = '14px Arial';

    // X-axis
    ctx.beginPath();
    ctx.moveTo(0, CENTER_Y);
    ctx.lineTo(WIDTH, CENTER_Y);
    ctx.stroke();

    ctx.beginPath();
    ctx.moveTo(WIDTH - 10, CENTER_Y - 5);
    ctx.lineTo(WIDTH, CENTER_Y);
    ctx.lineTo(WIDTH - 10, CENTER_Y + 5);
    ctx.stroke();
    ctx.fillText('X', WIDTH - 20, CENTER_Y - 10);

    // Y-axis
    ctx.beginPath();
    ctx.moveTo(CENTER_X, HEIGHT);
    ctx.lineTo(CENTER_X, 0);
    ctx.stroke();

    ctx.beginPath();
    ctx.moveTo(CENTER_X - 5, 10);
    ctx.lineTo(CENTER_X, 0);
    ctx.lineTo(CENTER_X + 5, 10);
    ctx.stroke();
    ctx.fillText('Y', CENTER_X + 10, 15);

    // Marks
    ctx.lineWidth = 1;
    ctx.textAlign = 'center';
    ctx.textBaseline = 'top';

    const xMarks = [-R, -R / 2, R / 2, R];
    const xLabels = ['-R', '-R/2', 'R/2', 'R'];
    xMarks.forEach((val, idx) => {
        const x = CENTER_X + val * scale;
        ctx.beginPath();
        ctx.moveTo(x, CENTER_Y - 5);
        ctx.lineTo(x, CENTER_Y + 5);
        ctx.stroke();
        ctx.fillText(xLabels[idx], x, CENTER_Y + 10);
    });

    ctx.textAlign = 'right';
    ctx.textBaseline = 'middle';
    const yMarks = [R / 2, R, -R / 2, -R];
    const yLabels = ['R/2', 'R', '-R/2', '-R'];
    yMarks.forEach((val, idx) => {
        const y = CENTER_Y - val * scale;
        ctx.beginPath();
        ctx.moveTo(CENTER_X - 5, y);
        ctx.lineTo(CENTER_X + 5, y);
        ctx.stroke();
        ctx.fillText(yLabels[idx], CENTER_X - 10, y);
    });
}

function drawPoints() {
    if (!Array.isArray(allResultsData) || allResultsData.length === 0) {
        return;
    }

    updateCurrentR();
    if (isNaN(currentR) || currentR <= 0) {
        currentR = 2;
    }

    const maxRadius = Math.min(WIDTH, HEIGHT) / 2 - 50;
    const scale = maxRadius / currentR;

    allResultsData.forEach((result) => {
        const x = result.x;
        const y = result.y;
        const r = result.r;
        const hit = result.hit;

        if (isNaN(x) || isNaN(y) || isNaN(r)) return;

        const scaleFactor = r / currentR;
        const canvasX = CENTER_X + (x * scaleFactor) * scale;
        const canvasY = CENTER_Y - (y * scaleFactor) * scale;

        if (!isFinite(canvasX) || !isFinite(canvasY)) return;

        ctx.fillStyle = hit ? 'green' : 'red';
        ctx.strokeStyle = '#000';
        ctx.lineWidth = 2;

        ctx.beginPath();
        ctx.arc(canvasX, canvasY, 5, 0, 2 * Math.PI);
        ctx.fill();
        ctx.stroke();
    });
}

function redrawCanvas() {
    if (!ctx) return;
    updateResultsFromHidden();
    drawCanvas();
}

/* Можно оставить для совместимости, но теперь не обязательно */
function updateResultsData(newData) {
    console.log('[UPDATE] updateResultsData called; prefer hidden-field sync');
    try {
        if (typeof newData === 'string') {
            allResultsData = JSON.parse(newData);
        } else if (Array.isArray(newData)) {
            allResultsData = newData;
        }
    } catch (e) {
        console.error('[UPDATE] Error parsing:', e);
    }
    redrawCanvas();
}

if (canvas) {
    canvas.addEventListener('click', function(event) {
        updateCurrentR();

        if (currentR <= 0 || isNaN(currentR)) {
            alert('Пожалуйста, выберите корректное значение R перед кликом по области');
            return;
        }

        const rect = canvas.getBoundingClientRect();
        const clickX = event.clientX - rect.left;
        const clickY = event.clientY - rect.top;

        const maxRadius = Math.min(WIDTH, HEIGHT) / 2 - 50;
        const scale = maxRadius / currentR;

        const mathX = (clickX - CENTER_X) / scale;
        const mathY = (CENTER_Y - clickY) / scale;

        if (Math.abs(mathX) > 5 || mathY < -3 || mathY > 3) {
            alert('Точка вне допустимой области!\nX: [-5, 5], Y: [-3, 3]');
            return;
        }

        const canvasXInput = document.querySelector('[id$="canvasX"]');
        const canvasYInput = document.querySelector('[id$="canvasY"]');
        const canvasRInput = document.querySelector('[id$="canvasR"]');

        if (canvasXInput && canvasYInput && canvasRInput) {
            canvasXInput.value = mathX.toFixed(2).replace('.', ',');
            canvasYInput.value = mathY.toFixed(2).replace('.', ',');
            canvasRInput.value = currentR.toFixed(2).replace('.', ',');

            if (typeof checkPointFromCanvas === 'function') {
                checkPointFromCanvas();
            } else {
                console.error('checkPointFromCanvas function not found');
            }
        } else {
            console.error('Canvas form inputs not found');
        }
    });
}

if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', redrawCanvas);
} else {
    redrawCanvas();
}
