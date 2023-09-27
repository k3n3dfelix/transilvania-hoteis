import { useEffect, useState } from "react";
import jwtDecode from 'jwt-decode';
import axios from 'axios';
import apiBaseUrl from '../../api';
import Footer from "../components/Footer";
import Navbar from "../components/NavBar/index";
import ListaReservas from "../components/ListaReservas";

const MinhasReservas = () => {
    const[reservas, setReservas] = useState([])
    
    useEffect(()=>{
        const loadReservas = async () => {
            const token = localStorage.getItem('token') 
            const decoded = jwtDecode(token);
            const response = await axios.get(`${apiBaseUrl}/reservas/customer/${decoded.sub}`);
            setReservas(response.data.content);
        }
        loadReservas();
    },[])
    
    return (
        <div>
            <Navbar/>
            <ListaReservas reservas={reservas}/>
            <Footer/>
        </div>
    );
};

export default MinhasReservas;