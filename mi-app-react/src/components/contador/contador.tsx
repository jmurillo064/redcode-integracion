import React, { useState } from 'react';

function Contador() {
  const [contador, setContador] = useState(0);
  const [nombre, setNombre] = useState('');

  return (
    <div>
      <p>Contador: {contador}</p>
      <button onClick={() => setContador(contador + 1)}>
        Incrementar
      </button>
      
      <input 
        value={nombre}
        onChange={(e) => setNombre(e.target.value)}
        placeholder="Tu nombre"
      />
      <p>Hola, {nombre}</p>
    </div>
  );
}

export default Contador;