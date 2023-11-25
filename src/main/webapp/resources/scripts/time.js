const time = document.querySelector("#time");

// Функция обновления времени
function updateTime() {
    let date = new Date();
    let separatedDate = date.toLocaleString('ru-RU', { timeZone: 'Europe/Moscow' }).split(",");
    time.textContent = separatedDate[0] + " " + separatedDate[1];
}

// Вызов функции обновления времени в начале
updateTime();

// Запуск интервала для периодического обновления времени каждые 13 секунд
setInterval(updateTime, 13000);