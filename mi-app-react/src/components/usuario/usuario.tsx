import PropTypes from 'prop-types';

Usuario.propTypes = {
  nombre: PropTypes.string.isRequired,
  edad: PropTypes.number,
  esAdmin: PropTypes.bool
};

Usuario.defaultProps = {
  edad: 18,
  esAdmin: false
};

function Usuario({ nombre, edad, esAdmin }) {
  return (
    <div>
      <h2>{nombre}</h2>
      <p>Edad: {edad}</p>
      {esAdmin && <span>Administrador</span>}
    </div>
  );
}

export default Usuario;