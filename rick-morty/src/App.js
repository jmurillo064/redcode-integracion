import imgRickMorty from './img/rick-morty.png';
import './App.css';
import { useState } from 'react';
import Character from './components/Character';

function App() {
  const [characters, setCharacters] = useState(null);
  const restApi = async () => {
    const api = await fetch('https://rickandmortyapi.com/api/character');
    const data = await api.json();
    setCharacters(data.results);
  }

  return (
    <div className="App">
      <header className="App-header">
        <h1 className='title'>Rick & Morty</h1>
        {
          characters ? (
            <Character characters={characters} setCharacters={setCharacters} />
          ) : (
            <>
              <img src={imgRickMorty} className="img-home" alt="Rick & Morty" />
              <button onClick={restApi} className='btn-search'>Buscar personajes</button>
            </>
          )
        }
      </header>
    </div>
  );
}

export default App;
