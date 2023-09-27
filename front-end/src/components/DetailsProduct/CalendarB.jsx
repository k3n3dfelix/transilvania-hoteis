import * as React from "react";
import { DemoContainer, DemoItem } from "@mui/x-date-pickers/internals/demo";
import { LocalizationProvider } from "@mui/x-date-pickers-pro";
import { AdapterDayjs } from "@mui/x-date-pickers-pro/AdapterDayjs";
import { DateRangeCalendar } from "@mui/x-date-pickers-pro/DateRangeCalendar";
import { useMediaQuery, useTheme } from '@mui/material';
import styles from "./calendar.module.css";
import { Link } from "react-router-dom";


export default function DateRangeCalendarCurrentMonthCalendarPositionProp({id}) {
  const theme = useTheme();
  const isXsScreen = useMediaQuery(theme.breakpoints.down('sm'));

  return (
    <section className={`${styles.section}`}>
      <h3 className={`${styles.h3data}`}>Datas disponíveis</h3>
      <div className={`${styles.sectionCalendar}`}>
        <div className={`${styles.calendar}`}>
          <LocalizationProvider dateAdapter={AdapterDayjs} >
            <DemoContainer components={["DateRangeCalendar"]}>
              <DemoItem label="2 calendars">
                <DateRangeCalendar
                  calendars={isXsScreen ? 1 : 2} />
              </DemoItem>
            </DemoContainer>
          </LocalizationProvider>
        </div>
        <div className={`${styles.dataEscolhida}`}>
          <h2>Adicione as datas da sua viagem para obter <br />preços exatos</h2>
          <Link to={`/reserva/${id}`}>
            <button className={`${styles.buttonReserva}`}>Iniciar Reserva </button>
          </Link>
        </div>
      </div>
    </section>
  );
}
