import { useNavigation } from '@react-navigation/native';
import React, {
  createContext,
  ReactNode,
  useContext,
  useEffect,
  useState,
} from 'react';
import { NativeModules } from 'react-native';

const { Auth } = NativeModules;

type User = {
  id: string;
  username: string;
  name: string;
  email: string;
};

type AuthContextData = {
  user: User | null;
  isAuthenticated: boolean;
  login: (username: string, password: string) => Promise<Boolean>;
  logout: () => void;
};

const AuthContext = createContext<AuthContextData | undefined>(undefined);

type AuthProviderProps = {
  children: ReactNode;
};

export function AuthProvider({ children }: AuthProviderProps) {
  const [user, setUser] = useState<User | null>(null);

  const navigation = useNavigation<any>();

  useEffect(() => {
    Auth.getAuthenticatedUser()
      .then((userData: User) => {
        setUser(userData);
      })
      .catch(() => {
        setUser(null);
      });
  }, []);

  const login = async (
    username: string,
    password: string,
  ): Promise<Boolean> => {
    try {
      await Auth.login(username, password);

      const userData: User = await Auth.getAuthenticatedUser();

      if (!userData?.id) {
        setUser(null);
        return false;
      }

      setUser(userData);

      navigation.navigate('Home');

      return true;
    } catch (error) {
      setUser(null);
      return false;
    }
  };

  const logout = () => {
    Auth.logout();

    setUser(null);

    navigation.navigate('Login');
  };

  return (
    <AuthContext.Provider
      value={{
        user,
        isAuthenticated: user !== null,
        login,
        logout,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth(): AuthContextData {
  const context = useContext(AuthContext);

  if (!context) {
    throw new Error('useAuth deve ser usado dentro de AuthProvider');
  }

  return context;
}
