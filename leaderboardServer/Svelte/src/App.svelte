<script>
  import {onMount} from "svelte";

  const url = "http://localhost:7070/";
  let players = [];
  let leaderboardId = 1;
  let numberOfPlayers;
  let lowestIndex;
  let highestIndex;
  let playersPerFetch = 50;
  let searchingNameState = false;
  let rankFormValue = "";
  let nameFormValue = "";

  // HTML elements
  let table;

  let scrollProcessing = false;

  onMount( async ()=> {

    // Startup tasks
    await loadPlayers(0, 49);
    numberOfPlayers = getNumberOfPlayers();

    table.addEventListener('scroll', async() => {
      if (scrollProcessing) {
        return;
      }
      scrollProcessing = true;
      try {
        if (Math.abs(table.scrollHeight - table.clientHeight - table.scrollTop) < 500) {
          await loadPlayers(highestIndex + 1, highestIndex + playersPerFetch);
          console.log("LI: " + lowestIndex + ", HI: " + highestIndex);
        }

        if (table.scrollTop < 500) {
          await loadPlayers(lowestIndex - playersPerFetch, lowestIndex - 1);
          //scrollToRow(playersPerFetch);
          console.log("LI: " + lowestIndex + ", HI: " + highestIndex);
        }



      } finally {
        scrollProcessing = false;
      }
    });

  });


  async function loadPlayers(start, stop) {

    //avoid negative value
    if (start < 0) {
      start = 0;
    }
    //avoid overflow (higher index then database has)
    if (stop > numberOfPlayers) {
      stop = numberOfPlayers;
    }

    if (lowestIndex === undefined) {
      lowestIndex = start;
    }
    if (highestIndex === undefined) {
      highestIndex = stop+1;
    }

    let direction = "down";
    if (start < lowestIndex) {
      direction = "up";
    }

    await getPlayers(start, stop, direction)
    if (stop > highestIndex) {
      highestIndex = stop;
    }
    if (start < lowestIndex) {
      lowestIndex = start;
    }
  }


  async function getPlayers(min, max, direction) {
    const res = await fetch(`${url}players?leaderboardId=${leaderboardId}&start=${min}&stop=${max}`);
    const data = await res.json();

    if (direction === "up") {
      players = data.concat(players);
      scrollToRow(playersPerFetch+8);
    } else if (direction === "down") {
      players = players.concat(data);
    }
  }

  function clearTable(){
    players = [];
    lowestIndex = undefined;
    highestIndex = undefined;
  }

  function scrollToRow(rowNumber) {
    const row = table.childNodes[rowNumber]
    if (row) {
      row.scrollIntoView({block: 'start', inline: "nearest"});
    }
  }

  async function getNumberOfPlayers() {
    const res = await fetch(`${url}size?leaderboardId=${leaderboardId}`);
    return parseInt(await res.json());
  }

  async function getPlayerSearch(name) {
    const response = await fetch(`${url}findPlayer?leaderboardId=${leaderboardId}&name=${name}`);
    players = await response.json();
  }

  async function handleRankSubmit(e) {
    e.preventDefault();
    let input = parseInt(rankFormValue);
    if (isNaN(input)) {
      clearTable();
      await loadPlayers(0, 49);
      return;
    }

    numberOfPlayers = await getNumberOfPlayers();
    clearTable();

    //handles end of table
    if (input > numberOfPlayers) {
      await loadPlayers(numberOfPlayers-playersPerFetch-1, numberOfPlayers-1);
      scrollToRow(players.length-1);
      return;
    }

    //start = input - playersPerFetch, so that scroll bar is in middle
    //lowestIndex = input - playersPerFetch;
    await loadPlayers(input - playersPerFetch, input + playersPerFetch - 1);
    if (input > playersPerFetch) {
      scrollToRow(playersPerFetch-1);
    } else {
      scrollToRow(input-1);
    }

    console.log(input);
  }
  function handleRankClear(e) {
    e.preventDefault();
    rankFormValue = "";
    handleRankSubmit(e);
  }

  async function handleNameSubmit(e) {
    e.preventDefault();
    let input = nameFormValue;

    if (input === "") {
      scrollProcessing = false;
      clearTable();
      await loadPlayers(0, 49);
      return;
    }
    scrollProcessing = true;
    await getPlayerSearch(input);
  }

  function handleNameClear(e) {
    e.preventDefault();
    nameFormValue = "";
    handleNameSubmit(e);
  }


</script>

<button id="resetPage">Take me to the top!</button>
<div id="leaderboard-container">

  <!--HEADER-->
  <nav id="leaderboard-header">

    <div class="header-elm">
      <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3.5" stroke-linecap="round" stroke-linejoin="bevel"><circle cx="11" cy="11" r="8"></circle><line x1="21" y1="21" x2="16.65" y2="16.65"></line></svg>
      <form id="form-rank" on:submit={handleRankSubmit}>
        <label for="rank"></label>
        <input bind:value={rankFormValue} placeholder="RANK" id="rank" name="rank">
        {#if rankFormValue !== ""}
          <button type="button" on:click={handleRankClear}>
            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3.5" stroke-linecap="round" stroke-linejoin="bevel"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>
          </button>
        {/if}
      </form>
    </div>
    <hr>
    <div class="header-elm">
      <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3.5" stroke-linecap="round" stroke-linejoin="bevel"><circle cx="11" cy="11" r="8"></circle><line x1="21" y1="21" x2="16.65" y2="16.65"></line></svg>
      <form id="form-name" on:submit={handleNameSubmit}>
        <label for="name"></label>
        <input bind:value={nameFormValue} placeholder="NAME" id="name" name="name">
        {#if nameFormValue !== ""}
          <button type="button" on:click={handleNameClear}>
            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 20 20" fill="none" stroke="currentColor" stroke-width="3.5" stroke-linecap="round" stroke-linejoin="bevel"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>
          </button>
        {/if}
      </form>

    </div>
    <hr>
    <div class="header-elm">
      REGION
    </div>
    <hr>
    <div class="header-elm">
      HIGHSCORE
    </div>
  </nav>
  <!--TABLE-->
  <div id="leaderboard-body" bind:this={table}>
    {#each players as player}
      <li>
        <div class="score-info">
          {player.rank}
        </div>
        <div class="score-info justify-flex-start">
          {player.name}  <div class="idTag">#{player.id}</div>
        </div>
        <div class="score-info">
          {player.region}
        </div>
        <div class="score-info">
          {player.score}
        </div>
      </li>
    {/each}
  </div>
</div>


<style lang="scss">
  #resetPage {
    display: flex;
    flex-direction: column;
    position: absolute;
    border: none;
    padding: 5px 10px;
    cursor: pointer;
    border-radius: 5px;
    font-size: 16px;
    background: none;
    width: 40px;
    bottom: 10px;
    right: 10px;
  }
  #leaderboard-container{
    display: flex;
    flex-direction: column;
    background-color: $indigo;
    border-radius: 0.5rem;
    padding: 1rem;
    height: 100%;
    width: 100%;
    #leaderboard-header{
      margin-bottom: 0.5rem;
      display: flex;
      flex-direction: row;
      justify-content: space-evenly;
      color: $white;
      background-color: $indigo-black;
      border-radius: 0.25rem;
      .header-elm{
        overflow-x: hidden;
        height: 3rem;
        text-align: center;
        font-weight: 900;
        width: 100%;
        display: flex;
        align-items: center;
        justify-content: center;
        form{
          transition: ease all 300ms;
          border-radius: 0.25rem;
          input{
            font-size: 1rem;
            font-weight: 900;
            color: $white;
            height: 2rem;
            width: 4rem;
            border: none;
            outline: none ! important;
            padding: 5px;
            background: transparent;
            transition: ease all 300ms;
            border-radius: 0.25rem;
          }
          input:hover{
            cursor: pointer;
            background-color: $indigo-dark;
            color: $primary-hover;
          }
          button{
            transition: ease all 300ms;
            background-color: #ff5d69;
            color: $white;
            border: none;
            padding: 0.25rem;
            cursor: pointer;
            border-radius: 0.25rem;
          }
          button:hover{
            cursor: pointer;
            scale: 1.15;
          }
        }
        #form-name input{
          width: 8rem;
        }
        svg{
          margin-right: 0.2rem;
        }
      }
    }
    #leaderboard-body{
      overflow-y: scroll;
      li{
        box-shadow: inset 0 0 5px black;
        padding: 1rem;
        border-radius: 0.25rem;
        margin: 0.5rem 0 0.5rem 0;
        background-color: $indigo-dark;
        display: flex;
        flex-direction: row;
        justify-content: space-evenly;
        color: $white;
        .score-info{
          color: inherit;
          background-color: inherit;
          overflow: hidden;
          width: 100%;
          display: flex;
          justify-content: center;
        }
        .idTag{
          text-align: center;
          font-size: 0.8rem;
          padding-left: 0.5rem;
          background-color: inherit;
          color: lightslategrey;
        }
      }
      :nth-child(odd){
        background-color: $indigo-ultradark;
        color: $primary;
      }

    }
  }
  .justify-flex-start{
    justify-content: flex-start !important;
  }
  hr{
    color: lightslategrey;
  }
 
</style>
