<script>
  import {onMount} from "svelte";

  const url = "http://localhost:7070/";
  let players = [];
  let leaderboardId = 1;
  let numberOfPlayers;
  let lowestIndex = 0;
  let highestIndex = 0;
  let playersPerFetch = 50;
  let searchingNameState = false;

  // HTML elements
  let table;
  let rankForm;
  let nameForm;

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
        }


      } finally {
        scrollProcessing = false;
      }
    });


    async function loadPlayers(start, stop) {
      await getPlayers(start, stop)
      if (stop > highestIndex) {
        highestIndex = stop;
      }
    }

    async function getPlayers(min, max) {
      const res = await fetch(`${url}players?leaderboardId=${leaderboardId}&start=${min}&stop=${max}`);
      const data = await res.json();

      players = players.concat(data);
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
      const res = await fetch(`${url}size?leaderboardId=${leaderboardId}`);
      return parseInt(await res.json());
    }

    async function getPlayerSearch(name) {
      const response = await fetch(`${url}findPlayer?leaderboardId=${leaderboardId}&name=${name}`);
      players = await response.json();
    }

    rankForm.addEventListener("submit", async (e) => {
      e.preventDefault();
      let input = parseInt(rankForm.elements.rank.value);
      if (isNaN(input)) {
        return;
      }
      clearTable();
      await loadPlayers(input-1, input+playersPerFetch);


      console.log(input);
    })

    nameForm.addEventListener("submit", async (e) =>{
      e.preventDefault();
      let input = nameForm.elements.name.value;
      if (input === "") {
        scrollProcessing = false;
        clearTable();
        await loadPlayers(0, 49);
        return;
      }
      console.log("HALLO");
      scrollProcessing = true;
      await getPlayerSearch(input);

    });

    $: searchingNameState = nameForm.elements.name.value === "";
  });
</script>


<div id="leaderboard-container">
  <!--HEADER-->
  <nav id="leaderboard-header">
    <div class="header-elm">
      <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3.5" stroke-linecap="round" stroke-linejoin="bevel"><circle cx="11" cy="11" r="8"></circle><line x1="21" y1="21" x2="16.65" y2="16.65"></line></svg>
      <form id="form-rank" bind:this={rankForm}>
        <label for="rank"></label>
        <input placeholder="RANK" value="" id="rank" name="rank">
      </form>
      <button></button>
    </div>
    <hr>
    <div class="header-elm">
      <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3.5" stroke-linecap="round" stroke-linejoin="bevel"><circle cx="11" cy="11" r="8"></circle><line x1="21" y1="21" x2="16.65" y2="16.65"></line></svg>
      <form id="form-name" bind:this={nameForm}>
        <label for="name"></label>
        <input placeholder="NAME" value="" id="name" name="name">
      </form>
      <button></button>
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
          :hover{
            cursor: pointer;
            background-color: $indigo-dark;
            color: $primary-hover;
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
