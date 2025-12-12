const canvas = document.getElementById('areaCanvas');
const ctx = canvas ? canvas.getContext('2d') : null;

const WIDTH = 500;
const HEIGHT = 500;
const CENTER_X = WIDTH / 2;
const CENTER_Y = HEIGHT / 2;

let currentR = 2;

function getCurrentR() {
    const rInput = document.querySelector('[id$="rInput_input"]');
    console.log('rInput element:', rInput);

    if (rInput && rInput.value) {
        const r = parseFloat(rInput.value);
        console.log('R from input:', r);
        if (!isNaN(r) && r >= 0.1 && r <= 3) {
            return r;
        }
    }

    const canvasR = document.querySelector('[id$="canvasR"]');
    console.log('canvasR element:', canvasR);

    if (canvasR && canvasR.value) {
        const r = parseFloat(canvasR.value);
        console.log('R from hidden field:', r);
        if (!isNaN(r)) {
            return r;
        }
    }

    console.log('Using default R:', currentR);
    return currentR;
}




function updateCurrentR() {
    const oldR = currentR;
    currentR = getCurrentR();
    console.log('updateCurrentR: old =', oldR, ', new =', currentR);
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

    ctx.beginPath();
    ctx.moveTo(CENTER_X, CENTER_Y);
    ctx.lineTo(CENTER_X + R * scale, CENTER_Y);
    ctx.lineTo(CENTER_X, CENTER_Y - R / 2 * scale);
    ctx.closePath();
    ctx.fill();
    ctx.stroke();

    ctx.beginPath();
    ctx.arc(
        CENTER_X,
        CENTER_Y,
        R / 2 * scale,
        Math.PI / 2,
        Math.PI,
        false
    );
    ctx.lineTo(CENTER_X, CENTER_Y);
    ctx.closePath();
    ctx.fill();
    ctx.stroke();


    ctx.fillRect(
        CENTER_X,
        CENTER_Y,
        R * scale,
        R * scale
    );
    ctx.strokeRect(
        CENTER_X,
        CENTER_Y,
        R * scale,
        R * scale
    );


    drawAxes(R, scale);

    drawPointsFromTable();
}

function drawAxes(R, scale) {
    ctx.strokeStyle = '#333';
    ctx.lineWidth = 2;
    ctx.fillStyle = '#333';
    ctx.font = '14px Arial';

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

    ctx.lineWidth = 1;
    ctx.textAlign = 'center';
    ctx.textBaseline = 'top';

    const xMarks = [-R, -R/2, R/2, R];
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
    const yMarks = [R/2, R, -R/2, -R];
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


function drawPointsFromTable() {
    console.log('drawPointsFromTable called');
    console.log('allResultsData:', allResultsData);
    console.log('allResultsData type:', typeof allResultsData);
    console.log('allResultsData is Array:', Array.isArray(allResultsData));

    if (typeof allResultsData === 'undefined') {
        console.log('allResultsData is undefined');
        return;
    }

    if (!Array.isArray(allResultsData)) {
        console.error('allResultsData is not an array!', allResultsData);
        return;
    }

    if (allResultsData.length === 0) {
        console.log('No results to draw (empty array)');
        return;
    }

    updateCurrentR();

    if (isNaN(currentR) || currentR <= 0) {
        console.warn('Invalid currentR:', currentR, '- using default value 2');
        currentR = 2;
    }

    console.log(`Drawing ${allResultsData.length} points with currentR=${currentR}`);

    const maxRadius = Math.min(WIDTH, HEIGHT) / 2 - 50;
    const scale = maxRadius / currentR;

    console.log('Drawing parameters:', {maxRadius, scale, currentR});

    allResultsData.forEach((result, index) => {
        console.log(`Drawing point ${index}:`, result);

        const x = result.x;
        const y = result.y;
        const r = result.r;
        const hit = result.hit;

        if (isNaN(x) || isNaN(y) || isNaN(r)) {
            console.warn(`Skipping point ${index} - invalid coordinates:`, {x, y, r});
            return;
        }

        const scaleFactor = r / currentR;
        const canvasX = CENTER_X + (x * scaleFactor) * scale;
        const canvasY = CENTER_Y - (y * scaleFactor) * scale;

        console.log(`Point ${index} calculated:`, {
            x, y, r, hit,
            scaleFactor,
            canvasX: canvasX.toFixed(2),
            canvasY: canvasY.toFixed(2)
        });

        if (!isFinite(canvasX) || !isFinite(canvasY)) {
            console.warn(`Skipping point ${index} - invalid canvas coordinates`);
            return;
        }

        ctx.fillStyle = hit ? 'green' : 'red';
        ctx.strokeStyle = '#000';
        ctx.lineWidth = 2;

        ctx.beginPath();
        ctx.arc(canvasX, canvasY, 5, 0, 2 * Math.PI);
        ctx.fill();
        ctx.stroke();
    });

    console.log('Finished drawing points');
}


function redrawCanvas() {
    if (ctx) {
        drawCanvas();
    }
}


function updateResultsData(newData) {
    console.log('updateResultsData called with:', newData, 'type:', typeof newData);

    try {
        if (typeof newData === 'string') {
            console.log('Parsing JSON string:', newData);
            allResultsData = JSON.parse(newData);
        } else if (Array.isArray(newData)) {
            allResultsData = newData;
        } else {
            console.error('Invalid data type:', typeof newData);
            return;
        }

        console.log('Updated results  ${allResultsData.length} points', allResultsData);
    } catch (e) {
        console.error('Error updating results ', e);
    }
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

        console.log('Click coordinates:', {clickX, clickY, mathX, mathY, R: currentR});

        if (Math.abs(mathX) > 5 || mathY < -3 || mathY > 3) {
            alert('Точка вне допустимой области!\nX: [-5, 5], Y: [-3, 3]');
            return;
        }

        const canvasXInput = document.querySelector('[id$="canvasX"]');
        const canvasYInput = document.querySelector('[id$="canvasY"]');
        const canvasRInput = document.querySelector('[id$="canvasR"]');

        if (canvasXInput && canvasYInput && canvasRInput) {
            canvasXInput.value = mathX.toFixed(2);
            canvasYInput.value = mathY.toFixed(2);
            canvasRInput.value = currentR.toFixed(2);

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
