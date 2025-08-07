import './App.css';
import Saludo from './components/saludo/saludo.tsx';
import Usuario from './components/usuario/usuario.tsx';
import Contador from './components/contador/contador.tsx';
import FormularioUsuario from './components/formulario/FormularioUsuario.tsx';

function App() {
  const nombre = "JORGE MURILLO";

  return (
    <div>
      <h1 style={{color:'red', backgroundColor:'white'}}>Bienvenido a React</h1>
      <Saludo nombre={nombre} />
      <Usuario nombre="Ana" edad={18} esAdmin={true}  />
      <Contador />
      <FormularioUsuario />
    </div>
  );
}

export default App;
