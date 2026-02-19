import React, { createContext, useContext, useState, useCallback, useEffect } from 'react';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [guest, setGuest] = useState(null);
  const [token, setToken] = useState(() => localStorage.getItem('token'));
  const [guestToken, setGuestToken] = useState(() => localStorage.getItem('guestToken'));
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const t = localStorage.getItem('token');
    const gt = localStorage.getItem('guestToken');
    setToken(t);
    setGuestToken(gt);
    if (t) {
      try {
        const payload = JSON.parse(atob(t.split('.')[1]));
        setUser({ id: payload.customerId, email: payload.sub, type: 'customer' });
      } catch {
        setUser(null);
      }
    } else {
      setUser(null);
    }
    if (gt) {
      try {
        const payload = JSON.parse(atob(gt.split('.')[1]));
        setGuest({ id: payload.guestId, email: payload.sub, type: 'guest' });
      } catch {
        setGuest(null);
      }
    } else {
      setGuest(null);
    }
    setLoading(false);
  }, []);

  const loginCustomer = useCallback((authResponse) => {
    localStorage.setItem('token', authResponse.data.token);
    localStorage.removeItem('guestToken');
    setToken(authResponse.data.token);
    setGuestToken(null);
    setGuest(null);
    setUser({
      id: authResponse.data.id,
      email: authResponse.data.email,
      name: authResponse.data.name,
      type: 'customer',
    });
  }, []);

  const loginGuest = useCallback((authResponse) => {
    localStorage.setItem('guestToken', authResponse.data.token);
    localStorage.removeItem('token');
    setGuestToken(authResponse.data.token);
    setToken(null);
    setUser(null);
    setGuest({
      id: authResponse.data.id,
      email: authResponse.data.email,
      name: authResponse.data.name,
      eventId: authResponse.data.eventId,
      eventCode: authResponse.data.eventCode,
      eventName: authResponse.data.eventName,
      type: 'guest',
    });
  }, []);

  const logout = useCallback(() => {
    localStorage.removeItem('token');
    localStorage.removeItem('guestToken');
    setToken(null);
    setGuestToken(null);
    setUser(null);
    setGuest(null);
  }, []);

  const isCustomer = !!token && !!user;
  const isGuest = !!guestToken && !!guest;

  const value = {
    user,
    guest,
    token,
    guestToken,
    loading,
    isCustomer,
    isGuest,
    loginCustomer,
    loginGuest,
    logout,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error('useAuth must be used within AuthProvider');
  return ctx;
}
