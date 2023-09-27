import React, { createContext, useState, useEffect } from "react";

const AuthContext = createContext();

const AuthProvider = ({ children }) => {
  const [user, setUser] = useState("");
  const [isLoggedIn, setIsLoggedIn] = useState(false);


  useEffect(() => {
    const response = localStorage.getItem("token");
    if (response) {
      saveIsLoggedIn(true)
      setUser(response);
    }
  }, [isLoggedIn]);

  const logout = () => {
    removeLocalStorage()
    saveIsLoggedIn(false);
    navigate("/");
  }

  function saveIsLoggedIn(status) {
    setIsLoggedIn(status);
  }

  function saveToken(token) {
    localStorage.setItem("token", token);
    saveIsLoggedIn(true)
  }

  function getToken() {
    return localStorage.getItem("token");
  }

  function removeLocalStorage() {
    localStorage.removeItem("token");
  }

  return (
    <AuthContext.Provider value={{ user, logout, isLoggedIn, saveIsLoggedIn, removeLocalStorage, getToken, saveToken }}>
      {children}
    </AuthContext.Provider>
  );
};

export { AuthContext, AuthProvider };
