<script>
  import {onMount} from "svelte";
  import Button from "./lib/Dropdown.svelte";
  import ResetPageButton from "./lib/ResetPageButton.svelte";
  import LeaderboardDropdown from "./lib/LeaderboardDropdown.svelte";
  import Dropdown from "./lib/Dropdown.svelte";
  import RegionDropdown from "./lib/RegionDropdown.svelte";


  const url = "http://localhost:7070/";
  let players = [];
  let leaderboardId = 1;
  let numberOfPlayers;
  let playersPerFetch = 50;
  let searchingNameState = false;
  let rankFormValue = "";
  let nameFormValue = "";
  let loading = false;
  let scrollTop = 0;

  let lowestIndex;
  let highestIndex;

  $: {
    if (players.length !== 0) {
      lowestIndex = players[0].rank - 1;
    }
  }

  $: {
    if (players.length !== 0) {
      highestIndex = players[players.length-1].rank - 1;
    }
  }


  // Leaderboard dropdown
  let showLBDropdown = false;
  let leaderboardItems = [];
  let currentLeaderboard = 1;

  // Region dropdown
  let showRegionDropdown = false;

  // HTML elements
  let table;

  let scrollProcessing = false;

  onMount(async ()=> {

    // Tasks when component gets re-rendered
    table = document.getElementById("leaderboard-body"); //re-bind table... bind:this={table} binding gets lost
    await resetPage();
    numberOfPlayers = getNumberOfPlayers();
    leaderboardItems = await getLeaderboardAmount();
    handleLeaderboardChange(leaderboardItems[0]);
  });

  async function handleScroll(){
    scrollTop = table.scrollTop;

    if (scrollProcessing) {
      return;
    }
    scrollProcessing = true;
    try {
      if (Math.abs(table.scrollHeight - table.clientHeight - table.scrollTop) < 500) {
        await loadPlayers(highestIndex + 1, highestIndex + playersPerFetch);
      }

      if (table.scrollTop < 500) {
        await loadPlayers(lowestIndex - playersPerFetch, lowestIndex - 1);
        //scrollToRow(playersPerFetch);
      }


    } finally {
      scrollProcessing = false;
    }
  }

  async function loadPlayers(start, stop) {

    //avoid negative value
    if (start < 0) {
      start = 0;
    }
    //avoid overflow (higher index then database has)
    if (stop > numberOfPlayers) {
      stop = numberOfPlayers;
    }


    let direction = "down";
    if (players.length !== 0 && start < lowestIndex) {
      direction = "up";
    }

    await getPlayers(start, stop, direction);

    loading = false;
  }

  async function getPlayers(min, max, direction) {
    const res = await fetch(`${url}players?leaderboardId=${currentLeaderboard}&start=${min}&stop=${max}`);
    const data = await res.json();

    if (direction === "up") {
      players = data.concat(players);
      scrollToRow(playersPerFetch+8);
    } else if (direction === "down") {
      players = players.concat(data);
    }
  }

  async function getLeaderboardAmount() {
    let items = [];
    const res = await fetch(`${url}leaderboards`);
    const data = await res.json();
    for(let i in data) {
      items[i] = "Leaderboard " + data[i];
    }
    return items;
  }

  function clearTable(){
    players = [];
  }

  function scrollToRow(rowNumber) {
    const row = table.childNodes[rowNumber]
    if (row) {
      row.scrollIntoView({block: 'start', inline: "nearest"});
    }
  }

  async function getNumberOfPlayers() {
    const res = await fetch(`${url}size?leaderboardId=${currentLeaderboard}`);
    return parseInt(await res.json());
  }

  async function getPlayerSearch(name) {
    const response = await fetch(`${url}findPlayer?leaderboardId=${currentLeaderboard}&name=${name}`);
    players = await response.json();
    loading = false; //TODO evt gør til exception
  }

  async function handleRankSubmit(e) {
    e.preventDefault();

    loading = true;

    let input = parseInt(rankFormValue);

    //Reset rankValue when name searching
    nameFormValue = "";

    if (isNaN(input) || input<1) {
      await resetPage();
      return;
    }

    numberOfPlayers = await getNumberOfPlayers();
    clearTable();

    //handles end of table
    if (input > numberOfPlayers) {
      alert("Input higher then number of players: " + numberOfPlayers);
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
  }
  function handleRankClear(e) {
    e.preventDefault();
    resetPage();
    handleRankSubmit(e);
  }

  async function handleNameSubmit(e) {
    e.preventDefault();

    let input = nameFormValue;

    if (input.length < 3 && !(input === "")) {
      alert("too few characters, use 3 or more");
      return;
    }

    loading = true;

    //Reset rankValue when name searching
    rankFormValue = "";

    if (input === "") {
      scrollProcessing = false;
      await resetPage();
      return;
    }
    scrollProcessing = true;
    await getPlayerSearch(input);
  }

  function handleNameClear(e) {
    e.preventDefault();
    resetPage();
    handleNameSubmit(e);
  }
  async function resetPage() {
    scrollProcessing = false;
    scrollTop = 0;
    rankFormValue = "";
    nameFormValue = "";
    clearTable();
    loading = true;
    await loadPlayers(0, 49);

  }

  function getLeaderboardIndex(leaderboardId) {
    for(let i = 0; i < leaderboardItems.length; i++){
      if(leaderboardId === leaderboardItems[i].split(" ")[1]){
        return i;
      }
    }
  }


  const handleLeaderboardChange = (item) => {
    showLBDropdown = false;
    let splitted = item.split(" ");
    if(currentLeaderboard === splitted[1]) {
      return;
    }


    currentLeaderboard = splitted[1];
    resetPage();
  }


</script>

<LeaderboardDropdown bind:currentLeaderboard={currentLeaderboard} on:callResetPage={resetPage}
                     menuOpen={showLBDropdown}
                     showOpen={leaderboardItems[getLeaderboardIndex(currentLeaderboard)]}
                     showClosed={leaderboardItems[getLeaderboardIndex(currentLeaderboard)]}
                     leaderboardItems={leaderboardItems}
/>

<div id="leaderboard-container">

  <!--ResetPage button-->
  <ResetPageButton on:click={resetPage} scrollTop={scrollTop}/>


  <!--HEADER-->
  <nav id="leaderboard-header">
    <div class="header-elm">
      <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3.5" stroke-linecap="round" stroke-linejoin="bevel"><circle cx="11" cy="11" r="8"></circle><line x1="21" y1="21" x2="16.65" y2="16.65"></line></svg>
      <form id="form-rank" on:submit={handleRankSubmit}>
        <label for="rank"></label>
        <input bind:value={rankFormValue} placeholder="RANK" id="rank" name="rank">
        {#if rankFormValue !== ""}
          <button type="button" on:click={handleRankClear}>
            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 20 20" fill="none" stroke="currentColor" stroke-width="3.5" stroke-linecap="round" stroke-linejoin="bevel"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>
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
      <div class="region">
        REGION
      </div>
    </div>
    <hr>
    <div class="header-elm">
      HIGHSCORE
    </div>
  </nav>
  <!--TABLE-->
  {#if loading}
    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 200 200"><path fill="#232334" stroke="%235989B1" stroke-width="15" transform-origin="center" d="m148 84.7 13.8-8-10-17.3-13.8 8a50 50 0 0 0-27.4-15.9v-16h-20v16A50 50 0 0 0 63 67.4l-13.8-8-10 17.3 13.8 8a50 50 0 0 0 0 31.7l-13.8 8 10 17.3 13.8-8a50 50 0 0 0 27.5 15.9v16h20v-16a50 50 0 0 0 27.4-15.9l13.8 8 10-17.3-13.8-8a50 50 0 0 0 0-31.7Zm-47.5 50.8a35 35 0 1 1 0-70 35 35 0 0 1 0 70Z"><animateTransform type="rotate" attributeName="transform" calcMode="spline" dur="2" values="0;120" keyTimes="0;1" keySplines="0 0 1 1" repeatCount="indefinite"></animateTransform></path></svg>
  {:else}
    <div id="leaderboard-body" bind:this={table} on:scroll={handleScroll}>
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
  {/if}
</div>



<style lang="scss">


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
          input, .region{
            font-size: 1rem;
            font-weight: 900;
            color: $white;
            height: 2rem;
            width: 4rem;
            border: none;
            outline: none ! important;
            padding: 5px;
            background: $indigo-dark;
            transition: ease all 300ms;
            border-radius: 0.25rem;
          }
          input:hover{
            cursor: pointer;
            background-color: #2f2f40;
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
