import Cookies from 'js-cookie'

const TokenKey = 'jwtToken'

export function getToken() {
  // return Cookies.get(TokenKey)
  return window.sessionStorage.getItem(TokenKey)
}

export function setToken(token) {
  // return Cookies.set(TokenKey, token)
  return window.sessionStorage.setItem(TokenKey, token)
}

export function removeToken() {
  // return Cookies.remove(TokenKey)
  return window.sessionStorage.removeItem(TokenKey)
}
