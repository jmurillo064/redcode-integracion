import React, { useState } from 'react';

function FormularioUsuario() {
    const [usuario, setUsuario] = useState({
        nombre: '',
        email: '',
        edad: 0
    });

    const [tareas, setTareas] = useState([]);

    const actualizarUsuario = (campo, valor) => {
        setUsuario(prevUsuario => ({
            ...prevUsuario,
            [campo]: valor
        }));
    };

    const agregarTarea = (nuevaTarea) => {
        setTareas(prevTareas => [...prevTareas, nuevaTarea]);   
        setUsuario({ nombre: '', email: '', edad: 0 });
    };

    return (
        <div>
            <input
                value={usuario.nombre}
                onChange={(e) => actualizarUsuario('nombre', e.target.value)}
                placeholder="Nombre"
            />
            <input
                value={usuario.email}
                onChange={(e) => actualizarUsuario('email', e.target.value)}
                placeholder="Email"
            />
            <input
                type="number"
                value={usuario.edad}
                onChange={(e) => actualizarUsuario('edad', parseInt(e.target.value))}
                placeholder="Edad"
            />
            <button onClick={() => agregarTarea({ nombre: usuario.nombre, email: usuario.email, edad: usuario.edad })}>
                Agregar Usuario
            </button>
            <h1>
                {tareas.map((tarea, idx) => (
                    <div key={idx}>
                        <span>{tarea.nombre}</span> - <span>{tarea.email}</span> - <span>{tarea.edad}</span>
                    </div>
                ))}
            </h1>
        </div>
    );
}

export default FormularioUsuario;