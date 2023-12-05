const url = "http://localhost:7070/";
let players = [];
let numberOfPlayers;
let lowestIndex;
let highestIndex;
let playersPerFetch = 50;
const rankForm = document.getElementById("rankForm");
const nameForm = document.getElementById("nameForm");
let leaderboardId = 1;
let name;
let table = document.getElementById("leaderboard");
let isEventProcessing = false;

nameForm.addEventListener("submit",  handleSubmitName);
rankForm.addEventListener("submit",  handleSubmitRank);

function scrollToRow(rowNumber) {
    let row = table.rows[rowNumber];
    if (row) {
        row.scrollIntoView({ block: 'start', inline: "nearest"});
    }
}

document.getElementById('btn-switch').addEventListener('click', function() {
    let svg = document.querySelector('svg');
    svg.classList.toggle('rotate180');
});

/*
function handleSubmit(e){
    e.preventDefault();

    let input = parseInt(document.getElementById("name").value);
    if (isNaN(input)) {
        return;
    }
    if (input < 1) {
        alert("LeaderboardID below 1 does not exist");
        return;
    }
    isEventProcessing = true;

    leaderboardId = input;
    tableClear();
    tableInsert(0, playersPerFetch - 1)
    isEventProcessing = false;
}
*/

async function handleSubmitName(e) {
    e.preventDefault();

    name = document.getElementById("name").value;
    if (name === "" || name === undefined) {
        tableClear();
        initialize();
    } else {
        tableClear();
        await searchTableInsert();
    }
    console.log(name);
}

async function handleSubmitRank(e) {
    e.preventDefault();
    let input = parseInt(document.getElementById("rank").value);
    if (isNaN(input)) {
        return;
    }

    await getNumberOfPlayers();
    if (input > numberOfPlayers) {
        alert(`Search Failed: Lowest Rank: ${numberOfPlayers}`);
        return;
    }
    if (input < 1) {
        alert("Rank below 1 does not exist");
        return;
    }
    tableClear();
    await tableInsert(input - 1 - playersPerFetch, input + playersPerFetch - 1);
    if (input > playersPerFetch) {
        scrollToRow(playersPerFetch-1);
    } else {
        scrollToRow(input-2);
    }

}

async function getNumberOfPlayers() {
    const response = await fetch(`${url}size?leaderboardId=${leaderboardId}`);
    numberOfPlayers = parseInt(await response.json());
}

async function getPlayers(min, max) {
    players = [];
    //localhost:8080/players?min=15&max=5
    const response = await fetch(`${url}players?leaderboardId=${leaderboardId}&start=${min}&stop=${max}`);
    players = await response.json();
}

async function getPlayerSearch() {
    players = [];
    const response = await fetch(`${url}findPlayer?leaderboardId=${leaderboardId}&name=${name}`);
    players = await response.json();
    console.log(players);
}

function tableClear(){
    players = [];
    let tableHeader = table.rows[0].innerHTML;
    table.innerHTML = "";
    table.insertRow(0).innerHTML = tableHeader;
    lowestIndex = undefined;
    highestIndex = undefined;
}

//inserts data from API fetch into table rows
async function tableInsert(startRange, endRange) {
    players = [];
    if (startRange < 0) {
        startRange = 0;
        if (endRange < 0) {
            return;
        }
    }
    //checks if first call
    if (lowestIndex === undefined) {
        lowestIndex = startRange;
    }
    if (highestIndex === undefined) {
        highestIndex = endRange+1;
    }


    await getPlayers(startRange, endRange);


    if (players.length === 0) {
        return;
    }
    let counter = startRange;
    let rows = table.rows.length
    if (startRange < lowestIndex) {
        rows = 1;
    }
    for (let i = 0; i < players.length; i++) {
        counter++;
        table.insertRow(i+rows).innerHTML = '<tr><td>' + (counter).toLocaleString() + '</td><td>' + players[i].name + '</td><td>' + players[i].score.toLocaleString() + '</td></tr>';
    }
    console.log(`Inserted ${startRange+1} to ${counter}`);
    if (startRange < lowestIndex) {
        lowestIndex = startRange;
    }
    if (endRange > highestIndex) {
        highestIndex = counter;
    }
}

async function searchTableInsert() {
    await getPlayerSearch();
    for (let i = 0; i < players.length; i++) {
        table.insertRow(i+1).innerHTML = '<tr><td>' + players[i].rank + '</td><td>' + players[i].name + '</td><td>' + players[i].score.toLocaleString() + '</td></tr>';
    }
}

function drawTable(rank, name, score) {
    for (let i = 0; i < players.length; i++) {
        counter++;
        table.insertRow(i+rows).innerHTML = '<tr><td>' + (counter).toLocaleString() + '</td><td>' + players[i].name + '</td><td>' + players[i].score.toLocaleString() + '</td></tr>';
    }
}

//lowestIndex = 299; highestIndex = 299+usersPerFetch;
//Initial call, when site is loaded
function initialize() {
    tableInsert(0, playersPerFetch - 1).then(r => "init failed");
}

initialize();

const div = document.querySelector("#leaderboard-container");

div.addEventListener("scroll", async () => {
    if (isEventProcessing) {
        return; // If the event is already being processed, exit early
    }

    isEventProcessing = true;

    try {
        if (Math.abs(div.scrollHeight - div.clientHeight - div.scrollTop) < 1) {
            await tableInsert(highestIndex, highestIndex + playersPerFetch - 1);
        }
        if (div.scrollTop < 1 && lowestIndex > playersPerFetch / 2) {
            await tableInsert(lowestIndex - playersPerFetch, lowestIndex - 1);
            scrollToRow(playersPerFetch);
        }
    } finally {
        isEventProcessing = false;
    }
}, {
    passive: true
});