import React, { createContext, useContext, useEffect, useState } from 'react';
import { NativeEventEmitter, NativeModules } from 'react-native';
import { GpsTelemetry } from '../interfaces';
const { GpsService } = NativeModules;

interface MainContextData {
  location: GpsTelemetry;
}

const MainContext = createContext<MainContextData | undefined>(undefined);

export function MainProvider({ children }: { children: React.ReactNode }) {
  const [location, setLocation] = useState<GpsTelemetry | any>();

  useEffect(() => {
    const gpsEmitter = new NativeEventEmitter(GpsService);
    const subscription = gpsEmitter.addListener('gpsLocation', location => {
      setLocation(location);
    });

    return () => {
      console.log('REMOVENDO LISTENER GPS');
      subscription.remove();
    };
  }, []);

  return (
    <MainContext.Provider
      value={{
        location,
      }}
    >
      {children}
    </MainContext.Provider>
  );
}

export function useMainContext() {
  const context = useContext(MainContext);

  if (!context) {
    throw new Error('useMainContext deve ser usado dentro de MainProvider');
  }

  return context;
}
