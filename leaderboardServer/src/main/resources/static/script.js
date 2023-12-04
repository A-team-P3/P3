const url = "http://localhost:7070/";
let players = [];
let numberOfPlayers;
let lowestIndex;
let highestIndex;
let playersPerFetch = 50;
const form = document.getElementById("frm");
const form2 = document.getElementById("frm2");
let leaderboardId = 1;
let table = document.getElementById("leaderboard");
let isEventProcessing = false;

form2.addEventListener("submit",  handleSubmit);
form.addEventListener("submit",  handleSubmitRank);

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


function handleSubmit(e){
    e.preventDefault();

    let input = parseInt(document.getElementById("leaderboardId").value);
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

async function handleSubmitRank(e) {
    e.preventDefault();
    let input = parseInt(document.getElementById("rank").value);
    if (isNaN(input)) {
        return;
    }

    await getNumberOfPlayers()
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
    const response2 = await fetch(`${url}size?leaderboardId=${leaderboardId}`);
    numberOfPlayers = parseInt(await response2.json());
}

async function getPlayers(min, max) {
    players = [];
    //localhost:8080/players?min=15&max=5
    const response = await fetch(`${url}players?leaderboardId=${leaderboardId}&start=${min}&stop=${max}`);
    const data = await response.json();

    for(const i in data) {
        players.push(data[i]);
    }

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
    //get number of users, should be improved so its called less
    await getNumberOfPlayers()

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
        table.insertRow(i+rows).innerHTML = '<tr><td>' + (counter).toLocaleString() + '</td><td>' + players[i].element + '</td><td>' + players[i].score.toLocaleString() + '</td></tr>';
    }
    console.log(`Inserted ${startRange+1} to ${counter}`);
    if (startRange < lowestIndex) {
        lowestIndex = startRange;
    }
    if (endRange > highestIndex) {
        highestIndex = counter;
    }


}

//lowestIndex = 299; highestIndex = 299+usersPerFetch;
tableInsert(0, playersPerFetch - 1);

const div = document.querySelector("#leaderboard-container")

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