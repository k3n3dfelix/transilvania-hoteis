import React, { useState, useEffect, useContext } from 'react';
import { useNavigate } from 'react-router-dom';
import { useParams } from "react-router-dom";
import axios from 'axios';
import jwtDecode from 'jwt-decode';
import { toast, ToastContainer } from 'react-toastify';
import { AuthContext } from '../../context/AuthContext';
import Calendar from '../DetailsProduct/Calendar';
import styles from './reserva.module.css'
import apiBaseUrl from '../../../api';


const ReservaComponent = () => {
  const navigate = useNavigate();
  const { id } = useParams();
  const { saveToken, isLoggedIn, logout, user } = useContext(AuthContext);
  const [firstName, setFirstname] = useState("");
  const [lastName, setLastname] = useState("");
  const [email, setEmail] = useState("");
  const [cidade, setCidade] = useState('');
  const [arrivalTime, setArrivalTime] = useState('');
  const [checkInDate, setCheckInDate] = useState(null);
  const [checkOutDate, setCheckOutDate] = useState(null);
  const [isSubmitting, setIsSubmitting] = useState(false);

  const [isLoading, setIsLoading] = useState(true);
  const [idProduto, setIdProduto] = useState(null);
  const [nomeProduto, setNomeProduto] = useState("");
  const [descricaoProduto, setDescricaoProduto] = useState("");
  const [imagemProduto, setImagemProduto] = useState("");
  const [localizacao, setLocalizacao] = useState("");
  const [datasBloqueadas, setDatasBloqueadas] = useState([]);

  useEffect(() => {
    console.log(`${apiBaseUrl}/produtos/${id}`);
    const loadProducts = async () => {
      try {
        const response = await axios.get(`${apiBaseUrl}/produtos/${id}`);

        const produto = response.data;
        setIdProduto(produto.id)
        setNomeProduto(produto.nome);
        setDescricaoProduto(produto.descricao);
        setImagemProduto(produto.imagens[0].url);
        setLocalizacao(produto.cidade.nome);
        setDatasBloqueadas(produto.reservas ?? [])
      } catch (error) {
        console.error("Produto nao existe:", error);
      } finally {
        setIsLoading(false);
      }
    }

    loadProducts();

  }, [])

  const handleSubmit = async (e) => {
    setIsSubmitting(true)
    e.preventDefault();
    
    const token = localStorage.getItem('token') 
    const decoded = jwtDecode(token);
    
    const reservationData = {
      firstName,
      lastName,
      email,
      cidade,
      dataInicioReserva: checkInDate,
      dataFimReserva: checkOutDate,
      horaInicioReserva: arrivalTime.toString().padStart(2, '0') + ":00",
      productId: idProduto,
      customerEmail: decoded.sub
    }
    const response = await axios.post(`${apiBaseUrl}/reservas`, reservationData)
    console.log("Informações enviadas no request:")
    debugger
    if (response.status === 201) {
      // TODO: Mostrar modal de sucesso
      // Once form is successfully submitted:
      toast("Reserva efetuada com sucesso",{ autoClose: 3000 })
      setTimeout(() => {
        navigate('/minhasReservas')
      }, 4000);
    } else {
      setErrorMessage("Por favor, tente novamente. A informações estão inválidas.");
    }
    setIsSubmitting(false)
  };

  const handleArrivalTimeChange = (event) => {
    setArrivalTime(event.target.value);
  };

  const handleCheckInChange = (event) => {
    setCheckInDate(event.target.value);
  };

  const handleCheckOutChange = (event) => {
    setCheckOutDate(event.target.value);
  };

  const handleSetCheckInFromCalendar = (date) => {
    setCheckInDate(date);
  }

  const handleSetCheckOutFromCalendar = (date) => {
    setCheckOutDate(date);
  }

  const getCheckinMessage = () => {
    if (arrivalTime !== "") {
      return (
        <div className={`${styles.messageContainer}`}>
          <div className={`${styles.message}`} dangerouslySetInnerHTML={{ __html: '&#x2713;' }} /> Seu quarto estará pronto para check-in no seguinte horário: {arrivalTime}h
        </div>
      );
    }
    return "";
  };


  console.log(getCheckinMessage)
  return (
    <div className={`${styles.containerReserva}`}>
      <section className={`${styles.sectionReserva}`}>
        <form onSubmit={handleSubmit} className={`${styles.formReserva}`}>
          <div className={`${styles.reservaCol2}`}>
            <h2 className={`${styles.h3Dados}`}>Complete seus dados</h2>
            <div className={`${styles.shadowDados}`}>
              <div className={`${styles.inputNomeEmail}`}>
                <div className={`${styles.inputContainer}`}>
                  <label>Nome</label>
                  <input
                    type="text"
                    name="firstname"
                    id="firstname"
                    value={firstName}
                    onChange={(e) => setFirstname(e.target.value)}
                    required
                  />
                </div>
                <div className={`${styles.inputContainer}`}>
                  <label>Sobrenome</label>
                  <input
                    type="text"
                    name="lastname"
                    id="lastname"
                    value={lastName}
                    onChange={(e) => setLastname(e.target.value)}
                    required
                  />
                </div>
              </div>
              <div className={`${styles.inputNomeEmail}`}>
                <div className={`${styles.inputContainer}`}>
                  <label>Email </label>
                  <input type="email" name="email" value={email} onChange={(e) => setEmail(e.target.value)} required />
                </div>
                <div className={`${styles.inputContainer}`}>
                  <label>Cidade </label>
                  <input type="text" name="cidade" value={cidade} onChange={(e) => setCidade(e.target.value)} required />
                </div>
              </div>
            </div>
            <Calendar
              setExternalCheckInDate={handleSetCheckInFromCalendar}
              setExternalCheckoutDate={handleSetCheckOutFromCalendar}
              bookedDates={datasBloqueadas}
            />
            <div className={`${styles.horarioReserva}`}>
              <p>{getCheckinMessage()}</p>
              <label>
                Indique a sua hora prevista de chegada:
                <select value={arrivalTime} onChange={handleArrivalTimeChange} required>
                  <option value="" disabled>Selecionar hora de chegada</option>
                  {Array.from({ length: 24 }, (_, index) => (
                    <option key={index} value={index}>
                      {index}:00
                    </option>
                  ))}
                </select>
              </label>
            </div>
          </div>
          <div className={`${styles.detalheDaReserva}`}>
            <h2 className={`${styles.h2Dados}`}>Detalhe da Reserva</h2>
            <div className={`${styles.reserva}`}>
              <div className={`${styles.imageView}`}>
                <img src={imagemProduto} alt="" />
                <div className={`${styles.imageViewDetalhes}`}>
                  <h2>{nomeProduto}</h2>
                  <p>{descricaoProduto}</p>
                  <p>{localizacao}</p>
                </div>

              </div>

              <div className={`${styles.checkInOut}`}>
                <label>
                  Check-in:
                  <input
                    type="date"
                    value={checkInDate || ""}
                    onChange={handleCheckInChange}
                    required
                  />
                </label>

                <label>
                  Check-out:
                  <input
                    type="date"
                    value={checkOutDate || ""}
                    onChange={handleCheckOutChange}
                    required
                  />
                </label>
              </div>
              {!isLoggedIn && <div className={`${styles.warning}`}>
                <p>* Para fazer a reserva você precisa estar logado</p>
              </div>}

              <button
                className={`${styles.buttonReserva}`}
                type="submit"
                disabled={isSubmitting || !isLoggedIn}
              >
                {isSubmitting ? 'Enviando...' : 'Confirmar Reserva'}
              </button>
            </div>
          </div>
          <ToastContainer/>
        </form>
      </section>

    </div>
  );
};

export default ReservaComponent;
