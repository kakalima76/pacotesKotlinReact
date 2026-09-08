import React, { createContext, useContext, useEffect, useState } from 'react';
import { NativeEventEmitter, NativeModules } from 'react-native';
import { GpsTelemetry } from '../interfaces';
const { GpsService } = NativeModules;
/** * Dados disponibilizados pelo MainContext. * * Representa o estado global relacionado à telemetria * de localização recebida do serviço nativo Android. */ interface MainContextData {
  /** * Última telemetria GPS recebida do GpsService. */ location: GpsTelemetry;
}
/** * Contexto principal da aplicação. * * O contexto é criado inicialmente como undefined. * O acesso aos seus dados deve ser feito através * do hook useMainContext(). */ const MainContext =
  createContext<MainContextData | undefined>(undefined);
/** * Provedor do contexto principal da aplicação. * * Responsável por: * * - Escutar os eventos enviados pelo GpsService nativo. * - Receber os dados de telemetria através do evento "gpsLocation". * - Armazenar a última localização recebida. * - Disponibilizar a localização para todos os componentes * que estiverem dentro do MainProvider. * * @param children Componentes filhos que terão acesso ao contexto. */ export function MainProvider({
  children,
}: {
  children: React.ReactNode;
}) {
  /** * Armazena a última telemetria recebida do GPS. * * O estado é atualizado sempre que o GpsService * emitir um novo evento "gpsLocation". */ const [
    location,
    setLocation,
  ] = useState<GpsTelemetry | any>();
  /** * Registra o listener responsável por receber * as atualizações de localização enviadas pelo * módulo nativo GpsService. * * O listener permanece ativo enquanto o MainProvider * estiver montado. */ useEffect(() => {
    /** * Cria um emissor de eventos associado ao módulo * nativo responsável pelo GPS. */ const gpsEmitter =
      new NativeEventEmitter(GpsService);
    /** * Escuta o evento "gpsLocation" enviado pelo Android. * * Cada nova telemetria recebida atualiza o estado * global de localização. */ const subscription =
      gpsEmitter.addListener('gpsLocation', location => {
        setLocation(location);
      });
    /** * Remove o listener quando o MainProvider * for desmontado. * * Isso evita que o aplicativo mantenha um listener * ativo desnecessariamente. */ return () => {
      console.log('REMOVENDO LISTENER GPS');
      subscription.remove();
    };
  }, []);
  /** * Disponibiliza a localização através do Context. */ return (
    <MainContext.Provider value={{ location }}>{children}</MainContext.Provider>
  );
}
/** * Hook utilizado pelos componentes da aplicação * para acessar os dados disponibilizados pelo MainContext. * * Exemplo: * * const { location } = useMainContext(); * * @returns Os dados disponíveis no MainContext. * * @throws Error caso o hook seja utilizado fora * de um MainProvider. */ export function useMainContext() {
  const context = useContext(MainContext);
  if (!context) {
    throw new Error('useMainContext deve ser usado dentro de MainProvider');
  }
  return context;
}
