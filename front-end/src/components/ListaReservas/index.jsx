import React from "react";
import styles from './listareservas.module.css'
import { Link } from "react-router-dom";

const ListaReservas = ({ reservas }) => {
    return(
        <div className={`${styles.minhasReservas}`}> 
          <h2>Minhas reservas</h2>
          {reservas.length === 0 ? (
            <div className={`${styles.semReservaHome}`}>
              <div className={`${styles.semReserva}`}>
                <p>Você ainda não fez uma reserva!</p> 
                <img className={`${styles.sadReserva}`} src="/sad.png" alt="Ícone" />
              </div>
              <div className={`${styles.retornoHome}`}>
                <Link to="/">Retornar à Página Inicial</Link>
                {/* Adicionar um link de retorno à página home */}
              </div>
            </div>
          ) : (
            /* Renderizar a lista de reservas */
            <ul className={`${styles.listaReservas}`}>
              {reservas.map((reservation) => (
                <li key={reservation.id} className={`${styles.lista}`}>
                  {reservation.product.nome} - {reservation.dataInicioReserva} - {reservation.dataFimReserva}
                </li>
              ))}
            </ul>
          )}
        </div>
    );
}

export default ListaReservas;