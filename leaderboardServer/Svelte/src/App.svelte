<script>
  import {onMount} from "svelte";

  const url = "http://localhost:7070/";
  let players = [];
  let leaderboardId = 1;
  let numberOfPlayers;
  let lowestIndex;
  let highestIndex;
  let playersPerFetch = 50;

  // elements
  let table;

  onMount(()=>{

    const getData = async () => {
      const res = await fetch("http://localhost:7070/players?leaderboardId=1&start=0&stop=150");
      const data = await res.json();
      console.log(data[2])
      players = data;
      return res
    }

    getData();


    console.log(getData());
    //getData();
    //console.log(date);
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
    async function getPlayers(min, max) {
      let players = [];
      //localhost:8080/players?min=15&max=5
      const res = await fetch(`${url}players?leaderboardId=${leaderboardId}&start=${min}&stop=${max}`);
      players = await res.json();
      return players;
    }
    async function getPlayerSearch() {
      let players = [];
      const response = await fetch(`${url}findPlayer?leaderboardId=${leaderboardId}&name=${name}`);
      players = await response.json();
      console.log(players);
      return players;
    }

    numberOfPlayers = getNumberOfPlayers();

    setTimeout(()=>{
      console.log(numberOfPlayers);
      scrollToRow(5);
    }, 1000)

  })
</script>


<div id="leaderboard-container">
  <!--HEADER-->
  <nav id="leaderboard-header">
    <div class="header-elm flex-row-center">
      Rank
    </div>
    <div class="header-elm">
      Username
    </div>
    <div class="header-elm flex-row-center">
        Region
    </div>
    <div class="header-elm flex-row-center">
      Highscore
    </div>
  </nav>
  <!--TABLE-->
  <div id="leaderboard-body" bind:this={table}>
    {#each players as player}
      <li>
        <div class="score-info flex-row-center">
          {player.rank}
        </div>
        <div class="score-info">
          {player.name}  <div class="idTag">#{player.id}</div>
        </div>
        <div class="score-info flex-row-center">
          {player.region}
        </div>
        <div class="score-info flex-row-center">
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
      height: 25rem;
      border-radius: 0.25rem;
      .header-elm{
        overflow: hidden;
        width: 100%;
        display: flex;
        justify-content: flex-start;
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
          justify-content: flex-start;
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
  .flex-row-center{
    flex-direction: column;
  }
 
</style>
